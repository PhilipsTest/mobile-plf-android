package com.philips.platform.pimdemo;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.philips.platform.pim.UDIRedirectReceiverActivity;
import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.uid.view.widget.Button;
import com.pim.demouapp.PIMDemoUAppActivity;
import com.pim.demouapp.PIMDemoUAppApplication;

import java.util.ArrayList;
import java.util.List;

public class PimDemoActivity extends UIDActivity {
    private AppCompatSpinner selectLibreary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo);
        Button launchUApp = findViewById(R.id.launch);
        ProgressBar progressBar = findViewById(R.id.relaunchProgressBar);
        selectLibreary = findViewById(R.id.selectLibrary);

        List<String> libraryList = new ArrayList<>();
        libraryList.add("PIM");
        libraryList.add("USR");
        ArrayAdapter libraryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, libraryList);
        selectLibreary.setAdapter(libraryAdapter);
        selectLibreary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String selectedLib = libraryList.get(position);
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
            launchUApp();
        });

        if (getIntent().hasExtra(UDIRedirectReceiverActivity.RELAUNCH_ON_EMAIL_VERIFY)) {
            progressBar.setVisibility(View.VISIBLE);
            selectLibreary.setVisibility(View.INVISIBLE);
            launchUApp.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchUApp();
                    progressBar.setVisibility(View.INVISIBLE);
                    selectLibreary.setVisibility(View.VISIBLE);
                    launchUApp.setVisibility(View.VISIBLE
                    );
                }
            }, 5000);
        }
    }

    private void launchUApp() {
        Intent intent = new Intent(PimDemoActivity.this, PIMDemoUAppActivity.class);
        intent.putExtra("SelectedLib", selectLibreary.getSelectedItem().toString());
        startActivity(intent);
    }
}
