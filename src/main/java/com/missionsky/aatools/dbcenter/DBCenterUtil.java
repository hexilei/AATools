package com.missionsky.aatools.dbcenter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: DBCenterUtil.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall DBCenterUtil.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-8-2			john.huang				Initial.
 *  
 * </pre>
 */
public class DBCenterUtil
{
	class Connection implements Comparable<Connection>
	{
		public String name;
		public String type;
		public String host;
		public String port;
		public String service;
		public String user;
		public String pass;
		
		// for db center
		public String AAVersion;
		public String date;
		public String comment;
		
		
		public int compareTo(Connection obj)
		{
			int rs = 0;
			if (this.AAVersion != null) {
				rs = this.AAVersion.compareTo(obj.AAVersion);
			}
			
			if (rs != 0) return rs;
			
			if (this.name != null) {
				rs = this.name.compareTo(obj.name);
			}
			
			return rs;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}
	
	public static File loadPropFile() {
//		String path = System.getProperty("user.dir");
		String path = "\\\\john-huang\\AA7.0.0_For Tracer\\av.7.0.0\\av.web\\deploy\\ROOT.war\\dbcenter";
		File file = new File (path + "/connections.properties");
		return file;
	}
	
	public ArrayList<Connection> loadConnections() {
		ArrayList<Connection> connections = new ArrayList<Connection>(); 
		
		File file = loadPropFile();
		ArrayList<String> lines = new ArrayList<String>();
		try
		{
			BufferedReader reader =  new BufferedReader(new FileReader(file));
			while (true) 
			{
				String s = reader.readLine();
				if (s == null) break;
				if (s.length() < 20) continue; // ignore the line content < 20
				lines.add(s);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// parse connections
		for (String s : lines)
		{
			try
			{
				int pos = s.indexOf(":");
				String name = s.substring(0, pos);
				
				String str = s.substring(pos + 1);
				String strUpper = str.toUpperCase();
				
				pos = strUpper.indexOf("TYPE=");
				int end = strUpper.indexOf(";", pos);
				String type = str.substring(pos + "TYPE=".length(), end);
				
				pos = strUpper.indexOf("HOST=");
				end = strUpper.indexOf(";", pos);
				String host = str.substring(pos + "HOST=".length(), end);
				
				pos = strUpper.indexOf("PORT=");
				end = strUpper.indexOf(";", pos);
				String port = str.substring(pos + "PORT=".length(), end);
				
				pos = strUpper.indexOf("SERVICE=");
				end = strUpper.indexOf(";", pos);
				String service = str.substring(pos + "SERVICE=".length(), end);
				
				pos = strUpper.indexOf("USER=");
				end = strUpper.indexOf(";", pos);
				String user = str.substring(pos + "USER=".length(), end);
				
				pos = strUpper.indexOf("PASS=");
				end = strUpper.indexOf(";", pos);
				String pass = str.substring(pos + "PASS=".length(), end);
				
				pos = strUpper.indexOf("AAVERSION=");
				end = strUpper.indexOf(";", pos);
				String AAVersion = str.substring(pos + "AAVERSION=".length(), end);
				
				pos = strUpper.indexOf("DATE=");
				end = strUpper.indexOf(";", pos);
				String date = str.substring(pos + "DATE=".length(), end);
				
				pos = strUpper.indexOf("COMMENT=");
				end = strUpper.indexOf(";", pos);
				String comment = str.substring(pos + "COMMENT=".length(), end);
				
				Connection conn = new Connection();
				conn.name = name.trim();
				conn.type = (type.trim());
				conn.host = (host.trim());
				conn.port = (port.trim());
				conn.service = (service.trim());
				conn.user = (user.trim());
				conn.pass = (pass.trim());
				
				conn.AAVersion = (AAVersion);
				conn.date = (date);
				conn.comment = (comment);
				
				connections.add(conn);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		Collections.sort(connections);
		
		return connections;
	}
	

}

/*
*$Log: av-env.bat,v $
*/