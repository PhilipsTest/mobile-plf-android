package com.ecs.demouapp.ui.screens;

import android.os.Bundle;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ecs.demouapp.R;
import com.ecs.demouapp.ui.adapters.DeliveryModeAdapter;
import com.ecs.demouapp.ui.container.CartModelContainer;
import com.ecs.demouapp.ui.controller.AddressController;
import com.ecs.demouapp.ui.session.NetworkConstants;
import com.ecs.demouapp.ui.utils.ECSUtility;
import com.philips.platform.ecs.model.address.ECSDeliveryMode;


import java.util.List;


public class DeliveryMethodFragment extends InAppBaseFragment implements OnSetDeliveryModeListener, AddressController.AddressListener {

    public static final String TAG = DeliveryMethodFragment.class.getName();
    private RecyclerView mDeliveryRecyclerView;
    private AddressController mAddressController;
    private RelativeLayout mParentContainer;
    List<ECSDeliveryMode> mDeliveryModes;

    public static DeliveryMethodFragment createInstance(final Bundle args, final AnimationType animType) {
        DeliveryMethodFragment fragment = new DeliveryMethodFragment();
        args.putInt(NetworkConstants.EXTRA_ANIMATIONTYPE, animType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitleAndBackButtonVisibility(getContext().getResources().getString(R.string.iap_delivery_method), true);
        setCartIconVisibility(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ecs_delivery_method_fragment, container, false);
        mDeliveryRecyclerView = view.findViewById(R.id.iap_parcel_delivery_list);
        mParentContainer = view.findViewById(R.id.delivery_method_container);
        mAddressController = new AddressController(getContext(), this);

        createCustomProgressBar(mParentContainer, BIG);
        mAddressController.getDeliveryModes();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mDeliveryRecyclerView.setLayoutManager(layoutManager);
    }

    private void settingDataToAdapter() {
        if (mDeliveryModes == null) return;
        DeliveryModeAdapter mDeliveryModeAdapter = new DeliveryModeAdapter(mDeliveryModes, this);
        mDeliveryRecyclerView.setAdapter(mDeliveryModeAdapter);
        mDeliveryModeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position) {
        createCustomProgressBar(mParentContainer, BIG);

        mAddressController.setDeliveryMode(mDeliveryModes.get(position));

    }

    @Override
    public void onGetRegions(Message msg) {
    //do nothing
    }

    @Override
    public void onGetUser(Message msg) {
    //do nothing
    }

    @Override
    public void onCreateAddress(Message msg) {
    //do nothing
    }

    @Override
    public void onGetAddress(Message msg) {
    //do nothing
    }

    @Override
    public void onSetDeliveryAddress(Message msg) {
    //do nothing
    }

    @Override
    public void onGetDeliveryModes(Message msg) {
        if ((msg.obj instanceof Exception)) {
            Exception exception =(Exception) msg.obj;
            ECSUtility.showECSAlertDialog(getActivity(),"Error",exception.getMessage());
        } else if ((msg.obj instanceof List )) {
            List<ECSDeliveryMode> deliveryModeList =( List<ECSDeliveryMode>) msg.obj;
            mDeliveryModes = deliveryModeList;
            CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
            settingDataToAdapter();
        }
        hideProgressBar();
    }

    @Override
    public void onSetDeliveryMode(Message msg) {
        hideProgressBar();
        if ((msg.obj instanceof Exception)) {
            Exception exception =(Exception) msg.obj;
            ECSUtility.showECSAlertDialog(getActivity(),"Error",exception.getMessage());
        }else {
            getFragmentManager().popBackStack();
        }
    }

}
