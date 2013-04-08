package com.wingedstone.babylearn;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class ResourceFileManager {
	static final String RESOURCE_DIRECTORY = "learnres";
	
	private Context m_context;
	private String m_resource_dir = null;
	
	public TreeSet<Integer> m_exist_keys = new TreeSet<Integer> ();
	
	public ResourceFileManager(Context context) {
		m_context = context;
	}
	
	public ResourceFile getResourceFile(int key) {
		ResourceFile res_file = null;
		if (isResourceExists(key) && m_resource_dir != null) {
			String file_name = m_resource_dir + String.valueOf(key) + ".zip";
			try {
				res_file =  new ResourceFile(file_name);	
			}
			catch (Exception e) {
				res_file = null;
			}
		}
		return res_file;
	}
	
	public ResourceFile getExistingNewestFile() {
		ResourceFile res_file = null;		
		try {
			int key = m_exist_keys.last();
			Log.v("zhangge", String.valueOf(key));
			res_file = getResourceFile(key);
		}
		catch (NoSuchElementException e) {
		}
		return res_file;
	}
	
	public boolean reInitialize() {
		if (canWriteAndReadStorage()) {
			File resource_dir = new File(m_context.getExternalFilesDir(null), RESOURCE_DIRECTORY);
			if( resource_dir.mkdir() || resource_dir.isDirectory()) {
				m_resource_dir = resource_dir.getAbsolutePath() + "/";
				Log.v("zhangge", String.valueOf(resource_dir.isDirectory()));
				Log.v("zhangge", m_resource_dir);
				rescanResources();
				return true;
			}
		}
		return false;
	}
	
	public void rescanResources() {
		if (!canReadStorage() || m_resource_dir == null) {
			return;
		}
		
		m_exist_keys.clear();
		File resource_dir = new File(m_resource_dir);
		String[] res_names = resource_dir.list();
		for(String filename: res_names) {
			m_exist_keys.add(Integer.valueOf(filename.split("\\.")[0]));
		}
		Log.v("zhangge", m_exist_keys.toString());
	}
	
	private boolean isResourceExists(int key) {
		return m_exist_keys.contains(Integer.valueOf(key));
	}
	
	private boolean canWriteAndReadStorage() {
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
	
	private boolean canReadStorage() {
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
