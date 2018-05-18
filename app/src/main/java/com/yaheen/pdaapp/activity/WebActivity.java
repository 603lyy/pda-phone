package com.yaheen.pdaapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yaheen.pdaapp.R;
import com.yaheen.pdaapp.util.ProgersssDialog;
import com.yaheen.pdaapp.widget.X5WebView;

public class WebActivity extends BaseActivity {

    //    private WebView web_content;

    private ViewGroup mViewParent;

    private X5WebView mWebView;

    private ProgersssDialog progersssDialog;

    private String url = "https://lyl.tunnel.echomod.cn/whnsubhekou/tool/toEntryMatch.do?shortLinkCode=";

    private String shortCode;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mViewParent = (ViewGroup) findViewById(R.id.web_parent);

        shortCode = getIntent().getStringExtra("shortCode");

        init();

        if (!TextUtils.isEmpty(shortCode)) {
            shortCode = shortCode.substring(shortCode.lastIndexOf("/") + 1);
            loadUrl();
        }
    }

    private void loadUrl() {
//        mWebView.loadUrl("file:///android_asset/web.html");
        mWebView.loadUrl(url + shortCode);
    }

    private void init() {
        mWebView = new X5WebView(this, null);

        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = ContextCompat.checkSelfPermission(WebActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WebActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                ActivityCompat.requestPermissions(WebActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                initWebViewSetting();
            }
        }
    }

    /**
     * init WebView
     */
    private void initWebViewSetting() {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);

        //手机屏幕适配
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setUseWideViewPort(true);

        //禁止放大
        webSetting.setBuiltInZoomControls(false);
        webSetting.setSupportZoom(false);
        webSetting.setDisplayZoomControls(false);

        //启用数据库
        webSetting.setDatabaseEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);//支持JavaScriptEnabled
        String dir = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        //启用地理定位
        webSetting.setGeolocationEnabled(true);
        //设置定位的数据库路径
        webSetting.setGeolocationDatabasePath(dir);
        //最重要的方法，一定要设置，这就是出不来的主要原因
        webSetting.setDomStorageEnabled(true);

        mWebView.setWebChromeClient(webChromeClient);

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        initWebViewSetting();
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private WebChromeClient webChromeClient = new WebChromeClient() {

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String s, GeolocationPermissionsCallback geolocationPermissionsCallback) {
            geolocationPermissionsCallback.invoke(s, true, true);
            super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback);
        }
    };
}
