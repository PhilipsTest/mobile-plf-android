package com.philips.cdp.ui.catalog.CustomListView;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.cdp.ui.catalog.R;

import java.util.ArrayList;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ListViewWithOptions extends BaseAdapter{

    public Activity activity;
    private LayoutInflater inflater=null;
    ArrayList<String> listOfItems;
    public ListViewWithOptions(Activity activity){
        this.activity = activity;
        inflater = (LayoutInflater)activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
       listOfItems = new ArrayList<>(7);
        listOfItems.add("DiamondClean");
        listOfItems.add("DiamondClean");
        listOfItems.add("DiamondClean Lorem ipsum dolor sit amet, consec tetur adipisicing elit, sed do eiusmod.");
        listOfItems.add("DiamondClean Lorem ipsum dolor sit amet, consec tetur adipisicing elit, sed do eiusmod.DiamondClean Lorem ipsum dolor sit amet, consec tetur adipisicing elit, sed do eiusmod.");
        listOfItems.add("DiamondClean Lorem ipsum dolor sit amet, consec tetur adipisicing elit, sed do eiusmod.DiamondClean Lorem ipsum dolor sit amet, consec tetur adipisicing elit, sed do eiusmod. DiamondClean Lorem ipsum dolor sit amet, consec tetur adipisicing elit, sed do eiusmod.DiamondClean Lorem ipsum dolor sit amet, consec tetur adipisicing elit, sed do eiusmod.");
        listOfItems.add("DiamondClean");
        listOfItems.add("DiamondClean");
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public Object getItem(final int position) {
        return position;
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View vi=convertView;


        if(convertView==null)
            vi = inflater.inflate(R.layout.uikit_listview_with_options_custom_layout, null);

        ImageView image = (ImageView) vi.findViewById(R.id.image);
        TextView name = (TextView) vi.findViewById(R.id.text1Name);
        TextView value = (TextView) vi.findViewById(R.id.text2value);
        TextView from = (TextView) vi.findViewById(R.id.from);

        image.setImageResource(R.drawable.image);
        //image.setColorFilter(Color.GREEN);
        name.setText(listOfItems.get(position));
        value.setText("€209,99*");
        from.setText("from");
        vi.setTag(position);
        return vi;
    }
}
