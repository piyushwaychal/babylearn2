package com.wingedstone.babylearn;

import android.content.Context;
import android.util.DisplayMetrics;


public class Configures {
	
	// server urls
	static final String version = "0.1";
	static final String configure_url = "http://c.wingedstone.com:8004/base/getconfig/";
	static final String query_thumbnail_url = "http://c.wingedstone.com:8004/base/querythumbnails/%d/";
	static final String query_item_url = "http://c.wingedstone.com:8004/base/queryitems/1/";
	static final String upyun_zip_url = "http://babylearn.b0.upaiyun.com/%s.zip";
	static final String get_thumbnail_url = "http://babylearn-thumbnails.b0.upaiyun.com/%s.png!half";
	
	//response names
	static final String query_item_response_item_name = "items";
	static final String query_item_response_key_name = "key";
	
	//intent extras
	static final String intent_key_name = "intent_key";
	static final String res_key = "res_key";
	static final int grid_choose_request_code = 819; 
	static final int start_camera_request_code = 820;
	
	// URL Connection timeouts
	static final int url_connect_timeout = 8000;
	static final int url_connect_read_timeout = 30000;
	
	// file places
	static final String thumbnails_image_relative_path = "thumbs";
	static final String zip_relative_path = "learnres";
	
	// share related
	public static final String weibo_appkey = "1437838508";
	public static final String weibo_redirect_url = "http://www.wingedstone.com/weibo/success.html";
	
	public static final String wechat_appkey = "wx4b9a37d1d5fc4db2";
	
	// page indicator parameters
	static final int indicator_line_width = 40;
	static final int indicator_stoke_width = 5;
	
	// photo size
	static final int photo_request_width = 600;
	
	public static String GetConfigureUrl() {
		return configure_url;
	}
	
	public static String GetQueryThumbnailUrl(int begin) {
		return String.format(query_thumbnail_url, begin);
	}
	
	public static String GetFirstItemUrl() {
		return query_item_url;
	}
	
	public static String GetZipUrl(String key) {
		return String.format(upyun_zip_url, key);
	}
	
	public static int GetRequestWidth(Context context) {
		int density_dpi = context.getResources().getDisplayMetrics().densityDpi;
		return photo_request_width * density_dpi / DisplayMetrics.DENSITY_XHIGH;
	}
	
}
