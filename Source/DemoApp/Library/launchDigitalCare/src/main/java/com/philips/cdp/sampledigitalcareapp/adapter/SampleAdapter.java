package com.philips.cdp.sampledigitalcareapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import com.philips.cdp.sampledigitalcareapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by 310190678 on 20-Jan-16.
 */
public class SampleAdapter extends RecyclerView.Adapter<SampleAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    ArrayList<String> mList = null;


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mList, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mList, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mCtnView = null;

        public ViewHolder(View itemView) {
            super(itemView);

            mCtnView = (TextView) itemView.findViewById(R.id.ctn_name);
        }
    }


    public SampleAdapter(ArrayList<String> list) {
        this.mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        String ctn = mList.get(i);

        if (ctn != null)
            viewHolder.mCtnView.setText(ctn);
        else
            viewHolder.mCtnView.setText("");

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
