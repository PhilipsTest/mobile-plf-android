/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.datasync.moments;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.philips.platform.core.BaseAppDataCreator;
import com.philips.platform.core.Eventing;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.datatypes.SynchronisationData;
import com.philips.platform.core.dbinterfaces.DBFetchingInterface;
import com.philips.platform.core.dbinterfaces.DBUpdatingInterface;
import com.philips.platform.core.events.BackendResponse;
import com.philips.platform.core.events.MomentBackendDeleteResponse;
import com.philips.platform.core.events.MomentDataSenderCreatedRequest;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;
import com.philips.platform.datasync.MomentGsonConverter;
import com.philips.platform.datasync.UCoreAccessProvider;
import com.philips.platform.datasync.UCoreAdapter;
import com.philips.platform.datasync.synchronisation.DataSender;
import com.philips.platform.datasync.userprofile.UserRegistrationInterface;

import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MomentsDataSender extends DataSender {
    @Inject
    UCoreAccessProvider accessProvider;

    @Inject
    UCoreAdapter uCoreAdapter;


    @NonNull
    private final MomentsConverter momentsConverter;

    @Inject
    BaseAppDataCreator baseAppDataCreater;

    @Inject
    UserRegistrationInterface userRegistrationImpl;

    @NonNull
    private final MomentGsonConverter momentGsonConverter;

    @Inject
    Eventing eventing;

    protected final Set<Integer> momentIds = new HashSet<>();

    DataServicesManager mDataServicesManager;
    private int eTagIndex=2;

    @Inject
    public MomentsDataSender(
            @NonNull final MomentsConverter momentsConverter,
            @NonNull final MomentGsonConverter momentGsonConverter) {

        DataServicesManager.getInstance().getAppComponant().injectMomentsDataSender(this);
        this.momentsConverter = momentsConverter;
        this.momentGsonConverter = momentGsonConverter;
    }

    @Override
    public boolean sendDataToBackend(@NonNull final List dataToSend) {

        if(dataToSend==null && dataToSend.size()!=0) return false;
        DSLog.i(DSLog.LOG,"sendDataToBackend MomentsDataSender sendDataToBackend data = " + dataToSend.toString());
        if (!accessProvider.isLoggedIn()) {
            return false;
        }

        List<Moment> momentToSync = new ArrayList<>();
        synchronized (momentIds) {
            for (Moment moment : (List<Moment>)dataToSend) {
                if (momentIds.add(moment.getId())) {
                    momentToSync.add(moment);
                }
            }
        }
        return sendMoments(momentToSync);
    }

    private boolean sendMoments(List<? extends Moment> moments) {
        DSLog.i(DSLog.LOG,"MomentsDataSender sendMoments and momets = " + moments.toString());
        if(moments == null || moments.isEmpty()) {
            return true;
        }
        boolean conflictHappened = false;
        String BASE = userRegistrationImpl.getHSDPUrl();

        MomentsClient client = uCoreAdapter.getClient(MomentsClient.class, BASE,
                accessProvider.getAccessToken(), momentGsonConverter);

        for (Moment moment : moments) {
            if (shouldMomentContainCreatorIdAndSubjectId(moment)) {
                conflictHappened = conflictHappened || sendMomentToBackend(client, moment);
            }

            synchronized (momentIds) {
                momentIds.remove(moment.getId());
            }
        }

        return conflictHappened;
    }

    private boolean sendMomentToBackend(MomentsClient client, final Moment moment) {
        DSLog.i(DSLog.LOG,"MomentsDataSender sendMomentToBackend and moment = " + moment.toString());
        if (shouldCreateMoment(moment)) {
            return createMoment(client, moment);
        } else if(shouldDeleteMoment(moment)) {
            return deleteMoment(client, moment);
        } else {
            return updateMoment(client, moment);
        }
    }

    private boolean shouldCreateMoment(final Moment moment) {
        SynchronisationData synchronisationData = moment.getSynchronisationData();
        if (isMomentNeverSynced(synchronisationData) || isMomentNeverSyncedAndDeleted(synchronisationData)) {
            return true;
        }
        return false;
    }

    private boolean isMomentNeverSyncedAndDeleted(final SynchronisationData synchronisationData) {
        return synchronisationData.getGuid().equals(Moment.MOMENT_NEVER_SYNCED_AND_DELETED_GUID);
    }

    private boolean isMomentNeverSynced(final SynchronisationData synchronisationData) {
        return synchronisationData == null;
    }

    private boolean shouldDeleteMoment(final Moment moment) {
        return moment.getSynchronisationData() != null && moment.getSynchronisationData().isInactive();
    }

    private boolean createMoment(MomentsClient client, final Moment moment) {
        try {
            com.philips.platform.datasync.moments.UCoreMomentSaveResponse response = client.saveMoment(moment.getSubjectId(), moment.getCreatorId(),
                    momentsConverter.convertToUCoreMoment(moment));
            if (response != null) {
                addSynchronizationData(moment, response);
                postUpdatedOk(Collections.singletonList(moment));
            }
        } catch (RetrofitError error) {
            onError(error);
            eventing.post(new BackendResponse(1, error));
        }
        return false;
    }

    private boolean updateMoment(MomentsClient client, final Moment moment) {
        try {
            String momentGuid = getMomentGuid(moment.getSynchronisationData());
            Response response = client.updateMoment(moment.getSubjectId(), momentGuid, moment.getCreatorId(),
                    momentsConverter.convertToUCoreMoment(moment));
            List<Header> responseHeaders = response.getHeaders();

            if (isResponseSuccess(response)) {
                Header eTag=responseHeaders.get(eTagIndex);
                //int currentVersion = moment.getSynchronisationData().getVersion();
                if(!TextUtils.isEmpty(eTag.getValue())) {
                    moment.getSynchronisationData().setVersion(Integer.parseInt(eTag.getValue()));
                }
                postUpdatedOk(Collections.singletonList(moment));
            }else if(isConflict(response)){
                DSLog.i(DSLog.LOG,"Exception - 409");
            }
            return false;
        } catch (RetrofitError error) {
            if(error!=null || isConflict(error.getResponse())){
                DSLog.i(DSLog.LOG,"Exception - 409");
            }else {
                eventing.post(new BackendResponse(1, error));
                onError(error);
            }

            return isConflict(error.getResponse());
        }
    }

    @NonNull
    private String getMomentGuid(final SynchronisationData synchronisationData) {
        return synchronisationData.getGuid();
    }

    private boolean deleteMoment(final MomentsClient client, final Moment moment) {
        try {
            String momentGuid = getMomentGuid(moment.getSynchronisationData());
            Response response = client.deleteMoment(moment.getSubjectId(), momentGuid, moment.getCreatorId());
            if (isResponseSuccess(response)) {
                postDeletedOk(moment);
            }
        } catch (RetrofitError error) {
            onError(error);
            eventing.post(new BackendResponse(1, error));
        }
        return false;
    }

    private boolean isResponseSuccess(final Response response) {
        return response != null && (response.getStatus() == HttpURLConnection.HTTP_OK || response.getStatus() == HttpURLConnection.HTTP_CREATED
                || response.getStatus() == HttpURLConnection.HTTP_NO_CONTENT);
    }

    private boolean isConflict(final Response response){
        boolean isconflict = response!=null && response.getStatus() == HttpURLConnection.HTTP_CONFLICT;
        DSLog.i(DSLog.LOG,"isConflict = " + isconflict);
        return isconflict;
    }

   /* private boolean isConflict(final RetrofitError retrofitError) {
        Response response = retrofitError.getResponse();
        return response != null && response.getStatus() == HttpURLConnection.HTTP_CONFLICT;
    }*/

    private boolean shouldMomentContainCreatorIdAndSubjectId(final Moment moment) {
        return isNotNullOrEmpty(moment.getCreatorId()) &&
                isNotNullOrEmpty(moment.getSubjectId());
    }

    private boolean isNotNullOrEmpty(final String string) {
        return string != null && !string.isEmpty();
    }

    private void addSynchronizationData(Moment moment, com.philips.platform.datasync.moments.UCoreMomentSaveResponse uCoreMomentSaveResponse) {
        SynchronisationData synchronisationData =
                baseAppDataCreater.createSynchronisationData(uCoreMomentSaveResponse.getMomentId(), false,
                        moment.getDateTime(), 1);
        moment.setSynchronisationData(synchronisationData);
    }

    private void postUpdatedOk(final List<Moment> momentList) {
        eventing.post(new MomentDataSenderCreatedRequest(momentList, null));
    }

    private void postDeletedOk(final Moment moment) {
        eventing.post(new MomentBackendDeleteResponse(moment, null));
    }

    @Override
    public Class<? extends Moment> getClassForSyncData() {
        return Moment.class;
    }
}
