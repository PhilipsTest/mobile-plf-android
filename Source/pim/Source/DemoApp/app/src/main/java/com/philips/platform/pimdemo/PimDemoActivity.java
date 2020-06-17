package com.philips.platform.pimdemo;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import androidx.appcompat.widget.AppCompatSpinner;

import com.philips.platform.pim.UDIRedirectReceiverActivity;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.uid.view.widget.Button;
import com.pim.demouapp.PIMDemoUAppActivity;
import com.pim.demouapp.PIMDemoUAppApplication;

import java.util.ArrayList;
import java.util.List;

public class PimDemoActivity extends UIDActivity {
    private AppCompatSpinner selectLibrary;
    private CheckBox enableChuck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo);
        Button launchUApp = findViewById(R.id.launch);
        enableChuck = findViewById(R.id.pim_checkbox);
        ProgressBar progressBar = findViewById(R.id.relaunchProgressBar);
        selectLibrary = findViewById(R.id.selectLibrary);

        List<String> libraryList = new ArrayList<>();
        libraryList.add("UDI");
        libraryList.add("USR");
        ArrayAdapter libraryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, libraryList);
        selectLibrary.setAdapter(libraryAdapter);
        selectLibrary.setSelection(0,false);
        selectLibrary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String selectedLib = libraryList.get(position);
                Log.i("UDIDemoActivity","selectedLib"+selectedLib);
                if (selectedLib.equalsIgnoreCase("USR"))
                    PIMDemoUAppApplication.getInstance().intialiseUR();
                else
                    PIMDemoUAppApplication.getInstance().initialisePim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        launchUApp.setOnClickListener(v -> {
            launchUApp(false);
        });

        if (getIntent().hasExtra(UDIRedirectReceiverActivity.REDIRECT_TO_CLOSED_APP)) {
            launchUApp(true);
        }
    }

    private void launchUApp(boolean isRedirectToClosedApp) {
        Intent intent = new Intent(PimDemoActivity.this, PIMDemoUAppActivity.class);
        intent.putExtra("SelectedLib", selectLibrary.getSelectedItem().toString());
        if(isRedirectToClosedApp)
            intent.putExtra(UDIRedirectReceiverActivity.REDIRECT_TO_CLOSED_APP,true);
        SharedPreferences preferences = this.getSharedPreferences("chuckEnabled", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CHUCK", enableChuck.isChecked());
        editor.apply();
        startActivity(intent);
    }
}
