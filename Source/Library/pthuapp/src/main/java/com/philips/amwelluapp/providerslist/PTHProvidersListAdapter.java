package com.philips.amwelluapp.providerslist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.americanwell.sdk.entity.provider.ProviderImageSize;
import com.americanwell.sdk.entity.provider.ProviderInfo;
import com.americanwell.sdk.exception.AWSDKInstantiationException;
import com.philips.amwelluapp.R;
import com.philips.amwelluapp.utility.PTHManager;
import com.philips.platform.uid.view.widget.RatingBar;

import java.util.List;

public class PTHProvidersListAdapter extends RecyclerView.Adapter<PTHProvidersListAdapter.MyViewHolder> {
    private List<ProviderInfo> providerList;

    public PTHProvidersListAdapter(List<ProviderInfo> providerList){
        this.providerList = providerList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, practice, isAvailble;
        public RatingBar providerRating;
        public ImageView providerImage;

        public MyViewHolder(View view) {
            super(view);
            practice = (TextView) view.findViewById(R.id.practiceNameLabel);
            name = (TextView) view.findViewById(R.id.providerNameLabel);
            isAvailble = (TextView) view.findViewById(R.id.isAvailableLabel);
            providerRating = (RatingBar) view.findViewById(R.id.providerRating);
            providerImage = (ImageView) view.findViewById(R.id.providerImage);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pth_provider_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ProviderInfo provider = providerList.get(position);

        holder.providerRating.setRating(provider.getRating());
        holder.name.setText("Dr. " + provider.getFullName());
        holder.practice.setText(provider.getSpecialty().getName());
        holder.isAvailble.setText(""+provider.getVisibility());
        if(provider.hasImage()) {
            try {
                PTHManager.getInstance().getAwsdk(holder.providerImage.getContext()).getPracticeProvidersManager().newImageLoader(provider, holder.providerImage, ProviderImageSize.SMALL).placeholder(holder.providerImage.getResources().getDrawable(R.drawable.doctor_placeholder)).build().load();
            } catch (AWSDKInstantiationException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return providerList.size();
    }
}
