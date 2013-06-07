package com.wingedstone.babylearn;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SlidePicturePagerAdapter extends FragmentStatePagerAdapter {
	
	private ArrayList<Bitmap> m_bm_list = null;
	private final FragmentManager m_fragment_manager;
	private Fragment m_last_fragment;
	
	public SlidePicturePagerAdapter(FragmentManager fm) {
		super(fm);
		m_fragment_manager = fm;
	}
	
	public void SetBitMapList(ArrayList<Bitmap> bml) {
		m_bm_list = bml;
	}

	@Override
	public Fragment getItem(int arg0) {
		if (m_bm_list != null && arg0 <= m_bm_list.size()) {
			if(arg0 == m_bm_list.size()) {
				if (m_last_fragment == null) {
					m_last_fragment = new TakePictureFragment();
				}
				return m_last_fragment;
			}
			return SlidePictureFragment.newInstance(m_bm_list.get(arg0));
		}
		return null;
	}

	@Override
	public int getCount() {
		if (m_bm_list == null) {
			return 0;
		}
		return m_bm_list.size() + 1;
	}
	
	@Override
	public int getItemPosition(Object obj) {
		if (obj instanceof TakePictureFragment && m_last_fragment instanceof ShareFragment) {
			return POSITION_NONE;
		}
		return POSITION_UNCHANGED;
	}
	
	public void switchPicFragmentToShareFragment(Uri bitmap_uri) {
		if (m_last_fragment != null) {
			m_fragment_manager.beginTransaction().remove(m_last_fragment).commit();
			m_last_fragment = ShareFragment.newInstance(bitmap_uri);
			notifyDataSetChanged();
		}
	}
	
}
