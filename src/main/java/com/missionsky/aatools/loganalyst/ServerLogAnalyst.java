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
 *  File: ServerLogAnalyst.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall ServerLogAnalyst.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-2-5			john.huang				Initial.
 *  
 * </pre>
 */
public class ServerLogAnalyst
{
	public final static String DBA_RISKY_PREFIX = "[com.accela.aa.datautil.DBAccessor] SQL: It is risky  to retrieve too many records once. s(";
	public final static String DBA_TIME_PREFIX = "WARN  [com.accela.aa.datautil.DBAccessor] SQL, s(";
	public final static String PROFILE_PREFIX = "INFO  [com.accela.util.Profiler]";
	public final static String SYSTEM_STATS_PREFIX = "[com.accela.diagnostics.DiagnosticThread] Total Memory";
	public final static String SYSTEM_STATS_SUFFIX = "[com.accela.diagnostics.DiagnosticThread] Available Processors"; 
	public final static String MEM_PREFIX = "% Memory Used (Total) = ";
	
	public final static String TIME_PREFIX = "Total time:";
	public final static String REC_PREFIX = " Total records:";
	
	public final static String PROFILE_TIME_PREFIX = ", Time ";
	public final static String PROFILE_REC_PREFIX = ", Count ";
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String fn = "C:\\Documents and Settings\\john.huang\\Desktop\\server.log";
		File f = new File(fn);
		String out = analyst(f);
		System.out.println(out);
	}
	
	public static String analyst(File f) {
		try
		{
			List lines = FileUtils.readLines(f);
			List<LogModel> logs = new ArrayList<LogModel>();
			StringBuffer sysStatsOut = new StringBuffer();
			
			for (int i = 0; i < lines.size(); i++) {
				String line = (String) lines.get(i);
				
				// print sys stats over 80%
				int sysBegin = line.indexOf(SYSTEM_STATS_PREFIX);
				if (sysBegin > 0) {
					boolean needOutput = false;
					StringBuffer tmp = new StringBuffer();
					tmp.append(line).append("\r\n");
					
					for (;i < lines.size(); i++) {
						line = (String) lines.get(i);
						tmp.append(line).append("\r\n");
						if (line.indexOf(SYSTEM_STATS_SUFFIX) > 0) {
							break;
						}
						int memBegin = line.indexOf(MEM_PREFIX);
						if (memBegin >= 0) {
							double used = Double.parseDouble(line.substring(memBegin + MEM_PREFIX.length()));
							if (used >= 70) {
								needOutput = true;
							}
							else {
								break;
							}
						}
					}
					if (needOutput) {
						sysStatsOut.append(tmp).append("\r\n");
					}
					continue;
				}
				
				int riskyBegin = line.indexOf(DBA_RISKY_PREFIX);
				if (riskyBegin > 0) { // add dba logs
					LogModel log = new LogModel();
					int seqEnd = line.indexOf(")");
					log.seq = line.substring(riskyBegin + DBA_RISKY_PREFIX.length(), seqEnd);
					
					log.content = line + "\r\n";
					
					for (;i < lines.size(); i++) {
						line = (String) lines.get(i);
						if (line.length() < 2) {
							break;
						}
						log.content += line + "\r\n";
					}
					
					for (int j = i;j < lines.size(); j++) {
						line = (String) lines.get(j);
						int seqBegin = line.indexOf(DBA_TIME_PREFIX + log.seq + ")");
						if (seqBegin > 0) {
							log.content = line + log.content;
							int timeBegin = line.indexOf(TIME_PREFIX);
							if ( timeBegin > 0) {
								int recBegin = line.indexOf(REC_PREFIX);
								try {
									log.time = Double.parseDouble(line.substring(timeBegin + TIME_PREFIX.length(), recBegin));
								}
								catch (NumberFormatException e) {}
								
								try {
									log.count = Integer.parseInt(line.substring(recBegin));
								}
								catch (NumberFormatException e) {}
								break;
							}
						}
					}
					if (log.time > 1000) {
						logs.add(log);
					}
				}
				else { // profile logs
					int profileBegin = line.indexOf(PROFILE_PREFIX);
					if (profileBegin > 0) {
						int timeBegin = line.indexOf(PROFILE_TIME_PREFIX);
						if (timeBegin > 0) {
							LogModel log = new LogModel();
							log.content = line + "\r\n";
							
							int end = line.indexOf("ms,", timeBegin + 1);
							try {
								log.time = Double.parseDouble(line.substring(timeBegin + PROFILE_TIME_PREFIX.length(), end).replaceAll("\\,", ""));
							}
							catch (NumberFormatException e) {}
							
							int recBegin = line.indexOf(PROFILE_REC_PREFIX);
							end = line.indexOf(",", recBegin + 1);
							try {
								log.count = Integer.parseInt(line.substring(recBegin + PROFILE_REC_PREFIX.length(), end));
							}
							catch (NumberFormatException e) {}
							
							i++;
							line = (String) lines.get(i);
							log.content += line + "\r\n";
							
							if (log.time > 1000) {
								logs.add(log);
							}
						}
					}
				}
			}
				
			Collections.sort(logs);
			StringBuffer output = new StringBuffer();
			for (int i = logs.size() - 1; i >= 0; i--) {
				output.append(logs.get(i).content).append("\r\n");
			}
			
			return sysStatsOut.toString() + output.toString();
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