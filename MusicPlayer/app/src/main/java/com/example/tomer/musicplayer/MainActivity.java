package com.example.tomer.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Intent mediaPlayerServiceIntent, implicitIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayerServiceIntent = new Intent(this, MediaPlayerService.class);
        implicitIntent = getIntent();
        Uri data =implicitIntent.getData();

        if (data != null)
        {
            //gets permissions
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

            mediaPlayerServiceIntent.setAction(MediaPlayerService.ACTION_PLAY_URI);
            mediaPlayerServiceIntent.putExtra(MediaPlayerService.EXTRA_URI,data);
            startService(mediaPlayerServiceIntent);
            finish();


        }
        else mediaPlayerServiceIntent.setAction(MediaPlayerService.ACTION_PLAY_RES);
    }

    public void onClickBtnLaugh(View view) {

        int resId = R.raw.inhuman_laugh;
        playFile(resId);
    }

    public void onClickBtnLeeroy(View view) {

        int resId = R.raw.leeroy;
        playFile(resId);
    }


    private void playFile(int resId) {

        mediaPlayerServiceIntent.putExtra(MediaPlayerService.EXTRA_RES_ID,resId);
        startService(mediaPlayerServiceIntent);
    }


    public void onClickBtnShepard(View view) {
        int resId = R.raw.shepard_tone;
        playFile(resId);
    }
}
