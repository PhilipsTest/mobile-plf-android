package com.philips.platform.pimdemo;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;

import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;

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
        libraryList.add("PIM");
        libraryList.add("USR");
        ArrayAdapter libraryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, libraryList);
        selectLibrary.setAdapter(libraryAdapter);
        selectLibrary.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            selectLibrary.setVisibility(View.INVISIBLE);
            launchUApp.setVisibility(View.INVISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    launchUApp();
                    progressBar.setVisibility(View.INVISIBLE);
                    selectLibrary.setVisibility(View.VISIBLE);
                    launchUApp.setVisibility(View.VISIBLE
                    );
                }
            }, 5000);
        }
    }

    private void launchUApp() {
        Intent intent = new Intent(PimDemoActivity.this, PIMDemoUAppActivity.class);
        intent.putExtra("SelectedLib", selectLibrary.getSelectedItem().toString());
        SharedPreferences preferences = this.getSharedPreferences("chuckEnabled", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CHUCK", enableChuck.isChecked());
        editor.apply();
        startActivity(intent);
    }
}
