package com.android.myfirstapplication;

import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class MainActivity extends Activity {
    //初始化变量,帧布局
    FrameLayout frame = null;
    private int[] imgLady;

    //自定义一个用于定时更新UI界面的handler类对象
    Handler handler = new Handler()
    {
        int i = -1;
        @Override
        public void handleMessage(Message msg) {
            imgLady = new int[]{R.drawable.s_1,R.drawable.s_2,R.drawable.s_3,R.drawable.s_4,R.drawable.s_5,R.drawable.s_6,R.drawable.s_7,R.drawable.s_8};
            //判断信息是否为本应用发出的
            if(msg.what == 0x123)
            {
                i++;
                i=i%imgLady.length;
                Log.v("MainActivity" ,"当前图片:"+i);
                move(i);
            }
            super.handleMessage(msg);
        }
    };


    //定义走路时切换图片的方法
    void move(int i)
    {
        //通过setForeground来设置前景图像
        frame.setForeground(getResources().getDrawable(imgLady[i]));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        frame = (FrameLayout) findViewById(R.id.container);
        //定义一个定时器对象,定时发送信息给handler
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                //发送一条空信息来通知系统改变前景图片
                handler.sendEmptyMessage(0x123);
            }
        }, 0,150);
    }
}