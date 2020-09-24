/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.productselection.fragments.listfragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.philips.cdp.productselection.R;
import com.philips.cdp.productselection.prx.VolleyWrapper;
import com.philips.cdp.productselection.utils.ProductSelectionLogger;
import com.philips.cdp.prxclient.datamodels.summary.Summary;
import com.philips.cdp.prxclient.datamodels.summary.SummaryModel;

import java.util.ArrayList;
import java.util.List;


public class ListViewWithOptions extends BaseAdapter implements Filterable {

    private static final String TAG = ListViewWithOptions.class.getSimpleName();
    private LayoutInflater inflater = null;
    private ArrayList<String> listOfItems = null;
    private List<SummaryModel> mProductsList = null;
    private Activity mActivity = null;
    private CustomFilter mCustomFilter;
    private List<SummaryModel> mOriginalSet;
    private Activity activity;

    SummaryModel mData = null;
    ImageLoader imageLoader;

    public ListViewWithOptions(Activity activity, List<SummaryModel> data) {
        this.activity = activity;
        if (activity != null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mActivity = activity;
        this.mProductsList = data;
        this.mOriginalSet = data;
        imageLoader = VolleyWrapper.getInstance(activity).getImageLoader();
    }

    @Override
    public int getCount() {
        return mProductsList.size();
    }

    @Override
    public SummaryModel getItem(int position) {
        return mProductsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ListViewOptionViewHolder listViewOptionViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_listscreen_adaper_view, null);
            listViewOptionViewHolder = new ListViewOptionViewHolder(convertView);
            convertView.setTag(R.string.view_holder, listViewOptionViewHolder);

        }
        listViewOptionViewHolder = (ListViewOptionViewHolder) convertView.getTag(R.string.view_holder);
        SummaryModel summaryModel = getItem(position);

        Summary data = summaryModel.getData();

        String imagepath = data.getImageURL();
        int imageWidth = (int) (85 * Resources.getSystem().getDisplayMetrics().density);
        imagepath = /*imagepath + "?wid=" + imageWidth + "&;";*/
                imagepath + "?wid=" + imageWidth +
                        "&hei=" + imageWidth +
                        "&fit=fit,1";

        ProductSelectionLogger.d(TAG, "Image URL's of the listed Products : " + imagepath);


        listViewOptionViewHolder.networkImageView.setImageUrl(imagepath, imageLoader);
        listViewOptionViewHolder.tvProductName.setText(data.getProductTitle());
        listViewOptionViewHolder.tvCtn.setText(data.getCtn());
        convertView.setTag(position);
        setData(summaryModel);

        return convertView;
    }


    public SummaryModel getData() {
        return mData;
    }

    private void setData(SummaryModel data) {
        mData = data;
    }

    private class CustomFilter extends Filter {

        private String constraintStr;

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraintStr = constraint.toString();
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<SummaryModel> filteredResultModelSet = new ArrayList<SummaryModel>();
                for (int i = 0; i < mOriginalSet.size(); i++) {
                    SummaryModel summaryModel = mOriginalSet.get(i);
                    Summary data = summaryModel.getData();
                    SummaryModel filteredResultModel = new SummaryModel();
                    filteredResultModel.setData(data);
                    String productMachTitle = data.getProductTitle().toUpperCase();
                    String productMachCtn = data.getCtn().toUpperCase();
                    if (productMachTitle.contains(constraint.toString().toUpperCase()) ||
                            productMachCtn.contains(constraint.toString().toUpperCase())) {
                        filteredResultModel.getData().setCtn(data.getCtn());
                        filteredResultModel.getData().setProductTitle(data.getProductTitle());
                        filteredResultModel.getData().setImageURL(data.getImageURL());
                        filteredResultModelSet.add(filteredResultModel);
                    }
                }
                filterResults.count = filteredResultModelSet.size();
                filterResults.values = filteredResultModelSet;
            } else {
                synchronized (this) {
                    filterResults.count = mOriginalSet.size();
                    filterResults.values = mOriginalSet;
                }
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results.count != 0) {
                mProductsList = (ArrayList<SummaryModel>) results.values;
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public Filter getFilter() {
        if (mCustomFilter == null) {
            mCustomFilter = new CustomFilter();
        }
        return mCustomFilter;
    }

    private static class ListViewOptionViewHolder {

        View view;
        NetworkImageView networkImageView;
        TextView tvProductName, tvCtn;

        public ListViewOptionViewHolder(View view) {
            this.view = view;
            networkImageView = (NetworkImageView) view.findViewById(R.id.image);
            tvProductName = (TextView) view.findViewById(R.id.product_name_view);
            tvCtn = (TextView) view.findViewById(R.id.ctn_view);
        }
    }
}
