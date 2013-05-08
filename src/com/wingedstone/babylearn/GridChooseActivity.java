package com.wingedstone.babylearn;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class GridChooseActivity extends Activity implements AdapterView.OnItemClickListener{

	private GridChooseAdapter m_adapter;
	private GridView m_grid_view;
	
	private LoadThumbnailsTask m_load_task;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_gridchoose);
		 MyApplication app = (MyApplication)getApplicationContext();
		 m_adapter = new GridChooseAdapter(app.m_thumbnails, this);
		 m_grid_view = (GridView) findViewById(R.id.gridchoose);
		 m_grid_view.setAdapter(m_adapter);
		 m_grid_view.setOnItemClickListener(this);
		 getMoreThumbnails();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Thumbnails.Item item = (Thumbnails.Item) parent.getItemAtPosition(position);
		startCoverActivity(item.key);
	}
	
	public void getMoreThumbnails() {
		MyApplication app = (MyApplication)getApplicationContext();
		int start_point = app.m_thumbnails.getItemCount();
		m_load_task = new LoadThumbnailsTask();
		m_load_task.execute(start_point);
	}
	
	private void startCoverActivity(String key) {
		Intent intent = new Intent(this, CoverActivity.class);
		intent.putExtra(Configures.res_key, key);
		startActivity(intent);
	}
	
	private class LoadThumbnailsTask extends AsyncTask<Integer, Integer, JSONObject> {

		@Override
		protected JSONObject doInBackground(Integer... params) {
			int start_point = params[0];
			String uri = String.format(Configures.query_thumbnail_url, start_point);
			JSONObject response = HttpJsonClient.request(uri);
			return response;
		}
		
		protected void onPostExecute(JSONObject response) {
			if (response == null) {
				
			} else {
				MyApplication app = (MyApplication)getApplicationContext();
				app.m_thumbnails.addThumbnailsFromJson(response);
				GridChooseActivity.this.m_adapter.notifyDataSetChanged();
				GridChooseActivity.this.m_load_task = null;
			}
		}
		
	}
}
