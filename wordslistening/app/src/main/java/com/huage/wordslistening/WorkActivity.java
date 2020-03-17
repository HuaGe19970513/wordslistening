package com.huage.wordslistening;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class WorkActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private Button btn_back;
    private Button btn_play;
    private Button btn_home;
    private TextView tv_word;
    private TextView tv_means;
    private SQLiteDatabase db;
    private String wordInfo;
    private final String DATABASE_FILENAME = "words.db";//数据库文件名称
    private String playUnit;    //播放单元
    private int palyId = 0;  //播放Id
    private int playnum = 0; //点击次数
    private int playTime = 1;
    private int playPeriod;
    private MediaPlayer mPlayer = new MediaPlayer();
    private final String SPEAK_API = "http://dict.youdao.com/dictvoice?type=1&audio=";//有道英文朗读接口
    private String word;
    private String means;
    private int model = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
        mContext = WorkActivity.this;
        db = openDatabase();//打开数据库，获得数据库对象
        bindViews();
    }

    private SQLiteDatabase openDatabase()
    {

        // 获取输出流,文件存储目录:data/data/包名/files目录下，文件名相同
        File file = new File(getFilesDir(),DATABASE_FILENAME);

        // 如果在/sdcard/files目录中不存在xxxx.db文件，则从assets目录中复制这个文件到SD卡的目录（/sdcard/files）
        /**
         * 通过输入流和输出流来实现文件的复制（这是最常用的复制文件的方法）
         */
        if (!file.exists())
        {
            AssetManager assetManager = getAssets();
            try {
                // 获得输入流
                InputStream is = assetManager.open(DATABASE_FILENAME);
                FileOutputStream fos = new FileOutputStream(file);
                //开始读写
                byte[] buffer = new byte[8192];
                int count = 0;
                // 开始复制dictionary.db文件
                while ((count = is.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, count);
                }
                is.close();
                fos.close();
            }catch (Exception e){
                Log.e("读取数据库失败",e.getMessage());
            }
        }

        // 打开/sdcard/files 目录中的xxxx.db文件
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
                getFilesDir()+"/"+DATABASE_FILENAME, null);
        return database;


    }
    private void bindViews() {
        Bundle b = getIntent().getExtras();
        playUnit ="unit" + String.format("%02d",b.getInt("unitNo"));
        playTime = b.getInt("playTime");
        playPeriod = b.getInt("playPeriod");

        btn_back =  findViewById(R.id.btn_back);
        btn_play  =  findViewById(R.id.btn_play);
        btn_home  =  findViewById(R.id.btn_home);
        tv_word   =  findViewById(R.id.tv_word);
        tv_means  =  findViewById(R.id.tv_means);

        btn_back.setOnClickListener(this);
        btn_play.setOnClickListener(this);
        btn_home.setOnClickListener(this);
        tv_word.setOnClickListener(this);
        tv_means.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                palyId = 0;
                playnum = 0;
                atuoplay();
                break;
            case R.id.btn_play:
                playnum += 1;
                if(playnum%2!=0){  //播放
                    atuoplay();
                }else{ //暂停
                    btn_play.setBackground(getResources().getDrawable(R.drawable.play));
                    while (!(mPlayer!=null&&mPlayer.isPlaying())){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    Log.e("暂停程序",e.getMessage());
                                }
                            }
                        }).start();
                    }
                    mPlayer.pause();
                }

                break;
            case R.id.btn_home:
                if(playnum%2!=0){
                while (!(mPlayer!=null&&mPlayer.isPlaying())){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                Log.e("停止程序",e.getMessage());
                            }
                        }
                    }).start();
                }
                mPlayer.stop();
                }
                palyId = 0;
                playnum = 0;
                btn_play.setBackground(getResources().getDrawable(R.drawable.play));
                Intent intent = new Intent(WorkActivity.this,MainActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_word:
            case R.id.tv_means:
                model *= -1;
                if(model>0){
                    tv_word.setTextColor(0xF5F0C7);
                    tv_means.setTextColor(0xF5F0C7);
                    Toast.makeText(mContext, "默写模式开启", Toast.LENGTH_SHORT).show();
                }else {
                    tv_word.setTextColor(Color.BLACK);
                    tv_means.setTextColor(Color.BLACK);
                    Toast.makeText(mContext, "默写模式关闭", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
    private void atuoplay(){
        playActivity();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                palyId += 1;
                Log.v("播放监听程序",(palyId/playTime+1)+"");
                playActivity();
            }
        });
    }
    private void playActivity(){
        btn_play.setBackground(getResources().getDrawable(R.drawable.pause));
        String sql = "select * from "+playUnit+" where id=?";
        Cursor cursor = db.rawQuery(sql,new String[]{(palyId/playTime+1)+""});

        //  如果查找单词，显示其中文的意思
        if (cursor.moveToFirst()) {

            int id = cursor.getInt(cursor.getColumnIndex("id"));
            word = cursor.getString(cursor.getColumnIndex("word"));
            means = cursor.getString(cursor.getColumnIndex("means"));
            wordInfo = "第" + id + "个单词： " + word + "："+ means;
            Log.v("数据库查询：",wordInfo);
            tv_word.setText(word.replace("-"," "));
            tv_means.setText(means);

            //开始播放
            try {
                mPlayer.reset();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDataSource(SPEAK_API+ word);
                mPlayer.prepareAsync();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(playPeriod*1000);
                        } catch (InterruptedException e) {
                            Log.e("休眠程序",e.getMessage());
                        }
                        mPlayer.start();
                    }
                }).start();

            }catch (Exception e){
                Log.e("播放失败",e.getMessage());
            }

        }else {
            new AlertDialog.Builder(this).setTitle("提醒").setMessage("播放完毕").setPositiveButton("确定", null).show();
            palyId = 0;
        }
        cursor.close();
    }
}
