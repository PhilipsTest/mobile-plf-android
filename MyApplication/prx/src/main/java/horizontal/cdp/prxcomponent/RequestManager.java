package horizontal.cdp.prxcomponent;

import android.content.Context;

import com.cdp.prx.assets.AssetModel;
import com.cdp.prx.network.NetworkWrapper;

import horizontal.cdp.prxcomponent.listeners.ResponseHandler;
import horizontal.cdp.prxcomponent.listeners.ResponseListener;

/**
 * Created by 310190678 on 02-Nov-15.
 */
public class RequestManager {

    private Context mContext = null;
    private AssetModel mAssetModel = null;
    private ResponseHandler mResponseHandler = null;

    public void init(Context applicationContext) {
        mContext = applicationContext;
    }

    public void executeRequest(PrxDataBuilder prxDataBuilder,ResponseListener responseListener) {
        new NetworkWrapper(mContext, prxDataBuilder, responseListener).execute();
    }

    public void cancelRequest(String requestTag) {

    }
}
