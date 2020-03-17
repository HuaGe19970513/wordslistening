package com.huage.wordslistening.service;

import com.huage.wordslistening.model.Music;

public class CacheService {
    private int index = 0;
    Music[] songs = new Music[1];

    public synchronized void push(Music music){
        while(index==songs.length){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        songs[index]=music;
        index++;
        notifyAll();
    }

    public synchronized Music pop(){
        while(index==0){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        index--;
        notifyAll();
        return songs[index];
    }
}
