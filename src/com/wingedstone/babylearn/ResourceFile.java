package com.wingedstone.babylearn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;


/*
 * this is resource reader for babylearn project.
 * just a simple wrapper for class ZipFile.
 * structure of resource zip:
 * 	- 8 animation pictures: a0.png ~ a7.png / 600px * 600px
 *  - n step pictures: p0.png ~ pn.png / 600px * 600px
 *  - 1 sound file: sound.wav
 */
public class ResourceFile extends ZipFile{
	
	static final String ANIMATION_PREFIX = "a" ;
	static final String STEP_PREFIX = "p";
	static final String PICTURE_FILE_EXTENSION = ".png";
	static final String SOUND_FILE_EXTENSION = ".wav";
	static final int ANIMATION_FRAME_COUNT = 8;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	
	public ArrayList<String> m_animations;
	public ArrayList<Bitmap> m_animation_bitmaps;
	
	public ArrayList<String> m_step_pictures;
	public ArrayList<Bitmap> m_step_picture_bitmaps;
	
	public String m_sound;
	
	private String m_key = "";
	private Context m_context = null;
	
	private boolean m_prepared = false;
	private boolean m_slides_prepared = false;
	private boolean m_cover_prepared = false;
	
	private BitmapFactory.Options m_options ;
	
	public static int copy(InputStream input, OutputStream output) throws IOException{
	     byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
	     int count = 0;
	     int n = 0;
	     while (-1 != (n = input.read(buffer))) {
	         output.write(buffer, 0, n);
	         count += n;
	     }
	     return count;
	 }

	public ResourceFile(String thiskey, File file, int mode, Context context) throws IOException {
		super(file, mode);
		initialize(thiskey, context);

	}

	public ResourceFile(String thiskey, File file, Context context) throws ZipException, IOException {
		super(file);
		initialize(thiskey, context);
	}
	
	public ResourceFile(String thiskey, String name, Context context) throws ZipException, IOException {
		super(name);
		initialize(thiskey, context);
	}
	
	public boolean prepareForSlides() {
		if (m_slides_prepared) {
			return true;
		}
		if (!isValidResourceZip()) {
			return false;
		}
		// read step pictures into memory
		for (Iterator<String> iter = m_step_pictures.iterator(); iter.hasNext();) {
			String entry_name = iter.next();
			try {
				InputStream ins = this.getInputStream(this.getEntry(entry_name));
				Bitmap bm = BitmapFactory.decodeStream(ins, null, m_options);
				m_step_picture_bitmaps.add(bm);
			}
			catch (Exception e) {
				return false;
			}	
		}
		m_slides_prepared = true;
		return true;
	}
	
	public ArrayList<Bitmap> getSlidePictures() {
		if (m_slides_prepared) {
			return m_step_picture_bitmaps;
		}
		return null;
	}
	
	public boolean prepareForCover() {
		if (m_cover_prepared) {
			return true;
		}
		if (!isValidResourceZip()) {
			return false;
		}
		// read animation frames into memory
		for (Iterator<String> iter = m_animations.iterator(); iter.hasNext(); ) {
			String entry_name = iter.next();
			try {
				InputStream ins = this.getInputStream(this.getEntry(entry_name));
				Bitmap bm = BitmapFactory.decodeStream(ins, null, m_options);
				m_animation_bitmaps.add(bm);
			}
			catch (Exception e) {
				return false;
			}	
		}
		try {
			InputStream ins = this.getInputStream(this.getEntry(m_sound));
			File tempFile = new File(m_context.getExternalFilesDir(null), "sound.wav");
			Log.v("zhangge", tempFile.getAbsolutePath());
	        FileOutputStream out = new FileOutputStream(tempFile, false);
	        copy(ins, out);
	        out.close();
		}
		catch (IOException e){
			return false;
		}
		m_cover_prepared = true;
		return true;
	}
	
	public ArrayList<Bitmap> getCoverAnimations() {
		if (m_cover_prepared) {
			return m_animation_bitmaps;
		}
		return null;
	}
	
	/*
	 * prepare the files into memory
	 */
	public boolean prepare() {
		if (!this.isValidResourceZip()) {
			return false;
		}
		// read animation frames into memory
		for (Iterator<String> iter = m_animations.iterator(); iter.hasNext(); ) {
			String entry_name = iter.next();
			try {
				InputStream ins = this.getInputStream(this.getEntry(entry_name));
				Bitmap bm = BitmapFactory.decodeStream(ins, null, m_options);
				m_animation_bitmaps.add(bm);
			}
			catch (Exception e) {
				return false;
			}	
		}
		// read step pictures into memory
		for (Iterator<String> iter = m_step_pictures.iterator(); iter.hasNext();) {
			String entry_name = iter.next();
			try {
				InputStream ins = this.getInputStream(this.getEntry(entry_name));
				Bitmap bm = BitmapFactory.decodeStream(ins, null, m_options);
				m_step_picture_bitmaps.add(bm);
			}
			catch (Exception e) {
				return false;
			}	
		}
		// read sound into temp file
		try {
			InputStream ins = this.getInputStream(this.getEntry(m_sound));
			File tempFile = new File(m_context.getExternalFilesDir(null), "sound.wav");
			Log.v("zhangge", tempFile.getAbsolutePath());
	        FileOutputStream out = new FileOutputStream(tempFile, false);
	        copy(ins, out);
	        out.close();
		}
		catch (IOException e){
			return false;
		}
		m_prepared = true;
		return true;  
	}
	

	
	
	private void initialize(String thiskey, Context context) {
		m_animations = new ArrayList<String>();
		m_step_pictures = new ArrayList<String>();
		m_animation_bitmaps = new ArrayList<Bitmap>();
		m_step_picture_bitmaps = new ArrayList<Bitmap>();
		m_sound = null;
		m_prepared = false;
		m_options = new BitmapFactory.Options();
		m_options.inScaled = false;
		m_options.inDensity = DisplayMetrics.DENSITY_HIGH;
		setKey(thiskey);
		m_context = context;
		Enumeration<? extends ZipEntry> e = this.entries();
		for(; e.hasMoreElements();) {
			ZipEntry zn = e.nextElement();
			String zn_name = zn.getName();
			if (zn_name.contains(SOUND_FILE_EXTENSION)) {
				m_sound = zn_name;
			}
			else if (zn_name.startsWith(ANIMATION_PREFIX)) {
				m_animations.add(zn_name);
			}
			else if (zn_name.startsWith(STEP_PREFIX)) {
				m_step_pictures.add(zn_name);
			}
		}
		Collections.sort(m_animations);
		Collections.sort(m_step_pictures);
	}
	
	/*
	 * check the whether the zip file contains full resource
	 */
	private boolean isValidResourceZip() {	
		// 8 animation pictures
		for (int i = 0; i < ANIMATION_FRAME_COUNT; i++) {
			String entry_name = ANIMATION_PREFIX + Integer.toString(i) + PICTURE_FILE_EXTENSION;
			if (!m_animations.contains(entry_name)) {
				return false;
			}
		}
		// *.wav
		if (m_sound == null) {
			return false;
		}
		// n step pictures
		for (int i = 0; i < m_step_pictures.size(); i++) {
			String entry_name = STEP_PREFIX + Integer.toString(i) + PICTURE_FILE_EXTENSION;
			if (!m_step_pictures.contains(entry_name)) {
				return false;
			}
		}
		return true;
	}

	public String getKey() {
		return m_key;
	}

	public void setKey(String m_key) {
		this.m_key = m_key;
	}

}
