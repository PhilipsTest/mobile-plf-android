package com.philips.platform.aildemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.philips.platform.appinfra.demo.R;

public class ChuckEnableActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private CheckBox cbChuck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chuck_enable);
        cbChuck= findViewById(R.id.pim_checkbox);
        SharedPreferences shared = this.getSharedPreferences("chuckEnabled", MODE_PRIVATE);
        boolean chuck = (shared.getBoolean("CHUCK", false));
        cbChuck.setChecked(chuck);
        cbChuck.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        SharedPreferences preferences = this.getSharedPreferences("chuckEnabled", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CHUCK", cbChuck.isChecked());
        editor.apply();
    }
}
