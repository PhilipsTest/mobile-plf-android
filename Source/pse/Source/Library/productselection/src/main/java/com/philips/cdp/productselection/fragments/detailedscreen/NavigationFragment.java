package com.philips.cdp.productselection.fragments.detailedscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.philips.cdp.productselection.R;
import com.philips.cdp.productselection.prx.VolleyWrapper;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;

public final class NavigationFragment extends Fragment {

    private static final String TAG = NavigationFragment.class.getSimpleName();
    private static final String KEY_CONTENT = "NavigationFragment:Content";
    private String message = "???";
    Context thiscontext;


    public static NavigationFragment newInstance(String message) {
        NavigationFragment fragment = new NavigationFragment();
        fragment.message = message;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            message = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView imageView = new ImageView(getActivity());
        // imageView.setBackgroundResource(R.drawable.navigation_image);
        //  if (ProductModelSelectionHelper.getInstance().getUserSelectedProduct() != null)
        loadProductImage(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.setGravity(Gravity.CENTER);
        layout.addView(imageView);

        return layout;
    }


    protected void loadProductImage(final ImageView image) {
        String imagepath = message;
        final int imageWidth = (int) (getResources().getDimension(R.dimen.productdetails_screen_image) / getResources().getDisplayMetrics().density) * 2;
        //imagepath = imagepath + "?wid=" + imageWidth + "&;";
        imagepath = imagepath + "?wid=" + imageWidth +
                "&hei=" + (imageWidth / 100) * 70 +
                "&fit=fit,1";

        final ImageRequest request = new ImageRequest(imagepath,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        image.setImageBitmap(bitmap);
                    }
                }, 0, 0, ImageView.ScaleType.FIT_XY,null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        image.setImageBitmap(getBlankThumbnail(imageWidth));
                        ProductSelectionLogger.e(TAG, "Some issues with Image downloading : " + error.toString());
                    }
                });

        VolleyWrapper.getInstance(getActivity()).addToRequestQueue(request);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        thiscontext = context;
    }

    public Bitmap getBlankThumbnail(int width) {
        Bitmap imageBitmap = drawableToBitmap(width, thiscontext.getDrawable(R.drawable.no_icon));
        return imageBitmap;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, message);
    }

    public static Bitmap drawableToBitmap (int width, Drawable drawable) {
        Bitmap bitmap = null;
        try {
            bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (Exception e){
            bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.BLACK);

        }
        return bitmap;
    }
}
