package com.philips.cl.di.dev.pa.newpurifier;

import java.util.List;

import com.philips.cl.di.dev.pa.ews.EWSConstant;
import com.philips.cl.di.dev.pa.purifier.SubscriptionHandler;
import com.philips.cl.di.dev.pa.util.ALog;
import com.philips.cl.di.dicomm.communication.CommunicationStrategy;
import com.philips.cl.di.dicomm.communication.Error;
import com.philips.cl.di.dicomm.communication.ResponseHandler;
import com.philips.cl.di.dicomm.communication.SubscriptionEventListener;
import com.philips.cl.di.dicomm.port.AirPort;
import com.philips.cl.di.dicomm.port.DICommPort;
import com.philips.cl.di.dicomm.port.ScheduleListPort;

/**
 * @author Jeroen Mols
 * @date 28 Apr 2014
 */
public class AirPurifier extends DICommAppliance implements ResponseHandler , SubscriptionEventListener {

	private final String mUsn;
	private String latitude;
	private String longitude;

    protected final ScheduleListPort mScheduleListPort;
	private SubscriptionHandler mSubscriptionHandler;

	private final AirPort mAirPort;
	private PurifierListener mPurifierListener;
	private final CommunicationStrategy mCommunicationStrategy;

	public AirPurifier(NetworkNode networkNode, CommunicationStrategy communicationStrategy, String usn) {
	    super(networkNode, communicationStrategy);
		mUsn = usn;
		mCommunicationStrategy = communicationStrategy;
		mSubscriptionHandler = new SubscriptionHandler(getNetworkNode(), this);		
		
        mAirPort = new AirPort(mNetworkNode,communicationStrategy);
		mScheduleListPort = new ScheduleListPort(mNetworkNode, communicationStrategy);
		

        addPort(mAirPort);
        addPort(mScheduleListPort);
	}

	public AirPurifier(NetworkNode networkNode, CommunicationStrategy communicationStrategy, String usn, SubscriptionHandler subscriptionHandler) {
		this(networkNode, communicationStrategy, usn);
		mSubscriptionHandler = subscriptionHandler;
	}

	public void setPurifierListener(PurifierListener mPurifierListener) {
		this.mPurifierListener = mPurifierListener;
	}

	public AirPort getAirPort() {
		return mAirPort;
	}

    public ScheduleListPort getScheduleListPort() {
        return mScheduleListPort;
    }
    
    public void enableSubscription() {
		mCommunicationStrategy.enableSubscription(this, mNetworkNode);
	}

	public void disableSubscription() {
		mCommunicationStrategy.disableSubscription(this, null);
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getUsn() {
		return mUsn;
	}

	public boolean isDemoPurifier() {
		return (EWSConstant.PURIFIER_ADHOCIP.equals(mNetworkNode.getIpAddress()));
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("name: ").append(getName()).append("   ip: ").append(getNetworkNode().getIpAddress())
				.append("   eui64: ").append(getNetworkNode().getCppId()).append("   bootId: ").append(getNetworkNode().getBootId())
				.append("   usn: ").append(getUsn()).append("   paired: ").append(getNetworkNode().getPairedState())
				.append("   airportInfo: ").append(getAirPort().getPortProperties()).append("   firmwareInfo: ").append(getFirmwarePort().getPortProperties())
				.append("   connectedState: ").append(getNetworkNode().getConnectionState()).append("   lastKnownssid: ")
				.append("   lat: ").append(getLatitude()).append("   long: ").append(getLongitude())
				.append(getNetworkNode().getHomeSsid());
		return builder.toString();
	}

	@Override
	public void onError(Error error, String errorData) {
		// TODO DIComm Refactor - remove
	}

	private void notifySubscriptionListeners(String data) {
		ALog.d(ALog.APPLIANCE, "Notify subscription listeners - " + data);

		List<DICommPort<?>> portList = getAvailablePorts();
		
		for (DICommPort<?> port : portList) {
            if (port.isResponseForThisPort(data)) {
                port.handleSubscription(data);
            }
        }

	}

	@Override
	public void onSuccess(String data) {
		ALog.d(ALog.APPLIANCE, "Success event received");
		notifySubscriptionListeners(data);
	}

	@Override
	public void onSubscriptionEventReceived(String data) {
		// TODO Auto-generated method stub
		notifySubscriptionListeners(data);
	}
}
