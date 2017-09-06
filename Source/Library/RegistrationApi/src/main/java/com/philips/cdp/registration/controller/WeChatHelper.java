package com.philips.cdp.registration.controller;


import android.app.*;
import android.content.*;

import com.philips.cdp.registration.ui.utils.*;
import com.tencent.mm.sdk.modelmsg.*;
import com.tencent.mm.sdk.openapi.*;

public class WeChatHelper {

    private   String weChatAppId = null;
    private   String weChatAppSecret = null;
    private IWXAPI weChatApi = null ;

    public boolean isWeChatSupported(){
        if(weChatApi == null){
           return false;
        }
        return  weChatApi.isWXAppInstalled()&& weChatApi.isWXAppSupportAPI();
    }

    public boolean register(Activity context, String weChatAppId, String weChatAppSecret ){
        weChatApi = WXAPIFactory.createWXAPI(context, weChatAppId, false);
        return weChatApi.registerApp(weChatAppSecret);
    }


    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra(RegConstants.WECHAT_ERR_CODE);
            String code = intent.getStringExtra(RegConstants.WECHAT_CODE);

        }
    };

    public BroadcastReceiver getWechatReciever(){
        return mMessageReceiver;
    }


    public void authenticate() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "123456";
        weChatApi.sendReq(req);
    }
}
