package com.wingedstone.babylearn;


public class Configures {
	static final String version = "0.1";
	static final String configure_url = "http://c.wingedstone.com:8004/base/getconfig/";
	static final String query_thumbnail_url = "http://c.wingedstone.com:8004/base/querythumbnails/%d/";
	static final String query_item_url = "http://c.wingedstone.com:8004/base/queryitems/1/";
	
	//response names
	static final String query_item_response_item_name = "items";
	static final String query_item_response_key_name = "key";
	
	//intent extras
	static final String intent_key_name = "intent_key";
	
	static final String upyun_zip_url = "http://babylearn.b0.upaiyun.com/%s.zip";
	static final String get_thumbnail_url = "http://babylearn-thumbnails.b0.upaiyun.com/%s.png";
	
	// intent string
	static final String res_key = "res_key";
	
	// URL Connection timeouts
	static final int url_connect_timeout = 8000;
	static final int url_connect_read_timeout = 30000;
	
	// file places
	static final String thumbnails_image_relative_path = "thumbs";
	static final String zip_relative_path = "learnres";
	
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
	
}
