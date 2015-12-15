package com.philips.cdp.ui.catalog.activity;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.philips.cdp.ui.catalog.R;
import com.philips.cdp.uikit.UiKitActivity;
import com.shamanland.fonticon.FontIconTypefaceHolder;

public class SpinnersRoundActivity extends CatalogActivity {
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spinners_round);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        textView = (TextView) findViewById(R.id.textView1);
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        //Just to display the progress slowly
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }



}