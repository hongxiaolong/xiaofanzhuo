package com.xiaolong.xiaofanzhuo.dataoperations;

public class DataOperations {

	public static String getActualString(String str) {
		
		if (str.indexOf("\'") == 0)
			str = str.substring(1, str.length()); // 去掉第一个'
		if (str.lastIndexOf("\n") == str.length() - 1)
			str = str.substring(0, str.length() - 1); // 去掉最后一个\n
		if (str.lastIndexOf("\'") == str.length() - 1)
			str = str.substring(0, str.length() - 1); // 去掉最后一个'
		
		return str;
		
	}

}
