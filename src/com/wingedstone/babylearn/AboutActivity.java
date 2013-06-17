package com.wingedstone.babylearn;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

public class AboutActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActionBar ab = getActionBar();
		ab.setCustomView(R.layout.actionbar_title_and_back);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		View ab_root = ab.getCustomView();
		ImageButton back = (ImageButton) ab_root.findViewById(R.id.action_back);
		back.setOnClickListener(this);
		
		TextView title = (TextView) ab_root.findViewById(R.id.action_title);
		title.setText(R.string.settings_about_us);
		
		WebView webview = new WebView(this);
		setContentView(webview);
		webview.loadUrl("file:///android_asset/html/about.html");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			finish();
			break;
		}
	}

}
