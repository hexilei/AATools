package com.missionsky.aatools.dbcenter;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.missionsky.aatools.ui.Connection;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: IntelligentFilUtil.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2011
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall IntelligentFilUtil.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  Apr 18, 2011			john.huang				Initial.
 *  
 * </pre>
 */
public class IntelligentFilUtil
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
//		String test = "St. Louis Co, MO	Done	AA	SQL2k8	6.7.0	10.50.0.134	stlouis	2008	stlouis	stlouis	04/15/2011	 	11ACC-02095	 	 	John Huang";
		String test = "Done	AA	SQL2K5	7.1.0	10.50.0.134	howard	2041	howard	howard	06/08/2011	 	11ACC-03530	12.8G";
		Connection conn = getConnInfoFromText(test);
		System.out.println(conn.getType() + " Ver: " + conn.getAAVersion() + " Host: " + conn.getHost() + " Port: "
				+ conn.getPort() + " User: " + conn.getUser() + " Pass: " + conn.getPass() + " Date: " + conn.getDate()
				+ " Comment: " + conn.getComment());
	}
	
	
	public static Connection getConnInfoFromText(String text) {
		Connection conn = new Connection();
		if (text == null) return null;
		
		if (text.toLowerCase().indexOf("oracle") >= 0) {
			conn.setType("ORACLE");
		}
		else {
			conn.setType("MSSQL");
		}
		
		StringTokenizer token = new StringTokenizer(text);

		ArrayList<String> tokens = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			String s = token.nextToken();
			if (tokens.contains(s)) { // check user/pass
				conn.setUser(s);
				conn.setPass(s);
				if ("MSSQL".equals(conn.getType())) {
					conn.setService(s);
				}
				continue;
			}
			
			tokens.add(s);
			
			if ("1521 1522 2008 1433 2041".indexOf(s) >= 0) { // check port
				conn.setPort(s);
			}
			else if ("6.5 6.6 6.7 7.0 7.1 7.2 6.5.0 6.6.0 6.7.0 7.0.0 7.0.5 7.1.0 7.2.0".indexOf(s) >= 0) { // check version
				conn.setAAVersion(s);
			}
			else if (s.indexOf("10.50.") >= 0) { // check host
				conn.setHost(s);
			}
			else if (s.indexOf("/201") >= 0 || s.indexOf("-201") >= 0) { // check date
				conn.setDate(s);
			}
			else if (s.toLowerCase().indexOf("acc-") >= 0) { // check comment
				conn.setComment(s.toUpperCase());
			}
			else if (s.indexOf("dbs") >= 0) {
				conn.setService(s);
			}
		}
		
		return conn;
	}
}

/*
*$Log: av-env.bat,v $
*/