package com.wingedstone.babylearn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;


/*
 * this is resource reader for babylearn project.
 * just a simple wrapper for class ZipFile.
 * structure of resource zip:
 * 	- 8 animation pictures: a0.png ~ a7.png / 600px * 600px
 *  - n step pictures: p0.png ~ pn.png / 600px * 600px
 *  - 1 sound file: sound.wav
 */
public class ResourceFile extends ZipFile {
	
	static final String ANIMATION_PREFIX = "a" ;
	static final String STEP_PREFIX = "p";
	static final String PICTURE_FILE_EXTENSION = ".png";
	static final String SOUND_FILE_EXTENSION = ".wav";
	static final int ANIMATION_FRAME_COUNT = 8;
	
	public ArrayList<String> m_animations;
	public ArrayList<Bitmap> m_animation_bitmaps;
	
	public ArrayList<String> m_step_pictures;
	public ArrayList<Bitmap> m_step_picture_bitmaps;
	
	public String m_sound;
	
	private boolean m_prepared = false;
	
	private BitmapFactory.Options m_options ;

	public ResourceFile(File file, int mode) throws IOException {
		super(file, mode);
		initialize();
	}

	public ResourceFile(File file) throws ZipException, IOException {
		super(file);
		initialize();
	}
	
	public ResourceFile(String name) throws ZipException, IOException {
		super(name);
		initialize();
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
		// read sound into memory
//		try {
//			InputStream ins = this.getInputStream(this.getEntry(m_sound));
//			File tempFile = File.createTempFile("_AUDIO_", ".wav");
//	        FileOutputStream out = new FileOutputStream(tempFile);
//	        //IOUtils.copy(ins, out);
//		}
//		catch (IOException e){
//			
//		}
		m_prepared = true;
		return true;  
	}
	
	private void initialize() {
		m_animations = new ArrayList<String>();
		m_step_pictures = new ArrayList<String>();
		m_animation_bitmaps = new ArrayList<Bitmap>();
		m_step_picture_bitmaps = new ArrayList<Bitmap>();
		m_sound = null;
		m_prepared = false;
		m_options = new BitmapFactory.Options();
		m_options.inScaled = false;
		m_options.inDensity = DisplayMetrics.DENSITY_HIGH;
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
	

}