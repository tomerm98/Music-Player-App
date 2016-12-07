package com.example.tomer.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;

import static android.content.ContentValues.TAG;

public class MediaPlayerService extends Service {
    MediaPlayer mediaPlayer;
    Notification notification;
    Intent endIntent, pauseIntent, resumeIntent, replayIntent;
    RemoteViews remoteViews;
    TelephonyManager telephonyManager;
    final static String EXTRA_RES_ID = "resID";
    final static String EXTRA_URI = "uri";
    final static String ACTION_END = "end";
    final static String ACTION_PLAY_RES = "playRes";
    final static String ACTION_PLAY_URI = "playUri";
    final static String ACTION_PAUSE = "pause";
    final static String ACTION_RESUME = "resume";
    final static String ACTION_REPLAY = "replay";

    /*
        Hierarchy:
            notification has remoteViews inside of it
            remoteViews contains the notificationLayout
            the remoteViews also gives the layout action-intents for its views' events
            the action-intents call the service and tells it what to do
            actions of starting a song will be called from main activity and therefore the main activity
            must give its intent the proper action value
     */




    @Override
    public void onCreate() {
        super.onCreate();
        initializeIntents();
        notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        initializeNotiLayout();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener()
        {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state)
                {
                    case TelephonyManager.CALL_STATE_RINGING:
                        startService(pauseIntent);
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        startService(resumeIntent);
                        break;
                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);

    }



    private void initializeNotiLayout() {
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);

        remoteViews.setOnClickPendingIntent(R.id.imgBtnStop,
                PendingIntent.getService(this, 0, endIntent, 0));
        remoteViews.setOnClickPendingIntent(R.id.imgBtnPause,
                PendingIntent.getService(this, 0, pauseIntent, 0));
        remoteViews.setOnClickPendingIntent(R.id.imgBtnReplay,
                PendingIntent.getService(this, 0, replayIntent, 0));

    }





    private void initializeIntents()
    {
        endIntent = new Intent(this,MediaPlayerService.class);
        endIntent.setAction(ACTION_END);

        pauseIntent = new Intent(this,MediaPlayerService.class);
        pauseIntent.setAction(ACTION_PAUSE);

        resumeIntent= new Intent(this,MediaPlayerService.class);
        resumeIntent.setAction(ACTION_RESUME);

        replayIntent = new Intent(this,MediaPlayerService.class);
        replayIntent.setAction(ACTION_REPLAY);

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction())
        {
            case ACTION_PLAY_RES:
            case ACTION_PLAY_URI:
                playAction(intent);
                break;

            case ACTION_END:
                stopSelf(); // calls onDestroy
                break;
            case ACTION_PAUSE:
                pauseAction();
                break;
            case ACTION_RESUME:
                resumeAction();
                break;
            case ACTION_REPLAY:
                replayAction();
                break;
        }
        notification.contentView = remoteViews;
        startForeground(1, notification);
        return super.onStartCommand(intent, flags, startId);
    }

    private void replayAction() {
        mediaPlayer.seekTo(0);
    }

    private void resumeAction() {
        mediaPlayer.start();
        remoteViews.setImageViewResource(R.id.imgBtnPause,R.drawable.pause);
        remoteViews.setOnClickPendingIntent(R.id.imgBtnPause,
                PendingIntent.getService(this, 0, pauseIntent, 0));
    }

    private void pauseAction() {
        mediaPlayer.pause();
        remoteViews.setImageViewResource(R.id.imgBtnPause,R.drawable.play);
        remoteViews.setOnClickPendingIntent(R.id.imgBtnPause,
                PendingIntent.getService(this, 0, resumeIntent, 0));
    }






    private void playAction(Intent intent) {
        String name ="";
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.stop();
        switch (intent.getAction())
        {
            case ACTION_PLAY_RES:
                int resId = intent.getIntExtra(EXTRA_RES_ID, 0);
                mediaPlayer = MediaPlayer.create(this, resId);
                name = getResources().getResourceEntryName(resId);
                break;
            case ACTION_PLAY_URI:
                Uri uri = intent.getParcelableExtra(EXTRA_URI);
                name = uri.toString().replace("file:///", "/");
                mediaPlayer = MediaPlayer.create(this,Uri.parse(name));

        }

        if (mediaPlayer != null)
        mediaPlayer.start();

        remoteViews.setTextViewText(R.id.tvTitle,name);
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer!=null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}
