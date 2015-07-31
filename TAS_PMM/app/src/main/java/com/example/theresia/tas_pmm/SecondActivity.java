package com.example.theresia.tas_pmm;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Created by T on 26-Jul-15.
 */
public class SecondActivity extends Activity {
    String get_videoUrl;
    String judul;
    String downloads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //nama yg d layout

        Bundle b = getIntent().getExtras();
        get_videoUrl = b.getString("parse_url");
        judul = b.getString("title");
        downloads = b.getString("downloads");

        VideoView myVideo = (VideoView) findViewById(R.id.myVideo);
        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtDownloads = (TextView) findViewById(R.id.txtDownloads);
        txtTitle.setText(judul);
        txtDownloads.setText(downloads+" Downloads");
        String url = get_videoUrl;

        Uri vidUri = Uri.parse(url);
        myVideo.setVideoURI(vidUri);
        myVideo.start();

        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(myVideo);


    }
}
