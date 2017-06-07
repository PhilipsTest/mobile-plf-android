package com.philips.platform.aildemo;

/**
 * Created by 310238655 on 10/3/2016.
 */


import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;


public class AndroidMediaPlayerExample extends Activity {
    ProgressBar progressBar = null;
    VideoView videoView = null;
    String videoUrl = "android.resource://com.philips.platform.aildemo/" + R.raw.demotagging;
    Context context = null;

    @Override
    public void onCreate(Bundle iclic) {
        super.onCreate(iclic);
        context = null;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mediaplayer_demo);
        videoView = (VideoView) findViewById(R.id.videoview);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        int rawId = getResources().getIdentifier("demotagging", "raw", getPackageName());
        String videoUrl = "android.resource://" + "com.philips.platform.aildemolaunch" + "/" + R.raw.demotagging;
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);
        MediaController mediaController = null;
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.start();
        progressBar.setVisibility(View.VISIBLE);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // TODO Auto-generated method stub
                Log.i("VedioonPrepared", "onPrepared");
                mp.start();
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int arg1,
                                                   int arg2) {
                        // TODO Auto-generated method stub
                        Log.i("VedioonVideoSizeChanged", "onVideoSizeChanged" + arg1 + " " + arg2);
//                        AppInfraApplication.mAIAppTaggingInterface.trackVideoStart("Tagging_trackVideoStart");
                        AILDemouAppInterface.mAIAppTaggingInterface.trackTimedActionStart("Tagging_trackTimedAction");
                        progressBar.setVisibility(View.GONE);
                        mp.start();
                    }
                });

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        Log.i("VedioonCompletion", "onVideoonCompletion");
                        if (getIntent().getBooleanExtra("VideoStart", false)) {
                            AILDemouAppInterface.mAIAppTaggingInterface.trackVideoEnd("Tagging_trackVideoEnd");
                        } else {
                            AILDemouAppInterface.mAIAppTaggingInterface.trackTimedActionEnd("Tagging_trackTimedAction");
                        }


                    }
                });
            }
        });
    }
}
