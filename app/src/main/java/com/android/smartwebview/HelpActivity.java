package com.android.smartwebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;

public class HelpActivity extends Activity {

    private WebView webView;
    // Functions
   ConnectionDetector mConnectionDetector;
    public Boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_main);
        // Declaring mConnection Detector
        mConnectionDetector = new ConnectionDetector(getApplicationContext());


        webView = (WebView)findViewById(R.id.web);

        WebSettings webSettings = webView.getSettings();

			webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }
        });
        isConnected = mConnectionDetector.isConnectingToInternet();

        if(isConnected == false){
                webView.loadUrl("file:///android_asset/services.html");
            } else {
        loadMainUrl();
    }
  }
    private void loadMainUrl() {

webView.loadUrl("https://try-tolearn.blogspot.com/2019/09/premium-smartwebview.html");
            }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
