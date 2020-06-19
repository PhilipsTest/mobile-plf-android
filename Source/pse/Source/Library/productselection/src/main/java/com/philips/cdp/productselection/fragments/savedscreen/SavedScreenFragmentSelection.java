package com.philips.cdp.productselection.fragments.savedscreen;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.philips.cdp.productselection.ProductModelSelectionHelper;
import com.philips.cdp.productselection.R;
import com.philips.cdp.productselection.fragments.homefragment.ProductSelectionBaseFragment;
import com.philips.cdp.productselection.prx.VolleyWrapper;
import com.philips.cdp.productselection.utils.Constants;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;

import java.util.List;

/**
 * This class holds responsible to inflate the UI of the saved screen & reselecting the product to save &
 * redirecting control the \
 * product information displaying screen.
 * <p>
 * Created by naveen@philips.com on 03-Feb-16.
 */
public class SavedScreenFragmentSelection extends ProductSelectionBaseFragment implements View.OnClickListener {

    private static final String TAG = SavedScreenFragmentSelection.class.getSimpleName();
    private Button mSettings = null;
    private Button mRedirectingButton = null;
    private LinearLayout mProductContainerBelow = null;
    private LinearLayout mBottomLayoutContainer = null;
    private LinearLayout mTitleCOntainer = null;
    private LinearLayout.LayoutParams mProductContainerBelowParams;
    private LinearLayout.LayoutParams mBottomLayoutContainerParams;
    private LinearLayout.LayoutParams mTitleLayoutContainerParams;
    private TextView mProductName = null;
    private TextView mProductCtn = null;
    private ImageView mProductImage = null;

    /**
     * setting Listeners & setting the values & controls to the inflated view's of the screen "fragment_saved_screen.xml"
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProductContainerBelow = (LinearLayout) getActivity().findViewById(R.id.savedScreen_screen_parent);
        mBottomLayoutContainer = (LinearLayout) getActivity().findViewById(R.id.bottom_layout_container);
        mTitleCOntainer = (LinearLayout) getActivity().findViewById(R.id.title_view_container);
        mProductContainerBelowParams = (LinearLayout.LayoutParams) mProductContainerBelow.getLayoutParams();
        mBottomLayoutContainerParams = (LinearLayout.LayoutParams) mBottomLayoutContainer.getLayoutParams();
        mTitleLayoutContainerParams = (LinearLayout.LayoutParams) mBottomLayoutContainer.getLayoutParams();
        mProductName.setText(mUserSelectedProduct.getData().getProductTitle());
        mProductCtn.setText(mUserSelectedProduct.getData().getCtn());
        loadProductImage(mProductImage);

        mSettings.setOnClickListener(this);
        mRedirectingButton.setOnClickListener(this);

        Configuration configuration = getResources().getConfiguration();
        setViewParams(configuration);
        ProductModelSelectionHelper.getInstance().getTaggingInterface().trackPageWithInfo
                (Constants.PAGE_CONFIRMATION_SCREEN, getPreviousName(), getPreviousName());
        setPreviousPageName(Constants.PAGE_CONFIRMATION_SCREEN);
    }

    /**
     * Inflating the View of the Screen "fragment_saved_screen.xmls"
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_saved_screen, container, false);
        mSettings = (Button) view.findViewById(R.id.savedscreen_button_settings);
        mRedirectingButton = (Button) view.findViewById(R.id.savedscreen_button_continue);
        mProductName = (TextView) view.findViewById(R.id.savedscreen_productname);
        mProductCtn = (TextView) view.findViewById(R.id.savedscreen_productvariant);
        mProductImage = (ImageView) view.findViewById(R.id.savedscreen_productimage);
        return view;
    }


    protected void loadProductImage(final ImageView image) {
        String imagepath = mUserSelectedProduct.getData().getImageURL();
        int imageWidth = (int) (getResources().getDimension(R.dimen.productdetails_screen_image) * Resources.getSystem().getDisplayMetrics().density);
        imagepath = /*imagepath + "?wid=" + imageWidth + "&;";*/
                imagepath + "?wid=" + 480 +
                        "&hei=" + 480 +
                        "&fit=fit,1";

        ProductSelectionLogger.v(TAG, "Image loaded in the Saved Screen is from the PATH : " + imagepath);

        final ImageRequest request = new ImageRequest(imagepath,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        image.setImageBitmap(bitmap);
                    }
                }, 0, 0,  ImageView.ScaleType.FIT_XY,null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        ProductSelectionLogger.e(TAG, "Selected Product Image is failed donalod  : " + error);
                    }
                });

        VolleyWrapper.getInstance(getActivity()).addToRequestQueue(request);
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        setViewParams(config);
    }

    @Override
    public void setViewParams(Configuration config) {

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mProductContainerBelowParams.leftMargin = mProductContainerBelowParams.rightMargin = mLeftRightMarginPort;
            mBottomLayoutContainerParams.leftMargin = mBottomLayoutContainerParams.rightMargin = mLeftRightMarginPort;
            mTitleLayoutContainerParams.leftMargin = mBottomLayoutContainerParams.rightMargin = mLeftRightMarginPort;

        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mProductContainerBelowParams.leftMargin = mProductContainerBelowParams.rightMargin = mLeftRightMarginLand;
            mBottomLayoutContainerParams.leftMargin = mBottomLayoutContainerParams.rightMargin = mLeftRightMarginLand;
            mTitleLayoutContainerParams.leftMargin = mTitleLayoutContainerParams.rightMargin = mLeftRightMarginLand;
        }

        mProductContainerBelow.setLayoutParams(mProductContainerBelowParams);
        mProductContainerBelow.setLayoutParams(mBottomLayoutContainerParams);
        mTitleCOntainer.setLayoutParams(mTitleLayoutContainerParams);
    }


    @Override
    public String getActionbarTitle() {
        return getResources().getString(R.string.Confirmation_Title);
    }

    private void removeDetailsScreen() {
        FragmentManager fragManager = getActivity().getSupportFragmentManager();
        fragManager.popBackStack();
        List<Fragment> listFragment = fragManager.getFragments();
        for (int i = listFragment.size() - 1; i >= 0; i--) {
            Fragment fragment = listFragment.get(i);

            try {
                if (fragment != null && (fragment instanceof SavedScreenFragmentSelection)) {
                    fragManager.popBackStack();
                }
            } catch (IllegalStateException e) {
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (isConnectionAvailable()) {
            if (v.getId() == R.id.savedscreen_button_settings) {
                ProductModelSelectionHelper.getInstance().getTaggingInterface().trackActionWithInfo
                        (Constants.ACTION_KEY_SEND_DATA, Constants.ACTION_NAME_SPECIAL_EVENT,
                                Constants.ACTION_VALUE_CHANGE_PRODUCT);
                removeDetailsScreen();
            } else if (v.getId() == R.id.savedscreen_button_continue) {
                ProductModelSelectionHelper.getInstance().getTaggingInterface().trackActionWithInfo
                        (Constants.ACTION_KEY_SEND_DATA, Constants.ACTION_NAME_SPECIAL_EVENT,
                                Constants.ACTION_VALUE_CONTINUE);
                ProductModelSelectionHelper.getInstance().getTaggingInterface().trackActionWithInfo
                        (Constants.ACTION_KEY_SEND_DATA, Constants.ACTION_PRODUCT_SELECTED_CTN,
                                mUserSelectedProduct.getData().getCtn());
                setPreference(mUserSelectedProduct.getData().getCtn());
                ProductModelSelectionHelper.getInstance().getProductSelectionListener().onProductModelSelected(mUserSelectedProduct);
                clearBackStackHistory(getActivity());
            }
        }
    }
}
