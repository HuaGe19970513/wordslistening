package com.huage.wordslistening;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;


public class MainActivity extends AppCompatActivity  {

    private EditText ed_unit;
    private RadioGroup timegroup;
    private RadioGroup periodgroup;
    private TextView tv_load;
    private int unitNo = 1;
    private int playTime = 1;
    private int playPeriod = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        }
        private void bindViews(){
            ed_unit =  findViewById(R.id.ed_unit);
            timegroup =  findViewById(R.id.TimeGroup);
            periodgroup =  findViewById(R.id.PeriodGroup);
            tv_load  =  findViewById(R.id.tv_load);

            ed_unit. addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Pattern pattern = Pattern.compile("^[1-9][0-9]*|");
                    if( pattern.matcher(s).matches()){
                        if(s==null||(s+"").equals("")){
                            Log.v("提示","编辑框内容为空");
                            unitNo = 1;
                        }else if(parseInt(s+"")<31){
                            unitNo = parseInt(s+"");
                            Log.v("播放单元",unitNo+"");
                        }else {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("选择单元")
                                    .setMessage("请选择正确的单元(1~30)")
                                    .setPositiveButton("重新输入", null)
                                    .show();
                            ed_unit.setText("1");
                        }
                    }else {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("选择单元")
                                .setMessage("请选择正确的单元(1~30)")
                                .setPositiveButton("重新输入", null)
                                .show();
                        ed_unit.setText("1");
                    }
                }
            });
            timegroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton btnTime =  findViewById(checkedId);
                    playTime =parseInt(btnTime.getText()+"");
                    Log.v("单词播放次数",playTime+"");
                }
            });
            periodgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton btnPeriod =  findViewById(checkedId);
                    playPeriod = parseInt((btnPeriod.getText()+"").substring(0,1));
                    Log.v("播放间隔",playPeriod+"");
                }
            });
            tv_load.setOnLongClickListener(fingerLongClickListener);
            tv_load.setOnClickListener(fingerClickListener);
        }
    private View.OnLongClickListener fingerLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Intent intent = new Intent(MainActivity.this, WorkActivity.class);
            intent.putExtra("unitNo",unitNo);
            intent.putExtra("playTime",playTime);
            intent.putExtra("playPeriod",playPeriod);
            startActivityForResult(intent,1);
            finish();
            return true;
        }

    };

    private View.OnClickListener fingerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast toast = Toast.makeText(MainActivity.this, "指纹识别失败，请重试", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL,0,-120);
            toast.show();
        }

    };
}

