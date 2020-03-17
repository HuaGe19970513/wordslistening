package com.huage.wordslistening.service;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.huage.wordslistening.model.Music;

public class PlayService implements Runnable{

    private MediaPlayer mPlayer;
    private String url;
    private int playPeriod;
    private String word;
    private String means;
    private Handler mHandler;
    private final int PLAYSERVICE_MESSAGE=101;

    public volatile boolean stop = false;

    CacheService cache = new CacheService();
    public PlayService(CacheService cache) {
        this.cache = cache;
    }

    public void run() {
        MediaPlayer mPlayer =new MediaPlayer();
        while (!stop) {
            Music music = cache.pop();
            word = music.getWord();
            means = music.getMeans();
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("word",word);
            bundle.putString("means",means);
            message.setData(bundle);
            message.what = PLAYSERVICE_MESSAGE;

            mHandler.sendMessage(message);
            if(!(word.equals("") && means.equals(""))){
                try {
                    mPlayer.reset();
                    mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mPlayer.setDataSource(url+word);
                    mPlayer.prepare();
                    mPlayer.start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(playPeriod*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }
    public MediaPlayer getmPlayer() {
        return mPlayer;
    }

    public void setmPlayer(MediaPlayer mPlayer) {
        this.mPlayer = mPlayer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPlayPeriod() {
        return playPeriod;
    }

    public void setPlayPeriod(int playPeriod) {
        this.playPeriod = playPeriod;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeans() {
        return means;
    }

    public void setMeans(String means) {
        this.means = means;
    }
}
