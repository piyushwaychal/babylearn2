package com.wingedstone.babylearn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

/*
 *  the activity for rendering content and animation to user
 *  this activity contains a animation, a sound , and some navigation buttons
 */
public class MainActivity extends Activity implements OnClickListener, MyGestureListener.SimpleGestureListener{
	static final int ANIMATION_FRAME_COUNT = 8;
	private AnimationDrawable m_animation;
	private ImageView m_animation_holder;
	private ImageButton m_enter_button;
	private MediaPlayer m_sound_player = null;
	
	private int m_sound_player_data_key = 0;
	
	private ResourceFileManager m_resource_manager;
	
	private MyGestureListener m_gesture_listener;
	
	// for test
	private int[] reskeys = {1364993314, 1365316297, 1365321200, 1365321271, 1365322296};
	private int current_key_index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		m_animation = new AnimationDrawable();
		m_animation.setOneShot(true);
		m_animation_holder = (ImageView) findViewById(R.id.MainImageView);
		m_animation_holder.setOnClickListener(this);
		m_enter_button = (ImageButton) findViewById(R.id.MainGoButton);
		m_enter_button.setOnClickListener(this);
		
		m_gesture_listener = new MyGestureListener(this, this);
		
		m_resource_manager = new ResourceFileManager(this);		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent me){
		this.m_gesture_listener.onTouchEvent(me);
		return super.dispatchTouchEvent(me);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.MainGoButton:
			testResource();
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
	public void onSwipe(int direction) {
		switch (direction) {
		case MyGestureListener.SWIPE_LEFT:
			Log.v("zhangge", "swipe left");
			current_key_index = current_key_index - 1;
			if (current_key_index < 0) {
				current_key_index = current_key_index + reskeys.length;
			}
			changeResource(reskeys[current_key_index]);
			break;
		case MyGestureListener.SWIPE_RIGHT:
			Log.v("zhangge", "swipe right");
			current_key_index = (current_key_index + 1) % reskeys.length;
			changeResource(reskeys[current_key_index]);
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onDoubleTap() {
		
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
			Log.v("zhangge", "new sound");
			File tempFile = new File(this.getExternalFilesDir(null), "sound.wav");
			if (tempFile.isFile()) {
				m_sound_player.stop();
				
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
	
	private void testResource() {
		m_resource_manager.reInitialize();
		ResourceFile rf = m_resource_manager.getExistingNewestFile();
		if (rf != null && rf.prepare()) {
			changeAnimation(rf.m_animation_bitmaps);
			changeSound();
		}
	}
	
	private void changeResource(int key) {
		m_resource_manager.reInitialize();
		ResourceFile rf = m_resource_manager.getResourceFile(key);
		if (rf != null && rf.prepare()) {
			changeAnimation(rf.m_animation_bitmaps);
			changeSound();
		}
	}

}
