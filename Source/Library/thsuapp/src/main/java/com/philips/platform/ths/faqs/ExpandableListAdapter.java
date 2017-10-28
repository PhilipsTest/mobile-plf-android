package com.philips.platform.ths.faqs;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.philips.platform.ths.R;
import com.philips.platform.ths.utility.THSConstants;

import java.util.HashMap;
import java.util.List;

/**
 * Created by philips on 10/27/17.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<FaqBean>> listDataChild;
    private THSFaqFragment mTHSFaqFragment;

    public ExpandableListAdapter(THSFaqFragment thsFaqFragment, List<String> listDataHeader,
                                 HashMap<String, List<FaqBean>> listChildData) {
        this.mTHSFaqFragment = thsFaqFragment;
        this.context = mTHSFaqFragment.getContext();
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        final FaqBean faqBean = this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
        return faqBean;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final FaqBean childObject = (FaqBean)getChild(groupPosition, childPosition);
        final String childText = childObject.getQuestion();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.ths_faqs_expandable_list_group_item, null);
        }

        TextView txtListChild = convertView
                .findViewById(R.id.lblListItem);

        txtListChild.setText(childText);
        txtListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(THSConstants.THS_FAQ_HEADER,listDataHeader.get(groupPosition).toString());
                bundle.putSerializable(THSConstants.THS_FAQ_ITEM,childObject);
                THSFaqAnswerFragment thsFaqAnswerFragment = new THSFaqAnswerFragment();
                mTHSFaqFragment.addFragment(thsFaqAnswerFragment,THSFaqAnswerFragment.TAG,bundle,false);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.ths_faqs_expandable_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

