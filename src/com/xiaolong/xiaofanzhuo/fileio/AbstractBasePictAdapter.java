package com.xiaolong.xiaofanzhuo.fileio;

import java.util.List;

import android.os.Bundle;
import android.widget.BaseAdapter;

public abstract class AbstractBasePictAdapter extends BaseAdapter {
	
	public abstract void addPicture(Bundle data);

	public abstract void clear();
	
	public abstract void setIViewAddAndEventSet(IViewAddAndEventSet iViewAddAndEventSet);
	
	public abstract void setMultiSelectMode(boolean isMultiSelect);
	
	public abstract boolean isMultiSelectMode();

	public abstract List<Bundle> getAll();
}
