package com.wingedstone.babylearn;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.renderscript.Sampler;
import android.util.Log;
import android.widget.Toast;

public class Utils {
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type){
	      return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_DCIM), "Camera");
	    Log.v("zhangge", mediaStorageDir.getAbsolutePath());
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("zhangge", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "IMG_"+ timeStamp + ".jpg");
	    } else if(type == MEDIA_TYPE_VIDEO) {
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
	        "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }

	    return mediaFile;
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	public static int calculateInSampleSize(
            BitmapFactory.Options options, 
            int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = reqHeight == 0 ? Integer.MAX_VALUE : Math.round((float) height / (float) reqHeight);
	        final int widthRatio = reqWidth == 0 ? Integer.MAX_VALUE : Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	    
	    return inSampleSize;
	}
	public static Bitmap decodeSampleBitmapFromInputStream(InputStream is, 
			int reqWidth, int reqHeight, 
			boolean faster_mode) {
		final BufferedInputStream ins = new BufferedInputStream(is, 32 * 1024);
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    ins.mark(32 * 1024);
		    BitmapFactory.decodeStream(ins, null, options);
		    ins.reset();
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		    options.inJustDecodeBounds = false;
		    options.inPreferredConfig = Bitmap.Config.RGB_565;
		    return BitmapFactory.decodeStream(ins, null, options);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	
	public static void makeToastAboutNetworkError(Context c) {
		String text = c.getResources().getString(R.string.network_failure);
		Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
	}
	
}
