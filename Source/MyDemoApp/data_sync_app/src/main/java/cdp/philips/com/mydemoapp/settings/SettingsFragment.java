package cdp.philips.com.mydemoapp.settings;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.philips.platform.core.datatypes.ConsentDetail;
import com.philips.platform.core.listeners.DBRequestListener;
import com.philips.platform.core.trackers.DataServicesManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import cdp.philips.com.mydemoapp.R;
import cdp.philips.com.mydemoapp.consents.ConsentDialogAdapter;
import cdp.philips.com.mydemoapp.database.table.OrmConsent;

/**
 * Created by sangamesh on 09/01/17.
 */

public class SettingsFragment extends DialogFragment implements DBRequestListener, View.OnClickListener {

    private Button mBtnOk;
    private Button mBtnCancel;
    private ConsentDialogAdapter lConsentAdapter;
    SettingsFragmentPresenter settingsFragmentPresenter;
    private ProgressDialog mProgressDialog;
    ArrayList<? extends ConsentDetail> consentDetails;
    private Context mContext;
    private DataServicesManager mDataServicesManager;
    private Spinner mSpinner_metrics,mSpinner_Local;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dialog_settings, container,
                false);

        mDataServicesManager = DataServicesManager.getInstance();
        mBtnOk = (Button) rootView.findViewById(R.id.btnOK);
        mBtnOk.setOnClickListener(this);
        mBtnOk.setEnabled(false);
        mBtnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        mBtnCancel.setOnClickListener(this);
        settingsFragmentPresenter = new SettingsFragmentPresenter(getActivity(), this);
        mProgressDialog = new ProgressDialog(getActivity());
        mDataServicesManager.registeredDBRequestListener(this);

        mSpinner_metrics = (Spinner)rootView.findViewById(R.id.spinner_metrics);
        mSpinner_Local = (Spinner)rootView.findViewById(R.id.spinner_locale);
        ArrayAdapter<CharSequence> adapterMetrics = ArrayAdapter.createFromResource(getActivity(),
                R.array.metrics, android.R.layout.simple_spinner_item);
        adapterMetrics.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_metrics.setAdapter(adapterMetrics);

        ArrayAdapter<CharSequence> adapterLocale = ArrayAdapter.createFromResource(getActivity(),
                R.array.locals, android.R.layout.simple_spinner_item);
        adapterLocale.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner_Local.setAdapter(adapterLocale);

        fetchSettings();
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccess(final ArrayList<? extends Object> data) {

        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onSuccess(Object data) {

        final OrmConsent ormConsent = (OrmConsent) data;
        if (getActivity() != null && ormConsent != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    mBtnOk.setEnabled(true);
                    dismissProgressDialog();
                }
            });
        }

    }

    @Override
    public void onFailure(final Exception exception) {

        if (getActivity() != null) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissProgressDialog();
                    Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
                lConsentAdapter.updateConsent();
                dismissConsentDialog(getDialog());
                break;
            case R.id.btnCancel:
                dismissConsentDialog(getDialog());
                break;

        }
    }

    private void dismissConsentDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        DataServicesManager.getInstance().unRegisteredDBRequestListener();
        dismissProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setTitle(R.string.settings);
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void fetchSettings() {
        showProgressDialog();
        DataServicesManager.getInstance().fetchConsent(this);

        JSONArray jsonArray=new JSONArray();

        try {
            JSONArray jsonArray1=new JSONArray(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
