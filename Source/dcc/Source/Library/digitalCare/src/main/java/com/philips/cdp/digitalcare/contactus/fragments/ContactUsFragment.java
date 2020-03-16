/*
 * ContactUsFragment will help to provide options to contact Philips.
 *
 * @author : Ritesh.jha@philips.com
 * @since : 19 Jan 2015
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.contactus.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.social.facebook.FacebookWebFragment;
import com.philips.cdp.digitalcare.social.twitter.TwitterWebFragment;
import com.philips.cdp.digitalcare.util.CommonRecyclerViewAdapter;
import com.philips.cdp.digitalcare.util.ContactUsUtils;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.digitalcare.util.MenuItem;
import com.philips.cdp.digitalcare.util.Utils;
import com.philips.cdp.digitalcare.view.ProgressAlertDialog;
import com.philips.platform.uid.utils.DialogConstants;
import com.philips.platform.uid.view.widget.AlertDialogFragment;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ContactUsFragment extends DigitalCareBaseFragment implements ContactUsContract,OnClickListener {
    private static final String USER_PREFERENCE = "user_product";
    private static final String USER_SELECTED_PRODUCT_CTN_CALL = "contact_call";
    private static final String USER_SELECTED_PRODUCT_CTN_HOURS = "contact_hours";
    private SharedPreferences prefs = null;
    private Button mChatBtn = null;
    private Button mCallPhilipsBtn = null;
    private TextView mFirstRowText = null;
    private TextView mContactUsOpeningHours = null;
    private RecyclerView mContactUsSocilaProviderButtonsParent = null;
    private LinearLayout.LayoutParams mSecondContainerParams = null;
    private LinearLayout mLLSocialParent = null;
    private ProgressAlertDialog mDialog = null;

    private static String TAG = ContactUsFragment.class.getSimpleName();

    private ContactUsPresenter contactUsFragmentPresenter;
    private Configuration config = null;
    private Utils mUtils = null;
    private ContactUsUtils mContactUsUtils = null;
    private AlertDialogFragment mAlertDialog = null;
    private long mLastClkTime;

    public static final String CONTACT_US_DIALOG_TAG = "CONTACT_US_DIALOG_TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        contactUsFragmentPresenter = new ContactUsPresenter(this);
        prefs = getActivity().getSharedPreferences(USER_PREFERENCE, Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.consumercare_fragment_contact_us, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mContactUsSocilaProviderButtonsParent =  view.findViewById(
                R.id.contactUsSocialProvideButtonsParent);
        mSecondContainerParams = (LinearLayout.LayoutParams) mContactUsSocilaProviderButtonsParent
                .getLayoutParams();
        mChatBtn = view.findViewById(R.id.contactUsChat);
        mCallPhilipsBtn = view.findViewById(R.id.contactUsCall);
        mContactUsOpeningHours = view.findViewById(R.id.contactUsOpeningHours);
        mFirstRowText = view.findViewById(R.id.firstRowText);
        mLLSocialParent = view.findViewById(R.id.contactUsSocialParent);
        mUtils = new Utils();
        mContactUsUtils = new ContactUsUtils();
        final float density = getResources().getDisplayMetrics().density;
        setHelpButtonParams(density);
        mChatBtn.setOnClickListener(this);
        mChatBtn.setTransformationMethod(null);
        mCallPhilipsBtn.setOnClickListener(this);
        mCallPhilipsBtn.setTransformationMethod(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initServiceDiscovery();
        if (isInternetAvailable && contactUsFragmentPresenter.isCdlsUrlNull()) {
            contactUsFragmentPresenter.requestCdlsData();
        } else {
            String contactNumber = prefs.getString(USER_SELECTED_PRODUCT_CTN_CALL, "");
            String hours = prefs.getString(USER_SELECTED_PRODUCT_CTN_HOURS, "");
            if (isContactHoursCached()) {
                setContactHours(hours);
            }
            if (isContactNumberCached()) {
                setContactNumberVisible(contactNumber);
            }
        }
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                    (AnalyticsConstants.PAGE_CONTACT_US,
                            getPreviousName(), getPreviousName());

    config = getResources().getConfiguration();

    createContactUsSocialProvideMenu();
    setViewParams(config);
}

    public void initServiceDiscovery() {
        contactUsFragmentPresenter.initialiseSD(getAppName());
    }

    private void setContactNumberVisible(String contactNumber) {
        mCallPhilipsBtn.setVisibility(View.VISIBLE);
        mContactUsOpeningHours.setVisibility(View.VISIBLE);
        mCallPhilipsBtn.setText(getResources().getString(R.string.call_number)
                + " "
                + contactNumber);
    }

    private void setContactHours(String hours) {
        mFirstRowText.setVisibility(View.VISIBLE);
        mFirstRowText.setText(getSpannedText(hours));
    }

    @Override
    public void setTextCallPhilipsBtn(String phoneNumber){
        mCallPhilipsBtn.setText(getResources().getString(R.string.call_number)+ " "+ phoneNumber);
    }

    public boolean isContactNumberCached() {
        String customerSupportNumber = prefs.getString(USER_SELECTED_PRODUCT_CTN_CALL, "");
        return (customerSupportNumber != null && !customerSupportNumber.isEmpty());
    }

    public boolean isContactHoursCached() {
        String contactHours = prefs.getString(USER_SELECTED_PRODUCT_CTN_HOURS, "");
        return (contactHours != null && !contactHours.isEmpty());
    }

    @Override
    public void onResume() {
        super.onResume();
        setViewParams(config);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        setViewParams(config);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        closeProgressDialog();
    }

    @Override
    public void showCallPhilipsBtn() {
        mCallPhilipsBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog.cancel();
            mDialog = null;
        }
    }

    @Override
    public void startProgressDialog() {
        if (getActivity() == null) {
            return;
        }
        if (mDialog == null) {
            mDialog = new ProgressAlertDialog(getActivity(), R.style.loaderTheme);
            mDialog.setCancelable(true);
        }
        mDialog.setMessage(getActivity().getResources().getString(
                R.string.loading));
        if (!(getActivity().isFinishing())) {
            mDialog.show();
        }
    }

    protected void callPhilips() {
        try {
            final Intent myintent = new Intent(Intent.ACTION_DIAL);
            myintent.setData(Uri.parse("tel:"+ prefs.getString(USER_SELECTED_PRODUCT_CTN_CALL, "")));
            myintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(myintent);
        } catch (NullPointerException e) {
      }
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();

        if (SystemClock.elapsedRealtime() - mLastClkTime < 2000) {
            return;
        }
        mLastClkTime = SystemClock.elapsedRealtime();

        final String tag = (String) view.getTag();
        boolean actionTaken = false;

        try {
            if (tag != null) {
                actionTaken = DigitalCareConfigManager.getInstance().getCcListener()
                        .onSocialProviderItemClicked(tag.toString());
            }
        } catch (NullPointerException exception) {
        }
        if (actionTaken) {
            return;
        }

        if (id == R.id.contactUsChat && isConnectionAvailable()) {
            tagServiceRequest(AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_CHAT);
            showFragment(new ChatFragment());
        } else if (id == R.id.contactUsCall) {
            if (!isContactNumberCached()) {
                showDialog(getActivity().getString(R.string.no_data));
            } else if (isSimAvailable() && !isTelephonyEnabled()){
                showDialog(getActivity().getString(R.string.no_call_functionality));
            } else if (isSimAvailable()) {
                tagServiceRequest(AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_CALL);
                callPhilips();
            } else if (!isSimAvailable()) {
                showDialog(getActivity().getString(R.string.check_sim));
            }
            else {
                showDialog(getActivity().getString(R.string.check_sim));
            }
        } else if (tag != null
                && tag.equalsIgnoreCase(getStringKey(R.string.dcc_facebook)) && isConnectionAvailable()) {
            launchFacebookFeature();
        } else if (tag != null
                && tag.equalsIgnoreCase(getStringKey(R.string.dcc_twitter)) && isConnectionAvailable()) {
            launchTwitterFeature();
        } else if (tag != null && (tag.equalsIgnoreCase(getStringKey(R.string.dcc_send_email))) && isConnectionAvailable()) {
            tagServiceRequest(AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_EMAIL);
            sendEmail();
        }
    }

    boolean isSimAvailable() {
        return mUtils.isSimAvailable(getActivity());
    }

    boolean isTelephonyEnabled(){
        return mUtils.isTelephonyEnabled(getActivity());
    }

    private void launchFacebookFeature() {
        tagServiceRequest(AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_Facebook);
        try {
            final Uri uri = Uri.parse(mContactUsUtils.facebooAppUrl(getActivity()));
            final Map<String, String> contextData = new HashMap<>();
            contextData.put(AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL,
                    AnalyticsConstants.ACTION_VALUE_FACEBOOK);
            contextData.put(AnalyticsConstants.ACTION_KEY_SOCIAL_TYPE,
                    AnalyticsConstants.ACTION_VALUE_FACEBOOK);
            contextData.put(AnalyticsConstants.ACTION_KEY_EXIT_LINK,
                    uri + toString());
            DigitalCareConfigManager.getInstance().getTaggingInterface().trackActionWithInfo
                    (AnalyticsConstants.ACTION_EXIT_LINK,
                            contextData);

            getActivity().getApplicationContext().getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception e) {
            showFragment(new FacebookWebFragment(mContactUsUtils.facebookWebUrl(getActivity())));
        }
    }

    public void launchTwitterFeature() {
        tagServiceRequest(AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_TWITTER);
        final Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        String information = getProductInformation();
        tweetIntent.putExtra(Intent.EXTRA_TEXT, information);
        tweetIntent.setType("text/plain");

        final PackageManager packManager = getActivity().getPackageManager();
        final List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,
                PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android")) {
                tweetIntent.setClassName(resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved) {
            trackTwitterAction();
            startActivity(tweetIntent);
        } else {
            showFragment(new TwitterWebFragment(mContactUsUtils.twitterPageName(getActivity())));
        }
    }

    private void trackTwitterAction() {
        final String twitterUrl = "www.twitter.com/";
        final String twitterSupportAccount = getActivity().getString(R.string.twitter_page);
        final String twitterPageName = twitterUrl + "@" + twitterSupportAccount;
        final Map<String, String> contextData = new HashMap<>();
        contextData.put(AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL, AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_TWITTER);
        contextData.put(AnalyticsConstants.ACTION_KEY_SOCIAL_TYPE, AnalyticsConstants.ACTION_VALUE_SERVICE_CHANNEL_TWITTER);
        contextData.put(AnalyticsConstants.ACTION_KEY_EXIT_LINK, twitterPageName);
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                (AnalyticsConstants.ACTION_EXIT_LINK, contextData);
    }

    protected String getProductInformation() {
        final String twitterLocalizedPrefixText = getActivity().getResources().getString(
                R.string.support_productinformation);
        final String productTitle = DigitalCareConfigManager.getInstance()
                .getConsumerProductInfo().getProductTitle();
        final String ctn = DigitalCareConfigManager.getInstance()
                .getConsumerProductInfo().getCtn();
        String productInfo = "@" + mContactUsUtils.twitterPageName(getActivity()) + " " + twitterLocalizedPrefixText
                + " "
                + productTitle
                + " "
                + ctn;

        return productInfo;
    }

    private void tagServiceRequest(String serviceChannel) {
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackActionWithInfo
                (AnalyticsConstants.ACTION_SERVICE_REQUEST,
                        AnalyticsConstants.ACTION_KEY_SERVICE_CHANNEL, serviceChannel);
    }

    private void tagTechnicalError() {
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackActionWithInfo
                (AnalyticsConstants.ACTION_SET_ERROR,
                        AnalyticsConstants.ACTION_KEY_TECHNICAL_ERROR,
                        AnalyticsConstants.ACTION_VALUE_TECHNICAL_ERROR_RESPONSE_CDLS);
    }

    @Override
    public void enableBottomText() {
        mCallPhilipsBtn.setVisibility(View.VISIBLE);
        mContactUsOpeningHours.setVisibility(View.VISIBLE);
        mFirstRowText.setVisibility(View.VISIBLE);
    }

    @Override
    public void fadeoutButtons() {
        tagTechnicalError();
        mCallPhilipsBtn.setVisibility(View.GONE);
        TypedArray titles = getResources().obtainTypedArray
                (R.array.social_service_provider_menu_title);
        boolean isSocialButtonsEnabled = titles.length() > 0 ? true : false;
        if (!isSocialButtonsEnabled && DigitalCareConfigManager.getInstance().getEmailUrl() == null) {
            showDialog(getResources().getString(R.string.NO_SUPPORT_KEY));
        }
    }

    @Override
    public void setViewParams(Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mSecondContainerParams.leftMargin = mSecondContainerParams.rightMargin = mLeftRightMarginPort;
        } else {
            mSecondContainerParams.leftMargin = mSecondContainerParams.rightMargin = mLeftRightMarginPort;
        }
        mContactUsSocilaProviderButtonsParent.setLayoutParams(mSecondContainerParams);
    }

    protected void sendEmail() {
        showFragment(new EmailFragment());
    }

    private String getStringKey(int resId) {
        return getResources().getResourceEntryName(resId);
    }

    private void setHelpButtonParams(float density) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, (int) (getActivity().getResources()
                .getDimension(R.dimen.support_btn_height) * density));
        params.topMargin = (int) getActivity().getResources().getDimension(R.dimen.marginTopButton);
        mCallPhilipsBtn.setLayoutParams(params);
        mChatBtn.setLayoutParams(params);
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.contact_us);
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_CONTACT_US;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != mAlertDialog)
            mAlertDialog.dismiss();
    }

    private void showDialog(String message){
        final AlertDialogFragment.Builder builder = new AlertDialogFragment.Builder(getContext())
                .setDialogType(DialogConstants.TYPE_ALERT)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAlertDialog != null) {
                            mAlertDialog.dismiss();
                        }
                    }
                })
                .setDimLayer(DialogConstants.DIM_SUBTLE)
                .setCancelable(false);
        mAlertDialog = builder.create();
        mAlertDialog.show(getFragmentManager(), CONTACT_US_DIALOG_TAG);

    }

    private void createContactUsSocialProvideMenu() {
        final ContactUsFragment context = this;

        TypedArray titles = getResources().obtainTypedArray(R.array.social_service_provider_menu_title);
        TypedArray resources = getResources().obtainTypedArray(R.array.social_service_provider_menu_resources);
        ArrayList<MenuItem> menus = new ArrayList<>();

        if (DigitalCareConfigManager.getInstance().getEmailUrl() != null){
            menus.add(new MenuItem(R.string.dls_message, R.string.dcc_send_email));
        }

        if(!Utils.isCountryChina())
            for (int i = 0; i < titles.length(); i++) {
                menus.add(new MenuItem(resources.getResourceId(i, 0), titles.getResourceId(i, 0)));
            }

        if(mContactUsUtils.serviceDiscoveryTwitterUrl() == null && getActivity().getString(R.string.twitter_page).trim().length() == 0 ){
            DigiCareLogger.d(TAG,"Removed Twitter");
            for (int i = 0; i < menus.size(); i++) {
                if (menus.get(i).mText == R.string.dcc_twitter) {
                    menus.remove(i);
                    break;
                }
            }
        }

        if(mContactUsUtils.serviceDiscoveryFacebookUrl() == null  && getActivity().getString(R.string.facebook_product_pageID).trim().length() == 0 ){
            DigiCareLogger.d(TAG,"Removed Facebook");
            for (int i = 0; i < menus.size(); i++) {
                if (menus.get(i).mText == R.string.dcc_facebook) {
                    menus.remove(i);
                    break;
                }
            }
        }

        if(menus.size() == 0){
            hideSocialView();
        }

        updateRecyclerView(context, menus);
    }

    @Override
    public void updateFirstRowSharePreference(StringBuilder stringBuilder,String phoneNumber){
        mFirstRowText.setText(getSpannedText(stringBuilder.toString()));
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_SELECTED_PRODUCT_CTN_HOURS, stringBuilder.toString());
        editor.putString(USER_SELECTED_PRODUCT_CTN_CALL, phoneNumber);
        editor.apply();
    }
    
    private Spanned getSpannedText(String string) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(string);
        }
    }

    private void hideSocialView() {
        mLLSocialParent.setVisibility(View.GONE);
        View view = getActivity().findViewById(R.id.dividerContactUsSplit);
        view.setVisibility(View.GONE);
    }

    private void updateRecyclerView(final ContactUsFragment context, final ArrayList<MenuItem> menus) {
        RecyclerView recyclerView = mContactUsSocilaProviderButtonsParent;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecyclerViewSeparatorItemDecoration(getContext()));
        recyclerView.setAdapter(new CommonRecyclerViewAdapter<MenuItem>(menus, R.layout.consumercare_icon_button) {
            @Override
            public void bindData(RecyclerView.ViewHolder viewHolder, MenuItem menuItem) {
                View container = viewHolder.itemView.findViewById(R.id.icon_button);
                Label contactUsListLabel = container.findViewById(R.id.icon_button_text);
                contactUsListLabel.setText(menuItem.mText);
                TextView contactUsListIcon = container.findViewById(R.id.icon_button_icon);
                contactUsListIcon.setText(menuItem.mIcon);
                container.setTag(getResources().getResourceEntryName(menuItem.mText));
                container.setOnClickListener(context);
            }
        });
    }

    @Override
    public boolean isViewAdded(){
       return isAdded();
    }


    @Override
    public void updateLiveChatButton(int visibility) {
        toggleLiveChatVisibility(visibility);
    }

    private void toggleLiveChatVisibility(int visibility) {
        if (getResources().getBoolean(R.bool.live_chat_required)) {
            mChatBtn.setVisibility(View.VISIBLE);
        } else {
            mChatBtn.setVisibility(visibility);
        }
    }

}
