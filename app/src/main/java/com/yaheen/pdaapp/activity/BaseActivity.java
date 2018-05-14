package com.yaheen.pdaapp.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yaheen.pdaapp.R;

public class BaseActivity extends AppCompatActivity {

    private TextView tvContent;

    public void setTitleContent(int content){
        tvContent = findViewById(R.id.tv_title_content);
        if(tvContent!=null){
            tvContent.setText(content);
        }
    }
}
