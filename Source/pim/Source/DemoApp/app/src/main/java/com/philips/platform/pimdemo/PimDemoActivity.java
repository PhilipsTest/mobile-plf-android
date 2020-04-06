package com.philips.platform.pimdemo;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatSpinner;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

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
        launchUApp.setOnClickListener(v -> {
            Intent intent = new Intent(PimDemoActivity.this, PIMDemoUAppActivity.class);
            boolean isRedirect = false;
            if(intent.hasExtra("RedirectOnAppKill")){
                isRedirect = intent.getExtras().getBoolean("RedirectOnAppKill");
            }
            intent.putExtra("SelectedLib", selectLibreary.getSelectedItem().toString());
            if (getIntent() != null && getIntent().getExtras() != null) {
                intent.putExtra("RedirectOnAppKill", getIntent().getExtras().getBoolean("RedirectOnAppKill"));
            }
            startActivity(intent);
        });

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
                Log.i("SHASHI", "onNothingSelected");
            }
        });

    }

}
