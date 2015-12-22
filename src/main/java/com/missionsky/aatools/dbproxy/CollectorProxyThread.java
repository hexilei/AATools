package com.missionsky.aatools.dbproxy;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.missionsky.aatools.ui.Connection;


/**
 * <pre>
 *  A test proxy to get the responses from DB server
 * 
 *  Accela Automation
 *  File: ProxyThread.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2009
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall ProxyThread.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2009-12-18			john.huang				Initial.
 *  
 * </pre>
 */
public class CollectorProxyThread extends Thread
{
	public final static int HEAD_SIZE = 34;
	public final static int HEAD_SIZE_710 = 34; //should be 58;
	public final static int TOTAL_SIZE_POS = 1;
	public final static int BODY_SIZE_POS = 25;
	
	private Socket server;
	private Socket client;
	private InputStream cin;
	private OutputStream cout;
	private InputStream sin;
	private OutputStream sout;
	
	private String dbType;
	private String host;
	private String port;
	private String ver;
	
	public CollectorProxyThread(Socket client) throws Exception
	{
		this.client = client;
		
		Connection conn = DBProxy.getCurrentConnection();
		dbType = conn.getType();
		host = conn.getHost();
		port = conn.getPort();
		ver = conn.getAAVersion();
		
		server = new Socket(host, Integer.parseInt(port));
		
		cin = new BufferedInputStream(new DataInputStream(client.getInputStream()));
		cout = new BufferedOutputStream(new DataOutputStream(client.getOutputStream()));
		sin = new BufferedInputStream(new DataInputStream(server.getInputStream()));
		sout = new BufferedOutputStream(new DataOutputStream(server.getOutputStream()));
	}

	@Override
	public void run()
	{
		try
		{
			if (dbType.equals("ORACLE")) {
				byte[] buf = new byte[8096];
				// read from client
				int l = cin.read(buf);
				if (l < 0) {
					try {
						client.close();
						server.close();
					}
					catch (Exception e) {}
					return;
				}
				
				// write to db server
				int headSize = HEAD_SIZE;
				if ("7.1.0".equals(ver)) {
					headSize = HEAD_SIZE_710;
				}
				byte[] head = new byte[headSize];
				System.arraycopy(buf, 0, head, 0, head.length);
				byte[] body = new byte[l - headSize];
				System.arraycopy(buf, headSize, body, 0, body.length);
				String str = new String(body);
				str = str.replace("127.0.0.1", host);
				str = str.replace(Integer.toString(DBProxy.COLLECTOR_PORT), port);
				body = str.getBytes();

				head[TOTAL_SIZE_POS] = (byte) (body.length + head.length);
				head[BODY_SIZE_POS] = (byte) body.length;
				sout.write(head);
				sout.write(body);
				sout.flush();

				byte[] request = new byte[body.length + head.length];
				System.arraycopy(head, 0, request, 0, head.length);
				System.arraycopy(body, 0, request, head.length, body.length);
				Debug.println("CollectorProxy -> DB Server: " + new String(head) + new String(body));
			}
			
			new CollectPiper("CollectPiper: DB Server->Collector Client", sin, cout).start();
			new CollectReadPiper("CollectPiper: Collector Client->DB Server", cin, sout).start();
		}
		catch (Exception e1)
		{
			e1.printStackTrace();
			try {
				client.close();
				server.close();
			}
			catch (Exception e) {}
		}
	}
}

/*
*$Log: av-env.bat,v $
*/