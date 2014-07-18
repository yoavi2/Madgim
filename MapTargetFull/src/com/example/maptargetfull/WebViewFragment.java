package com.example.maptargetfull;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends Fragment {

	public static String TAG = "webview";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		WebView webView = (WebView) inflater.inflate(R.layout.fragment_webview, container, false);

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		webView.getSettings().setUserAgentString("Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0");
		webView.loadUrl("http://www.mapa.co.il/general/searchresult_locked.asp?CurrMapTab=3&MapType=combination&ImagesOption=COMB_&CurrHeaderTab=2&RoutePoints=&UserEarthX=96120&UserEarthY=1086953&UserMapZoom=150000&LoadUserPosition=1&RouteType=2&PNGLayers=");
		return webView;

	}
}
