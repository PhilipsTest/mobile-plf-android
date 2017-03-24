package com.philips.cdpp.dicommtestapp.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.philips.cdpp.dicommtestapp.R;

import java.util.ArrayList;
import java.util.List;

import nl.rwslinkman.presentable.PresentableAdapter;
import nl.rwslinkman.presentable.Presenter;
import nl.rwslinkman.presentable.interaction.PresentableItemClickListener;

abstract public class SampleAppFragment<T> extends BaseFragment
{
    private PresentableAdapter<T> mAdapter;
    protected TextView titleView;
    protected TextView subtitleView;

    @Override
    final void setupFragmentView(View fragmentView)
    {
        Presenter p = getListPresenter();

        RecyclerView itemList = (RecyclerView) fragmentView.findViewById(R.id.sample_app_fragment_list);
        mAdapter = new PresentableAdapter<>(p, new ArrayList<T>());
        titleView = (TextView) fragmentView.findViewById(R.id.sample_app_fragment_title);
        subtitleView = (TextView) fragmentView.findViewById(R.id.sample_app_fragment_subtitle);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(llm);
        itemList.setAdapter(mAdapter);
    }

    @Override
    final protected int getLayoutId() {
        return R.layout.fragment_sample_app;
    }

    abstract Presenter getListPresenter();

    protected void updateList(List<T> content) {
        mAdapter.setData(content);
        notifyListUpdated();
    }

    protected void notifyListUpdated() {
        mAdapter.notifyDataSetChanged();
    }

    protected void setListItemClickListener(PresentableItemClickListener<T> clickListener) {
        mAdapter.setItemClickListener(clickListener);
    }
}
