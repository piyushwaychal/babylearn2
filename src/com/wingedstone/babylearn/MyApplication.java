package com.wingedstone.babylearn;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Application;
import android.os.AsyncTask;

public class MyApplication extends Application {
	
	public Thumbnails m_thumbnails;
	
	@Override
	public void onCreate() {
		super.onCreate();
		File cache_dir = new File(getApplicationContext().getExternalFilesDir(null), 
				Configures.thumbnails_image_relative_path);
		if (! cache_dir.isDirectory()) {
			cache_dir.mkdir();
		}
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
		.taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
		.threadPoolSize(5)
		.denyCacheImageMultipleSizesInMemory()
		.discCache(new UnlimitedDiscCache(cache_dir))
		.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
		.build();
		ImageLoader.getInstance().init(config);
		m_thumbnails = new Thumbnails();
	}
}
