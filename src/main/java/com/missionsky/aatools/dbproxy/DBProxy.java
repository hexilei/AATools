package com.missionsky.aatools.dbproxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.missionsky.aatools.ui.Connection;


/**
 * <pre>
 * 
 *  Accela Automation
 *  File: DBProxy.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2009
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall DBProxy.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2009-12-18			john.huang				Initial.
 *  
 * </pre>
 */
public class DBProxy
{
	public static Connection currentConnection;
	public static ArrayList<Socket> clients = new ArrayList<Socket>();
	public static ArrayList<Socket> servers = new ArrayList<Socket>();
	
	public static ServerSocket serverSocket;
	public static ServerSocket prepareSocket;
	public static ServerSocket collectorSocket;
	
	public final static int PROXY_PORT = 2009;
	public final static int COLLECTOR_PORT = 2008;
	public final static int PRE_PORT = 2007;
	
	public static boolean SERVER_STARTED_SUCCESSFULLY = true;
	public static String ERR_MSG = "";
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		startCollectorProxy();
		
		try { Thread.sleep(1500);}	catch (Exception e) {}
		
		// collect responses between JDBC and db server
		Connection conn = DBProxy.getCurrentConnection();
		String dbType = conn.getType();
		String host = conn.getHost();
		String port = conn.getPort();
		String scheme = conn.getService();
		String user = conn.getUser();
		String password = conn.getPass();
		ConnectionUtil.collectResponses(dbType, host, port, scheme, user, password);
		
		startJDBCProxy();
		startPrepareDBProxy();
	}
	
	public static boolean start() {
		SERVER_STARTED_SUCCESSFULLY = true;
		startCollectorProxy();
		
		try { Thread.sleep(1500);}	catch (Exception e) {}
		
		if (!SERVER_STARTED_SUCCESSFULLY) {
			return false;
		}
		
		boolean test = startCollectDBResponses();
		if (test) {
			startJDBCProxy();
			if (!SERVER_STARTED_SUCCESSFULLY) {
				return false;
			}
			
			startPrepareDBProxy();
			if (!SERVER_STARTED_SUCCESSFULLY) {
				return false;
			}
			
			return true;
		}
		else {
			stop();
			return false;
		}
	}
	
	public static void stop() {
		setCurrentConnection(null);
		try { if (serverSocket != null) serverSocket.close(); } catch (Exception e) {}
		try { if (prepareSocket != null) prepareSocket.close(); } catch (Exception e) {}
		try { if (collectorSocket != null) collectorSocket.close(); } catch (Exception e) {}
		
		try { Thread.sleep(1000); } catch (InterruptedException e1) {}
		
		for (Socket s : clients)
		{
			try { s.close(); } catch (Exception e) {}
		}
		
		for (Socket s : servers)
		{
			try { s.close(); } catch (Exception e) {}
		}
		
		clients.clear();
		servers.clear();
		
		System.out.println("DB Proxy stopped!");
	}
	
	public static boolean startCollectDBResponses() {
		// collect responses between JDBC and db server
		Connection conn = DBProxy.getCurrentConnection();
		String dbType = conn.getType();
		String host = conn.getHost();
		String port = conn.getPort();
		String scheme = conn.getService();
		String user = conn.getUser();
		String password = conn.getPass();
		return ConnectionUtil.collectResponses(dbType, host, port, scheme, user, password);
	}
	
	public static void startJDBCProxy() {
		new Thread() {
			public void run() {
				try
				{
					serverSocket = new ServerSocket(PROXY_PORT);
					System.out.println("JDBC proxy started.");
					while(true) {
						Socket socket = serverSocket.accept();
						Debug.println("Accepted one JDBC connection");
						new ProxyThread(socket).start();
					}
				}
				catch (Exception e)
				{
					SERVER_STARTED_SUCCESSFULLY = false;
					ERR_MSG = e.getMessage();
				}
			}
		}.start();
	}
	
	public static void startCollectorProxy() {
		new Thread() {
			public void run() {
				try
				{
					collectorSocket = new ServerSocket(COLLECTOR_PORT);
					System.out.println("DB conn info collector proxy started.");
					while(true) {
						Socket socket = collectorSocket.accept();
						Debug.println("Accepted one collector connection");
						new CollectorProxyThread(socket).start();
					}
				}
				catch (Exception e)
				{
					SERVER_STARTED_SUCCESSFULLY = false;
					ERR_MSG = e.getMessage();
				}
			}
		}.start();
	}
	
	public static void startPrepareDBProxy() {
		new Thread() {
			public void run() {
				try
				{
					prepareSocket = new ServerSocket(PRE_PORT);
					System.out.println("DB Prepare proxy started.");
					while(true) {
						Socket socket = prepareSocket.accept();
						Debug.println("Accepted one prepared connection");
						new PrepareProxyThread(socket).start();
					}
				}
				catch (Exception e)
				{
					SERVER_STARTED_SUCCESSFULLY = false;
					ERR_MSG = e.getMessage();
				}
			}
		}.start();
	}
	
	public static void setCurrentConnection(Connection connection) {
		currentConnection = connection;
	}
	
	public static Connection getCurrentConnection() {
		return currentConnection;
	}
	
	public static String getLastErrMSG() {
		return ERR_MSG;
	}
}

/*
*$Log: av-env.bat,v $
*/