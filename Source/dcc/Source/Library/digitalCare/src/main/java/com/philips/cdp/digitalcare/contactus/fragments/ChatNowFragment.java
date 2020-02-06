/*
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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.util.ContactUsUtils;
import com.philips.cdp.digitalcare.util.DigiCareLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


@SuppressLint("SetJavaScriptEnabled")
@SuppressWarnings("serial")
public class ChatNowFragment extends DigitalCareBaseFragment {

    private static final int SELECT_IMAGE = 0x2;
    private static final int PERMISSION_GALLERY = 0x11;

    private WebView mWebView = null;
    private ProgressBar mProgressBar = null;
    private String mUrl = null;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallbackArray;

    private boolean layoutListenerAdded;
    private boolean isImmersiveMode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.consumercare_webview_noscroll, container, false);

        int systemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        if (((systemUiVisibility & View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) != 0)
                || ((systemUiVisibility & View.SYSTEM_UI_FLAG_IMMERSIVE) != 0)) {
            isImmersiveMode = true;
        }

        setChatEndPoint(ContactUsUtils.liveChatUrl(getActivity()) + "&origin=15_global_en_" + getAppName() + "-app_" + getAppName() + "-app");
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Map<String, String> contextData = new HashMap<>();
        contextData.put(AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL, AnalyticsConstants.PAGE_CONTACTUS_CHATNOW);
        contextData.put(AnalyticsConstants.PAGE_CONTACTUS_CHATNOW, getPreviousName());
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                (AnalyticsConstants.PAGE_CONTACTUS_CHATNOW, contextData);
        loadChat();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isImmersiveMode && !layoutListenerAdded) {
            layoutListenerAdded = true;
            getView().getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        }
    }


    int getKeyboardHeight() {
        InputMethodManager systemService = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            Method getInputMethodWindowVisibleHeight = systemService.getClass().getDeclaredMethod("getInputMethodWindowVisibleHeight", null);
            getInputMethodWindowVisibleHeight.setAccessible(true);
            return (int) getInputMethodWindowVisibleHeight.invoke(systemService);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return 0;
    }



    private void loadChat() {
        if (ContactUsUtils.liveChatUrl(getActivity()) == null) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mWebView.setWebChromeClient(new CustomWebChromeClient());
            mWebView.getSettings().setSaveFormData(false);
            loadWebPageContent(getChatEndPoint(), mWebView, mProgressBar);
        }
    }

    private void initView(View view) {
        mWebView = view.findViewById(R.id.webView);
        setSaveFromData();
        mProgressBar = view
                .findViewById(R.id.common_webview_progress);
        mProgressBar.setVisibility(View.GONE);
    }


    private void setSaveFromData() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
            mWebView.getSettings().setSaveFormData(false);
        }
        //No need to handle for version more than "o" ,Can be refer here https://developer.android.com/reference/android/webkit/WebSettings
    }


    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.chat_now);
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
    public void onDestroyView() {
        if (isImmersiveMode && layoutListenerAdded) {
            layoutListenerAdded = false;
            View view = getView();
            if (view != null) {
                ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
                if (viewTreeObserver != null) {
                    viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener);
                }
            }
        }
        super.onDestroyView();
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
        CookieManager.getInstance().removeSessionCookies(null);

    }

    private void clearWebViewData() {
        mWebView.saveState(new Bundle());
    }

    protected class CustomWebChromeClient extends WebChromeClient {

        //Android 5.0+
        @Override
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

                    mFilePathCallbackArray.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));

                } else {

                    mFilePathCallbackArray.onReceiveValue(null);

                }
                mFilePathCallbackArray = null;
                mUploadMessage = null;
                clearWebViewData();
                break;
        }
    }

    private void checkPermissionForGallery(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_GALLERY);
        } else {
            startActivityForResult(createImagePickerIntent(), SELECT_IMAGE);
        }
    }

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

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (getActivity() != null && isVisible()) {
                int keyboardHeight = getKeyboardHeight();
                int screenHeight = getActivity().findViewById(R.id.webViewParent).getHeight();
                DigiCareLogger.d("globalLayoutListener", "keypadHeight = " + keyboardHeight);
                if (keyboardHeight != 0 && mWebView.getHeight() == screenHeight) {
                    ViewGroup.LayoutParams layoutParams = mWebView.getLayoutParams();
                    layoutParams.height = screenHeight - keyboardHeight;
                    mWebView.setLayoutParams(layoutParams);
                    mWebView.requestLayout();
                }

                if (keyboardHeight == 0 && mWebView.getHeight() != screenHeight) {
                    ViewGroup.LayoutParams layoutParams = mWebView.getLayoutParams();
                    layoutParams.height = screenHeight;
                    mWebView.setLayoutParams(layoutParams);
                    mWebView.requestLayout();
                }
            }
        }
    };

    public static String loadWebPageContent(final String webUrl, final WebView webView, final ProgressBar viewProgressBar) {

        webView.loadUrl(webUrl);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView v, String pageUrl, Bitmap icon) {
                super.onPageStarted(v, pageUrl, icon);

                viewProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageCommitVisible(WebView v, String pageUrl) {
                super.onPageCommitVisible(v, pageUrl);

                viewProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageFinished(WebView v, String pageUrl) {
                super.onPageFinished(v, pageUrl);

                viewProgressBar.setVisibility(View.GONE);
            }

        });

        return webView.getUrl();
    }
}