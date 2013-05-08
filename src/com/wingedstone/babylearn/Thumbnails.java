package com.wingedstone.babylearn;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;

public class Thumbnails {

	public class Item {
		public String key;
		public String title;
		public Item(String key, String title) {
			this.key = key;
			this.title = title;
		}
	}
	
	public Thumbnails() {
		m_items = new ArrayList<Thumbnails.Item>();
	}
	
	public ArrayList<Item> m_items;
	
	public void addThumbnailsFromJson(JSONObject json) {
		try {
			JSONArray items = json.getJSONArray("items");
			for (int i = 0; i < items.length(); i++) {
				JSONObject item_json = items.getJSONObject(i);
				String key = item_json.getString("key");
				if (!hasKey(key)) {
					Item new_item = new Item(key, item_json.getString("title"));
					m_items.add(new_item);
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public int getItemCount() {
		return m_items.size();
	}
	
	public Item getItem(int index) {
		return m_items.get(index);
	}
	
	private boolean hasKey(String key) {
		for (int i = 0; i < m_items.size(); i++) {
			if (key == m_items.get(i).key) {
				return true;
			}
		}
		return false;
	}
}