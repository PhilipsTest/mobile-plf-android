package com.philips.cdp.di.iap.screens;

import android.os.Bundle;
import android.os.Message;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.philips.cdp.di.iap.R;
import com.philips.cdp.di.iap.adapters.DeliveryModeAdapter;
import com.philips.cdp.di.iap.container.CartModelContainer;
import com.philips.cdp.di.iap.controller.AddressController;
import com.philips.cdp.di.iap.response.addresses.DeliveryModes;
import com.philips.cdp.di.iap.response.addresses.GetDeliveryModes;
import com.philips.cdp.di.iap.session.IAPNetworkError;
import com.philips.cdp.di.iap.session.NetworkConstants;
import com.philips.cdp.di.iap.utils.NetworkUtility;

import java.util.List;

public class DeliveryMethodFragment extends InAppBaseFragment implements OnSetDeliveryModeListener, AddressController.AddressListener {

    public static final String TAG = DeliveryMethodFragment.class.getName();
    private static final long serialVersionUID = -7707405017483135470L;
    private RecyclerView mDeliveryRecyclerView;
    private AddressController mAddressController;
    private RelativeLayout mParentContainer;
    List<DeliveryModes> mDeliveryModes;

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
        View view = inflater.inflate(R.layout.iap_delivery_method_fragment, container, false);
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

//    private View.OnClickListener mConfirmBtnClick = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View view) {
//            //do nothing
//        }
//    };

    @Override
    public void onItemClick(int position) {
        createCustomProgressBar(mParentContainer, BIG);

        mAddressController.setDeliveryMode(mDeliveryModes.get(position).getCode());

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
        if ((msg.obj instanceof IAPNetworkError)) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getContext());
        } else if ((msg.obj instanceof GetDeliveryModes)) {
            List<DeliveryModes> deliveryModeList;
            GetDeliveryModes deliveryModes = (GetDeliveryModes) msg.obj;
            deliveryModeList = deliveryModes.getDeliveryModes();
            mDeliveryModes = deliveryModeList;
            CartModelContainer.getInstance().setDeliveryModes(deliveryModeList);
            settingDataToAdapter();
        }
        hideProgressBar();
    }

    @Override
    public void onSetDeliveryMode(Message msg) {
        hideProgressBar();
        if ((msg.obj instanceof IAPNetworkError)) {
            NetworkUtility.getInstance().showErrorMessage(msg, getFragmentManager(), getContext());
        }else {
            getFragmentManager().popBackStack();
        }
    }

}
