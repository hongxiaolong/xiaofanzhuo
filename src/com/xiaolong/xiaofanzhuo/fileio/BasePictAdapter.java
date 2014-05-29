package com.xiaolong.xiaofanzhuo.fileio;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class BasePictAdapter extends AbstractBasePictAdapter{
	
	private Context context;
	private List<Bundle> list = new ArrayList<Bundle>();
	private IViewAddAndEventSet viewAdd = null;
	private boolean isMultiSelectMode;
	
	public BasePictAdapter(Context context) {
		this.context = context;
	}
	
	public int getCount() {
		return list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		return viewAdd.addViewAndAddEvenet(context, convertView, position, list, isMultiSelectMode);
	}
	
	public void addPicture(Bundle data) {
		list.add(data);
		this.notifyDataSetChanged();
	}

	@Override
	public void clear() {
		list.clear();
		this.notifyDataSetChanged();
	}

	@Override
	public void setIViewAddAndEventSet(IViewAddAndEventSet iViewAddAndEventSet) {
		this.viewAdd = iViewAddAndEventSet;
	}

	@Override
	public void setMultiSelectMode(boolean isMultiSelectMode) {
		this.isMultiSelectMode = isMultiSelectMode;
		for (int i = 0; i < list.size(); i++) {
			Bundle data = list.get(i);
			data.putBoolean(IViewAddAndEventSet.KEY_IS_MULTI_SELECT, false);
			list.set(i, data);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public boolean isMultiSelectMode() {
		return isMultiSelectMode;
	}
	
	@Override
	public List<Bundle> getAll() {
		List<Bundle> bundleList = new ArrayList<Bundle>();
		for (int i = 0; i < bundleList.size(); i++) {
			Bundle data = bundleList.get(i);
			boolean is = data.getBoolean(IViewAddAndEventSet.KEY_IS_MULTI_SELECT);
			if(is == isMultiSelectMode()){
				bundleList.add(data);
			}
		}//for
		return bundleList;
	}
}