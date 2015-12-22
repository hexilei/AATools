package com.missionsky.aatools.dbproxy;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: Debug.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2009
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall Debug.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2009-12-25			john.huang				Initial.
 *  
 * </pre>
 */
public class Debug
{	
	public final static boolean DEBUG = true;
	
	public final static void println(String s) {
		if (DEBUG) {
			System.out.println(s);
		}
	}
	
	public final static void err(String s) {
		System.out.println(s);
	}
}

/*
*$Log: av-env.bat,v $
*/