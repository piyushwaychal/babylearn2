package com.wingedstone.babylearn;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ViewFlipper;

/*
 *  the activity for rendering content and animation to user
 *  this activity contains a animation, a sound , and some navigation buttons
 */
public class MainActivity extends FragmentActivity implements OnClickListener{
	static final int MAIN_VIEW_L = 0;
	static final int PIC_VIEW_L = 1;
	static final int SHARE_VIEW_L = 2;
	
	// in main view
	private AnimationDrawable m_animation;
	private ImageView m_animation_holder;
	private ImageButton m_enter_button;
	private ProgressBar m_spinner;
	private MediaPlayer m_sound_player = null;
	
	// in step picture view
	private ImageSwitcher m_image_switcher;
	
	// view container
	private ViewPager m_view_pager;
	private SlidePicturePagerAdapter m_view_slide_adapter;
	
	private ChangeResourceTask m_change_task = null;
	
	private ResourceFile m_resource_file = null;
	
	private View m_view_main;
	private View m_view_pic_slide;
	
	private int m_cur_view_l = MAIN_VIEW_L;
	
	// for test
	private String[] reskeys = {"1364993314", "1366965984","1365321200", "1365321271", "1365322296"};
	private int current_key_index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// enable up button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		LayoutInflater inflater = LayoutInflater.from(this);
		m_view_main = inflater.inflate(R.layout.activity_cover, null);
		m_view_pic_slide = inflater.inflate(R.layout.activity_pictureslide, null);
		setContentView(m_view_main);
		m_animation = new AnimationDrawable();
		m_animation.setOneShot(true);
		m_animation_holder = (ImageView) m_view_main.findViewById(R.id.MainImageView);
		m_animation_holder.setOnClickListener(this);
		m_enter_button = (ImageButton) m_view_main.findViewById(R.id.MainGoButton);
		m_enter_button.setOnClickListener(this);
		m_spinner = (ProgressBar) m_view_main.findViewById(R.id.MainprogressBar);
		m_spinner.setVisibility(View.GONE);
		
		m_view_pager = (ViewPager) m_view_pic_slide.findViewById(R.id.main_pager);
		m_view_slide_adapter = new SlidePicturePagerAdapter(getSupportFragmentManager());
		m_view_pager.setAdapter(m_view_slide_adapter);
		
		
		
		changeResource("1366965984");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (m_sound_player != null) {
			m_sound_player.reset();
			m_sound_player.release();
			m_sound_player = null;
		}
	}
	
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent me){
//		this.m_gesture_listener.onTouchEvent(me);
//		return super.dispatchTouchEvent(me);
//	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.MainGoButton:
			switchTo(PIC_VIEW_L);
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
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	
	public void switchTo(int layout) {
		if (m_cur_view_l == layout) {
			return;
		}
		m_cur_view_l = layout;
		switch (m_cur_view_l) {
		case MAIN_VIEW_L:
			setContentView(m_view_main);
			break;
		case PIC_VIEW_L:
			setContentView(m_view_pic_slide);
			break;
		}
	}
	
	private class ChangeResourceTask extends AsyncTask<String, Integer, ResourceFile> {
		protected ResourceFile doInBackground(String... zip_id) {
			ResourceFile rf = ResourceFileManager.getResourceIfExists(zip_id[0], MainActivity.this);
			if (rf == null) {
				// download this zipfile
				try {
		            URL url = new URL(Configures.GetZipUrl(zip_id[0]));
		            String resource_dir_name = ResourceFileManager.getResourceDirectory(zip_id[0], MainActivity.this);
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
		            rf = new ResourceFile(zip_id[0], resource_file, MainActivity.this);
		        } catch (Exception e) {
			        return null;		        	
		        }
			}
			if (rf.prepareForCover()){
				return rf;
			}
			return null;
		}
		
		protected void onProgressUpdate(int progress) {
			m_spinner.setProgress(progress);
		}
		
		protected void onPostExecute(ResourceFile rf) {
			if (rf == null) {
				// some thing wrong ?
				Log.e("zhangge", "null resource file!");
			}
			else {
				MainActivity.this.changeAnimation(rf.m_animation_bitmaps);
				changeSound();
				MainActivity.this.m_resource_file = rf;
				MainActivity.this.m_view_slide_adapter.SetBitMapList(rf.m_step_picture_bitmaps);
			}
			MainActivity.this.m_spinner.setVisibility(View.GONE);
			MainActivity.this.m_change_task = null;
		}
		
		protected void onPreExecute() {
			MainActivity.this.m_spinner.setVisibility(View.VISIBLE);
			MainActivity.this.m_change_task = this;
		}
		
		protected void onCancelled(ResourceFile rf) {
			//do nothing
		}
	}


}
