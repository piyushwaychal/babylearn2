package com.wingedstone.babylearn;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_settings);
		Button clear_cache = (Button) findViewById(R.id.setting_clear_cache);
		clear_cache.setOnClickListener(this);
		Button about = (Button) findViewById(R.id.settings_about);
		about.setOnClickListener(this);
		
		ActionBar ab = getActionBar();
		ab.setCustomView(R.layout.actionbar_title_and_back);
		ab.setDisplayShowCustomEnabled(true);
		ab.setDisplayShowTitleEnabled(false);
		ab.setDisplayShowHomeEnabled(false);
		View ab_root = ab.getCustomView();
		ImageButton back = (ImageButton) ab_root.findViewById(R.id.action_back);
		back.setOnClickListener(this);
		
		TextView title = (TextView) ab_root.findViewById(R.id.action_title);
		title.setText(R.string.settings_title);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_back:
			finish();
			break;
		case R.id.setting_clear_cache:
			String res_dir = ResourceFileManager.getResourceDirectory(this);
			String cache_dir = ResourceFileManager.getCacheDirectory(this);
			new ClearTask().execute(res_dir, cache_dir);
			break;
		case R.id.settings_about:
			Intent i = new Intent(this, AboutActivity.class	);
			startActivity(i);
			break;
		}
		
	}

	private class ClearTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try{
				for (String path : params) {
					File target_dir = new File(path);
					if(target_dir.isDirectory()) {
						String[] children = target_dir.list();
						for (String child : children) {
							new File(target_dir, child).delete();
						}
					}
				}
			} catch (Exception e) {
				return false;
			}
			
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			String result_str = null;
			if (result) {
				result_str = getResources().getString(R.string.clear_cache_success);
			}
			else {
				result_str = getResources().getString(R.string.clear_cache_fail);
			}
			Toast.makeText(SettingsActivity.this, result_str, Toast.LENGTH_SHORT).show();
		}
		
	}
	
}
