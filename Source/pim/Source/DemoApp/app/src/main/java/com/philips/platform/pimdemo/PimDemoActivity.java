package com.philips.platform.pimdemo;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.philips.platform.uid.utils.UIDActivity;
import com.philips.platform.uid.view.widget.Button;
import com.pim.demouapp.PIMDemoUAppActivity;

import java.util.ArrayList;
import java.util.List;

public class PimDemoActivity extends UIDActivity {
    private AppCompatSpinner selectLibreary;
    private CheckBox enableChuck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pim_demo);
        Button launchUApp = findViewById(R.id.launch);
        enableChuck = findViewById(R.id.pim_checkbox);
        launchUApp.setOnClickListener(v -> {
            Intent intent = new Intent(PimDemoActivity.this, PIMDemoUAppActivity.class);
            intent.putExtra("SelectedLib", selectLibreary.getSelectedItem().toString());
            SharedPreferences preferences = this.getSharedPreferences("chuckEnabled", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("CHUCK", enableChuck.isChecked());
            editor.apply();
            startActivity(intent);
        });

        selectLibreary = findViewById(R.id.selectLibrary);
        List<String> libraryList = new ArrayList<>();
        libraryList.add("PIM");
        libraryList.add("USR");
        ArrayAdapter libraryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, libraryList);
        selectLibreary.setAdapter(libraryAdapter);

    }

}
