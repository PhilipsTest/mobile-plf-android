/*
 * © Koninklijke Philips N.V., 2015.
 *   All rights reserved.
 */

package com.philips.cdp.dicommclient.port.common;

import android.support.annotation.VisibleForTesting;

import com.philips.cdp.dicommclient.appliance.DICommAppliance;
import com.philips.cdp.dicommclient.cpp.CppController;
import com.philips.cdp.dicommclient.cpp.ICPEventListener;
import com.philips.cdp.dicommclient.cpp.pairing.AppPairingHandlerRelationship;
import com.philips.cdp.dicommclient.cpp.pairing.PairingHandlerRelationship;
import com.philips.cdp.dicommclient.cpp.pairing.UserPairingHandlerRelationship;
import com.philips.cdp.dicommclient.discovery.DICommClientWrapper;
import com.philips.cdp.dicommclient.discovery.DiscoveryManager;
import com.philips.cdp.dicommclient.networknode.ConnectionState;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.port.DICommPort;
import com.philips.cdp.dicommclient.port.DICommPortListener;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.dicommclient.util.DICommLog;
import com.philips.icpinterface.ICPClient;
import com.philips.icpinterface.PairingService;
import com.philips.icpinterface.data.Commands;
import com.philips.icpinterface.data.Errors;
import com.philips.icpinterface.data.PairingEntitiyReference;
import com.philips.icpinterface.data.PairingReceivedRelationships;
import com.philips.icpinterface.data.PairingReceivedRelationships.PairingEntity;

import java.util.Date;
import java.util.HashMap;

public class PairingHandler<T extends DICommAppliance> {

    public static final String PAIRING_REFERENCEPROVIDER = "cpp";
    public static final String PAIRING_USER_REFERENCEPROVIDER = "cphuser";
    public static final String PAIRING_DI_COMM_RELATIONSHIP = "di-comm";
    public static final String PAIRING_NOTIFY_RELATIONSHIP = "notify";
    public static final String PAIRING_DATA_ACCESS_RELATIONSHIP = "dataaccess";

    public static final int MAX_RETRY = 3;

    private CppController mCloudController;

    private String currentRelationshipType;
    private PairingListener<T> pairingListener;
    private String secretKey;

    private PermissionListener permissionListener = null;
    private static HashMap<String, Integer> attemptsCount = new HashMap<String, Integer>();
    private PairingHandlerRelationship pairingHandlerRelationship;

    private enum ENTITY {
        PURIFIER, APP, DATAACCESS
    }

    private ENTITY entity_state;
    private T mAppliance;

    @VisibleForTesting
    ICPEventListener mIcpEventListener = new ICPEventListener() {
        /**
         * Method onICPCallbackEventOccurred.
         *
         * @param eventType *            int
         * @param status    *            int
         * @param obj       *            ICPClient
         * @see com.philips.cdp.dicommclient.cpp.ICPEventListener#onICPCallbackEventOccurred(int,
         * int, ICPClient)
         */
        @Override
        public void onICPCallbackEventOccurred(int eventType, int status,
                                               ICPClient obj) {
            DICommLog.d(DICommLog.PAIRING, "onICPCallbackEventOccurred eventType "
                    + eventType + " status " + status);

            if (status != Errors.SUCCESS) {
                if (permissionListener == null) {
                    DICommLog.e(DICommLog.PAIRING, "Pairing call-FAILED (get or add), pairing attempt:" + getPairingAttempts(mAppliance.getNetworkNode().getCppId()) + " Appliance name:" + mAppliance.getNetworkNode().getName());
                    notifyListenerFailed();
                } else {
                    DICommLog.e(DICommLog.PAIRING, "get permission call failed");
                    permissionListener.onCallFailed();
                }
                return;
            }

            PairingService pairingObj = (PairingService) obj;
            if (eventType == Commands.PAIRING_REMOVE_RELATIONSHIP) {
                DICommLog.i(DICommLog.PAIRING, "RemoveRelation call-SUCCESS");
                if (currentRelationshipType.equals(PAIRING_DI_COMM_RELATIONSHIP)) {
                    if (entity_state == ENTITY.PURIFIER) {
                        DICommLog.i(DICommLog.PAIRING, "Outgoing di-comm relationship (one removed) - Need to remove the other");
                        entity_state = ENTITY.APP;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mCloudController.getPairingController().removeRelationship(getDICommApplianceEntity(), getAppEntity(), currentRelationshipType, mIcpEventListener);
                            }
                        }).start();
                    } else if (entity_state == ENTITY.APP) {
                        DICommLog.i(DICommLog.PAIRING, "DI-COMM Relationship removed successfully");
                        entity_state = ENTITY.DATAACCESS;
                        currentRelationshipType = PAIRING_NOTIFY_RELATIONSHIP;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mCloudController.getPairingController().removeRelationship(getAppEntity(), getDICommApplianceEntity(), currentRelationshipType, mIcpEventListener);
                            }
                        }).start();
                    }
                } else if (currentRelationshipType.equals(PAIRING_NOTIFY_RELATIONSHIP)) {
                    if (entity_state == ENTITY.DATAACCESS) {
                        DICommLog.i(DICommLog.PAIRING, "Notify Relationship removed successfully");
                        entity_state = ENTITY.PURIFIER;
                        currentRelationshipType = PAIRING_DATA_ACCESS_RELATIONSHIP;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                mCloudController.getPairingController().removeRelationship(getDICommApplianceEntity(), null, currentRelationshipType, mIcpEventListener);
                            }
                        }).start();
                    }
                }
                // This will indicate all relations have been removed
                else if (currentRelationshipType.equals(PAIRING_DATA_ACCESS_RELATIONSHIP)) {
                    if (entity_state == ENTITY.PURIFIER) {
                        DICommLog.i(DICommLog.PAIRING, "DATAACCESS Relationship removed successfully - Pairing removed successfully");
                        notifyListenerSuccess();
                    }
                }
            } else if (eventType == Commands.PAIRING_ADD_RELATIONSHIP) {
                DICommLog.i(DICommLog.PAIRING, "AddRelation call-SUCCESS");
                String relationStatus = pairingObj.getAddRelationStatus();
                if (relationStatus.equalsIgnoreCase("completed")) {

                    if (currentRelationshipType.equals(PAIRING_DI_COMM_RELATIONSHIP)) {
                        if (pairingHandlerRelationship instanceof UserPairingHandlerRelationship) {
                            notifyListenerSuccess();
                        } else {
                            DICommLog.i(DICommLog.PAIRING, "Pairing relationship added successfully - Requesting Notification relationship");
                            currentRelationshipType = PAIRING_NOTIFY_RELATIONSHIP;
                            AppPairingHandlerRelationship appPairingHandlerRelationship = new AppPairingHandlerRelationship(mCloudController.getAppCppId(), PAIRING_REFERENCEPROVIDER, mAppliance.getNetworkNode().getCppId(), mAppliance.getDeviceType());
                            mCloudController.getPairingController().addRelationship(PAIRING_NOTIFY_RELATIONSHIP, appPairingHandlerRelationship, mIcpEventListener);
                        }
                    } else {
                        DICommLog.i(DICommLog.PAIRING, "Notification relationship added successfully - Pairing completed");
                        DICommLog.i(DICommLog.PAIRING, "Paring status set to true");
                        mAppliance.getNetworkNode().setPairedState(NetworkNode.PAIRED_STATUS.PAIRED);
                        mAppliance.getNetworkNode().setLastPairedTime(new Date().getTime());

                        //TODO see user story COM-89
                        DiscoveryManager<T> discoveryManager = (DiscoveryManager<T>) DiscoveryManager.getInstance();
                        T appliance = discoveryManager.getApplianceByCppId(mAppliance.getNetworkNode().getCppId());
                        appliance.getNetworkNode().setPairedState(NetworkNode.PAIRED_STATUS.PAIRED);
                        discoveryManager.updateApplianceInDatabase(appliance);

                        notifyListenerSuccess();
                    }
                } else {
                    DICommLog.e(DICommLog.PAIRING, "Pairing status is PENDING, pairing attempt:" + getPairingAttempts(mAppliance.getNetworkNode().getCppId()) + " Appliance name:" + mAppliance.getNetworkNode().getName());
                    notifyListenerFailed();
                }
            } else if (eventType == Commands.PAIRING_ADD_PERMISSIONS) {
                permissionListener.onPermissionAdded();
            } else if (eventType == Commands.PAIRING_GET_PERMISSIONS) {
                boolean permissionExists = false;
                for (int i = 0; i < pairingObj.getNumberOfPermissionsReturned(); i++) {
                    permissionExists = pairingObj.getPermissionAtIndex(i).equals("Push");
                    if (permissionExists) break;
                }
                permissionListener.onPermissionReturned(permissionExists);
            } else if (eventType == Commands.PAIRING_REMOVE_PERMISSIONS) {
                permissionListener.onPermissionRemoved();
            }
        }
    };

    /**
     * Constructor for PairingHandler.
     *
     * @param appliance T
     */
    public PairingHandler(T appliance) {
        this(appliance, null);
    }

    /**
     * Constructor for PairingHandler.
     *
     * @param appliance       T
     * @param pairingListener PairingListener
     */
    public PairingHandler(T appliance, PairingListener<T> pairingListener) {
        if (appliance == null) return;
        this.mAppliance = appliance;
        this.pairingListener = pairingListener;
        mCloudController = DICommClientWrapper.getCloudController();
    }

    /**
     * Constructor for PairingHandler.
     *
     * @param iPairingListener PairingListener
     * @param appliance        T
     */
    public PairingHandler(PairingListener<T> iPairingListener, T appliance) {
        this(appliance, iPairingListener);
    }

    public void setPermissionListener(PermissionListener iPermissionListener) {
        permissionListener = iPermissionListener;
    }

    /**
     * Method startPairing- starts pairing procedure
     */
    public void startPairing() {
        if (mAppliance == null) return;
        DICommLog.i(DICommLog.PAIRING, "Started pairing with appliance = " + mAppliance.getNetworkNode().getName() + " attempt: " + getPairingAttempts(mAppliance.getNetworkNode().getCppId()));

        currentRelationshipType = PAIRING_DI_COMM_RELATIONSHIP;
        String appEui64 = mCloudController.getAppCppId();
        pairingHandlerRelationship = new AppPairingHandlerRelationship(appEui64, PAIRING_REFERENCEPROVIDER, mAppliance.getNetworkNode().getCppId(), mAppliance.getDeviceType());

        startPairingPortTask(currentRelationshipType, pairingHandlerRelationship);
    }

    public void startUserPairing(String userId, String accessToken, String relationType) {
        if (mAppliance == null) return;
        DICommLog.i(DICommLog.PAIRING, "Started user pairing with appliance = " + mAppliance.getNetworkNode().getName() + " attempt: " + getPairingAttempts(mAppliance.getNetworkNode().getCppId()));

        currentRelationshipType = PAIRING_DI_COMM_RELATIONSHIP;
        pairingHandlerRelationship = new UserPairingHandlerRelationship(userId, PAIRING_USER_REFERENCEPROVIDER, relationType, accessToken, mAppliance.getNetworkNode().getCppId(), mAppliance.getDeviceType());

        startPairingPortTask(currentRelationshipType, pairingHandlerRelationship);
    }

    /**
     * Method startPairingPortTask.
     *
     * @param relationshipType           String
     * @param pairingHandlerRelationship PairingHandlerRelationship
     */
    private void startPairingPortTask(final String relationshipType, final PairingHandlerRelationship pairingHandlerRelationship) {
        if (relationshipType.equals(PAIRING_DI_COMM_RELATIONSHIP)) {
            if (mAppliance == null) return;
            secretKey = generateRandomSecretKey();

            PairingPort pairingPort = mAppliance.getPairingPort();
            pairingPort.addPortListener(new DICommPortListener() {

                @Override
                public void onPortUpdate(DICommPort<?> port) {
                    DICommLog.i(DICommLog.PAIRING, "PairingPort call-SUCCESS");

                    if (PAIRING_DI_COMM_RELATIONSHIP.equals(currentRelationshipType)) {
                        mCloudController.getPairingController().addRelationship(currentRelationshipType, secretKey, pairingHandlerRelationship, mIcpEventListener);
                    } else {
                        mCloudController.getPairingController().addRelationship(currentRelationshipType, pairingHandlerRelationship, mIcpEventListener);
                    }
                    port.removePortListener(this);
                }

                @Override
                public void onPortError(DICommPort<?> port, Error error, String errorData) {
                    DICommLog.e(DICommLog.PAIRING, "PairingPort call-FAILED");

                    notifyListenerFailed();
                    port.removePortListener(this);
                }
            });
            pairingPort.triggerPairing(mCloudController.getAppType(), pairingHandlerRelationship.getCppId(), secretKey);
        } else {
            currentRelationshipType = relationshipType;
            mCloudController.getPairingController().addRelationship(relationshipType, pairingHandlerRelationship, mIcpEventListener);
        }
    }

    public void initializeRelationshipRemoval() {
        currentRelationshipType = PAIRING_DI_COMM_RELATIONSHIP;
        entity_state = ENTITY.PURIFIER;

        mCloudController.getPairingController().removeRelationship(null, getDICommApplianceEntity(), currentRelationshipType, mIcpEventListener);
    }

    /**
     * add Trustee data
     *
     * @return PairingEntitiyReference
     */
    private PairingEntitiyReference getDICommApplianceEntity() {
        PairingEntitiyReference pairingTrustee = new PairingEntitiyReference();
        pairingTrustee.entityRefId = mAppliance.getNetworkNode().getCppId();
        pairingTrustee.entityRefProvider = PAIRING_REFERENCEPROVIDER;
        pairingTrustee.entityRefType = mAppliance.getDeviceType();
        pairingTrustee.entityRefCredentials = null;

        DICommLog.i(DICommLog.PAIRING, "Appliance entityRefId"
                + pairingTrustee.entityRefId);
        DICommLog.i(DICommLog.PAIRING, "Appliance entityRefType"
                + pairingTrustee.entityRefType);

        return pairingTrustee;
    }

    /**
     * add Trustee data
     *
     * @return PairingEntitiyReference
     */
    private PairingEntitiyReference getAppEntity() {
        PairingEntitiyReference pairingTrustor = new PairingEntitiyReference();
        pairingTrustor.entityRefId = mCloudController.getAppCppId();
        pairingTrustor.entityRefProvider = PAIRING_REFERENCEPROVIDER;
        pairingTrustor.entityRefType = mCloudController.getAppType();
        pairingTrustor.entityRefCredentials = null;

        DICommLog.i(DICommLog.PAIRING, "app entityRefId" + pairingTrustor.entityRefId);
        DICommLog.i(DICommLog.PAIRING, "app entityRefType" + pairingTrustor.entityRefType);

        return pairingTrustor;
    }

    /**
     * generates random key	 *
     *
     * @return random secret key
     */
    public String generateRandomSecretKey() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    public static int getPairingAttempts(String eui64) {
        int attempts = 0;
        if (attemptsCount.containsKey(eui64)) {
            attempts = attemptsCount.get(eui64);
        }
        return attempts;
    }

    public static void clear() {
        if (attemptsCount != null)
            attemptsCount.clear();
    }

    public void setPairingAttempts(String eui64) {
        int attempts = 0;
        if (attemptsCount.containsKey(eui64)) {
            attempts = attemptsCount.get(eui64);
        }
        attemptsCount.put(eui64, attempts + 1);
    }

    public void resetPairingAttempts(String eui64) {
        attemptsCount.put(eui64, 0);
    }

    private int getNumberOfRelationships(PairingService pairingObj) {
        int noOfRelationReturned = 0;
        if (pairingObj == null)
            return noOfRelationReturned;

        for (int i = 0; i < pairingObj.getNumberOfRelationsReturned(); i++) {
            PairingReceivedRelationships relation = pairingObj
                    .getReceivedRelationsAtIndex(i);
            PairingEntity entity = relation.pairingRcvdRelEntityTo;
            if (entity.PairingEntityId.equalsIgnoreCase(mAppliance.getNetworkNode().getCppId()))
                noOfRelationReturned++;
        }
        return noOfRelationReturned;
    }

    private void notifyListenerSuccess() {
        if (pairingListener == null) return;
        pairingListener.onPairingSuccess(mAppliance);
    }

    private void notifyListenerFailed() {

        if (mAppliance == null) return;
        if (getPairingAttempts(mAppliance.getNetworkNode().getCppId()) < MAX_RETRY) {
            setPairingAttempts(mAppliance.getNetworkNode().getCppId());
            // If DI-COMM local (Pairing Port) request fails, then retry only the DI-COMM request
            if (pairingHandlerRelationship instanceof UserPairingHandlerRelationship) {
                startUserPairing(pairingHandlerRelationship.getCppId(), pairingHandlerRelationship.getCredentials(), pairingHandlerRelationship.getType());
            } else {
                startPairing();
            }
        } else {
            mAppliance.getNetworkNode().setPairedState(NetworkNode.PAIRED_STATUS.NOT_PAIRED);
            if (pairingListener == null) return;
            pairingListener.onPairingFailed(mAppliance);
        }
    }

    /**
     * Method addPermission- adds permission to a existing relationship
     *
     * @param relationType String
     * @param permission   String[]
     */
    public void addPermission(String relationType, String[] permission) {
        if (!mCloudController.isSignOn())
            return;
        PairingService pairingService = mCloudController.getPairingController().createPairingService(mIcpEventListener);
        int retStatus = pairingService.addPermissionsRequest(null, getDICommApplianceEntity(),
                relationType, permission);
        if (Errors.SUCCESS != retStatus) {
            DICommLog.d(DICommLog.PAIRING, "Request Invalid/Failed Status: " + retStatus);
            return;
        }
        pairingService.setPairingServiceCommand(Commands.PAIRING_ADD_PERMISSIONS);
        retStatus = pairingService.executeCommand();
        if (Errors.REQUEST_PENDING != retStatus) {
            DICommLog.d(DICommLog.PAIRING, "Request Invalid/Failed Status: " + retStatus);
        }
    }

    /**
     * Method getPermission-get permissions of a existing relationship
     *
     * @param relationType String
     * @param permission   String[]
     */
    public void getPermission(String relationType, String[] permission) {
        if (!mCloudController.isSignOn()) {
            permissionListener.onCallFailed();
        } else {
            int iMaxPermissons = 5;
            int iPermIndex = 0;
            PairingService pairingService = mCloudController.getPairingController().createPairingService(mIcpEventListener);
            int retStatus;

            retStatus = pairingService.getPermissionsRequest(null,
                    getDICommApplianceEntity(), relationType, iMaxPermissons, iPermIndex);
            if (Errors.SUCCESS != retStatus) {
                DICommLog.d(DICommLog.PAIRING, "Request Invalid/Failed Status: " + retStatus);
                permissionListener.onCallFailed();
                return;
            }
            pairingService.setPairingServiceCommand(Commands.PAIRING_GET_PERMISSIONS);
            retStatus = pairingService.executeCommand();
            if (Errors.REQUEST_PENDING != retStatus) {
                permissionListener.onCallFailed();
                DICommLog.d(DICommLog.PAIRING, "Request Invalid/Failed Status: " + retStatus);
            }
        }
    }

    /**
     * Method removePermission-remove permission from a existing relationship
     *
     * @param relationType String
     * @param permission   String[]
     */
    public void removePermission(String relationType, String[] permission) {
        if (!mCloudController.isSignOn())
            return;
        PairingService pairingService = mCloudController.getPairingController().createPairingService(mIcpEventListener);
        int retStatus = pairingService.removePermissionsRequest(null, getDICommApplianceEntity(),
                relationType, permission);
        if (Errors.SUCCESS != retStatus) {
            DICommLog.d(DICommLog.PAIRING, "Request Invalid/Failed Status: " + retStatus);
            return;
        }
        pairingService.setPairingServiceCommand(Commands.PAIRING_REMOVE_PERMISSIONS);
        retStatus = pairingService.executeCommand();
        if (Errors.REQUEST_PENDING != retStatus) {
            DICommLog.d(DICommLog.PAIRING, "Request Invalid/Failed Status: " + retStatus);
        }
    }

    public static long getDiffInDays(long pairedOn) {
        Date currentDate = new Date();
        long currenttimeInMillis = currentDate.getTime();

        // Difference between current and previous timestamp
        long diff = currenttimeInMillis - pairedOn;
        long diffInDays = diff / (1000 * 60 * 60 * 24);

        return diffInDays;
    }

    public static boolean pairApplianceIfNecessary(NetworkNode networkNode) {
        if (networkNode == null || networkNode.getConnectionState() != ConnectionState.CONNECTED_LOCALLY) {
            return false;
        }

        DICommLog.i(DICommLog.PAIRING, "In PairToPurifier: " + networkNode.getPairedState());

        // First time pairing or on EWS
        if (networkNode.getPairedState() == NetworkNode.PAIRED_STATUS.NOT_PAIRED) {
            return true;
        }
        //Everyday check for pairing
        long lastPairingCheckTime = networkNode.getLastPairedTime();
        long diffInDays = PairingHandler.getDiffInDays(lastPairingCheckTime);

        if (networkNode.getPairedState() == NetworkNode.PAIRED_STATUS.PAIRED && diffInDays != 0) {
            return true;
        }
        return false;
    }
}
