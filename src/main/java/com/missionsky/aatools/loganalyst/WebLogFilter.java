package com.missionsky.aatools.loganalyst;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: WebLogFilter.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall WebLogFilter.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-2-9			john.huang				Initial.
 *  
 * </pre>
 */
public class WebLogFilter
{

	public static void main(String[] args) throws Exception
	{
		String fn = "C:\\Documents and Settings\\john.huang\\Desktop\\av.web.10.50.70.143.access.2010-02-05.log";
		File f = new File(fn);
		String out = analyst(f);
		System.out.println(out);
		
		String ofn = "C:\\Documents and Settings\\john.huang\\Desktop\\out.log";
		File of = new File(ofn);
		of.createNewFile();
		
		FileUtils.writeStringToFile(of, out);
	}
	
	public static String analyst(File f) throws Exception {
			List lines = FileUtils.readLines(f);
			List<LogModel> logs = new ArrayList<LogModel>();
			
			StringBuilder out = new StringBuilder();
			
			for (int i = 0; i < lines.size(); i++) {
				String line = (String) lines.get(i);
				
//				if (line.indexOf(".js") > 0
//						|| line.indexOf(".css") > 0
//						|| line.indexOf(".jpg") > 0
//						|| line.indexOf(".gif") > 0
//						|| line.indexOf(".png") > 0
//						|| line.indexOf(".jsp") > 0
//						|| line.indexOf(".htm") > 0
//						|| line.indexOf(".psml") > 0) {
//					
//				}
//				else {
//					out.append(line).append("\r\n");
//				}
				
				if (line.indexOf(".do") >= 0) {
					out.append(line).append("\r\n");
				}
			}
			
			return out.toString();
	}
}

/*
*$Log: av-env.bat,v $
*/