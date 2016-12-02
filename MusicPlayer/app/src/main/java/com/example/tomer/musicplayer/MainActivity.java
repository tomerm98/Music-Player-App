package com.example.tomer.musicplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Intent mediaPlayerServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mediaPlayerServiceIntent = new Intent(this, MediaPlayerService.class);
        mediaPlayerServiceIntent.setAction(MediaPlayerService.ACTION_PLAY_RES);
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
