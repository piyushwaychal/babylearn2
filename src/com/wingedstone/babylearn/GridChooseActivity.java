package com.wingedstone.babylearn;

import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

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
	private PullToRefreshGridView m_grid_view;
	private LoadThumbnailsTask m_load_task;
	private boolean m_has_more = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_gridchoose);
		 MyApplication app = (MyApplication)getApplicationContext();
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
				
			} else {
				MyApplication app = (MyApplication)getApplicationContext();
				int cur_position = app.m_thumbnails.getItemCount();
				app.m_thumbnails.addThumbnailsFromJson(response);
				GridChooseActivity.this.m_grid_view.onRefreshComplete();
				GridChooseActivity.this.m_adapter.notifyDataSetChanged();
				GridChooseActivity.this.m_grid_view.getRefreshableView().smoothScrollByOffset(1);
				GridChooseActivity.this.m_load_task = null;
			}
		}
		
	}
}
