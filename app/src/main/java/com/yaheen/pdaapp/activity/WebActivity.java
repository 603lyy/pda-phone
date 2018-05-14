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
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.yaheen.pdaapp.R;
import com.yaheen.pdaapp.widget.X5WebView;

public class WebActivity extends BaseActivity {

    //    private WebView web_content;

    private ViewGroup mViewParent;

    private X5WebView mWebView;

    private String url = "http://wxyq.020szsq.com/app/index.php?i=2&c=entry&m=ewei_shopv2&do=mobile&r=member.getDoorMessage";

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        mViewParent = (ViewGroup) findViewById(R.id.web_parent);

        init();
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

//        mWebView.setWebChromeClient(new WebChromeClient() {
//
//            @Override
//            public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
//                                       JsResult arg3) {
//                return super.onJsConfirm(arg0, arg1, arg2, arg3);
//            }
//
//            View myVideoView;
//            View myNormalView;
//            IX5WebChromeClient.CustomViewCallback callback;
//
//            @Override
//            public void onHideCustomView() {
//                if (callback != null) {
//                    callback.onCustomViewHidden();
//                    callback = null;
//                }
//                if (myVideoView != null) {
//                    ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
//                    viewGroup.removeView(myVideoView);
//                    viewGroup.addView(myNormalView);
//                }
//            }
//
//            @Override
//            public boolean onJsAlert(WebView arg0, String arg1, String arg2,
//                                     JsResult arg3) {
//                /**
//                 * write your own custom window alert
//                 */
//                return super.onJsAlert(null, arg1, arg2, arg3);
//            }
//        });

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
//        mWebView.loadUrl("file:///android_asset/web.html");
        mWebView.loadUrl(url);

        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

//    private void initWebContent() {
//        if (!TextUtils.isEmpty(url)) {
//            web_content.setWebViewClient(webViewClient);
//            web_content.setWebChromeClient(webChromeClient);
//            web_content.addJavascriptInterface(new WebJavaScriptProvider(this,this), "android");
//            web_content.getSettings().setJavaScriptEnabled(true);
//
//            web_content.getSettings().setDatabaseEnabled(true);
//            String dir =this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();//设置数据库路径
//            web_content.getSettings().setCacheMode(web_content.getSettings().LOAD_CACHE_ELSE_NETWORK);//本地缓存
//            web_content.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//支持JavaScriptEnabled
//            web_content.getSettings().setGeolocationEnabled(true);//定位
//            web_content.getSettings().setGeolocationDatabasePath(dir);//数据库
//            web_content.getSettings().setDomStorageEnabled(true);//缓存 （ 远程web数据的本地化存储）
//            web_content.getSettings().setBuiltInZoomControls(true);
//
//            web_content.getSettings().setBlockNetworkImage(false);//显示网络图像
////            web_content.getSettings().setLoadsImagesAutomatically(true);//显示网络图像
////            web_content.getSettings().setPluginState(WebSettings.PluginState.ON);//插件支持
//            web_content.getSettings().setSupportZoom(true);//设置是否支持变焦
//
//            web_content.loadUrl(url);
////            web_content.addJavascriptInterface(new showOrderProvider(getContext(), this), "android");
//        }
//    }

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
