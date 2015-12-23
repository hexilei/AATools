package com.missionsky.aatools.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageUtil {
	
	public static String getText(String key)
	{
		ResourceBundle resource = ResourceBundle.getBundle("config/AATools",Locale.US);
		return resource.getString(key);
	}

}
