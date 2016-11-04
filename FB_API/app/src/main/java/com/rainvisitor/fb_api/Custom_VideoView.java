package com.rainvisitor.fb_api;

import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

public class Custom_VideoView extends AppCompatActivity {
    private VideoView vidView;
    private MediaStore.Video.Media vidControl;
    String vidAddress;
    private MediaController mc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom__video_view);
        if (getIntent().getExtras() != null) {
            vidAddress = getIntent().getExtras().getString("url");
        }
        MediaController mc = new MediaController(this);
        Uri vidUri = Uri.parse(vidAddress);
        vidView = (VideoView) findViewById(R.id.myVideo);
        vidView.setMediaController(mc);
        vidView.requestFocus();
        vidView.setVideoURI(vidUri);
        vidView.start();
    }
}
