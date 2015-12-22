package com.missionsky.aatools.dbproxy;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import com.missionsky.aatools.ui.Connection;


/**
 * <pre>
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
public class ProxyThread extends Thread
{
	public final static int HEAD_SIZE = 34;
	public final static int TOTAL_SIZE_POS = 1;
	public final static int BODY_SIZE_POS = 25;
	
	public static String REMOTE_SERVER = "10.50.0.134"; //"10.50.0.60";
	public static int REMOTE_PORT = 2041; //1522;
	
	private Socket server;
	private Socket client;
	private InputStream cin;
	private OutputStream cout;
	private InputStream sin;
	private OutputStream sout;
	private ArrayList<byte[]> responses;
	
	public ProxyThread(Socket client) throws Exception
	{
		this.client = client;
		
		Connection conn = DBProxy.getCurrentConnection();
		if (conn == null) {
			client.close();
			throw new Exception("Not available connection setting!");
		}
		String dbType = conn.getType();
		String host = conn.getHost();
		String port = conn.getPort();
		String scheme = conn.getService();
		String user = conn.getUser();
		String password = conn.getPass();
		
		server = ConnectionUtil.getDBSocket(dbType, host, port, scheme, user, password);
		responses = ConnectionUtil.getCurrentResponses();
		cin = new BufferedInputStream(new DataInputStream(client.getInputStream()));
		cout = new BufferedOutputStream(new DataOutputStream(client.getOutputStream()));
		sin = new BufferedInputStream(new DataInputStream(server.getInputStream()));
		sout = new BufferedOutputStream(new DataOutputStream(server.getOutputStream()));
		
		DBProxy.clients.add(client);
		DBProxy.servers.add(server);
	}

	@Override
	public void run()
	{
		try
		{
			byte[] buf = new byte[8096];
//			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			for (byte[] rsp : responses)
			{
				int l = cin.read(buf);
				if (l < 0)
					break;
//				bout.write(buf, 0, l);
				while (cin.available() > 0) {
					l = cin.read(buf);
					if (l < 0)
						break;
//					bout.write(buf, 0, l);
				}

				cout.write(rsp);
				cout.flush();
//				bout.reset();
			}
			new Piper("Client -> Server", cin, sout).start();
			new Piper("Server -> Client", sin, cout).start();
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