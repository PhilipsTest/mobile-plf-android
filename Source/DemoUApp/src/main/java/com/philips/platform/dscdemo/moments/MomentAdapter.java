/* Copyright (c) Koninklijke Philips N.V., 2017
* All rights are reserved. Reproduction or dissemination
* in whole or in part is prohibited without the prior written
* consent of the copyright holder.
*/
package com.philips.platform.dscdemo.moments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.dscdemo.R;
import com.philips.platform.dscdemo.database.table.OrmMoment;

import java.util.ArrayList;
import java.util.List;

public class MomentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<? extends Moment> mData;
    private Context mContext;
    private Drawable mOptionsDrawable;
    DataServicesManager mDataServices;
    private final MomentPresenter mTemperaturePresenter;


    public MomentAdapter(final Context context, final ArrayList<? extends Moment> data, MomentPresenter mTemperaturePresenter) {

        this.mTemperaturePresenter = mTemperaturePresenter;
        mDataServices = DataServicesManager.getInstance();
        mData = data;
        mContext = context;
        initDrawables();
    }

    @Override
    public DataSyncViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.temperature_timeline, parent, false);
        return new DataSyncViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DataSyncViewHolder) {
            DataSyncViewHolder mSyncViewHolder = (DataSyncViewHolder) holder;
            mSyncViewHolder.mOptions.setImageDrawable(mOptionsDrawable);
            MomentHelper helper = new MomentHelper();
            Moment moment = (OrmMoment) mData.get(position);
            if (moment.getSynchronisationData() != null)
                mSyncViewHolder.mMomentID.setText(moment.getSynchronisationData().getGuid());
            else
                mSyncViewHolder.mMomentID.setText("Fetching...");

            mSyncViewHolder.mPhase.setText(helper.getTime(moment));
            mSyncViewHolder.mTemperature.setText(String.valueOf(helper.getTemperature(moment)));
            mSyncViewHolder.mLocation.setText(helper.getNotes(moment));
            mSyncViewHolder.mExpirationDate.setText(helper.getExpirationDate(moment));
            mSyncViewHolder.mDotsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mTemperaturePresenter.bindDeleteOrUpdatePopUp(MomentAdapter.this, mData, view, holder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null)
            return mData.size();
        else
            return 0;
    }

    public void setData(final ArrayList<? extends Moment> data) {
        this.mData = data;
    }

    public class DataSyncViewHolder extends RecyclerView.ViewHolder {
        public TextView mMomentID;
        public TextView mTemperature;
        public TextView mPhase;
        public TextView mLocation;
        public TextView mExpirationDate;
        public ImageView mOptions;
        public FrameLayout mDotsLayout;
        public TextView mIsSynced;

        public DataSyncViewHolder(final View itemView) {
            super(itemView);
            mMomentID = (TextView) itemView.findViewById(R.id.moment_id);
            mTemperature = (TextView) itemView.findViewById(R.id.time_line_data);
            mExpirationDate = (TextView) itemView.findViewById(R.id.expiration_date_detail);
            mPhase = (TextView) itemView.findViewById(R.id.phasedata);
            mLocation = (TextView) itemView.findViewById(R.id.location_detail);
            mOptions = (ImageView) itemView.findViewById(R.id.dots);
            mDotsLayout = (FrameLayout) itemView.findViewById(R.id.frame);
            mIsSynced = (TextView) itemView.findViewById(R.id.is_synced);
        }
    }

    private void initDrawables() {
        mOptionsDrawable = VectorDrawableCompat.create(mContext.getResources(), R.drawable.dots, mContext.getTheme());
    }
}
