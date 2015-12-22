package com.missionsky.aatools.loganalyst;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: WebAccessLogUtil.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2011
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall WebAccessLogUtil.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2011-12-15			john.huang				Initial.
 *  
 * </pre>
 */
public class WebAccessLogFilter
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String path = "I:\\dump\\11ACC-07497\\WebLogs_20111214\\av.web.172.21.26.32.access.2011-12-14.log";
		String outputPath = "I:\\dump\\11ACC-07497\\WebLogs_20111214\\av.web.172.21.26.32.access.2011-12-14_filter.log";
		String output = reserveDoLog(path);
		try
		{
			BufferedOutputStream outFile = new BufferedOutputStream(new FileOutputStream(outputPath));
			outFile.write(output.getBytes());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static String reserveDoLog(String path) {
		File f = new File(path);
		try
		{
			List lines = FileUtils.readLines(f);
			StringBuilder output = new StringBuilder();
			for (int i = 0; i < lines.size(); i++) {
				String line = (String) lines.get(i);
				if (line.indexOf(".do") > 0) {
					System.out.println(line);
					output.append(line).append("\r\n");
				}
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