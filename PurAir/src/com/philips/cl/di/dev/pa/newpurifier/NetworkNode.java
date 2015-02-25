package com.philips.cl.di.dev.pa.newpurifier;

import java.util.Observable;

import android.os.Parcel;
import android.os.Parcelable;

import com.philips.cl.di.dev.pa.util.MetricsTracker;


public class NetworkNode extends Observable implements Parcelable {
	public static enum PAIRED_STATUS {PAIRED, NOT_PAIRED, UNPAIRED, PAIRING}

	private String mIpAddress;
	private String mCppId;
	private ConnectionState mConnectionState;
	
	private String mName;
	private String mModelName;
	private String mHomeSsid;
	private long mBootId;
	private String mEncryptionKey;
	
	private boolean mIsOnlineViaCpp = false;
	private PAIRED_STATUS mPairedState = PAIRED_STATUS.NOT_PAIRED;
	private long mLastPairedTime;

	public NetworkNode() {
	}
	
	public synchronized String getIpAddress() {
		return mIpAddress;
	}
	
	public synchronized void setIpAddress(String ipAddress) {
		this.mIpAddress = ipAddress;
	}

	public synchronized String getCppId() {
		return mCppId;
	}

	public synchronized void setCppId(String cppId) {
		this.mCppId = cppId;
	}

	public synchronized ConnectionState getConnectionState() {
		return mConnectionState;
	}
	
	public synchronized void setConnectionState(ConnectionState connectionState) {
		// TODO remove vertical specific code (MetricsTracker)
		if (!connectionState.equals(mConnectionState)) {
			MetricsTracker.trackActionConnectionType(connectionState);
		}
		
		synchronized(this) { // notifyObservers called from same Thread
			if (connectionState.equals(mConnectionState)) return;
			this.mConnectionState = connectionState;
		}
		setChanged();
		notifyObservers();
	}

	public synchronized String getName() {
		return mName;
	}

	public synchronized void setName(String name) {
		this.mName = name;
	}
	
	//TODO: to implement
	public synchronized String getModelName() {
		throw new UnsupportedOperationException();
		//return mModelName;
	}

	public synchronized void setModelName(String modelName) {
		this.mModelName = modelName;
	}

	public synchronized String getHomeSsid() {
		return mHomeSsid;
	}

	public synchronized void setHomeSsid(String homeSsid) {
		if (mHomeSsid == null || mHomeSsid.isEmpty()) return;
		this.mHomeSsid = homeSsid;
	}

	public synchronized long getBootId() {
		return mBootId;
	}

	public synchronized void setBootId(long bootId) {
		this.mBootId = bootId;
	}
	
	public synchronized String getEncryptionKey() {
		return mEncryptionKey;
	}

	public synchronized void setEncryptionKey(String encryptionKey) {
		this.mEncryptionKey = encryptionKey;
	}
	
	public synchronized boolean isOnlineViaCpp() {
		return mIsOnlineViaCpp;
	}

	public synchronized void setOnlineViaCpp(boolean isOnlineViaCpp) {
		this.mIsOnlineViaCpp = isOnlineViaCpp;
	}

	public synchronized NetworkNode.PAIRED_STATUS getPairedState() {
		return mPairedState;
	}

	public synchronized void setPairedState(NetworkNode.PAIRED_STATUS pairedState) {
		this.mPairedState = pairedState;
	}

	public synchronized long getLastPairedTime() {
		return mLastPairedTime;
	}

	public synchronized void setLastPairedTime(long lastPairedTime) {
		this.mLastPairedTime = lastPairedTime;
	}

	

    protected NetworkNode(Parcel in) {
        mIpAddress = in.readString();
        mCppId = in.readString();
        mConnectionState = ConnectionState.values()[in.readInt()];
        mName = in.readString();
        mModelName = in.readString();
        mHomeSsid = in.readString();
        mBootId = in.readLong();
        mEncryptionKey = in.readString();
        mIsOnlineViaCpp = in.readByte() != 0x00;
        mPairedState = PAIRED_STATUS.values()[in.readInt()];
        mLastPairedTime = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mIpAddress);
        dest.writeString(mCppId);
        dest.writeInt(mConnectionState.ordinal());
        dest.writeString(mName);
        dest.writeString(mModelName);
        dest.writeString(mHomeSsid);
        dest.writeLong(mBootId);
        dest.writeString(mEncryptionKey);
        dest.writeByte((byte) (mIsOnlineViaCpp ? 0x01 : 0x00));
        dest.writeInt(mPairedState.ordinal());
        dest.writeLong(mLastPairedTime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NetworkNode> CREATOR = new Parcelable.Creator<NetworkNode>() {
        @Override
        public NetworkNode createFromParcel(Parcel in) {
            return new NetworkNode(in);
        }

        @Override
        public NetworkNode[] newArray(int size) {
            return new NetworkNode[size];
        }
    };

    public static NetworkNode.PAIRED_STATUS getPairedStatusKey(int status){
		if (status>= 0 && status < NetworkNode.PAIRED_STATUS.values().length) {
		return NetworkNode.PAIRED_STATUS.values()[status];
		}
		return NetworkNode.PAIRED_STATUS.NOT_PAIRED;
	}
}