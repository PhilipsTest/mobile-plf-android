package cdp.cdp.philips.com.prxclientlib;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.philips.cdp.prxclient.request.ProductSummaryRequest;
import com.philips.cdp.prxclient.request.PrxRequest;
import com.philips.cdp.prxclient.datamodels.summary.Brand;
import com.philips.cdp.prxclient.datamodels.summary.Data;
import com.philips.cdp.prxclient.datamodels.summary.Price;
import com.philips.cdp.prxclient.datamodels.summary.ReviewStatistics;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;
import com.philips.cdp.prxclient.response.ResponseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Description :
 * Project : PRX Common Component.
 * Created by naveen@philips.com on 09-Nov-15.
 */
public class SummaryModelTest extends InstrumentationTestCase {

    private static final String TAG = SummaryModelTest.class.getSimpleName();
    private PrxRequest mProductSummaryBuilder = null;
    private ReviewStatistics mReviewStatistics = null;
    private Price mPrice = null;
    private Brand mBrand = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mProductSummaryBuilder = new ProductSummaryRequest("125", null);

        mProductSummaryBuilder.setLocale("nl_NL");

    }


    public void testSummaryDataLoad() {
        JSONObject mJsonObject = null;
        try {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().getAssets().open("summary_template_one.txt")));

                // do reading, usually loop until end of file reading
                String mLine = reader.readLine();
                while (mLine != null) {
                    // process line
                    sb.append(mLine);
                    mLine = reader.readLine();
                }

                reader.close();
            } catch (IOException e) {
                // log the exception
                e.printStackTrace();
            }
            Log.d(TAG, "Parsed Data : " + sb.toString());
            mJsonObject = new JSONObject(sb.toString());
            ResponseData mResponseData = mProductSummaryBuilder.getResponseData(mJsonObject);
            SummaryModel summaryModel = new SummaryModel();
            ResponseData responseData = summaryModel.parseJsonResponseData(mJsonObject);
            assertNotNull(responseData);
        } catch (JSONException e) {
            Log.d(TAG, "JSON : " + e);

        } catch (Exception e) {
            Log.d(TAG, "IO " + e);
        }
    }


    public void testSummaryObjectModelWithSingleCOnstructorData() {
        JSONObject mJsonObject = null;
        try {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().getAssets().open("summary_template_one.txt")));

                // do reading, usually loop until end of file reading
                String mLine = reader.readLine();
                while (mLine != null) {
                    // process line
                    sb.append(mLine);
                    mLine = reader.readLine();
                }

                reader.close();
            } catch (IOException e) {
                // log the exception
                e.printStackTrace();
            }
            Log.d(TAG, "Parsed Data : " + sb.toString());
            mJsonObject = new JSONObject(sb.toString());
            ResponseData mResponseData = mProductSummaryBuilder.getResponseData(mJsonObject);
           /* SummaryModel summaryModel = new SummaryModel();
            ResponseData responseData = summaryModel.parseJsonResponseData(mJsonObject);
            assertNotNull(responseData);*/
            SummaryModel mSummaryModel = (SummaryModel) mResponseData;

            Data data = new Data(null, null, null, null, null, null, null, mSummaryModel.getData().getBrand(),
                    null, null, null, null, null, mSummaryModel.getData().getFilterKeys(),
                    null, null, null, null, null, true, mSummaryModel.getData().getPriority(), mSummaryModel.getData().getPrice(), mSummaryModel.getData().getReviewStatistics(),
                    mSummaryModel.getData().getVersions(), null, null, null, null, mSummaryModel.getData().getFilterKeys(), null);

            mSummaryModel.setData(data);
            assertNotNull(mSummaryModel.getData().getVersions());

        } catch (JSONException e) {
            Log.d(TAG, "JSON  : " + e);

        } catch (Exception e) {
            Log.d(TAG, "IO " + e);
        }
    }


    public void testSummaryDataLoadToModel() {
        JSONObject mJsonObject = null;
        try {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().getAssets().open("summary_template_one.txt")));

                // do reading, usually loop until end of file reading
                String mLine = reader.readLine();
                while (mLine != null) {
                    // process line
                    sb.append(mLine);
                    mLine = reader.readLine();
                }

                reader.close();
            } catch (IOException e) {
                // log the exception
                e.printStackTrace();
            }
            Log.d(TAG, "Parsed Data : " + sb.toString());
            mJsonObject = new JSONObject(sb.toString());
            ResponseData mResponseData = mProductSummaryBuilder.getResponseData(mJsonObject);
           /* SummaryModel summaryModel = new SummaryModel();
            ResponseData responseData = summaryModel.parseJsonResponseData(mJsonObject);
            assertNotNull(responseData);*/
            SummaryModel mSummaryModel = (SummaryModel) mResponseData;
            Log.d(TAG, " Success : " + mSummaryModel.isSuccess());
            assertNotNull(mSummaryModel.getData().getBrandName());
            assertNotNull(mSummaryModel.getData().getAlphanumeric());
            assertNotNull(mSummaryModel.getData().getBrand());
            assertNotNull(mSummaryModel.getData().getCareSop());
            assertNotNull(mSummaryModel.getData().getCtn());
            assertNotNull(mSummaryModel.getData().getDescriptor());
            assertNotNull(mSummaryModel.getData().getDomain());
            assertNotNull(mSummaryModel.getData().getDtn());
            assertNotNull(mSummaryModel.getData().getEop());
            assertNotNull(mSummaryModel.getData().getFamilyName());
            assertNotNull(mSummaryModel.getData().getFilterKeys());
            assertNotNull(mSummaryModel.getData().getImageURL());
            assertNotNull(mSummaryModel.getData().getKeyAwards());
            assertNotNull(mSummaryModel.getData().getLeafletUrl());
            assertNotNull(mSummaryModel.getData().getLocale());
            assertNotNull(mSummaryModel.getData().getMarketingTextHeader());
            assertNotNull(mSummaryModel.getData().getPrice());
            assertNotNull(mSummaryModel.getData().getPriority());
            assertNotNull(mSummaryModel.getData().getProductPagePath());
            assertNotNull(mSummaryModel.getData().getProductStatus());
            assertNotNull(mSummaryModel.getData().getProductTitle());
            assertNotNull(mSummaryModel.getData().getProductURL());
            assertNotNull(mSummaryModel.getData().getReviewStatistics());
            assertNotNull(mSummaryModel.getData().getSomp());
            assertNotNull(mSummaryModel.getData().getSop());
            assertNotNull(mSummaryModel.getData().getSubcategory());
            assertNotNull(mSummaryModel.getData().getSubWOW());
            assertNotNull(mSummaryModel.getData().getVersions());
            assertNotNull(mSummaryModel.getData().getWow());
            assertFalse(mSummaryModel.getData().isIsDeleted());
        } catch (JSONException e) {
            Log.d(TAG, "JSON  : " + e);

        } catch (Exception e) {
            Log.d(TAG, "IO " + e);
        }
    }


    public void testSummaryDataObjectNullTest() {
        SummaryModel mSummaryModel = new SummaryModel();
        mSummaryModel.setData(getSummaryDataWithNullInput());

        assertNull(mSummaryModel.getData().getBrandName());
        assertNull(mSummaryModel.getData().getAlphanumeric());
        assertNull(mSummaryModel.getData().getBrand());
        assertNull(mSummaryModel.getData().getCareSop());
        assertNull(mSummaryModel.getData().getCtn());
        assertNull(mSummaryModel.getData().getDescriptor());
        assertNull(mSummaryModel.getData().getDomain());
        assertNull(mSummaryModel.getData().getDtn());
        assertNull(mSummaryModel.getData().getEop());
        assertNull(mSummaryModel.getData().getFamilyName());
        assertNull(mSummaryModel.getData().getFilterKeys());
        assertNull(mSummaryModel.getData().getImageURL());
        assertNull(mSummaryModel.getData().getKeyAwards());
        assertNull(mSummaryModel.getData().getLeafletUrl());
        assertNull(mSummaryModel.getData().getLocale());
        assertNull(mSummaryModel.getData().getMarketingTextHeader());
        assertNull(mSummaryModel.getData().getPrice());
        assertEquals(13450, mSummaryModel.getData().getPriority());
        assertNull(mSummaryModel.getData().getProductPagePath());
        assertNull(mSummaryModel.getData().getProductStatus());
        assertNull(mSummaryModel.getData().getProductTitle());
        assertNull(mSummaryModel.getData().getProductURL());
        assertNull(mSummaryModel.getData().getReviewStatistics());
        assertNull(mSummaryModel.getData().getSomp());
        assertNull(mSummaryModel.getData().getSop());
        assertNull(mSummaryModel.getData().getSubcategory());
        assertNull(mSummaryModel.getData().getSubWOW());
        assertNull(mSummaryModel.getData().getVersions());
        assertNull(mSummaryModel.getData().getWow());
    }


    public void testReviewStaticTotalCountLogic() {
        mReviewStatistics = new ReviewStatistics();
        mReviewStatistics.setAverageOverallRating(PRXComponentConstant.REVIEW_STATICS_TOTAL);
        mReviewStatistics.setTotalReviewCount(PRXComponentConstant.REVIEW_STATICS_AVAERAGE);

        assertNotNull(mReviewStatistics);
        assertEquals(mReviewStatistics.getTotalReviewCount(), 1206l);
    }


    public void testReviewStaticAverageCountLogic() {
        mReviewStatistics = new ReviewStatistics();
        mReviewStatistics.setAverageOverallRating(PRXComponentConstant.REVIEW_STATICS_TOTAL);
        mReviewStatistics.setTotalReviewCount(PRXComponentConstant.REVIEW_STATICS_AVAERAGE);

        assertNotNull(mReviewStatistics);
        assertEquals(mReviewStatistics.getAverageOverallRating(), 1245.6);
    }

    public void testReviewStaticAverageCountLogic2() {
        mReviewStatistics = new ReviewStatistics(PRXComponentConstant.REVIEW_STATICS_TOTAL, PRXComponentConstant.REVIEW_STATICS_AVAERAGE);
        assertEquals(1245.6, mReviewStatistics.getAverageOverallRating());
    }


    public void testPriceCurrencyCode() {
        mPrice = new Price();
        mPrice.setCurrencyCode(PRXComponentConstant.CURRENCYCODE);
        mPrice.setDisplayPrice(PRXComponentConstant.DISPLAYPRICE);
        mPrice.setDisplayPriceType(PRXComponentConstant.DISPLAYPRICETYPE);
        mPrice.setFormattedDisplayPrice(PRXComponentConstant.FORMATTEDDISPLAYPRICE);
        mPrice.setFormattedPrice(PRXComponentConstant.FORMATTED_PRICE);
        mPrice.setProductPrice(PRXComponentConstant.PRODUCT_PRICE);
        assertEquals("INR", mPrice.getCurrencyCode());
    }


    public void testDisplayPrice() {
        mPrice = new Price();
        mPrice.setCurrencyCode(PRXComponentConstant.CURRENCYCODE);
        mPrice.setDisplayPrice(PRXComponentConstant.DISPLAYPRICE);
        mPrice.setDisplayPriceType(PRXComponentConstant.DISPLAYPRICETYPE);
        mPrice.setFormattedDisplayPrice(PRXComponentConstant.FORMATTEDDISPLAYPRICE);
        mPrice.setFormattedPrice(PRXComponentConstant.FORMATTED_PRICE);
        mPrice.setProductPrice(PRXComponentConstant.PRODUCT_PRICE);
        assertEquals("12.5", mPrice.getDisplayPrice());
    }


    public void testDisplayPriceType() {
        mPrice = new Price();
        mPrice.setCurrencyCode(PRXComponentConstant.CURRENCYCODE);
        mPrice.setDisplayPrice(PRXComponentConstant.DISPLAYPRICE);
        mPrice.setDisplayPriceType(PRXComponentConstant.DISPLAYPRICETYPE);
        mPrice.setFormattedDisplayPrice(PRXComponentConstant.FORMATTEDDISPLAYPRICE);
        mPrice.setFormattedPrice(PRXComponentConstant.FORMATTED_PRICE);
        mPrice.setProductPrice(PRXComponentConstant.PRODUCT_PRICE);
        assertEquals("Rupees", mPrice.getDisplayPriceType());
    }


    public void testFormattedDisplayPrice() {
        mPrice = new Price();
        mPrice.setCurrencyCode(PRXComponentConstant.CURRENCYCODE);
        mPrice.setDisplayPrice(PRXComponentConstant.DISPLAYPRICE);
        mPrice.setDisplayPriceType(PRXComponentConstant.DISPLAYPRICETYPE);
        mPrice.setFormattedDisplayPrice(PRXComponentConstant.FORMATTEDDISPLAYPRICE);
        mPrice.setFormattedPrice(PRXComponentConstant.FORMATTED_PRICE);
        mPrice.setProductPrice(PRXComponentConstant.PRODUCT_PRICE);
        assertEquals("12.8", mPrice.getFormattedDisplayPrice());
    }


    public void testFormattedPrice() {
        mPrice = new Price();
        mPrice.setCurrencyCode(PRXComponentConstant.CURRENCYCODE);
        mPrice.setDisplayPrice(PRXComponentConstant.DISPLAYPRICE);
        mPrice.setDisplayPriceType(PRXComponentConstant.DISPLAYPRICETYPE);
        mPrice.setFormattedDisplayPrice(PRXComponentConstant.FORMATTEDDISPLAYPRICE);
        mPrice.setFormattedPrice(PRXComponentConstant.FORMATTED_PRICE);
        mPrice.setProductPrice(PRXComponentConstant.PRODUCT_PRICE);
        assertEquals("12", mPrice.getFormattedPrice());
    }


    public void testProductPrice() {
        mPrice = new Price(PRXComponentConstant.CURRENCYCODE, PRXComponentConstant.DISPLAYPRICE, PRXComponentConstant.DISPLAYPRICETYPE, PRXComponentConstant.FORMATTEDDISPLAYPRICE
                , PRXComponentConstant.FORMATTED_PRICE, PRXComponentConstant.PRODUCT_PRICE);
        assertNotNull(mPrice.getProductPrice());
    }


    private Data getSummaryDataWithNullInput() {
        Data mData = new Data();
        mData.setBrandName(null);
        mData.setAlphanumeric(null);
        mData.setBrand(null);
        mData.setCareSop(null);
        mData.setCtn(null);
        mData.setDescriptor(null);
        mData.setDomain(null);
        mData.setDtn(null);
        mData.setEop(null);
        mData.setFamilyName(null);
        mData.setFilterKeys(null);
        mData.setImageURL(null);
        mData.setKeyAwards(null);
        mData.setLeafletUrl(null);
        mData.setLocale(null);
        mData.setMarketingTextHeader(null);
        mData.setPrice(null);
        mData.setPriority(13450);
        mData.setProductPagePath(null);
        mData.setProductStatus(null);
        mData.setProductTitle(null);
        mData.setProductURL(null);
        mData.setReviewStatistics(null);
        mData.setSomp(null);
        mData.setSop(null);
        mData.setSubcategory(null);
        mData.setSubWOW(null);
        mData.setVersions(null);
        mData.setWow(null);
        mData.setIsDeleted(true);

        return mData;
    }


    public void testSummaryObjectWithMultiParamConstructor()
    {
        SummaryModel summaryModel = new SummaryModel(true, getSummaryDataWithNullInput());
        summaryModel.setSuccess(false);
        assertTrue(summaryModel.getData().isIsDeleted());
    }

    public void testBrandType() {
        mBrand = new Brand();
        mBrand.setBrandLogo(PRXComponentConstant.BRANDNAME);
        assertEquals("Philips", mBrand.getBrandLogo());
    }

    public void testBrandTypeWithCDP() {
        mBrand = new Brand(PRXComponentConstant.BRANDNAME_1);
        assertEquals("CDP", mBrand.getBrandLogo());
    }

}
