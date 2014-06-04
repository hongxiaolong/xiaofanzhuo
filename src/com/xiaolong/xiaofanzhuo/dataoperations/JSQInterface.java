package com.xiaolong.xiaofanzhuo.dataoperations;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSQInterface {
	
	public String getSQLClause(String table, String[] marchedKeys, JSONObject row, boolean recordExisting,String pkName) throws JSONException;
	
	public JSONObject prepareData(JSONObject json);
	
}
