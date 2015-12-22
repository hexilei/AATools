package com.missionsky.aatools.dbproxy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * <pre>
 * 
 *  A collect piper to collect the response from DB server
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
public class CollectPiper extends Thread
{
	private InputStream in;
	private OutputStream out;
	private String tag;
	
	public CollectPiper(String tag, InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
		this.tag = tag;
	}
	
	public void run() {
		byte[] buf = new byte[8096];
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try
		{
			while (true) {
				while (in.available() <= 0) {
					try {sleep(100); } catch (InterruptedException e) {}
					if (ConnectionUtil.COLLECT_FINISH) {
						Debug.println("Collect responses finished!");
						return;
					}
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
				ConnectionUtil.addResponse(bout.toByteArray());
				bout.reset();
				Debug.println(tag + ": " + new String(bout.toByteArray()));
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