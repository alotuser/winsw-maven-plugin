package com.alotuser.util;

import cn.alotus.core.util.ArrayUtil;
import cn.alotus.core.util.StrUtil;

public class WinVersionUtil {

	
	/**
	 * getEqualsStrIgnoreCase
	 * @param str str
	 * @param testStrs testStrs
	 * @return str
	 */
	public static String getEqualsStrIgnoreCase(CharSequence str, CharSequence... testStrs) {
		if (StrUtil.isEmpty(str) || ArrayUtil.isEmpty(testStrs)) {
			return null;
		}
		for (CharSequence testStr : testStrs) {
			if (StrUtil.equalsIgnoreCase(testStr, str)) {
				return testStr.toString();
			}
		}
		return null;
	}
	
	
}
