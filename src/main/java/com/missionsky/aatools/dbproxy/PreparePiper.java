package com.missionsky.aatools.dbproxy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * <pre>
 * 
 *  Accela Automation
 *  File: Writer.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2009
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall Writer.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2009-12-18			john.huang				Initial.
 *  
 * </pre>
 */
public class PreparePiper extends Thread
{
	private InputStream in;
	private OutputStream out;
	private String tag;
	private int count;
	
	public PreparePiper(String tag, InputStream in, OutputStream out, int count) {
		this.in = in;
		this.out = out;
		this.tag = tag;
		this.count = count;
	}
	
	public void run() {
		byte[] buf = new byte[8096];
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try
		{
			int i = 0;
			while (true) {
				if (i >= count) {
					break;
				}
				int l = in.read(buf);
				if (l < 0)
					break;
				bout.write(buf, 0, l);
				while (in.available() > 0) {
					l = in.read(buf);
					if (l < 0)
						break;
					bout.write(buf, 0, l);
				}
				
				out.write(bout.toByteArray());
				out.flush();
				i++;
				bout.reset();
				Debug.println(tag + i + ": " + new String(buf, 0, l));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

/*
*$Log: av-env.bat,v $
*/