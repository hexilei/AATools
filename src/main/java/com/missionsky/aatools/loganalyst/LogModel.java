package com.missionsky.aatools.loganalyst;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: SQLLogModel.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall SQLLogModel.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-2-4			john.huang				Initial.
 *  
 * </pre>
 */
public class LogModel implements Comparable<LogModel>
{
	public double time = 0;
	public int count = 0;
	public String content = "";
	public String seq = "";
	
	public int compareTo(LogModel arg0)
	{
		if (this.time == arg0.time) {
			return 0;
		}
		else if (this.time > arg0.time) {
			return 1;
		}
		else {
			return -1;
		}
	}
}

/*
*$Log: av-env.bat,v $
*/