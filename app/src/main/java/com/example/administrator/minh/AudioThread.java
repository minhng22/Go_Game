package com.example.administrator.minh;


import android.content.Context;
import android.media.MediaPlayer;


public class AudioThread extends Thread {
    public MediaPlayer mediaPlayer;

    public AudioThread(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.timeflux);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f, 0.5f);
    }

    @Override
    public void run() {
        mediaPlayer.start();
    }
}
