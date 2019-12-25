package com.android.smartwebview.push;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.android.smartwebview.R;
import android.widget.Button;
import android.view.View;
import com.android.smartwebview.ConnectionDetector;
import android.webkit.WebSettings;
import android.widget.TextView;
import android.os.CountDownTimer;
import android.view.View.OnClickListener;

public class AdsActivity extends Activity  {
    private View offlineLayout;
    private WebView webView;
    private Button start;
    private TextView tv;
    // Functions
   ConnectionDetector mConnectionDetector;
    public Boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ads_main);
        // Declaring mConnection Detector
        mConnectionDetector = new ConnectionDetector(getApplicationContext());

        offlineLayout = findViewById(R.id.offline_layout);

        Button tryAgainButton = (Button) findViewById(R.id.try_again_button);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadBackUrl();
            }
        });

        start = (Button) findViewById(R.id.start);
        tv  = (TextView)findViewById(R.id.tv);
        tv.setText("10"); // startting from 10.

        final MyCounter timer = new MyCounter(10000,1000);
        start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				timer.start();
			}
		});

     Thread t = new Thread() {
      @Override
            public void run(){ 
          start.performClick();
     }
  };t.start();

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

webView.loadUrl("https://www.google.com");
            }

    private void loadBackUrl() {
        finish();
            }

    private class MyCounter extends CountDownTimer{

    	public MyCounter(long millisInFuture, long countDownInterval) {
    		super(millisInFuture, countDownInterval);
    	}

		@Override
		public void onFinish() {
    offlineLayout.setVisibility(View.VISIBLE);

        start.setVisibility(View.GONE);

        tv.setVisibility(View.GONE);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			tv.setText((millisUntilFinished/1000)+"");
			System.out.println("Timer  : " + (millisUntilFinished/1000));
	  	}
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