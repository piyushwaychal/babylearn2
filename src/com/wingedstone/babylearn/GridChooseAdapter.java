package com.wingedstone.babylearn;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;

public class GridChooseAdapter extends BaseAdapter {
	
	private Thumbnails s_thumbnails;
	private Context m_context;
	
	DisplayImageOptions options;

	public GridChooseAdapter(Thumbnails tb, Context c) {
		s_thumbnails = tb;
		m_context = c;
		
		// :TODO add stub image & empty image & fail image
		options = new DisplayImageOptions.Builder()
		//.showStubImage(R.drawable.ic_stub)
		//.showImageForEmptyUri(R.drawable.ic_empty)
		//.showImageOnFail(R.drawable.ic_error)
		.cacheOnDisc()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}
	
	@Override
	public int getCount() {
		return s_thumbnails.getItemCount();
	}

	@Override
	public Object getItem(int position) {
		return s_thumbnails.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(m_context);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setAdjustViewBounds(true);
			//imageView.setBackgroundResource(R.drawable.grid_background);
			imageView.setBackgroundColor(Color.rgb(230, 230, 230));
            imageView.setLayoutParams(new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			imageView = (ImageView) convertView;
		}
		String uri = String.format(Configures.get_thumbnail_url, s_thumbnails.getItem(position).key);
		ImageLoader.getInstance().displayImage(uri, imageView, options);
		return imageView;
	}
}
