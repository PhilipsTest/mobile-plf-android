package cdp.philips.com.mydemoapp.listener;

import com.philips.platform.core.datatypes.Consent;
import com.philips.platform.core.datatypes.ConsentDetail;

import java.util.ArrayList;

public interface DBChangeListener {

    public void onSuccess(ArrayList<? extends Object> data);
    public void onSuccess(Object data);
    public void onFailure(Exception exception);
    //TODO: Spoorti - Before changing interfaces please align with Ajay and iOS team
    public void onBackEndConsentSuccess(Consent consent);
}
