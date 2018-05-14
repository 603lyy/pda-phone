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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yaheen.pdaapp.R;
import com.yaheen.pdaapp.util.ProgersssDialog;
import com.yaheen.pdaapp.util.nfc.AESUtils;
import com.yaheen.pdaapp.util.nfc.Converter;
import com.yaheen.pdaapp.util.nfc.NfcVUtil;

import java.io.IOException;
import java.util.ArrayList;

import static com.yaheen.pdaapp.util.nfc.NFCUtils.ByteArrayToHexString;
import static com.yaheen.pdaapp.util.nfc.NFCUtils.toStringHex;

public class ReportActivity extends BaseActivity {

    private final static String SCAN_ACTION = "scan.rcv.message";

    private ScanDevice sm;

    private Gson gson = new Gson();

    private ProgersssDialog dialog;

    private NfcB nfcbTag;
    private Tag tagFromIntent;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mNfcPendingIntent;
    private IntentFilter[] mNdefExchangeFilters;

    private TextView tvScan, tvAddress, tvFetch, tvFetchShow, tvCommit;

    private LinearLayout llBack;

    private String ex_id = "", types = "";

    private String barcodeStr;

    //是否可以读芯片
    private boolean load = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setTitleContent(R.string.report_title_content);

        llBack = findViewById(R.id.back);
        tvScan = findViewById(R.id.tv_scan);
        tvFetch = findViewById(R.id.tv_fetch);
        tvCommit = findViewById(R.id.tv_commit);
        tvAddress = findViewById(R.id.tv_address);
        tvFetchShow = findViewById(R.id.tv_fetch_show);

        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

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

        init();
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

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barocode, 0, barocodelen);
            tvAddress.setText(barcodeStr);
            sm.stopScan();
        }

    };

    private void clearData() {
        tvAddress.setText("");
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
                        String str = "";
                        byte[] by = str.getBytes();
//                        nfcVUtil.writeBlock(5,by);
                        str = nfcVUtil.readOneBlock(2);
                        tech.close();
//                        if (response != null) {
                            setNoteBody(str);
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
            tvFetchShow.setText(body);
            load = false;
        } else {
            Toast.makeText(ReportActivity.this, "读取芯片失败", Toast.LENGTH_SHORT).show();
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
