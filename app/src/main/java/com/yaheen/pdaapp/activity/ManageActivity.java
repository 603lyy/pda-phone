package com.yaheen.pdaapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.yaheen.pdaapp.R;
import com.yaheen.pdaapp.adapter.ManageMsgAdapter;
import com.yaheen.pdaapp.bean.MsgBean;
import com.yaheen.pdaapp.util.ProgersssDialog;

import java.util.ArrayList;
import java.util.List;

public class ManageActivity extends BaseActivity {

    private ListView listView;

    private LinearLayout llBack, llLoading;

    private ManageMsgAdapter msgAdapter;

    private ProgersssDialog progersssDialog;

    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);

        setTitleContent(R.string.title_bar_msg_text);

        llLoading = findViewById(R.id.common_loading_view);
        listView = findViewById(R.id.lv_msg);
        llBack = findViewById(R.id.back);

//        progersssDialog = new ProgersssDialog(this);

        msgAdapter = new ManageMsgAdapter(this);
        listView.setAdapter(msgAdapter);
        initLocalMsg();

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initLocalMsg() {
        MsgBean bean = new MsgBean();
        MsgBean.EntityBean entityBean = new MsgBean.EntityBean();
        entityBean.setCommunity("车陂生活区");
        entityBean.setUsername("梁玉兰");
        entityBean.setId("20180502");
        bean.setEntity(entityBean);
        List<MsgBean> beanList = new ArrayList<>();
        beanList.add(bean);
        msgAdapter.setDatas(beanList);
        msgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (isLoading) {
            llLoading.setVisibility(View.GONE);
            isLoading = false;
            return;
        }
        super.onBackPressed();
    }
}
