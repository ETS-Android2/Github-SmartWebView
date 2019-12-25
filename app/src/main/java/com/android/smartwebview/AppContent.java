package com.android.smartwebview;

public class AppContent {
	
// Type of the Application
	
/*[1] Offline Application*/	
							public static final Boolean offline_app = false;
/*[2] One Page Application*/
							public static final Boolean one_page_app = false;
/*[3] One Page Application*/
							public static final Boolean fullscreen_webview = false;
/*[4] show progress bar in app*/
							public static boolean ASWP_PBAR        = true;			
/*[5] whether the loading webpages are offline or online*/
							public static boolean ASWP_OFFLINE     = false;		
/*[6] Set to true to clear the WebView cache on each app startup and do not use cached versions of your web app/website*/
         public static final boolean CLEAR_CACHE_ON_STARTUP = true;
/*[7] Set to "true" to use local "assets/index.html" file instead of URL*/
         public static final boolean USE_LOCAL_HTML_FOLDER = false;
/*[8]*/
         public	 boolean webUrl_PULLFRESH	 = true;		// pull refresh current url
/*[9]Load a webpage when no internet connection was found (must be in assets). Leave empty to show dialog*/
         public static String NO_CONNECTION_PAGE = "";

/*[10]All urls that should always be opened outside the WebView and in the browser, download manager, or their respective app*/
         public static final String[] OPEN_OUTSIDE_WEBVIEW = new String[]{"market://", "play.google.com", "plus.google.com", "mailto:", "tel:", "vid:", "geo:", "whatsapp:", "sms:", "intent://"};

/*[11]Defines a set of urls/patterns that should exlusively load inside the webview. All other urls are loaded outside the WebView. Ignored if no urls are defined*/
         public static final String[] OPEN_ALL_OUTSIDE_EXCEPT = new String[]{};




//===================================APP ADMIN and URLS=======================================

 
/*[A1]Mobile website or web based mobile app URL*/
  	    public static final String mobile_website_url = "https://try-tolearn.blogspot.com/2019/09/premium-smartwebview.html"; /*<<=========Important
/*[A2]if it is null, browser's URL will be shared*/
       public static final String share_url = "null";
/*[A3]contact page URL*/
       public static final String contact_url = "file:///android_asset/about.html"; /*<<=========Important
/*[A4]about page URL*/
       public static final String about_url = "file:///android_asset/about.html"; /*<<=========Important

//========================================MESSAGES=============================================

/*[B1]Welcome message shows only one time*/
     		public static final String welcome_url = "WELCOME TO Your App"; /*<<=========Important
/*[B2]Connection issues message*/
       public static final String connection_error_message = "Connection Error! please try later";
/*[B3]Message shows before the shared link*/
   			public static final String share_message = "Check Out this link: ";
/*[B4]Invalid mobile number error message*/
		    public static final String invalid_mobile_number_error_message = "Please enter valid mobile number";
/*[B5]Loading message*/
	     	public static final String loading_message = "Loading...";
//==========================================RateME================================================	

/*[D1]Application Name( Used for Rating )*/
     		public static final String application_name = "Smart WebView"; /*<<=========Important
/*[D2]Package Name(Example com.example.appname)*/
     	public static final String package_name  = "com.android.smartwebview";  /*<<=========Important 
/*[D3]Number of lunches before Rate me message shows*/
     	public static final int number_of_uses_before_launching_the_rate_dialog = 5;
/*[D4]To deactivate RateDialog change "yes" to "no"*/
    	public static final boolean rate_dialog_active = true;

	/* -- SECURITY VARIABLES -- */

	static boolean ASWP_CERT_VERIFICATION = true;		// verify whether HTTPS port needs certificate verification
    //AdMob options
    //set to true if you want to display AdMob banner ads (set AdMob IDs in the strings.xml file)
    public static final boolean SHOW_BANNER_AD = false;

    //set to true if you want to display AdMob fullscreen interstitial ads after X website clicks (set AdMob IDs in the strings.xml file)
    public static final boolean SHOW_FULL_SCREEN_AD = true;

    //X website clicks for AdMob interstitial ads (set AdMob IDs in the strings.xml file)
    public static final int SHOW_AD_AFTER_X = 7;

}
