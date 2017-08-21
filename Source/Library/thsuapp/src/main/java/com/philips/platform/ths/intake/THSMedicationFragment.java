/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.intake;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.americanwell.sdk.entity.health.Medication;
import com.philips.platform.ths.R;
import com.philips.platform.ths.base.THSBaseFragment;
import com.philips.platform.uappframework.listener.ActionBarListener;
import com.philips.platform.uid.view.widget.Button;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.philips.platform.ths.utility.THSConstants.MEDICATION_ON_ACTIVITY_RESULT;


public class THSMedicationFragment extends THSBaseFragment implements View.OnClickListener {
    public static final String TAG = THSBaseFragment.class.getSimpleName();

    private ActionBarListener actionBarListener;
    private THSMedicationPresenter mPresenter;
    private RelativeLayout mProgressbarContainer;
    private ListView mExistingMedicationListView;
    private THSExistingMedicationListAdapter mTHSExistingMedicationListAdapter;
    protected THSMedication mExistingMedication;
    private Medication mSelectedMedication;
    private Button updateMedicationButton;
    private Button mSkipLabel;
    boolean existingMedicineFetched = false; // flag to know if medication is fetched which can be null also


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.ths_intake_medication, container, false);
        setRetainInstance(true);
        mProgressbarContainer = (RelativeLayout) view.findViewById(R.id.ths_medication_container);
        mPresenter = new THSMedicationPresenter(this);
        updateMedicationButton = (Button) view.findViewById(R.id.ths_intake_medication_continue_button);
        updateMedicationButton.setOnClickListener(this);
        mSkipLabel = (Button) view.findViewById(R.id.ths_intake_medication_skip_step_label);
        mSkipLabel.setOnClickListener(this);
        mExistingMedicationListView = (ListView) view.findViewById(R.id.ths_intake_medication_listview);
        View viewFooter = inflater.inflate(R.layout.ths_existing_medicine_footer, null);
        RelativeLayout footer = (RelativeLayout) viewFooter.findViewById(R.id.ths_existing_medicine_footer_relative_layout);
        footer.setOnClickListener(this);
        mExistingMedicationListView.addFooterView(footer);
        mTHSExistingMedicationListAdapter = new THSExistingMedicationListAdapter(getActivity());
        mExistingMedicationListView.setAdapter(mTHSExistingMedicationListAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        actionBarListener = getActionBarListener();

        if (existingMedicineFetched) {
            // show existing medicines
            showExistingMedicationList(mExistingMedication);

        } else {
            // fetch existing medicines
            createCustomProgressBar(mProgressbarContainer, BIG);
            mPresenter.fetchMedication();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (null != actionBarListener) {
            actionBarListener.updateActionBar(getString(R.string.ths_prepare_your_visit), true);
        }
    }

    public void showExistingMedicationList(THSMedication pTHMedication) {
        hideProgressBar();
        mExistingMedication = pTHMedication; // required for server update
        existingMedicineFetched = true;
        if (null != mSelectedMedication) {
            addSearchedMedicineToExistingMedication(mSelectedMedication);
        }
        mTHSExistingMedicationListAdapter.setData(mExistingMedication);
    }


    public void addSearchedMedicineToExistingMedication(Medication searchedMedication) {
        if (null == mExistingMedication) {
            mExistingMedication = new THSMedication();
            List<Medication> medication = new ArrayList<Medication>();
            mExistingMedication.setMedicationList(medication);

        }
        if (null != mExistingMedication.getMedicationList() && !mExistingMedication.getMedicationList().contains(searchedMedication)) {
            mExistingMedication.getMedicationList().add(searchedMedication);

        }

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    //TODO: Review comment - Please move it to Presenter
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ths_intake_medication_continue_button) {
            createCustomProgressBar(mProgressbarContainer, BIG);
            mPresenter.onEvent(R.id.ths_intake_medication_continue_button);
        } else if (id == R.id.ths_existing_medicine_footer_relative_layout) {
            mPresenter.onEvent(R.id.ths_existing_medicine_footer_relative_layout);
        } else if (id == R.id.ths_intake_medication_skip_step_label) {
            mPresenter.onEvent(R.id.ths_intake_medication_skip_step_label);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == MEDICATION_ON_ACTIVITY_RESULT) {
                mSelectedMedication = data.getExtras().getParcelable("selectedMedication");
            }
        }
    }

}
