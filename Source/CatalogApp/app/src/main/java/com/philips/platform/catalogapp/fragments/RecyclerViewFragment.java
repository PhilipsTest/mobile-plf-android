/*
 * (C) Koninklijke Philips N.V., 2017.
 * All rights reserved.
 */
package com.philips.platform.catalogapp.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.platform.catalogapp.DataHolder;
import com.philips.platform.catalogapp.DataHolderView;
import com.philips.platform.catalogapp.MainActivity;
import com.philips.platform.catalogapp.R;
import com.philips.platform.catalogapp.RecyclerViewSettingsHelper;
import com.philips.platform.catalogapp.databinding.FragmentRecyclerviewBinding;
import com.philips.platform.uid.drawable.SeparatorDrawable;
import com.philips.platform.uid.thememanager.ThemeUtils;
import com.philips.platform.uid.view.widget.Label;
import com.philips.platform.uid.view.widget.RecyclerViewSeparatorItemDecoration;

public class RecyclerViewFragment extends BaseFragment {

    private FragmentRecyclerviewBinding fragmentRecyclerviewBinding;
    private static boolean isIconTemplateSelected;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final Context context = getContext();
        RecyclerViewSettingsHelper settingsHelper = new RecyclerViewSettingsHelper(context);
        isIconTemplateSelected = settingsHelper.isIconTemplateSelected();

        DataHolderView dataHolderView = isIconTemplateSelected ? getIconDataHolderView(context) : getTwoLinesDataHolderView(context);

        fragmentRecyclerviewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recyclerview, container, false);
        fragmentRecyclerviewBinding.setFrag(this);

        fragmentRecyclerviewBinding.recyclerviewRecyclerview.findViewById(R.id.uid_recyclerview_header).setVisibility(settingsHelper.isHeaderEnabled() ? View.VISIBLE : View.GONE);

        SeparatorDrawable separatorDrawable = new SeparatorDrawable(context);
        fragmentRecyclerviewBinding.getRoot().findViewById(R.id.divider).setBackground(separatorDrawable);

        RecyclerView recyclerView = ((RecyclerView)fragmentRecyclerviewBinding.recyclerviewRecyclerview.findViewById(R.id.uid_recyclerview_recyclerview));

        if(settingsHelper.isSeperatorEnabled()) {
            recyclerView.addItemDecoration(new RecyclerViewSeparatorItemDecoration(getContext()));
        }

        recyclerView.setAdapter(new RecyclerViewAdapter(dataHolderView.dataHolders));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ((Label)fragmentRecyclerviewBinding.recyclerviewRecyclerview.findViewById(R.id.uid_recyclerview_header)).setText(R.string.recyclerview_the_header);

        return fragmentRecyclerviewBinding.getRoot();
    }

    @NonNull
    private DataHolderView getTwoLinesDataHolderView(Context context) {
        DataHolderView dataHolderView = new DataHolderView();
        dataHolderView.addTwoLineItem(R.string.title1, R.string.description1, context);
        dataHolderView.addTwoLineItem(R.string.title2, R.string.description2, context);
        dataHolderView.addTwoLineItem(R.string.title3, R.string.description3, context);
        dataHolderView.addTwoLineItem(R.string.title4, R.string.description4, context);
        dataHolderView.addTwoLineItem(R.string.title5, R.string.description5, context);
        dataHolderView.addTwoLineItem(R.string.title6, R.string.description6, context);
        return dataHolderView;
    }

    @NonNull
    private DataHolderView getIconDataHolderView(Context context) {
        DataHolderView dataHolderView = new DataHolderView();
        dataHolderView.addIconItem(R.drawable.ic_add_folder, R.string.title1, context);
        dataHolderView.addIconItem(R.drawable.ic_home, R.string.title2, context);
        dataHolderView.addIconItem(R.drawable.ic_lock, R.string.title3, context);
        dataHolderView.addIconItem(R.drawable.ic_alarm, R.string.title4, context);
        dataHolderView.addIconItem(R.drawable.ic_bottle, R.string.title5, context);
        dataHolderView.addIconItem(R.drawable.ic_location, R.string.title6, context);
        return dataHolderView;
    }

    static class RecyclerViewAdapter extends RecyclerView.Adapter {
        private ObservableArrayList<DataHolder> dataHolders;

        public RecyclerViewAdapter(@NonNull final ObservableArrayList<DataHolder> dataHolders) {
            this.dataHolders = dataHolders;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
            int layoutId = isIconTemplateSelected ? R.layout.recyclerview_one_line_icon : R.layout.recyclerview_two_line_text_item;
            View v = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            RecyclerViewAdapter.BindingHolder holder = new RecyclerViewAdapter.BindingHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            final DataHolder dataHolder = dataHolders.get(position);
            ((RecyclerViewAdapter.BindingHolder) holder).getBinding().setVariable(1, dataHolder);
            ((RecyclerViewAdapter.BindingHolder) holder).getBinding().executePendingBindings();

            Resources.Theme theme = ThemeUtils.getTheme(holder.itemView.getContext(), null);
            ColorStateList colorStateList = ThemeUtils.buildColorStateList(holder.itemView.getResources(), theme, R.color.uid_recyclerview_background_selector);
            final int selectedStateColor = colorStateList.getDefaultColor();

            ((BindingHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    boolean isSelected = holder.itemView.isSelected();
                    holder.itemView.setSelected(!isSelected);
                    holder.itemView.setBackgroundColor(isSelected ? Color.TRANSPARENT : selectedStateColor);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataHolders.size();
        }

        private int getSelectedStateColor(Context context) {
            ColorStateList colorStateList = ContextCompat.getColorStateList(context, R.color.uid_recyclerview_background_selector);
            return colorStateList.getDefaultColor();
        }

        static class BindingHolder extends RecyclerView.ViewHolder {
            private ViewDataBinding binding;

            public BindingHolder(@NonNull View rowView) {
                super(rowView);
                binding = DataBindingUtil.bind(rowView);
            }

            public ViewDataBinding getBinding() {
                return binding;
            }
        }
    }

    @Override
    public int getPageTitle() {
        return R.string.page_title_recyclerview;
    }

    public void showRecyclerViewSettingsFragment() {
        ((MainActivity) getActivity()).getNavigationController().switchFragment(new RecyclerViewSettingsFragment());
    }
}
