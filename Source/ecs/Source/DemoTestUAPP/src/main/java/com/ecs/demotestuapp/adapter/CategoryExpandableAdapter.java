/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.ecs.demotestuapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.ecs.demotestuapp.R;
import com.ecs.demotestuapp.activity.SubGroupActivity;
import com.ecs.demotestuapp.jsonmodel.GroupItem;

import java.util.HashMap;
import java.util.List;

public class CategoryExpandableAdapter extends BaseExpandableListAdapter {

    public LayoutInflater inflater;
    public Activity activity;


    private HashMap<String, List<GroupItem>> hashMap ;

    private List<List<GroupItem>> masterList;

    public CategoryExpandableAdapter(Activity act, List<List<GroupItem>> masterList) {
        activity = act;
        inflater = act.getLayoutInflater();
        this.masterList = masterList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return masterList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final GroupItem children = (GroupItem) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_recycler, null);
        }
        text = (TextView) convertView.findViewById(R.id.tv_item);
        text.setText(children.getName());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                gotoSubGroupActivity(children);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return masterList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        if(groupPosition == 0){
            return "OCC";
        }else{
            return "PIL";
        }

    }

    @Override
    public int getGroupCount() {
        return masterList.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_parent, null);
        }
        String group = (String) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private void gotoSubGroupActivity(GroupItem groupItem) {
        Intent intent = new Intent(activity, SubGroupActivity.class);

        Bundle bundle = new Bundle();
        bundle.putSerializable("group",groupItem);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
}