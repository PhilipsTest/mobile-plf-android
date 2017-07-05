package com.philips.platform.appframework.testmicroappfw.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.philips.platform.appframework.R;
import com.philips.platform.appframework.homescreen.HamburgerActivity;
import com.philips.platform.appframework.testmicroappfw.data.TestConfigManager;
import com.philips.platform.appframework.testmicroappfw.models.Chapter;
import com.philips.platform.appframework.testmicroappfw.models.CommonComponent;
import com.philips.platform.baseapp.base.AbstractAppFrameworkBaseFragment;
import com.philips.platform.baseapp.base.FragmentView;
import com.philips.platform.uappframework.listener.ActionBarListener;

import java.util.ArrayList;

/**
 * Created by philips on 13/02/17.
 */

public class COCOListFragment extends AbstractAppFrameworkBaseFragment implements COCOListContract.View, ActionBarListener,FragmentView {

    public static final String SELECTED_CHAPTER = "selected_chapter";

    public static final String TAG=COCOListFragment.class.getSimpleName();
    private RecyclerView cocoRecyclerView;

    private COCOListPresenter cocoListPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.coco_list_fragment,container,false);
        cocoRecyclerView=(RecyclerView)view.findViewById(R.id.coco_recyclerview);
        cocoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cocoListPresenter=new COCOListPresenter(this, TestConfigManager.getInstance(),getActivity(),this);
        Bundle bundle=getArguments();
        Chapter chapter=(Chapter) bundle.getSerializable(SELECTED_CHAPTER);
        cocoListPresenter.loadCoCoList(chapter);

    }

    @Override
    public String getActionbarTitle() {
        return null;
    }

    @Override
    public void displayCoCoList(ArrayList<CommonComponent> commonComponentsList) {
        CoCoAdapter coCoAdapter=new CoCoAdapter(getActivity(),commonComponentsList,cocoListPresenter);
        cocoRecyclerView.setAdapter(coCoAdapter);
        coCoAdapter.notifyDataSetChanged();
    }

    @Override
    public FragmentActivity getFragmentActivity() {
        return getActivity();
    }

    @Override
    public ActionBarListener getActionBarListener() {
        return this;
    }

    @Override
    public int getContainerId() {
        if(getActivity() instanceof HamburgerActivity){
            return ((HamburgerActivity)getActivity()).getContainerId();
        }
        return 0;
    }

    @Override
    public void updateActionBar(@StringRes int i, boolean b) {

    }

    @Override
    public void updateActionBar(String s, boolean b) {

    }
}
