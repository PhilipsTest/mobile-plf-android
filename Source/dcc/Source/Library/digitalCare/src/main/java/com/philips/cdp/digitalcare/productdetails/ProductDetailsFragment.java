/*
 * ProductDetailsFragment will help to show product details.
 *
 * @author : Ritesh.jha@philips.com
 * @since : 16 Jan 2015
 * <p/>
 * Copyright (c) 2016 Philips. All rights reserved.
 */

package com.philips.cdp.digitalcare.productdetails;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.faq.fragments.FaqListFragment;
import com.philips.cdp.digitalcare.homefragment.DigitalCareBaseFragment;
import com.philips.cdp.digitalcare.listeners.PrxFaqCallback;
import com.philips.cdp.digitalcare.listeners.PrxSummaryListener;
import com.philips.cdp.digitalcare.productdetails.model.ViewProductDetailsModel;
import com.philips.cdp.digitalcare.prx.PrxWrapper;
import com.philips.cdp.digitalcare.util.CommonRecyclerViewAdapter;
import com.philips.cdp.digitalcare.util.MenuItem;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.cdp.prxclient.datamodels.support.SupportModel;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ProductDetailsFragment extends DigitalCareBaseFragment implements
        OnClickListener {

    private ImageView mProductImageTablet = null;
    private static int mSmallerResolution = 0;
    private static boolean isTablet = false;
    private static int mScrollPosition = 0;
    private RecyclerView mProdButtonsParent = null;
    private LinearLayout mProdVideoContainer = null;
    private TextView mProductTitle = null;
    private TextView mProductVideoHeader = null;
    private TextView mCtn = null;
    private ImageView mProductImage = null;
    private HorizontalScrollView mVideoScrollView = null;
    private String mManualPdf = null;
    private String mProductPage = null;
    private String mDomain = null;
    private ViewProductDetailsModel mViewProductDetailsModel = null;
    private PrxWrapper mPrxWrapper = null;
    private CommonRecyclerViewAdapter<MenuItem> mAdapter;
    private static final String TAG ="ProductDetailsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.consumercare_fragment_view_product,
                container, false);
        initView(view);
        DigitalCareConfigManager.getInstance().getTaggingInterface().trackPageWithInfo
                (AnalyticsConstants.PAGE_VIEW_PRODUCT_DETAILS,
                        getPreviousName(), getPreviousName());
        getDisplayWidth();
        return view;
    }

    private void initView(View view) {
        mProdButtonsParent = view.findViewById(
                R.id.prodbuttonsParent);

        mProdVideoContainer = view.findViewById(
                R.id.videoContainerParent);


        mProductImageTablet = view.findViewById(R.id.productImageTablet);
        mProductImage = view.findViewById(R.id.productimage);

        mProductTitle = view.findViewById(R.id.name);
        mProductVideoHeader = view.findViewById(R.id.productVideoText);
        mCtn = view.findViewById(R.id.variant);
        mVideoScrollView = view.findViewById(R.id.videoScrollView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        createProductDetailsMenu();
        updateViewsWithData();
        setViewParams(config);

        mVideoScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                mScrollPosition = mVideoScrollView.getScrollX(); //for horizontalScrollView
            }
        });
    }

    private void initView(List<String> mVideoLength) throws NullPointerException {

        if (mVideoLength != null && mVideoLength.size() > 0) {
            mProductVideoHeader.setVisibility(View.VISIBLE);
        } else {
            return;
        }

        final int totalVideoCount = mVideoLength.size()>20?20:mVideoLength.size(); // Max video count 20 to avoid any out of memory situation
        for (int i = 0; i < totalVideoCount; i++) {
            View child = getActivity().getLayoutInflater().inflate(R.layout.consumercare_viewproduct_video_view, null);
            ImageView videoThumbnail = child.findViewById(R.id.videoContainer);
            TextView videoPlay = child.findViewById(R.id.videoPlay);
            ImageView videoLeftArrow = child.findViewById(R.id.videoLeftArrow);
            ImageView videoRightArrow = child.findViewById(R.id.videoRightArrow);

            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) videoThumbnail
                    .getLayoutParams();

            if (getActivity() != null) {
                float density = getActivity().getResources().getDisplayMetrics().density;

                if (mVideoLength.size() > 1 && (mVideoLength.size() - 1) != i && isTablet) {
                    param.rightMargin = (int) (25 * density);
                    videoThumbnail.setLayoutParams(param);
                }

                videoLeftArrow.bringToFront();
                videoRightArrow.bringToFront();
                if(mVideoLength.size() < 2){
                    videoLeftArrow.setVisibility(View.GONE);
                    videoRightArrow.setVisibility(View.GONE);
                }

                addNewVideo(i, mVideoLength.get(i), child, videoThumbnail, videoPlay, videoLeftArrow, videoRightArrow);
            }
        }
    }

    private int getDisplayWidth() {
        if (getActivity() == null) {
            return 0;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float density = metrics.density;

        if (widthPixels > heightPixels) {
            mSmallerResolution = heightPixels;
        } else {
            mSmallerResolution = widthPixels;
        }

        isTablet = ((float) mSmallerResolution / density > 480);

        if (isTablet) {
            return (int) getActivity().getResources().getDimension(R.dimen.view_prod_details_video_height);
        }

        return mSmallerResolution;
    }

    protected void loadVideoThumbnail(final ImageView imageView, final String thumbnail) {
        ImageRequest request = new ImageRequest(thumbnail,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imageView.setImageBitmap(addBlankThumbnail());
                    }
                });

        RequestQueue imageRequestQueue = Volley.newRequestQueue(getContext());
        imageRequestQueue.add(request);
    }


    private Bitmap addBlankThumbnail() {
        int height;
        if (isTablet) {
            height = (getDisplayWidth() / 2) + 13;
        } else {
            height = (getDisplayWidth() / 2) + 46;
        }
        int width;

        try {
            width = getDisplayWidth();
        } catch (NullPointerException e) {
            width = (int) getActivity().getResources().getDimension(R.dimen.view_prod_details_video_height);
        }

        Bitmap imageBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        imageBitmap.eraseColor(Color.BLACK);
        return imageBitmap;
    }

    private void addNewVideo(int counter, final String video, View child, ImageView videoThumbnail, TextView videoPlay,
                             ImageView videoLeftArrow, ImageView videoRightArrow) {
        String tag = counter + "";
        final String thumbnail = video.replace("/content/", "/image/") + "?wid=" + getDisplayWidth() + "&amp;";

        loadVideoThumbnail(videoThumbnail, thumbnail);
        child.setTag(tag);
        videoPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> contextData = new HashMap<>();
                contextData.put(AnalyticsConstants.ACTION_KEY_VIEW_PRODUCT_VIDEO_NAME, video);
                DigitalCareConfigManager.getInstance().getTaggingInterface().
                        trackActionWithInfo(AnalyticsConstants.ACTION_KEY_VIEW_PRODUCT_VIDEO_START,
                                contextData);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(video), "video/mp4");
                getActivity().startActivity(intent);
            }
        });

        if (isTablet) {
            videoLeftArrow.setVisibility(View.GONE);
            videoRightArrow.setVisibility(View.GONE);
        }
        videoLeftArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mScrollPosition >= 400) {
                    mVideoScrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Log.d(TAG,"getWidthOfScreen"+mScrollPosition+" "+getDisplayWidth());
                            mVideoScrollView.smoothScrollTo((mScrollPosition - getDisplayWidth()), 0);
                        }
                    }, 5);
                }
            }
        });

        videoRightArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoScrollView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"getWidthOfScreen"+mScrollPosition+" "+getDisplayWidth());
                        mVideoScrollView.smoothScrollTo((mScrollPosition + getDisplayWidth()), 0);
                    }
                }, 5);
            }
        });
        mProdVideoContainer.addView(child);
    }



    private void createProductDetailsMenu() {
        final ProductDetailsFragment context = this;

        RecyclerView recyclerView = mProdButtonsParent;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new RecyclerViewSeparatorItemDecoration(getContext()));
        mAdapter = new CommonRecyclerViewAdapter<MenuItem>(getMenuItems(), R.layout.consumercare_icon_right_button) {
            @Override
            public void bindData(RecyclerView.ViewHolder holder, MenuItem item) {
                View container = holder.itemView.findViewById(R.id.icon_button);
                Label label = container.findViewById(R.id.icon_button_text1);
                label.setText(item.mText);
                TextView icon = container.findViewById(R.id.icon_button_icon1);
                if(label.getText().equals(getString(R.string.FAQ_KEY)))
                    icon.setText(getString(R.string.dls_navigationright_32));
                else
                    icon.setText(getString(R.string.dls_linkexternal_32));

                container.setTag(getResources().getResourceEntryName(item.mText));
                container.setOnClickListener(context);
            }
        };
        recyclerView.setAdapter(mAdapter);
    }

    private ArrayList<MenuItem> getMenuItems() {
        mViewProductDetailsModel = DigitalCareConfigManager.getInstance().getViewProductDetailsData();
        TypedArray titles = getResources().obtainTypedArray(R.array.product_menu_title);
        ArrayList<MenuItem> menus = new ArrayList<>();
        if(mViewProductDetailsModel != null){
            for (int i = 0; i < titles.length(); i++) {
                if (titles.getText(i).equals(getResources().getString(R.string.dcc_productDownloadManual)) && mViewProductDetailsModel.getManualLink() == null) {
                    continue;
                }else if(titles.getText(i).equals(getResources().getString(R.string.dcc_productInformationOnWebsite)) && mViewProductDetailsModel.getProductInfoLink() == null){
                    continue;
                }
                menus.add(new MenuItem(R.drawable.consumercare_list_right_arrow, titles.getResourceId(i, 0)));
            }
        }
        titles.recycle();
        return menus;
    }

    private void updateMenus(ArrayList<Integer> disabledButtons) {
        ArrayList<MenuItem> menus = getMenuItems();
        if (disabledButtons != null) {
            for (Iterator<MenuItem> iterator = menus.iterator(); iterator.hasNext(); ) {
                MenuItem item = iterator.next();
                if (disabledButtons.contains(item.mText)) {
                    iterator.remove();
                }
            }
        }
        mAdapter.swap(menus);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);

        setViewParams(config);
    }

    protected void updateViewsWithData() {
        mViewProductDetailsModel = DigitalCareConfigManager.getInstance().getViewProductDetailsData();
        if (mViewProductDetailsModel != null) {
            if (mViewProductDetailsModel.getProductName() != null) {
                onUpdateSummaryData();
                requestPRXAssetData();
            } else
                showAlert(getResources().getString(R.string.no_data_available));
        } else {
            showAlert(getResources().getString(R.string.no_data_available));
        }
    }

    protected void requestPRXAssetData() {
        mPrxWrapper = new PrxWrapper(getActivity(), new PrxSummaryListener() {
            @Override
            public void onResponseReceived(SummaryModel isAvailable) {
                if (getContext() != null) {
                    onUpdateAssetData();
                }
            }
        });

        mPrxWrapper.executePrxAssetRequestWithSummaryData(mViewProductSummaryModel);
    }

    @Override
    public void setViewParams(Configuration config) {
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        mViewProductDetailsModel = DigitalCareConfigManager.getInstance().getViewProductDetailsData();
        String tag = (String) view.getTag();

        if (DigitalCareConfigManager.getInstance()
                .getCcListener() != null) {
            DigitalCareConfigManager.getInstance()
                    .getCcListener().onProductMenuItemClicked(tag);
        }

        if (isConnectionAvailable()) {
            if (tag.equalsIgnoreCase(getResources().getResourceEntryName(
                    R.string.dcc_productDownloadManual))) {
                String mFilePath = mViewProductDetailsModel.getManualLink();

                if ((mFilePath != null) && (!mFilePath.equals(""))) {

                    String pdfName = mFilePath.substring(mFilePath.lastIndexOf("/") + 1);

                    int hasPermission = getActivity().checkSelfPermission(Manifest.permission.
                            WRITE_EXTERNAL_STORAGE);
                    if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.
                                        WRITE_EXTERNAL_STORAGE},
                                123);
                    } else {
                        callDownloadPDFMethod(mFilePath, pdfName);
                    }

                } else {
                    showAlert(getResources().getString(R.string.no_data));
                }

            } else if (tag.equalsIgnoreCase(getResources().getResourceEntryName(
                    R.string.dcc_productInformationOnWebsite))) {

                showFragment(new ProductInformationFragment());
            } else if (tag.equals(getResources().getResourceEntryName(R.string.FAQ_KEY))) {
                launchFaqScreen();
            }
        }
    }

    private void launchFaqScreen() {
        PrxWrapper mPrxWrapper = new PrxWrapper(getActivity(), new PrxFaqCallback() {
            @Override
            public void onResponseReceived(SupportModel supportModel) {
                if (supportModel == null && getActivity() != null) {
                    showAlert(getString(R.string.NO_SUPPORT_KEY));
                } else {
                    FaqListFragment faqListFragment = new FaqListFragment(getActivity());
                    faqListFragment.setSupportModel(supportModel);
                    showFragment(faqListFragment);
                }
            }
        });
        mPrxWrapper.executeFaqSupportRequest();
    }

    private void callDownloadPDFMethod(String filePath, String pdfName) {
        DownloadAndShowPDFHelper downloadAndShowPDFHelper = new DownloadAndShowPDFHelper();
        downloadAndShowPDFHelper.downloadAndOpenPDFManual(getActivity(), filePath, pdfName, isConnectionAvailable());
    }

    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.product_info);
    }

    @Override
    public String setPreviousPageName() {
        return AnalyticsConstants.PAGE_VIEW_PRODUCT_DETAILS;
    }

    public void onUpdateSummaryData() {

        if (mViewProductDetailsModel.getProductName() != null)
            mProductTitle.setText(mViewProductDetailsModel.getProductName());
        if (mViewProductDetailsModel.getCtnName() != null)
            mCtn.setText(mViewProductDetailsModel.getCtnName());
        if (mViewProductDetailsModel.getProductImage() != null) {
            ImageRequest request = new ImageRequest(mViewProductDetailsModel.getProductImage(),
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            if (isTablet) {
                                if (mProductImageTablet != null) {
                                    mProductImageTablet.setVisibility(View.VISIBLE);
                                    mProductImageTablet.setImageBitmap(bitmap);
                                }
                            } else {
                                if (mProductImage != null) {
                                    mProductImage.setVisibility(View.VISIBLE);
                                    mProductImage.setImageBitmap(bitmap);
                                }
                            }
                        }
                    }, 0, 0, null, null,
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Map<String, String> contextData = new HashMap<>();
                            contextData.put(AnalyticsConstants.ACTION_KEY_TECHNICAL_ERROR,
                                    error.getMessage());
                            contextData.put(AnalyticsConstants.ACTION_KEY_URL,
                                    mViewProductDetailsModel.getProductImage());
                            DigitalCareConfigManager.getInstance().getTaggingInterface().
                                    trackActionWithInfo(AnalyticsConstants.ACTION_SET_ERROR,
                                            contextData);
                        }
                    });
            RequestQueue imageRequestQueue = Volley.newRequestQueue(getContext());
            imageRequestQueue.add(request);
        }
    }

    public void onUpdateAssetData() {
        ViewProductDetailsModel viewProductDetailsModel = DigitalCareConfigManager.getInstance().getViewProductDetailsData();
        mManualPdf = viewProductDetailsModel.getManualLink();
        ArrayList<Integer> disabledButtons = new ArrayList<>();
        if (mManualPdf != null) {
            viewProductDetailsModel.setManualLink(mManualPdf);
        } else {
            disabledButtons.add(R.string.dcc_productDownloadManual);
        }
        updateMenus(disabledButtons);
        mProductPage = viewProductDetailsModel.getProductInfoLink();
        if (mProductPage != null)
            viewProductDetailsModel.setProductInfoLink(mProductPage);
        mDomain = viewProductDetailsModel.getDomain();
        if (mDomain != null)
            viewProductDetailsModel.setDomain(mDomain);
        List<String> productVideos = viewProductDetailsModel.getVideoLinks();
        if (productVideos != null)
            initView(viewProductDetailsModel.getVideoLinks());
        DigitalCareConfigManager.getInstance().setViewProductDetailsData(viewProductDetailsModel);
    }
}