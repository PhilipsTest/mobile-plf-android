/**
 * ChatNowFragment will help to inflate chat webpage on the screen.
 *
 * @author : Ritesh.jha@philips.com
 * @since : 16 Jan 2015
 * Copyright (c) 2016 Philips. All rights reserved.
 */
package com.philips.cdp.digitalcare.contactus.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.util.Utils;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


@SuppressLint("SetJavaScriptEnabled")
public class ChatNowFragment extends DigitalCareBaseFragment {

    private static final int SELECT_IMAGE = 0x2;
    private static final int PERMISSION_GALLERY = 0x11;

    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;
    private String mUrl = null;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallbackArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consumercare_webview_noscroll, container, false);
        setChatEndPoint(getChatUrl() + "&origin=15_global_en_" + getAppName() + "-app_" + getAppName() + "-app");
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Map<String, String> contextData = new HashMap<String, String>();
        contextData.put(AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL, AnalyticsConstants.PAGE_CONTACTUS_CHATNOW);
        contextData.put(AnalyticsConstants.PAGE_CONTACTUS_CHATNOW, getPreviousName());
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                (AnalyticsConstants.PAGE_CONTACTUS_CHATNOW, contextData);
        loadChat();
    }

    private void loadChat() {
        if (getChatUrl() == null) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mWebView.setWebChromeClient(new CustomWebChromeClient());
            Utils.loadWebPageContent(getChatEndPoint(), mWebView, mProgressBar);
        }
    }

    private void initView(View view) {
        mWebView = (WebView) view.findViewById(R.id.webView);
        mProgressBar = (ProgressBar) view
                .findViewById(R.id.common_webview_progress);
        mProgressBar.setVisibility(View.GONE);
    }

    private String getChatUrl() {
        String chatLink = null;
        if(DigitalCareConfigManager.getInstance().getLiveChatUrl() == null){
            chatLink = getResources().getString(R.string.live_chat_url);
        }else {
            chatLink = DigitalCareConfigManager.getInstance().getLiveChatUrl();
        }
        return chatLink;
    }

    @Override
    public String getActionbarTitle() {
        String title = getResources().getString(R.string.chat_now);
        return title;
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void setViewParams(Configuration config) {
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_CONTACTUS_CHATNOW;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView = null;
        }
    }

    public String getChatEndPoint() {
        return mUrl;
    }
    public void setChatEndPoint(final String url) {

        if (url.startsWith("http://") || url.startsWith("https://"))
            mUrl = url;
    }

    @Override
    public void onStop() {
        super.onStop();
        clearWebViewData();
    }

    private void clearWebViewData() {
        mWebView.saveState(new Bundle());
    }

    protected class CustomWebChromeClient extends WebChromeClient {

        //Android 5.0+
        @Override
        @SuppressLint("NewApi")
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mFilePathCallbackArray != null) {
                mFilePathCallbackArray.onReceiveValue(null);
            }
            mFilePathCallbackArray = filePathCallback;
            checkPermissionForGallery(getContext());
            return true;
        }
        // For Android > 4.1
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            handleImageChooser(uploadMsg);
        }
        // Andorid 3.0 +
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            handleImageChooser(uploadMsg);
        }
        //Android 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            handleImageChooser(uploadMsg);
        }
    }

    private void handleImageChooser(ValueCallback<Uri> uploadMsg) {
        if (mUploadMessage != null) {
            mUploadMessage.onReceiveValue(null);
        }
        mUploadMessage = uploadMsg;
        startActivityForResult(createImagePickerIntent(), SELECT_IMAGE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_IMAGE:
                if (mFilePathCallbackArray == null && mUploadMessage == null) {
                    return;
                }
                Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();


                if (result != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mFilePathCallbackArray.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    } else {
                        mUploadMessage.onReceiveValue(result);
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mFilePathCallbackArray.onReceiveValue(null);
                    } else {
                        mUploadMessage.onReceiveValue(null);
                    }
                }
                mFilePathCallbackArray = null;
                mUploadMessage = null;
                clearWebViewData();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void checkPermissionForGallery(Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_GALLERY);
            } else {
                startActivityForResult(createImagePickerIntent(), SELECT_IMAGE);
            }
        }else{
            startActivityForResult(createImagePickerIntent(), SELECT_IMAGE);
        }
    }
    

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(createImagePickerIntent(), SELECT_IMAGE);
                }
                break;
        }
    }

    private Intent createImagePickerIntent() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return Intent.createChooser(intent,"Choose image");
    }
}
