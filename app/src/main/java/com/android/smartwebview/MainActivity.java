package com.android.smartwebview;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.annotation.TargetApi;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.graphics.Color;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.webkit.WebSettings.PluginState;
import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.FrameLayout;
import com.android.smartwebview.push.AdsActivity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.view.WindowManager;
import android.webkit.WebViewClient;
import android.app.DownloadManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebResourceRequest;
import android.webkit.SslErrorHandler;
import android.widget.Toast;
import android.net.Uri;
import android.os.Environment;
import android.util.Patterns;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.SearchManager;
import android.view.MenuInflater;
import android.util.Log;
import android.app.Fragment;
import android.support.v7.widget.SearchView;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.webkit.WebSettings.RenderPriority;
import android.widget.LinearLayout;
import android.webkit.GeolocationPermissions;
import android.widget.ProgressBar;
import com.android.smartwebview.Rateme.AppRater;
import java.net.HttpURLConnection;
import java.net.URL;
import android.net.http.SslError;
import android.widget.ScrollView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.content.res.Resources.NotFoundException;
import android.webkit.WebChromeClient.FileChooserParams;
import java.io.File;
import java.io.IOException;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.PermissionRequest;
import android.view.ContextMenu;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.content.ActivityNotFoundException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private View offlineLayout;
    private View errorLayout;
    private AlertDialog noConnectionDialog;
   	private static final int API = android.os.Build.VERSION.SDK_INT;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    public int webviewCount = 0;
    public static final int REQUEST_SELECT_FILE = 100;
    AppCompatActivity WEB_ACTIVITY;
    // Functions
   ConnectionDetector mConnectionDetector;
	// security variables
	static boolean ASWP_CERT_VERIFICATION 	= AppContent.ASWP_CERT_VERIFICATION;

    // UI Variables
    protected WebView mWebView;
    protected Thread t;
    protected Button mButton;
    protected ProgressBar mProgressBar;
        	int mWeberror_counter = 0;
    public Boolean fullscreen_webview = AppContent.fullscreen_webview;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String deepLinkingURL;
    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private String mCM;
    private final static int FCR = 1;
    private boolean multiple_files = true;
    public static final int MULTIPLE_PERMISSIONS = 10;
    // mWebView Controls
    String appName = AppContent.application_name;
    String webUrl = AppContent.mobile_website_url;
    String CURR_URL = webUrl;
    String shareMessage = AppContent.share_message;
    String shareUrl = AppContent.share_url;
    String contact_url = AppContent.contact_url;
    String about_url = AppContent.about_url;
	Boolean ASWP_PBAR		= AppContent.ASWP_PBAR;
	Boolean ASWP_OFFLINE		= AppContent.ASWP_OFFLINE;
    Boolean rateActive = AppContent.rate_dialog_active;
    String connection_error_message = AppContent.connection_error_message;
// mWebView Settings
	public Boolean mWebView_cache = false;
	public Boolean mWebView_zoom = false;
    // Geo-location
    Boolean geoLocationValue = false;
    // Logs
    String tag = "state";
    // Others
    public Boolean isConnected = false;
    protected String last_link = "#null";
    protected String link_bank = "#null";

    private static final int MY_PERMISSION_REQUEST_CODE = 123;

    private Context mContext;
    private Activity mActivity;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the application context
        mContext = getApplicationContext();
        mActivity = MainActivity.this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Hiding the Action Bar for different android versions
        hideActionBar();

        webviewCount = 0;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
						Intent ine = new Intent(getApplicationContext(), HelpActivity.class);
  ine.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(ine);
            }
          });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Declaring mConnection Detector
        mConnectionDetector = new ConnectionDetector(getApplicationContext());

        // Restore LastLink
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        last_link = pref.getString("lastlink", "#null");

        mySwipeRefreshLayout = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);
       mySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { 
        mWebView.stopLoading();
        mWebView.reload();
 
                /**Set Refreshing to True**/ 
                mySwipeRefreshLayout.setRefreshing(true);
        } 
    }); 

        // Getting WebView Ready
        mWebView = (WebView) findViewById(R.id.mWebView);

        offlineLayout = findViewById(R.id.offline_layout);

        errorLayout = findViewById(R.id.error_layout);

        mProgressBar = (ProgressBar) findViewById(R.id.mProgressBar);

        Button tryAgainButton = (Button) findViewById(R.id.try_again_button);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMainUrl();

            }
        });

        Button LoadHomePage = (Button) findViewById(R.id.loadhomepage_button);
        LoadHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadHomepage();

            }
        });

        registerForContextMenu(mWebView);
        WebSettings webSettings = mWebView.getSettings();
		webSettings.setDisplayZoomControls(false);
		webSettings.setBuiltInZoomControls(false);
   webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSettings.setSupportMultipleWindows(false);
		webSettings.setEnableSmoothTransition(true);
		if (API < Build.VERSION_CODES.JELLY_BEAN_MR2) {
			//noinspection deprecation
			webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
		}
		if (API < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			//noinspection deprecation
			webSettings.setEnableSmoothTransition(true);
		}
		if (API > Build.VERSION_CODES.JELLY_BEAN) {
			webSettings.setMediaPlaybackRequiresUserGesture(true);
		}
   if (API >= Build.VERSION_CODES.LOLLIPOP) {
			webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
		}

			webSettings.setDomStorageEnabled(true);
			webSettings.setAppCacheEnabled(true);
    webSettings.setCacheMode(webSettings.LOAD_NO_CACHE);
    webSettings.setLoadWithOverviewMode(true);
   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
} else {
    mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
}
			webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
			webSettings.setDatabaseEnabled(true);
			webSettings.setGeolocationEnabled(false);
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(false);
		webSettings.setDisplayZoomControls(false);
		webSettings.setAllowContentAccess(true);
		webSettings.setAllowFileAccess(true);
		if (API >= Build.VERSION_CODES.JELLY_BEAN) {
			webSettings.setAllowFileAccessFromFileURLs(false);
			webSettings.setAllowUniversalAccessFromFileURLs(false);
		}
		webSettings.setJavaScriptEnabled(true);


		webSettings.setAppCacheEnabled(true);
	    webSettings.setLoadsImagesAutomatically(true);
   mWebView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				return true;
			}
		});
   mWebView.setHapticFeedbackEnabled(false);
        // Check permission for write external storage
        checkPermission();

		// download listener
		mWebView.setDownloadListener(new DownloadListener() {
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(
                                    mActivity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSION_REQUEST_CODE
                            );
				}else {
					DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

					request.setMimeType(mimeType);
					String cookies = CookieManager.getInstance().getCookie(url);
					request.addRequestHeader("cookie", cookies);
					request.addRequestHeader("User-Agent", userAgent);
					request.setDescription(getString(R.string.dl_downloading));
					request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
					request.allowScanningByMediaScanner();
					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
					request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
					DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
					assert dm != null;
					dm.enqueue(request);
					Toast.makeText(getApplicationContext(), getString(R.string.dl_downloading2), Toast.LENGTH_LONG).show();
		  		}
     }
		});
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mWebView.setVerticalScrollBarEnabled(false);


        mWebView.setWebViewClient(new WebViewClient() {
     public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        //For android below API 23
		@Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                errorLayout.setVisibility(View.VISIBLE);
        }

    	@Override
    	public void onPageStarted(WebView view, String url, Bitmap favicon) {

        isConnected = mConnectionDetector.isConnectingToInternet();

        if(isConnected == false){
                errorLayout.setVisibility(View.VISIBLE);

      }

mProgressBar.setVisibility(View.VISIBLE);
    		// TODO Auto-generated method stub
    		super.onPageStarted(view, url, favicon);
    	}

        @Override
        public void onPageFinished(WebView view, String url) {
        setTitle(view.getTitle());
            mProgressBar.setVisibility(View.GONE);
        isConnected = mConnectionDetector.isConnectingToInternet();

        if(isConnected == false){
                errorLayout.setVisibility(View.VISIBLE);

      } else { 
                errorLayout.setVisibility(View.GONE);
      }
                offlineLayout.setVisibility(View.GONE);
                mySwipeRefreshLayout.setRefreshing(false);
            super.onPageFinished(view, url);

   }

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        	if(ASWP_CERT_VERIFICATION) {
				super.onReceivedSslError(view, handler, error);
			} else {
        		handler.proceed(); // Ignore SSL certificate errors
	      		}
	     	}
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (urlShouldOpenExternally(url)) {
            // Load new URL Don't override URL Link
            try {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            } catch(ActivityNotFoundException e) {
                if (url.startsWith("intent://")) {
                    view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url.replace("intent://", "http://"))));
                } else {
        Toast.makeText(getApplicationContext(), getString(R.string.no_app_message), Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        } else if (url.endsWith(".mp4") || url.endsWith(".avi")
                || url.endsWith(".flv")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/mp4");
                view.getContext().startActivity(intent);
            } catch (Exception e) {
                // error
            }

            return true;
        } else if (url.endsWith(".mp3") || url.endsWith(".wav")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "audio/mp3");
                view.getContext().startActivity(intent);
            } catch (Exception e) {
                // error
            }

            return true;
        }

        // Return true to override url loading (In this case do
        // nothing).
        return super.shouldOverrideUrlLoading(view, url);
    }
    /**
     * Check if we need an internet connection to load an url, and if we a connection, if it is present
     * @param loadUrl The url we are trying to load
     * @param showDialog If a dialog should be shown if a connection is required, but not found
     * @return If we can load the url (based on the fact if we need an connection for it, and if the connection, if needed, is present0
     */
    public boolean hasConnectivity(String loadUrl, boolean showDialog) {
        boolean enabled = true;

        if (loadUrl.startsWith("file:///android_asset")){
            return true;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) mActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if ((info == null || !info.isConnected() || !info.isAvailable())) {

            enabled = false;

            if (showDialog){

                if (AppContent.NO_CONNECTION_PAGE.length() > 0 && AppContent.NO_CONNECTION_PAGE.startsWith("file:///android_asset")) {
                    mWebView.loadUrl(AppContent.NO_CONNECTION_PAGE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage(mActivity.getString(R.string.no_connection_title));
                    builder.setCancelable(false);
                    builder.setNeutralButton(R.string.ok, null);
                    builder.setTitle(mActivity.getString(R.string.error));
                    builder.create().show();
                }
            }
        }
        return enabled;
      }
    /**
     * Check if an url should load externally and not in the WebView
     * @param url The url that we would like to load
     * @return If it should be loaded inside or outside the WebView
     */
    public boolean urlShouldOpenExternally(String url){

        /*
         * If there is a set of urls defined that may only open inside the WebView and
         * the passed url does not match to one of these urls, it should be opened outside the WebView
         */
        if (AppContent.OPEN_ALL_OUTSIDE_EXCEPT.length > 0) {
            for (String pattern : AppContent.OPEN_ALL_OUTSIDE_EXCEPT) {
                if (!url.contains(pattern))
                    return true;
            }
        }

        /*
         * If there is an url defined that should open outside the WebView, these urls will be loaded outside the webview
         */
        for (String pattern : AppContent.OPEN_OUTSIDE_WEBVIEW){
            if (url.contains(pattern))
                return true;
        }

        return false;
    }
  });

        mWebView.setWebChromeClient(new WebChromeClient() {
        // GeoLocation Code
        @Override
        public void onGeolocationPermissionsShowPrompt(final String origin,
                                                       final GeolocationPermissions.Callback callback) {

            //===>> Logging
            Log.d(tag, "In the OnGeoLocationPermissions");

            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            // Retrieve GeoLocation Setting
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            geoLocationValue = pref.getBoolean("geoLocation", false);

            // GeoLocation Dialog
            if (geoLocationValue == false) {
                AlertDialog.Builder geoAlertDialog = new AlertDialog.Builder(MainActivity.this);
                geoAlertDialog.setIcon(R.drawable.ic_launcher)
                        .setTitle(appName)
                        .setMessage("Would Like to Use Your Current Location")
                        .setPositiveButton("Ok", new OnClickListener() {

                            // OnClick
                            public void onClick(DialogInterface dialog, int id) {
                                callback.invoke(origin, true, false);
                                 Toast.makeText(getApplicationContext(), getString(R.string.went_wrong), Toast.LENGTH_SHORT).show();
                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
                                Editor editor = pref.edit();
                                editor.putBoolean("geoLocation", true);
                                editor.commit();
                                Log.d(tag, "GeoLocation = true");
                                mProgressBar.setVisibility(View.VISIBLE);
                                refresh();
                            }

                        }).setNegativeButton("Don't Allow", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        callback.invoke(origin, false, false);
                    }
                }).create();

                // GeoLocation Dialog Show
                geoAlertDialog.show();

            } else {
                callback.invoke(origin, true, false);
            }
        }
            //Getting webview rendering progress
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (ASWP_PBAR) {
            if (newProgress < 100) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(view.getProgress());
            } else {
                mProgressBar.setProgress(0);
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUM = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                if (multiple_files && Build.VERSION.SDK_INT >= 18) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                }
                startActivityForResult(Intent.createChooser(i, "Image Chooser"), FCR);
            }
            @SuppressLint("InlinedApi")
            public boolean onShowFileChooser(WebView WebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                
                    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

                    //checking for storage permission to write images for upload
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, perms, FCR);

                        //checking for WRITE_EXTERNAL_STORAGE permission
                    } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, FCR);

                        //checking for CAMERA permissions
                    } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, FCR);
                    }
                    if (mUMA != null) {
                        mUMA.onReceiveValue(null);
                    }
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        } catch (IOException ex) {
                            Log.e(TAG, "Image file creation failed", ex);
                        }
                        if (photoFile != null) {
                            mCM = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("image/*");
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    }
                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }

                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, FCR);
                    return true;
            }
        });

                loadHomepage();

        // RateApp
        if (rateActive == true) {
            AppRater.app_rate(this);
        }
      }


    public void onStart(){

        super.onStart();
        Log.d(tag, "In the onStart() event");

        }


    public void onRestart(){
        super.onRestart();
        Log.d(tag, "In the onRestart() event");
    }

    public void onResume(){
        super.onResume();
        Log.d(tag, "In the onResume() event");
    }

    public void onPause(){
        super.onPause();
        Log.d(tag, "In the onPause() event");
        // Stop mWebView
        stopEveryThing();
    }

    public void onStop(){

        super.onStop();
        Log.d(tag, "In the onStop() event");

        // Stop mWebView
        stopEveryThing();

    }

	public void onDestroy(){
        super.onDestroy();

        // Stop mWebView
     stopEveryThing();
	}

    protected void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    // show an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setMessage("Write external storage permission is required.");
                    builder.setTitle("Please grant permission");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    mActivity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSION_REQUEST_CODE
                            );
                        }
                    });
                    builder.setNeutralButton("Cancel",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            mActivity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_CODE
                    );
                }
            }else {
                // Permission already granted
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case MY_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Permission granted
                }else {
                    // Permission denied
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(Build.VERSION.SDK_INT >= 21){

            Uri[] results = null;
            //checking if response is positive
            if(resultCode == Activity.RESULT_OK){
                if(requestCode == FCR){
                    if(null == mUMA){
                        return;
                    }
                    if(intent == null || intent.getData() == null){
                        if(mCM != null){
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    }else{
                        String dataString = intent.getDataString();
                        if(dataString != null){
                            results = new Uri[]{Uri.parse(dataString)};
                        } else {
                            if(multiple_files) {
                                if (intent.getClipData() != null) {
                                    final int numSelectedFiles = intent.getClipData().getItemCount();
                                    results = new Uri[numSelectedFiles];
                                    for (int i = 0; i < numSelectedFiles; i++) {
                                        results[i] = intent.getClipData().getItemAt(i).getUri();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                mUMA.onReceiveValue(null);
                mUMA = null;
            }
            if(results!=null)
                mUMA.onReceiveValue(results);
            mUMA = null;
        }
        else{
            if(requestCode == FCR){
                if(null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    // Open previous opened link from history on webview when back button pressed
     
    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }


    private void loadMainUrl() {
        isConnected = mConnectionDetector.isConnectingToInternet();

        if(isConnected == false){

			Toast.makeText(getApplicationContext(), getString(R.string.check_connection), Toast.LENGTH_SHORT).show();

            } else {

                refresh();
            }
         }

	private void loadHomepage(){
        isConnected = mConnectionDetector.isConnectingToInternet();

        if(isConnected == false){
                offlineLayout.setVisibility(View.VISIBLE);

			Toast.makeText(getApplicationContext(), getString(R.string.check_connection), Toast.LENGTH_SHORT).show();

            } else {

		     	mWebView.loadUrl(webUrl);
                errorLayout.setVisibility(View.GONE);
            }
	     	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setQueryHint("search here");
		searchView.setIconified(true);
		searchView.setIconifiedByDefault(true);
		searchView.clearFocus();

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            String url = "https://www.google.com/search?q="+query;
            mWebView.loadUrl(url);
            searchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    });
    return true;
  }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if( id == R.id.share){
            sharepost();
            return true;
        }else if (id == R.id.refresh){
            mProgressBar.setVisibility(View.VISIBLE);

            refresh();
            return true;
        }else if (id == R.id.home){

mProgressBar.setVisibility(View.VISIBLE);

            mWebView.loadUrl(webUrl);
            return true;
        }else if (id == R.id.contact) {

mProgressBar.setVisibility(View.VISIBLE);

            mWebView.loadUrl(contact_url);
            return true;
        }else if (id == R.id.about) {

mProgressBar.setVisibility(View.VISIBLE);

            mWebView.loadUrl(about_url);
            return true;
        }else if (id == R.id.close) {
            exitDialog();
            return true;
        }else if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Share Page
    private void sharepost() {
        Intent shareintent = new Intent(Intent.ACTION_SEND);
        shareintent.setType("text/plain");
        String xshare = "link";

        if (shareUrl != "null") {
            xshare = shareMessage + shareUrl;
        } else {
            xshare = shareMessage + mWebView.getUrl();
        }

        shareintent.putExtra(Intent.EXTRA_TEXT, xshare);
        startActivity(Intent.createChooser(shareintent, "How do you want to share?"));
    }

    // Exit Dialog
    public void exitDialog() {
        Log.d(tag, "In the exitDialog()");
        AlertDialog.Builder exitAlertDialog = new AlertDialog.Builder(MainActivity.this);

        exitAlertDialog.setTitle("Confirm Exit")
                .setMessage("Do you want to quit?")
                .setPositiveButton("Okay", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //===>> Logging
                        Log.d(tag, "Exit Dialog = true");
                        stopEveryThing();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //===>> Logging
                        Log.d(tag, "Exit Dialog = false");
                    }
                }).create();

        exitAlertDialog.show();
    }

    // ConnectionDetector Function
    @SuppressLint("NewApi")
    public void hideActionBar(){
        if (Build.VERSION.SDK_INT < 16 && fullscreen_webview == true) {
            // Hide the Action Bar on Android 4.0 and Lower
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else if (fullscreen_webview == true) {
            // Hide the Action Bar on Android 4.1 and Higher
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            android.app.ActionBar actionBar = getActionBar();
            actionBar.hide();
        }
    }


    // Stop mWebView
    public void stopEveryThing() {
        mWebView.clearAnimation();
        mWebView.clearDisappearingChildren();
        mWebView.stopLoading();
    }

    // Refresh
    public void refresh() {
        mWebView.stopLoading();
        mWebView.reload();
    }

    private class MyWebChromeClient extends WebChromeClient {

        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        MyWebChromeClient() {
        }


        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846);
        }

        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

        @Override
        public void onPermissionRequest(final PermissionRequest request) {
            request.grant(request.getResources());

        }

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.navigation_item) {

mProgressBar.setVisibility(View.VISIBLE);

            mWebView.loadUrl(webUrl);
        } else if (id == R.id.nav_intro) {
            mWebView.loadUrl("file:///android_asset/intro.html");
        } else if (id == R.id.nav_products) {
            mWebView.loadUrl("file:///android_asset/products.html");
        } else if (id == R.id.nav_services) {
            mWebView.loadUrl("file:///android_asset/services.html");
        } else if (id == R.id.nav_about) {
            mWebView.loadUrl("file:///android_asset/about.html");
        } else if (id == R.id.nav_Website) {
            String url = "https://try-tolearn.blogspot.com/2019/09/premium-smartwebview.html";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else if (id == R.id.nav_call) {
            String phone = "+91 999 99 999 99";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        } else if (id == R.id.nav_RateUs) {
            String url = "https://play.google.com/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
