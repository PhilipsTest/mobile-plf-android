/*----------------------------------------------------------------------------
Copyright(c) Philips Electronics India Ltd
All rights reserved. Reproduction in whole or in part is prohibited without
the written consent of the copyright holder.

Project           : Consumer Care
----------------------------------------------------------------------------*/


package com.philips.cdp.digitalcare.prx;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.philips.cdp.digitalcare.ConsumerProductInfo;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.R;
import com.philips.cdp.digitalcare.analytics.AnalyticsConstants;
import com.philips.cdp.digitalcare.listeners.PrxFaqCallback;
import com.philips.cdp.digitalcare.listeners.PrxSummaryListener;
import com.philips.cdp.digitalcare.productdetails.model.ViewProductDetailsModel;
import com.philips.cdp.digitalcare.util.DigiCareLogger;
import com.philips.cdp.digitalcare.view.ProgressAlertDialog;
import com.philips.cdp.prxclient.PRXDependencies;
import com.philips.cdp.prxclient.PrxConstants.Catalog;
import com.philips.cdp.prxclient.PrxConstants.Sector;
import com.philips.cdp.prxclient.RequestManager;
import com.philips.cdp.prxclient.datamodels.assets.Asset;
import com.philips.cdp.prxclient.datamodels.assets.AssetModel;
import com.philips.cdp.prxclient.datamodels.assets.Assets;
import com.philips.cdp.prxclient.datamodels.summary.Summary;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.cdp.prxclient.datamodels.support.SupportModel;
import com.philips.cdp.prxclient.error.PrxError;
import com.philips.cdp.prxclient.request.ProductAssetRequest;
import com.philips.cdp.prxclient.request.ProductSummaryRequest;
import com.philips.cdp.prxclient.request.ProductSupportRequest;
import com.philips.cdp.prxclient.response.ResponseData;
import com.philips.cdp.prxclient.response.ResponseListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Description :
 * Project : PRX Common Component.
 *
 * @Since 03-Nov-15.
 */
public class PrxWrapper {

    private static final String PRX_ASSETS_USERMANUAL_PDF = "User manual";
    private static final String PRX_ASSETS_USERMANUAL_QSG_PDF = "qsg";
    private static final String PRX_ASSETS_USERMANUAL_DFU_PDF = "dfu";
    private static final String PRX_ASSETS_VIDEO_URL = "mp4";
    private static final String TAG = PrxWrapper.class.getSimpleName();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Thread mUiThread = Looper.getMainLooper().getThread();
    private ConsumerProductInfo mProductInfo = null;
    private DigitalCareConfigManager mConfigManager = null;
    private Activity mActivity = null;
    private PrxSummaryListener mSummaryCallback = null;
    private PrxFaqCallback mSupportCallback = null;
    private String mCtn = null;
    private String mSectorCode = null;
    private String mLocale = null;
    private String mCatalogCode = null;
    private SummaryModel mSummaryModel = null;
    private AssetModel mAssetModel = null;
    private ViewProductDetailsModel mProductDetailsObject = null;
    private ProgressAlertDialog mProgressDialog = null;
    private RequestManager mRequestManager = null;


    public PrxWrapper(Activity activity, PrxSummaryListener callback) {
        this.mActivity = activity;
        this.mSummaryCallback = callback;
        mConfigManager = DigitalCareConfigManager.getInstance();
        mProductDetailsObject = new ViewProductDetailsModel();


        initProductCredentials();
    }

    public PrxWrapper(Activity activity, PrxFaqCallback callback) {
        this.mActivity = activity;
        this.mSupportCallback = callback;
        mConfigManager = DigitalCareConfigManager.getInstance();
        initProductCredentials();
    }

    public void executeRequests() {
        updateUi(new Runnable() {
            @Override
            public void run() {
                //   executeAssetRequest();
                executeSummaryRequest();
            }
        });

    }


    public void executePrxAssetRequestWithSummaryData(final SummaryModel summaryModel) {
        updateUi(new Runnable() {
                     @Override
                     public void run() {

                         final Summary data = summaryModel.getData();
                         if (data != null) {
                             mProductDetailsObject.setProductName(data.getProductTitle());
                             mProductDetailsObject.setCtnName(data.getCtn());
                             mProductDetailsObject.setProductImage(data.getImageURL());
                             mProductDetailsObject.setProductInfoLink(data.getProductURL());
                             mProductDetailsObject.setDomain(data.getDomain());
                             mConfigManager.setViewProductDetailsData(mProductDetailsObject);

                             try {
                                 executeAssetRequest();
                             } catch (Exception ex) {
                                 DigiCareLogger.e(TAG, "File not Found Exception log from " +
                                         "localematch component: " + ex);
                             }


                         }
                     }
                 }


        );
    }

    protected final void updateUi(Runnable runnable) {
        if (isCurrentThreadNotUiThread()) {
            mHandler.post(runnable);
        } else {
            runnable.run();
        }
    }

    private boolean isCurrentThreadNotUiThread() {
        return Thread.currentThread() != mUiThread;
    }

    protected void initProductCredentials() {
        if (mRequestManager == null) {
            mRequestManager = new RequestManager();
        }
        PRXDependencies prxDependencies = new PRXDependencies(mActivity, DigitalCareConfigManager.getInstance().getAPPInfraInstance(), AnalyticsConstants.COMPONENT_NAME_CC);
        mRequestManager.init(prxDependencies);
        final DigitalCareConfigManager mConfigManager = DigitalCareConfigManager.getInstance();
        mProductInfo = mConfigManager.getConsumerProductInfo();
        mCtn = mProductInfo.getCtn();
        mSectorCode = mProductInfo.getSector();
        mCatalogCode = mProductInfo.getCatalog();

    }

    public ProductSummaryRequest getPrxSummaryData() {
        final ProductSummaryRequest mProductSummaryRequest = new ProductSummaryRequest(mCtn, null);
        mProductSummaryRequest.setSector(getSectorEnum(mSectorCode));
        mProductSummaryRequest.setCatalog(getCatalogEnum(mCatalogCode));

        return mProductSummaryRequest;
    }

    public ProductSupportRequest getPrxSupportData() {
        final ProductSupportRequest productSupportRequest = new ProductSupportRequest(mCtn, null);
        productSupportRequest.setSector(getSectorEnum(mSectorCode));
        productSupportRequest.setCatalog(getCatalogEnum(mCatalogCode));
        return productSupportRequest;
    }

    public ProductAssetRequest getPrxAssetData() {
        final ProductAssetRequest mProductAssetRequest = new ProductAssetRequest(mCtn, null);
        mProductAssetRequest.setSector(getSectorEnum(mSectorCode));
        mProductAssetRequest.setCatalog(getCatalogEnum(mCatalogCode));

        return mProductAssetRequest;
    }


    private Sector getSectorEnum(String sectorText) {
        switch (sectorText) {
            case "B2C":
                return Sector.B2C;

            case "B2B_LI":
                return Sector.B2B_LI;

            case "B2B_HC":
                return Sector.B2B_HC;
            default:
                return Sector.DEFAULT;

        }
    }

    private Catalog getCatalogEnum(String catalogText) {
        switch (catalogText) {
            case "CONSUMER":
                return Catalog.CONSUMER;
            case "NONCONSUMER":
                return Catalog.NONCONSUMER;
            case "CARE":
                return Catalog.CARE;
            case "PROFESSIONAL":
                return Catalog.PROFESSIONAL;
            case "LP_OEM_ATG":
                return Catalog.LP_OEM_ATG;
            case "LP_PROF_ATG":
                return Catalog.LP_PROF_ATG;
            case "HC":
                return Catalog.HC;
            case "HHSSHOP":
                return Catalog.HHSSHOP;
            case "MOBILE":
                return Catalog.MOBILE;
            case "EXTENDEDCONSENT":
                return Catalog.EXTENDEDCONSENT;
            default:
                return Catalog.DEFAULT;
        }
    }

    public void executeFaqSupportRequest() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressAlertDialog(mActivity, R.style.loaderTheme);
        }

        mProgressDialog.setCancelable(false);
        if (!(mActivity.isFinishing())) {
            mProgressDialog.show();
        }
        mRequestManager.executeRequest(getPrxSupportData(), new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                SupportModel supportModel = null;
                if (responseData != null) {
                    supportModel = (SupportModel) responseData;
                    DigiCareLogger.i(TAG, "FaqData Received from PRX ?  " + supportModel.isSuccess());
                    if (supportModel.isSuccess()) {
                        final com.philips.cdp.prxclient.datamodels.support.Data data
                                = supportModel.getData();
                        if (data != null) {
                            if (mSupportCallback != null) {
                                mSupportCallback.onResponseReceived(supportModel);
                            }
                        } else {
                            if (mSupportCallback != null) {
                                mSupportCallback.onResponseReceived(null);
                            }
                        }
                    } else {
                        DigiCareLogger.i(TAG, "FAQData not received from PRX");
                        if (mSupportCallback != null) {
                            mSupportCallback.onResponseReceived(null);
                        }
                    }
                    closeProgressDialog();
                }
            }

            @Override
            public void onResponseError(PrxError prxError) {
                if (mSupportCallback != null) {
                    mSupportCallback.onResponseReceived(null);
                }
                closeProgressDialog();
            }
        });
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing() && !mActivity.isFinishing()) {
            try {
                mProgressDialog.cancel();
                mProgressDialog = null;
            } catch (IllegalArgumentException e) {
                DigiCareLogger.e(TAG, "Progress Dialog got IllegalArgumentException");
            }
        }
    }


    public void executeSummaryRequest() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressAlertDialog(mActivity, R.style.loaderTheme);
        }

        mProgressDialog.setCancelable(false);
        if (!(mActivity.isFinishing())) {
            mProgressDialog.show();
        }
        mRequestManager.executeRequest(getPrxSummaryData(), new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {
                if (responseData != null) {
                    mSummaryModel = (SummaryModel) responseData;
                    DigiCareLogger.d(TAG, "Summary Data Received ? " + mSummaryModel.isSuccess());
                    if (mSummaryModel.isSuccess()) {
                        final Summary data = mSummaryModel.getData();
                        if (data != null) {
                            mProductDetailsObject.setProductName(data.getProductTitle());
                            mProductDetailsObject.setCtnName(data.getCtn());
                            mProductDetailsObject.setProductImage(data.getImageURL());
                            mProductDetailsObject.setProductInfoLink(data.getProductURL());
                            mProductDetailsObject.setDomain(data.getDomain());
                            mConfigManager.setViewProductDetailsData(mProductDetailsObject);
                            if (mSummaryCallback != null)
                                mSummaryCallback.onResponseReceived(mSummaryModel);
                        }
                    } else {
                        mConfigManager.setViewProductDetailsData(mProductDetailsObject);
                        if (mSummaryCallback != null) {
                            mSummaryCallback.onResponseReceived(null);
                        }
                    }
                    closeProgressDialog();
                }
            }

            @Override
            public void onResponseError(PrxError error) {
                DigiCareLogger.e(TAG, "Summary Error Response : " + error);
                mConfigManager.setViewProductDetailsData(mProductDetailsObject);
                if (mSummaryCallback != null) {
                    mSummaryCallback.onResponseReceived(null);
                }
                closeProgressDialog();
            }
        });
    }

    public void executeAssetRequest() {

        mRequestManager.executeRequest(getPrxAssetData(), new ResponseListener() {
            @Override
            public void onResponseSuccess(ResponseData responseData) {

                ViewProductDetailsModel viewProductDetailsData = DigitalCareConfigManager.getInstance().getViewProductDetailsData();
                if (viewProductDetailsData == null) {
                    viewProductDetailsData = new ViewProductDetailsModel();
                }

                if (responseData != null) {
                    mAssetModel = (AssetModel) responseData;
                    com.philips.cdp.prxclient.datamodels.assets.Data data = mAssetModel.getData();
                    String qsgManual = null, usermanual = null, dfuManual = null;
                    if (data != null) {
                        final Assets assets = data.getAssets();
                        if (assets != null) {
                            final List<Asset> asset = assets.getAsset();
                            final List<String> mVideoList = new ArrayList<String>();
                            for (Asset assetObject : asset) {
                                String assetDescription = assetObject.getDescription();
                                String assetType = assetObject.getType();
                                String assetResource = assetObject.getAsset();
                                String assetExtension = assetObject.getExtension();

                                if(assetResource == null) return;

                                if (isEquals(assetType, PRX_ASSETS_USERMANUAL_DFU_PDF))
                                    dfuManual = assetResource;

                                if (isEquals(assetType, PRX_ASSETS_USERMANUAL_QSG_PDF))
                                        qsgManual = assetResource;

                                if (isEquals(assetDescription,PRX_ASSETS_USERMANUAL_PDF) && (mProductDetailsObject.getManualLink() == null))
                                        usermanual = assetResource;
                                if (isEquals(assetExtension, PRX_ASSETS_VIDEO_URL))
                                        mVideoList.add(assetResource);

                            }

                            if(dfuManual != null) {
                              viewProductDetailsData.setDfuLink(dfuManual);
                            }
                            if (qsgManual != null) {
                                viewProductDetailsData.setManualLink(qsgManual);
                            } else if (usermanual != null) {
                                viewProductDetailsData.setManualLink(usermanual);
                            }
                            viewProductDetailsData.setmVideoLinks(mVideoList);
                            DigiCareLogger.d(TAG, "Manual qsg : " + qsgManual);
                            DigiCareLogger.d(TAG, "Manual user : " + usermanual);
                            DigiCareLogger.d(TAG, "Manual videoListSize : " + mVideoList.size());
                            mConfigManager.setViewProductDetailsData(viewProductDetailsData);
                            if (mSummaryCallback != null) {
                                mSummaryCallback.onResponseReceived(null);
                            }
                        } else {
                            if (mSummaryCallback != null) mSummaryCallback.onResponseReceived(null);
                        }

                    }
                }
            }

            private boolean isEquals(String str, String prxAssetsUsermanual) {
                return prxAssetsUsermanual.equalsIgnoreCase(str);
            }

            @Override
            public void onResponseError(PrxError error) {
                DigiCareLogger.e(TAG, "Asset Error Response : " + error);
                mConfigManager.setViewProductDetailsData(DigitalCareConfigManager.getInstance().getViewProductDetailsData());
            }
        });
    }

}
