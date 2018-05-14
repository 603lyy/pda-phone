package com.yaheen.pdaapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yaheen.pdaapp.BaseApp;
import com.yaheen.pdaapp.R;
import com.yaheen.pdaapp.util.DialogUtils;
import com.yaheen.pdaapp.util.dialog.DialogCallback;
import com.yaheen.pdaapp.util.dialog.IDialogCancelCallback;

public class MainActivity extends BaseActivity {

    private final static String SCAN_ACTION = "scan.rcv.message";

    private ScanDevice sm;

    private TextView tvBind, tvMsg, tvManage, tvReport;

    private LinearLayout llBind, llMsg, llManage, llReport;

    private String barcodeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        llMsg = findViewById(R.id.ll_msg);
        tvBind = findViewById(R.id.tv_bind);
        llBind = findViewById(R.id.ll_bind);
        llReport = findViewById(R.id.ll_report);
        llManage = findViewById(R.id.ll_manage);

        llBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sm.startScan();
                Intent intent = new Intent(MainActivity.this, BindActivity.class);
                startActivity(intent);
            }
        });

        llManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ManageActivity.class);
                startActivity(intent);
            }
        });

        llMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,WebActivity.class);
                startActivity(intent);
            }
        });

        llReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,ReportActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DialogUtils.showDialog(MainActivity.this, "确定要退出该APP吗？", new DialogCallback() {
            @Override
            public void callback() {
                BaseApp.exit();
            }
        }, new IDialogCancelCallback() {
            @Override
            public void cancelCallback() {
            }
        });
    }
}
