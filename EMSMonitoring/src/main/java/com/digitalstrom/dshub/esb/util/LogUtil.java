package com.digitalstrom.dshub.esb.util;

public class LogUtil {
	
	public static String lazyFormat(final String s, final Object... o) {
		    return String.format(s, o);
		}

}
