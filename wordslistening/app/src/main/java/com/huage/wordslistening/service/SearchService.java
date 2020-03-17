package com.huage.wordslistening.service;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.huage.wordslistening.model.Music;

public class SearchService implements Runnable{
    private String playUnit;
    private SQLiteDatabase db;
    private int palyId = 0;
    private int playTime = 1;
    private String word;
    private String means;
    private String wordInfo;

    CacheService cache = new CacheService();
    private int id;

    public SearchService(CacheService cache) {
        this.cache = cache;
    }

    public volatile boolean stop = false;

    public void run() {
        while (!stop) {
            String sql = "select * from " + playUnit + " where id=?";
            Cursor cursor = db.rawQuery(sql, new String[]{(palyId / playTime + 1) + ""});

            //  如果查找单词，显示其中文的意思
            if (cursor.moveToFirst()) {

                id = cursor.getInt(cursor.getColumnIndex("id"));
                word = cursor.getString(cursor.getColumnIndex("word"));
                means = cursor.getString(cursor.getColumnIndex("means"));
            }else {
                id = palyId / playTime + 1;
                word = "";
                means = "";
            }
            cursor.close();

            palyId += 1;
            wordInfo = "第" + id + "个单词： " + word + "：" + means;
            Log.v("数据库查询：", wordInfo);

            Music music = new Music();
            music.setId(id);
            music.setWord(word);
            music.setMeans(means);
            cache.push(music);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPlayUnit() {
        return playUnit;
    }

    public void setPlayUnit(String playUnit) {
        this.playUnit = playUnit;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public int getPalyId() {
        return palyId;
    }

    public void setPalyId(int palyId) {
        this.palyId = palyId;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
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

    public String getWordInfo() {
        return wordInfo;
    }

    public void setWordInfo(String wordInfo) {
        this.wordInfo = wordInfo;
    }
}
