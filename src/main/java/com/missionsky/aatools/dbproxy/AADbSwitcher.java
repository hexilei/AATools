package com.missionsky.aatools.dbproxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: AADbSwitcher.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2011-2014
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall AADbSwitcher.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  Mar 8, 2011			john.huang				Initial.
 *  
 * </pre>
 */
public class AADbSwitcher
{
	private static final String toMSSQL = "start changeToMSSQL.bat";
	private static final String toOracle = "start changeToOracle.bat";
	
	public static void runChangeToBat(String ver, String dbType) {
		if (ver == null || ver.length() == 0) {
			return;
		}
		
		String path = "";
		if (ver.equals("6.7.0")) {
			path = "D:\\AA6.7.0\\av.6.7.0";
		}
		else if (ver.equals("7.0.0")) {
			path = "D:\\AA7.0.0\\av.7.0.0";
		}
		else if (ver.equals("7.0.5")) {
			path = "D:\\AA7.0.5\\av.7.0.5";
		}
		else if (ver.equals("7.1.0")) {
			path = "D:\\AA7.1.0\\av.7.1.0";
		}
		else if (ver.equals("7.2.0")) {
			path = "D:\\AA7.2.0\\av.7.2.0";
		}
		else if (ver.equals("7.3.0")) {
			path = "D:\\AA7.3.0\\av.7.3.0";
		}
		
		String bat = "";
		if ("ORACLE".equals(dbType)) {
			bat = toOracle;
		}
		else {
			bat = toMSSQL;
		}
		
		String[] cmds = new String[] {"cd /d " + path, bat};
		runCMD(cmds);
		
		/*
		
		String program = "cmd.exe /c start " + path; //new String[]{"cmd.exe", "/c", path};
		System.out.println("Switch db bat " + program);
		try
		{
			Process p = Runtime.getRuntime().exec(program);
//			p.waitFor();
//			p.destroy();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}*/
	}
	
	public static void runCMD(String[] cmds) {
		try
		{
			Process p = Runtime.getRuntime().exec("cmd");
			OutputStream out = p.getOutputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			for (String cmd : cmds) {
				out.write( (cmd + "\r\n").getBytes() );
				out.flush();
			}
			out.write("exit\r\n".getBytes());
			out.flush();
			readConsole(reader);
			out.close();
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void readConsole(BufferedReader reader) throws Exception {
		String str = null;
		while ((str = reader.readLine()) != null)
		{
			System.out.println(str);
		}
	}
}

/*
*$Log: av-env.bat,v $
*/
