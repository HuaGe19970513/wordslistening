package com.huage.wordslistening;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huage.wordslistening.service.CacheService;
import com.huage.wordslistening.service.PlayService;
import com.huage.wordslistening.service.SearchService;

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
    private final String DATABASE_FILENAME = "words.db";//数据库文件名称
    private final int PLAYSERVICE_MESSAGE=101;
    private String playUnit;    //播放单元
    private String word;
    private String means;
    private boolean playEnd = false ;//停止播放标志
    private int palyId = 0;  //播放Id
    private boolean isPlay = true; //是否可以播放
    private int playTime = 1;
    private int playPeriod;
    private MediaPlayer mPlayer;
    private final String SPEAK_API = "http://dict.youdao.com/dictvoice?type=1&audio=";//有道英文朗读接口
    private int model = 1;
    private CacheService cache;
    private SearchService search;
    private PlayService play;


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
        searchServiceInit();

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
    private void searchServiceInit(){
        cache = new CacheService();
        search = new SearchService(cache);
        search.setDb(db);
        search.setPlayUnit(playUnit);
        search.setPalyId(palyId);
        search.setPlayTime(playTime);
        new Thread(search).start();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                play.stop=true;
                search.stop = true;
                palyId = 0;
                searchServiceInit();
                playActivity();
                break;
            case R.id.btn_play:
                if(isPlay){  //可以播放状态
                    playActivity();
                }else{ //暂停
                    btn_play.setBackground(getResources().getDrawable(R.drawable.play));
                    play.stop = true;
                    isPlay = true;
                }
                break;
            case R.id.btn_home:
                play.stop=true;
                search.stop = true;
                palyId = 0;
                btn_play.setBackground(getResources().getDrawable(R.drawable.play));
                Intent intent = new Intent(WorkActivity.this,MainActivity.class);
                startActivity(intent);
                break;

            case R.id.tv_word:
            case R.id.tv_means:
                model *= -1;
                if(model<0){
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

    private void playActivity(){    //播放过程中将按钮切换为不可再播放状态
        btn_play.setBackground(getResources().getDrawable(R.drawable.pause));
        isPlay = false;
        final Handler mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == PLAYSERVICE_MESSAGE){
                    if (word==null||means==null||(!(word.equals("") && means.equals("")))) {
                        word = msg.getData().getString("word");
                        means = msg.getData().getString("means");
                        tv_word.setText(word.replace("-", " "));
                        tv_means.setText(means);
                    } else {
                        new AlertDialog.Builder(WorkActivity.this).setTitle("提醒").setMessage("播放完毕").setPositiveButton("确定", null).show();
                        palyId = 0;
                        play.stop = true;
                        search.stop = true;
                    }
                }
                return false;
            }
        });
        play = new PlayService(cache);
        play.setmHandler(mHandler);
        play.setPlayPeriod(playPeriod);
        play.setUrl(SPEAK_API);
        new Thread(play).start();
    }

}
