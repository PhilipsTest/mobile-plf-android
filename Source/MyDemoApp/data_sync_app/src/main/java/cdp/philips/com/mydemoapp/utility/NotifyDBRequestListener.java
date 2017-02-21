package cdp.philips.com.mydemoapp.utility;

import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.listeners.DBChangeListener;
import com.philips.platform.core.listeners.DBFetchRequestListner;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.DSLog;

import java.util.ArrayList;
import java.util.List;

import cdp.philips.com.mydemoapp.database.OrmTypeChecking;
import cdp.philips.com.mydemoapp.database.table.OrmConsentDetail;
import cdp.philips.com.mydemoapp.database.table.OrmMoment;

/**
 * Created by sangamesh on 18/01/17.
 */

public class NotifyDBRequestListener {


    public void notifySuccess(List<? extends Object> ormObjectList, DBRequestListener dbRequestListener) {
        if(dbRequestListener!=null) {
            dbRequestListener.onSuccess((ArrayList<? extends Object>) ormObjectList);
        }else if(DataServicesManager.getInstance().getDbChangeListener()!=null){
            DataServicesManager.getInstance().getDbChangeListener().dBChangeSuccess();
        }else {
            //CallBack not registered
            DSLog.i(DataServicesManager.TAG,"CallBack not registered");
        }
    }

    public void notifySuccess(DBRequestListener dbRequestListener) {
        if(dbRequestListener!=null) {
            dbRequestListener.onSuccess(null);
        }else if(DataServicesManager.getInstance().getDbChangeListener()!=null){
            DataServicesManager.getInstance().getDbChangeListener().dBChangeSuccess();
        }else {
            //Callback not registered
            DSLog.i(DataServicesManager.TAG,"Callback not registered");
        }
    }

    public void notifySuccess(DBRequestListener dbRequestListener,Settings settings) {
        if(dbRequestListener!=null) {
            List list = new ArrayList();
            list.add(settings);
            dbRequestListener.onSuccess(list);
        }else if(DataServicesManager.getInstance().getDbChangeListener()!=null){
            DataServicesManager.getInstance().getDbChangeListener().dBChangeSuccess();
        }else {
            //Callback not registered
            DSLog.i(DataServicesManager.TAG,"Callback not registered");
        }
    }

    public void notifySuccess(DBRequestListener dbRequestListener, ArrayList<OrmConsentDetail> ormConsents) {
        if(dbRequestListener!=null) {
            dbRequestListener.onSuccess(ormConsents);
        }else if(DataServicesManager.getInstance().getDbChangeListener()!=null){
            DataServicesManager.getInstance().getDbChangeListener().dBChangeSuccess();
        }else {
            //Callback not registerd
            DSLog.i(DataServicesManager.TAG,"Callback Not registered");
        }
    }

    public void notifySuccess(DBRequestListener dbRequestListener, OrmMoment ormMoment) {
        if (dbRequestListener != null) {
            List list = new ArrayList();
            list.add(ormMoment);
            dbRequestListener.onSuccess(list);
        } else if (DataServicesManager.getInstance().getDbChangeListener() != null) {
            DataServicesManager.getInstance().getDbChangeListener().dBChangeSuccess();
        } else {
            //No Callback registered
            DSLog.i(DataServicesManager.TAG, "No callback registered");
        }
    }

    public void notifySuccess(DBRequestListener dbRequestListener, List<OrmConsentDetail> ormConsents) {
        if(dbRequestListener!=null) {
            dbRequestListener.onSuccess(ormConsents);
        }else if(DataServicesManager.getInstance().getDbChangeListener()!=null){
            DBChangeListener dbChangeListener=DataServicesManager.getInstance().getDbChangeListener();
            dbChangeListener.dBChangeSuccess();
        }else {
            DSLog.i(DataServicesManager.TAG,"Callback not registered");
        }
    }

    public void notifyFailure(Exception e, DBRequestListener dbRequestListener) {
        if(dbRequestListener!=null) {
            dbRequestListener.onFailure(e);
        }else if(DataServicesManager.getInstance().getDbChangeListener()!=null){
            DataServicesManager.getInstance().getDbChangeListener().dBChangeFailed(e);
        }else {
            //Callback No registered
            DSLog.i(DataServicesManager.TAG,"Callback not registered");
        }
    }

    public void notifyOrmTypeCheckingFailure(DBRequestListener dbRequestListener, OrmTypeChecking.OrmTypeException e, String msg) {
        if (dbRequestListener != null) {
            dbRequestListener.onFailure(e);
        } else if (DataServicesManager.getInstance().getDbChangeListener() != null) {
            DataServicesManager.getInstance().getDbChangeListener().dBChangeFailed(e);
        } else {
            //Callback not registered
            DSLog.i(DataServicesManager.TAG, msg);
        }
    }

    public void notifyMomentFetchSuccess(List<OrmMoment> ormMoments, DBFetchRequestListner dbFetchRequestListner) {
        if(dbFetchRequestListner!=null){
            dbFetchRequestListner.onFetchSuccess(ormMoments);
        }else {
            //CallBack not registered
            DSLog.i(DataServicesManager.TAG,"CallBack not registered");
        }
    }

    public void notifyConsentFetchSuccess(DBFetchRequestListner dbFetchRequestListner, ArrayList<OrmConsentDetail> ormConsents) {
        if(dbFetchRequestListner!=null){
            dbFetchRequestListner.onFetchSuccess(ormConsents);
        }else {
            //CallBack not registered
            DSLog.i(DataServicesManager.TAG,"CallBack not registered");
        }
    }
}
