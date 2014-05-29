package com.xiaolong.xiaofanzhuo.fileio;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public interface IViewAddAndEventSet {
	
	public static final String KEY_NID = "nid";
	public static final String KEY_IS_MULTI_SELECT = "isMultiSelect";
	public View addViewAndAddEvenet(Context context, View convertView, int position, List<Bundle> list, boolean isMultiSelectMode);

}
