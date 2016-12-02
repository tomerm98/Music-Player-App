package com.example.tomer.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.RemoteViews;

public class MediaPlayerService extends Service {
    MediaPlayer mediaPlayer;
    Notification notification;
    Intent endIntent;
    RemoteViews remoteViews;
    final static String EXTRA_RES_ID = "resID";
    final static String ACTION_END = "end";
    final static String ACTION_PLAY_RES = "playRes";
    final static String ACTION_PLAY_URI = "playUri";

    /*
        heiarchy:
            notificationIntent is inside pendingIntent
            pendingIntent is inside notification
            notification also has remoteViews inside of it
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
        Intent notificationIntent = new Intent(this, MediaPlayerService.class);

        //possibly necessary (but probably not):
        //notificationIntent.setAction("main");
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                //| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification = new Notification.Builder(this)
                //very possibly necessary:
                .setContentTitle("")
                .setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setWhen(System.currentTimeMillis())
                //.setContentIntent(pendingIntent)
                .build();

        initializeNotiLayout();
    }

    private void initializeNotiLayout() {
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification);

        remoteViews.setOnClickPendingIntent(R.id.btnStop,
                PendingIntent.getService(this, 0, endIntent, 0));
    }
    private void updateNotiLayout()
    {

        notification.contentView = remoteViews;
        startForeground(1, notification);
    }

    private void initializeIntents()
    {
        endIntent = new Intent(this,MediaPlayerService.class);
        endIntent.setAction(ACTION_END);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction())
        {
            case ACTION_PLAY_RES:
                playResAction(intent);
                break;

            case ACTION_PLAY_URI:
                playUriAction(intent);
                break;
            case ACTION_END:
                stopSelf(); // calls onDestroy
                break;


        }
        updateNotiLayout();


        //int resId = intent.getIntExtra(EXTRA_RES_ID, 0);
        //startForeground(1, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    private void playUriAction(Intent intent) {

    }

    private void playResAction(Intent intent) {
        int resId = intent.getIntExtra(EXTRA_RES_ID, 0);
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mediaPlayer.stop();
        mediaPlayer = MediaPlayer.create(this, resId);
        mediaPlayer.start();
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
