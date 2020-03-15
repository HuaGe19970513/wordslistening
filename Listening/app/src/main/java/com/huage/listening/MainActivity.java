package com.huage.listening;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_play;
    private Button btn_pause;
    private Button btn_stop;
    private MediaPlayer mPlayer = new MediaPlayer();
    private boolean isRelease = true;   //判断是否MediaPlayer是否释放的标志

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
    }

    private void bindViews() {
        btn_play = (Button) findViewById(R.id.btn_play);
        btn_pause = (Button) findViewById(R.id.btn_pause);
        btn_stop = (Button) findViewById(R.id.btn_stop);

        btn_play.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                if(isRelease){
                    String url = "http://dict.youdao.com/dictvoice?type=1&audio=hello"; // your URL here

                    //mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mPlayer.setDataSource(url);
                        mPlayer.prepare(); // might take long! (for buffering, etc)
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isRelease = false;
                }
                mPlayer.start();   //开始播放
                btn_play.setEnabled(false);
                btn_pause.setEnabled(true);
                btn_stop.setEnabled(true);
                break;
            case R.id.btn_pause:
                mPlayer.pause();     //停止播放
                btn_play.setEnabled(true);
                btn_pause.setEnabled(false);
                btn_stop.setEnabled(false);
                break;
            case R.id.btn_stop:
                mPlayer.reset();     //重置MediaPlayer
                mPlayer.release();   //释放MediaPlayer
                isRelease = true;
                btn_play.setEnabled(true);
                btn_pause.setEnabled(false);
                btn_stop.setEnabled(false);
                break;
        }
    }
}