package com.wingedstone.babylearn;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class ResourceFileManager {	
	static final String RESOURCE_DIRECTORY = Configures.zip_relative_path;	
		
	public static ResourceFile getResourceIfExists(String key, Context context) {
		ResourceFile res_file = null;
		File resource_dir = new File(context.getExternalFilesDir(null), RESOURCE_DIRECTORY);
		if (!resource_dir.isDirectory()) {
			resource_dir.mkdir();
		}
		if (resource_dir.isDirectory()) {
			String file_name = resource_dir.getAbsolutePath() + "/" + key + ".zip";
			try {
				res_file =  new ResourceFile(key, file_name, context);	
			}
			catch (Exception e) {
				res_file = null;
			}
		}
		return res_file;
	}
	
	public static String getResourceDirectory(Context context) {
		File resource_dir = new File(context.getExternalFilesDir(null), RESOURCE_DIRECTORY);
		String dir_name = resource_dir.getAbsolutePath() + "/" ;
		return dir_name;
	}
	
	public static String getCacheDirectory(Context context) {
		File resource_dir = new File(context.getExternalFilesDir(null), Configures.thumbnails_image_relative_path);
		String dir_name = resource_dir.getAbsolutePath() + "/" ;
		return dir_name;
	}
	
	public static boolean canWriteAndReadStorage() {
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
	
	public static boolean canReadStorage() {
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}
}
