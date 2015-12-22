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
 *  File: LogAnalyst.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall LogAnalyst.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-2-4			john.huang				Initial.
 *  
 * </pre>
 */
public class DBALogAnalyst
{
	public final static String TIME_PREFIX = ") Total time:";
	public final static String REC_PREFIX = " Total records:";
	public final static String SEQ_PREFIX = "SQL, s(";
	public final static String SEQ_SUFFIX = ") ";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String fn = "C:\\Documents and Settings\\john.huang\\Desktop\\aadba.log.2010-02-03";
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
					int recBegin = line.indexOf(REC_PREFIX);
					try {
						log.time = Double.parseDouble(line.substring(timeBegin + TIME_PREFIX.length(), recBegin));
					}
					catch (NumberFormatException e) {}
					
					try {
						log.count = Integer.parseInt(line.substring(recBegin));
					}
					catch (NumberFormatException e) {}
					
					if (log.time < 1000.0) {
						continue;
					}
					
					int seqBegin = line.indexOf(SEQ_PREFIX);
					if (seqBegin < 0) {
						continue;
					}
					int seqEnd = line.indexOf(SEQ_SUFFIX, seqBegin);
					log.seq = line.substring(seqBegin + SEQ_PREFIX.length(), seqEnd);
					
					log.content = line + "\r\n";
					
					int beginLine = i;
					for (int j = i - 1; j > 0; j--) {
						line = (String) lines.get(j);
						if (line.indexOf(log.seq) > 0) {
							beginLine = j;
							break;
						}
					}
					
					for (int j = beginLine; j < i; j++) {
						line = (String) lines.get(j);
						log.content += line + "\r\n";
						if (line.length() < 2) {
							break;
						}
					}
					logs.add(log);
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