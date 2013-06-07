package com.wingedstone.babylearn;

import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class GridChooseActivity extends Activity implements AdapterView.OnItemClickListener{

	private GridChooseAdapter m_adapter;
	private PullToRefreshGridView m_grid_view;
	private LoadThumbnailsTask m_load_task;
	private boolean m_has_more = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_gridchoose);
		 MyApplication app = (MyApplication)getApplicationContext();
		 getActionBar().setLogo(R.drawable.ic_action_back);
		 getActionBar().setDisplayShowTitleEnabled(false);
		 getActionBar().setHomeButtonEnabled(true);
		 getActionBar().setDisplayShowCustomEnabled(true);
		 getActionBar().setCustomView(R.layout.actionbar_cover);
		 getActionBar().setTitle(R.string.grid_activity_title);
		 m_adapter = new GridChooseAdapter(app.m_thumbnails, this);
		 m_grid_view = (PullToRefreshGridView) findViewById(R.id.gridchoose);
		 m_grid_view.setMode(Mode.PULL_FROM_END);
		 m_grid_view.setAdapter(m_adapter);
		 m_grid_view.setOnItemClickListener(this);
		 m_grid_view.setOnRefreshListener(new OnRefreshListener<GridView>() {
			    @Override
			    public void onRefresh(PullToRefreshBase<GridView> refreshView) {
			        // Do work to refresh the list here.
			        getMoreThumbnails(false);
			    }
			});
		 getMoreThumbnails(true);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Thumbnails.Item item = (Thumbnails.Item) parent.getItemAtPosition(position);
		returnCoverActivity(item.key);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.grid, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	public void getMoreThumbnails(boolean first_time) {
		MyApplication app = (MyApplication)getApplicationContext();
		int start_point = app.m_thumbnails.getItemCount();
		if (m_has_more && (start_point == 0 || !first_time) ) {
			m_load_task = new LoadThumbnailsTask();
			if (first_time) {
				m_grid_view.setRefreshing();
			}
			m_load_task.execute(start_point);		
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_settings:
			Intent i = new Intent(this, SettingsActivity.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void returnCoverActivity(String key) {
		Intent intent = new Intent(this, CoverActivity.class);
		intent.putExtra(Configures.res_key, key);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private class LoadThumbnailsTask extends AsyncTask<Integer, Integer, JSONObject> {

		@Override
		protected JSONObject doInBackground(Integer... params) {
			int start_point = params[0];
			String uri = String.format(Configures.query_thumbnail_url, start_point);
			JSONObject response = HttpJsonClient.request(uri);
			return response;
		}
		
		@Override
		protected void onPostExecute(JSONObject response) {
			if (response == null) {  
				Utils.makeToastAboutNetworkError(GridChooseActivity.this);
			} else {
				MyApplication app = (MyApplication)getApplicationContext();
				app.m_thumbnails.addThumbnailsFromJson(response);
				GridChooseActivity.this.m_grid_view.onRefreshComplete();
				GridChooseActivity.this.m_adapter.notifyDataSetChanged();
				GridChooseActivity.this.m_grid_view.getRefreshableView().smoothScrollByOffset(1);
				GridChooseActivity.this.m_load_task = null;
			}
		}
		
	}
}
