package com.missionsky.aatools.loganalyst;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: WebLogAnalyst.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall WebLogAnalyst.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-2-5			john.huang				Initial.
 *  
 * </pre>
 */
public class WebLogAnalyst
{
	public final static String TIME_PREFIX = "] ";
	public final static String TIME_SUFFIX = " \"";
	
	public static void main(String[] args)
	{
		String fn = "C:\\Documents and Settings\\john.huang\\Desktop\\av.web.10.47.4.109.access.2010-02-02.log";
		File f = new File(fn);
		String out = analyst(f);
		System.out.println(out);
	}
	
	public static String analyst(File f) {
		try
		{
			List lines = FileUtils.readLines(f);
			List<LogModel> logs = new ArrayList<LogModel>();
			
			for (int i = 0; i < lines.size(); i++) {
				String line = (String) lines.get(i);
				int timeBegin = line.indexOf(TIME_PREFIX);
				if ( timeBegin > 0) {
					LogModel log = new LogModel();
					int timeEnd = line.indexOf(TIME_SUFFIX);
					try {
						log.time = Double.parseDouble(line.substring(timeBegin + TIME_PREFIX.length(), timeEnd));
					}
					catch (NumberFormatException e) {}
					
					if (log.time > 5.0) {
						log.content = line;
						logs.add(log);
					}
				}
			}
			
			Collections.sort(logs);
			StringBuffer output = new StringBuffer();
			for (int i = logs.size() - 1; i >= 0; i--) {
				output.append(logs.get(i).content).append("\r\n");
			}
			
			return output.toString();
		}
		catch (IOException e)
		{
			return e.getMessage();
		}
	}
}

/*
*$Log: av-env.bat,v $
*/