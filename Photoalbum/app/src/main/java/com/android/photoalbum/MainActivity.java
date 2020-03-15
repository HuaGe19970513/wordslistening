package com.android.photoalbum;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private TextView text;
    private String[] title;
    private int[] photoNo;
    private int photoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //视图初始化
        initView();
        //数据初始化
        initData();
    }


    private void initView() {
        image = findViewById(R.id.iv_lady);
        text = findViewById(R.id.tv_photoNo);
        findViewById(R.id.bt_up).setOnClickListener(this);
        findViewById(R.id.bt_next).setOnClickListener(this);
    }
    private void initData() {
        title = new String[]{"第一张","第二张","第三张","第四张","第五张"};
        photoNo = new int[]{R.drawable.lady_1, R.drawable.lady_2, R.drawable.lady_3, R.drawable.lady_4, R.drawable.lady_5};
        photoIndex = 0;
        image.setImageResource(photoNo[photoIndex]);
        text.setText(title[photoIndex]);
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.bt_up:
                  photoIndex = photoIndex == 0?(photoNo.length-1):(--photoIndex);
                  break;
              case R.id.bt_next:
                   photoIndex = photoIndex == (photoNo.length-1)?0:(++photoIndex);
                   break;
          }
          updateImageAndText();
    }

    private void updateImageAndText() {
        image.setImageResource(photoNo[photoIndex]);
        text.setText(title[photoIndex]);
    }
}
