package com.wingedstone.babylearn;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CoverActivity extends Activity implements OnClickListener {
	private AnimationDrawable m_animation;
	private ImageView m_animation_holder;
	private ImageButton m_enter_button;
	//private ProgressBar m_spinner;
	private ChangeResourceTask m_change_task = null;
	private ResourceFile m_resource_file = null;
	private MediaPlayer m_sound_player = null;
	
	private ImageView m_download_animation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// enable up button
		setContentView(R.layout.activity_cover);
		ActionBar action_bar = getActionBar();
		action_bar.setDisplayShowHomeEnabled(false);
		action_bar.setDisplayShowHomeEnabled(true);
		action_bar.setLogo(R.drawable.ic_action_logo);
		m_animation = new AnimationDrawable();
		m_animation.setOneShot(true);
		m_animation_holder = (ImageView)findViewById(R.id.MainImageView);
		m_animation_holder.setOnClickListener(this);
		m_enter_button = (ImageButton) findViewById(R.id.MainGoButton);
		m_enter_button.setOnClickListener(this);
		m_enter_button.setClickable(false);
		
		m_download_animation = (ImageView) findViewById(R.id.downloading_animation);
		AnimationDrawable ad = (AnimationDrawable) m_download_animation.getDrawable();
		ad.start();
		
		Intent intent = getIntent();
		String key = intent.getStringExtra(Configures.res_key);
		Log.v("zhangge", key == null ? "null": key);
		changeResource(key);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.MainGoButton:
			if (m_resource_file != null) {
				startSlideActivity();				
			}
			break;
		case R.id.MainImageView:
			//start animation and sound
			if(!m_animation.isRunning()) {
				startAnimation();				
			}
			startSound();
			break;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	private void startSlideActivity() {
		Intent intent = new Intent(this, SlidePictureActivity.class);
		intent.putExtra(Configures.intent_key_name, m_resource_file.getKey());
		startActivity(intent);
	}
	
	private void startChooseActivity() {
		Intent intent = new Intent(this, GridChooseActivity.class);
		startActivityForResult(intent, Configures.grid_choose_request_code);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == Configures.grid_choose_request_code) {
			if (resultCode == RESULT_OK) {
				String key = intent.getStringExtra(Configures.res_key);
				Log.v("zhangge", key == null ? "null": key);
				changeResource(key);
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startChooseActivity();
			return true;
		case R.id.action_grid:
			startChooseActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void startAnimation() {
		m_animation.start();
	}
	
	private void changeAnimation(ArrayList<Bitmap> bitmaps) {
		if(m_animation.isRunning()) {
			m_animation.stop();
		}
		Log.v("zhangge", "start new animation");
		//m_animation_holder.setImageDrawable(null);
		//m_animation_holder.setImageBitmap(bitmaps.get(0));
		m_animation = new AnimationDrawable();
		for (Bitmap bitmap : bitmaps) {
			m_animation.addFrame(new BitmapDrawable(getResources(), bitmap), 200);				
		}
		m_animation.setOneShot(false);
		m_animation_holder.setImageDrawable(m_animation);
	}
	
	private void changeSound() {
		if (m_sound_player != null) {
			m_sound_player.reset();
			m_sound_player.release();
			m_sound_player = null;
		}
	}
	
	private void startSound() {
		if (m_sound_player == null) {
			Log.v("zhangge", "new sound");
			File tempFile = new File(this.getExternalFilesDir(null), "sound.wav");
			if (tempFile.isFile()) {
				m_sound_player = new MediaPlayer();
				
				try {
					m_sound_player.setDataSource(tempFile.getAbsolutePath());
					m_sound_player.prepare();
					m_sound_player.start();
				} catch (Exception e) {
					e.printStackTrace();
					m_sound_player.release();
					m_sound_player = null;
				}
			}
		}
		else {
			m_sound_player.seekTo(0);
			m_sound_player.start();
		}
		
	}
	
	private void changeResource(String key) {
		if (m_change_task != null) {
			m_change_task.cancel(false);
		}
		m_change_task = new ChangeResourceTask();
		m_change_task.execute(key);
	}
	
	private class ChangeResourceTask extends AsyncTask<String, Integer, ResourceFile> {
		protected ResourceFile doInBackground(String... zip_id) {
			if(zip_id[0] == null) {
				JSONObject response = HttpJsonClient.request(Configures.query_item_url);
				try {
					zip_id[0] = response.getJSONArray(Configures.query_item_response_item_name)
						.getJSONObject(0).getString(Configures.query_item_response_key_name);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			ResourceFile rf = ResourceFileManager.getResourceIfExists(zip_id[0], CoverActivity.this);
			if (rf == null) {
				// download this zipfile
				try {
		            URL url = new URL(Configures.GetZipUrl(zip_id[0]));
		            String resource_dir_name = ResourceFileManager.getResourceDirectory( CoverActivity.this);
		            String tmp_file_name = resource_dir_name + zip_id[0] + ".tmp";
		            String final_file_name = resource_dir_name + zip_id[0] + ".zip";
		            URLConnection connection = url.openConnection();
		            connection.setConnectTimeout(Configures.url_connect_timeout);
		            connection.setReadTimeout(Configures.url_connect_read_timeout);
		            connection.connect();
		            // this will be useful so that you can show a typical 0-100% progress bar
		            int fileLength = connection.getContentLength();

		            // download the file
		            InputStream input = new BufferedInputStream(url.openStream());
		            OutputStream output = new FileOutputStream(tmp_file_name);

		            byte data[] = new byte[1024];
		            long total = 0;
		            int count;
		            while ((count = input.read(data)) != -1) {
		                total += count;
		                // publishing the progress....
		                publishProgress((int) (total * 100 / fileLength));
		                output.write(data, 0, count);
		            }

		            output.flush();
		            output.close();
		            input.close();
		            File tmp = new File(tmp_file_name);
		            File resource_file = new File(final_file_name);
		            tmp.renameTo(resource_file);
		            rf = new ResourceFile(zip_id[0], resource_file, CoverActivity.this);
		        } catch (Exception e) {
			        return null;		        	
		        }
			}
			if (rf.prepareForCover()){
				return rf;
			}
			return null;
		}
		
//		protected void onProgressUpdate(int progress) {
//			//m_spinner.setProgress(progress);
//		}
//		
		protected void onPostExecute(ResourceFile rf) {
			if (rf == null) {
				// some thing wrong ?
				Log.e("zhangge", "null resource file!");
				Utils.makeToastAboutNetworkError(CoverActivity.this);
			}
			else {
				CoverActivity.this.changeAnimation(rf.getCoverAnimations());
				changeSound();
				CoverActivity.this.m_resource_file = rf;
			}
			//CoverActivity.this.m_spinner.setVisibility(View.GONE);
			CoverActivity.this.m_change_task = null;
			CoverActivity.this.m_enter_button.setClickable(true);
			CoverActivity.this.m_download_animation.setVisibility(View.GONE);
		}
		
		protected void onPreExecute() {
			//CoverActivity.this.m_spinner.setVisibility(View.VISIBLE);
			CoverActivity.this.m_download_animation.setVisibility(View.VISIBLE);
			CoverActivity.this.m_animation_holder.setImageDrawable(null);
			if (m_resource_file != null) {
				m_resource_file.releaseResources();
			}
			CoverActivity.this.m_change_task = this;
			m_enter_button.setClickable(false);
		}
		
		protected void onCancelled(ResourceFile rf) {
			//do nothing
		}
	}
}
