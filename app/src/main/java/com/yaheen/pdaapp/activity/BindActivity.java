package com.yaheen.pdaapp.activity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanDevice;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yaheen.pdaapp.R;
import com.yaheen.pdaapp.bean.BindBean;
import com.yaheen.pdaapp.util.ProgersssDialog;
import com.yaheen.pdaapp.util.nfc.AESUtils;
import com.yaheen.pdaapp.util.nfc.Converter;
import com.yaheen.pdaapp.util.nfc.NfcVUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;

import static com.yaheen.pdaapp.util.nfc.NFCUtils.ByteArrayToHexString;
import static com.yaheen.pdaapp.util.nfc.NFCUtils.toStringHex;

public class BindActivity extends BaseActivity {

    private final static String SCAN_ACTION = "scan.rcv.message";

    private ScanDevice sm;

    private Gson gson = new Gson();

    private ProgersssDialog dialog;

    private NfcB nfcbTag;
    private Tag tagFromIntent;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNdefExchangeFilters;

    private TextView tvFetch, tvFetchShow, tvScan, tvScanShow, tvCommit;

    private String url = "http://192.168.199.114:8080/shortlink/eai/updateLongLink.do";

//    private String updateUrl = "http://lyl.tunnel.echomod.cn/whnsubhekou/houseNumbers/updateFormDataManagement.do";

    private String updateUrl = "https://lhhk.020szsq.com/houseNumbers/updateFormDataManagement.do";

    private String ex_id = "", types = "";

    private String barcodeStr;

    //是否可以读芯片
    private boolean load = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        setTitleContent(R.string.bind_activity_title_content);

        tvScan = findViewById(R.id.tv_scan);
        tvFetch = findViewById(R.id.tv_fetch);
        tvCommit = findViewById(R.id.tv_commit);
        tvScanShow = findViewById(R.id.tv_scan_show);
        tvFetchShow = findViewById(R.id.tv_fetch_show);

        tvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sm.isScanOpened()) {
                    sm.startScan();
                }
            }
        });

        tvFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                load = true;
                tvFetch.setBackground(getResources().getDrawable(R.drawable.btn_gary_round));
            }
        });

        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog = new ProgersssDialog(BindActivity.this);
//                bind();
                update(tvScanShow.getText().toString(),tvFetchShow.getText().toString());
            }
        });

//        init();
        initNFC();
    }

    private void init() {
        sm = new ScanDevice();
        sm.setOutScanMode(0); //接收广播
    }

    private void initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);// 设备注册
        if (mNfcAdapter == null) {
            // 判断设备是否可用
            Toast.makeText(this, "该设备不支持NFC功能", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "请在系统设置中先启用NFC功能！", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndefDetected = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefDetected.addDataType("*/*");// text/plain
        } catch (IntentFilter.MalformedMimeTypeException e) {
        }

        IntentFilter td = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ttech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mNdefExchangeFilters = new IntentFilter[]{ndefDetected, ttech, td};
    }

    private void bind() {

        String slink = tvScanShow.getText().toString();
        String chip = tvFetchShow.getText().toString();

        if (TextUtils.isEmpty(slink)) {
            Toast.makeText(this, R.string.bind_activity_short_link_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(chip)) {
            Toast.makeText(this, R.string.bind_activity_chip_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams(url);
        params.addQueryStringParameter("key", "7zbQUBNY0XkEcUoushaJD7UcKyWkc91q");
        params.addQueryStringParameter("shortLinkCode", chip);
        params.addQueryStringParameter("note", "1994");
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                BindBean bindBean = gson.fromJson(result, BindBean.class);
                if (bindBean != null && bindBean.isResult()) {
                    Toast.makeText(BindActivity.this,
                            R.string.bind_activity_bind_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BindActivity.this,
                            R.string.bind_activity_bind_fail, Toast.LENGTH_SHORT).show();
                }
                clearData();
                dialog.dismiss();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void update(String chipId, String id) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", "402847fb63961f440163962e98450026");
        jsonObject.addProperty("chipId", "052802");

        RequestParams params = new RequestParams(updateUrl);
        params.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;");
        params.addQueryStringParameter("json", com.yaheen.pdaapp.util.nfc.Base64.encode(jsonObject.toString().getBytes()));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(BindActivity.this,"成功了",Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(BindActivity.this,"失败",Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barocode, 0, barocodelen);
            tvScanShow.setText(barcodeStr);
            sm.stopScan();
        }

    };

    private void clearData() {
        tvScanShow.setText("");
        tvFetchShow.setText("");
    }

    private void resolvIntent(Intent intent) {
        if (!load) {
            return;
        }
        String action = intent.getAction();
        //toast(action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            tagFromIntent = getIntent()
                    .getParcelableExtra(NfcAdapter.EXTRA_TAG);
            getresult(tagFromIntent);
//            Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(
//                    NfcAdapter.EXTRA_NDEF_MESSAGES);
//            NdefMessage[] msgs;
//            if (rawMsgs != null) {
//                msgs = new NdefMessage[rawMsgs.length];
//                for (int i = 0; i < rawMsgs.length; i++) {
//                    msgs[i] = (NdefMessage) rawMsgs[i];
//                }
//            } else {
//                // Unknown tag type
//                byte[] empty = new byte[]{};
//                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,   //NdefRecord.TNF_UNKNOWN
//                        empty, empty, empty);
//                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
//                msgs = new NdefMessage[]{msg};
//            }
//            setUpWebView(msgs);
            // dialog(ByteArrayToHexString(msgs[0].getRecords()[0].getPayload()));
            //	dialog(msgs[0].getRecords()[0].getPayload()));
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            // 处理该intent
            tagFromIntent = getIntent()
                    .getParcelableExtra(NfcAdapter.EXTRA_TAG);
            getresult(tagFromIntent);

        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            types = "Tag";
            tagFromIntent = getIntent()
                    .getParcelableExtra(NfcAdapter.EXTRA_TAG);
            getresult(tagFromIntent);
        }
    }

    void getresult(Tag tag) {
        ArrayList<String> list = new ArrayList<String>();
        types = "";
        for (String string : tag.getTechList()) {
            list.add(string);
            types += string.substring(string.lastIndexOf(".") + 1, string.length()) + ",";
        }
        types = types.substring(0, types.length() - 1);
        if (list.contains("android.nfc.tech.MifareUltralight")) {
            String str = readTagUltralight(tag);
            setNoteBody(str);
        } else if (list.contains("android.nfc.tech.NfcV")) {//完成
            NfcV tech = NfcV.get(tag);
            if (tech != null) {
                try {
                    tech.connect();
                    if (tech.isConnected()) {
                        NfcVUtil nfcVUtil = new NfcVUtil(tech);
                        String str = "测";
                        byte[] by = str.getBytes();
//                        nfcVUtil.writeBlock(5,by);
                        nfcVUtil.readOneBlock(2);
//                        byte[] tagUid = tag.getId();  // store tag UID for use in addressed commands
//
//                        int blockAddress = 0;
//                        int blocknum = 4;
//                        byte[] cmd = new byte[]{
//                                (byte) 0x22,  // FLAGS
//                                (byte) 0x23,  // 20-READ_SINGLE_BLOCK,23-所有块
//                                0, 0, 0, 0, 0, 0, 0, 0,
//                                (byte) (blockAddress & 0x0ff), (byte) (blocknum - 1 & 0x0ff)
//                        };
//                        System.arraycopy(tagUid, 0, cmd, 2, tagUid.length);  // paste tag UID into command
//
//                        byte[] response = tech.transceive(cmd);
                        tech.close();
//                        if (response != null) {
//                            setNoteBody(new String(response, Charset.forName("utf-8")));
//                        }
                    }
                } catch (IOException e) {

                }
            }
        } else if (list.contains("android.nfc.tech.NdefFormatable")) {
            NdefMessage[] messages = getNdefMessages(getIntent());
            byte[] payload = messages[0].getRecords()[0].getPayload();
            setNoteBody(new String(payload));
        }
    }

    NdefMessage[] getNdefMessages(Intent intent) {
        //读取nfc数据
        // Parse the intent

        NdefMessage[] msgs = null;
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            } else {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        record
                });
                msgs = new NdefMessage[]{
                        msg
                };
            }
        } else {
            finish();
        }
        return msgs;
    }

    public String readTagUltralight(Tag tag) {
        MifareUltralight mifare = MifareUltralight.get(tag);
        try {
            mifare.connect();
            StringBuffer sb = new StringBuffer();
            byte[] no10 = new byte[4];  //校验芯片
            byte[] no11 = new byte[4];  //数据块数量

            byte[] readTag = mifare.readPages(10);

            byte[] readCount = mifare.readPages(11);

            if (readTag.length >= 4) {

                for (int i = 0; i < 4; i++) {
                    no10[i] = readTag[i];
                }

                String tagStr = toStringHex(ByteArrayToHexString(no10));

                if (tagStr.equals("YAHN")) {
                    for (int i = 0; i < 4; i++) {
                        no11[i] = readCount[i];
                    }

                    String countStr = toStringHex(ByteArrayToHexString(no11));
                    int count = Integer.valueOf(countStr.trim());

                    for (int i = 12; i < (count); i++) {
                        byte[] readResult = mifare.readPages(i);
                        if (i % 4 == 0) {
                            if (i == count) {
                                byte[] codeEnd = new byte[4];
                                for (int j = 0; j < 4; j++) {
                                    codeEnd[j] = readResult[j];
                                }
                                sb.append(ByteArrayToHexString(codeEnd));
                            } else {
                                sb.append(ByteArrayToHexString(readResult));
                            }
                        }
                    }
                }
            }
            //  String  str=toStringHex(sb.toString());

            String finalResult = AESUtils.decryptToString(toStringHex(sb.toString()), "X2Am6tVLnwMMX8kVgdDk5w==");
//            String finalResult = toStringHex(sb.toString());

            return finalResult;

        } catch (IOException e) {
//            Log.e(TAG, "IOException while writing MifareUltralight message...", e);
            return "";
        } catch (Exception ee) {
//            Log.e(TAG, "IOException while writing MifareUltralight message...", ee);
            return "";
        } finally {
            if (mifare != null) {
                try {
                    mifare.close();
                } catch (IOException e) {
//                    Log.e(TAG, "Error closing tag...", e);
                }
            }
        }
    }

    private void setNoteBody(final String body) {

        if (!TextUtils.isEmpty(body)) {
            String[] bodys = body.trim().split("\\|");

//            if (bodys.length >= 2) {
//                chipNum = bodys[1];
//                String s = longLink + chipNum;
//                etLongLink.setText(s);
//            }
            load = false;
        } else {
            Toast.makeText(BindActivity.this, "读取芯片失败", Toast.LENGTH_SHORT).show();
        }
        tvFetch.setBackground(getResources().getDrawable(R.drawable.btn_red_round));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // NDEF exchange mode
        // 读取uidgetIntent()
        byte[] myNFCID = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
        ex_id = Converter.getHexString(myNFCID, myNFCID.length);
        // 读取uidgetIntent()
        setIntent(intent);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (sm != null) {
            sm.stopScan();
        }
        unregisterReceiver(mScanReceiver);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);

        if (mNfcAdapter == null || !mNfcAdapter.isEnabled()) {
            return;
        }

        //nfc自动读取芯片内容后调用activity的onResume
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, null, null);
            resolvIntent(getIntent());
        }
    }
}
