package com.missionsky.aatools.dbproxy;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.ddtek.jdbc.extensions.ExtEmbeddedConnection;


/**
 * <pre>
 * 
 *  Accela Automation
 *  File: ConnectionUtil.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2009
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall ConnectionUtil.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2009-12-21			john.huang				Initial.
 *  
 * </pre>
 */
public class ConnectionUtil
{
	public static ArrayList<byte[]> responses = new ArrayList<byte[]>();
	public static ArrayList<Socket> sockets = new ArrayList<Socket>();
	
	public static boolean COLLECT_FINISH = false;
	
	public static Connection createDriverConnection(String dbType, String host, String port, String scheme, String user, String password) throws Exception
	{
        String driver = "";
        String url = "";
        
        if(dbType.equalsIgnoreCase("ORACLE"))
        {
            driver = "oracle.jdbc.driver.OracleDriver";
            url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + scheme;
        }
        else if(dbType.equalsIgnoreCase("MSSQL"))
        {
        	driver = "com.accela.av.jdbc.sqlserver.SQLServerDriver";
            url = "jdbc:accela:sqlserver://" + host + ":" + port + ";DatabaseName=" + scheme;
        }

        Connection con = null;
		Class.forName(driver);
		DriverManager.setLoginTimeout(5);
		con = DriverManager.getConnection(url, user, password);
		
		if (dbType.equalsIgnoreCase("MSSQL")) {
			ExtEmbeddedConnection embeddedCon = (ExtEmbeddedConnection)con;
			embeddedCon.unlock("Vantage360Rules!");
		}

		return con;
	}
	
	public static String errMessage = "";
    public static boolean testConnection(com.missionsky.aatools.ui.Connection connection) {
    	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
    	try
		{
			java.sql.Connection conn = createDriverConnection(dbType, host, port, scheme, user, password);
			
			ResultSet rs = conn.createStatement().executeQuery("select * from puser");
			rs.next();
			if (rs.getString(1) != null && rs.getString(1).length() > 0) {
				return true;
			}
		}
		catch (Exception e)
		{
			errMessage = e.getMessage();
		}
		
		return false;
    }
    
    public static String getLastErrorMessage() {
    	return errMessage;
    }
    
    public static boolean collectResponses(String dbType, String host, String port, String scheme, String user, String password) {
    	try
		{
    		COLLECT_FINISH = false;
    		responses.clear();
			createDriverConnection(dbType, "127.0.0.1", Integer.toString(DBProxy.COLLECTOR_PORT), scheme, user, password);
			COLLECT_FINISH = true;
			
			return true;
		}
		catch (Exception e)
		{
			Debug.err("Failed to collect responses: " + e.getMessage());
		}
		return false;
    }
    
    public static ArrayList<byte[]> getCurrentResponses() {
    	return responses;
    }
    
    public static void addResponse(byte[] bytes) {
    	responses.add(bytes);
    }
    
    public static Socket getDBSocket(String dbType, String host, String port, String scheme, String user, String password) throws Exception {
    	try
		{
			createDriverConnection(dbType, "127.0.0.1", Integer.toString(DBProxy.PRE_PORT), scheme, user, password);
			
			if (sockets.size() > 0) {
				return sockets.remove(0);
			}
			else {
				throw new Exception("Failed to get an availalbe socket: Unknown reason");
			}
		}
		catch (Exception e)
		{
			throw new Exception("Failed to get an availalbe socket: " + e.getMessage());
		}
    }
    
    public static String[] queryAgencies(com.missionsky.aatools.ui.Connection connection) {
    	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
    	try
		{
			java.sql.Connection conn = createDriverConnection(dbType, host, port, scheme, user, password);
			ArrayList<String> agencies = new ArrayList<String>();
			ResultSet rs = conn.createStatement().executeQuery("select distinct serv_prov_code from puser");
			while (rs.next()) {
				agencies.add(rs.getString(1));
			}
			
			return agencies.toArray(new String[0]);
		}
		catch (Exception e)
		{
			errMessage = e.getMessage();
		}
		
		return null;
    }
    
    public static boolean resetPassword(com.missionsky.aatools.ui.Connection connection) {
    	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
    	try
		{
			java.sql.Connection conn = createDriverConnection(dbType, host, port, scheme, user, password);
			int rs = conn.createStatement().executeUpdate("update puser set password='d033e22ae348aeb5660fc2140aec35850c4da997'");
			if (rs > 0) {
				return true;
			}
			else {
				return false;
			}
		}
		catch (Exception e)
		{
			errMessage = e.getMessage();
		}
		
		return false;
    }
    
    public static boolean clearConsole(com.missionsky.aatools.ui.Connection connection) {
    	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
    	try
		{
			java.sql.Connection conn = createDriverConnection(dbType, host, port, scheme, user, password);
			conn.createStatement().executeUpdate("delete jconsolereceipt");
			return true;
		}
		catch (Exception e)
		{
			errMessage = e.getMessage();
		}
		
		return false;
    }
    
    public static String getProductVer(com.missionsky.aatools.ui.Connection connection) {
    	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
    	try
		{
			java.sql.Connection conn = createDriverConnection(dbType, host, port, scheme, user, password);
			ResultSet rs = conn.createStatement().executeQuery("select script_name from upgrade_scripts order by release_version desc ");
			if (rs.next()) {
				return rs.getString(1);
			}
		}
		catch (Exception e)
		{
			errMessage = e.getMessage();
		}
		
		return "Sorry, query failed!";
    }
}

/*
*$Log: av-env.bat,v $
*/