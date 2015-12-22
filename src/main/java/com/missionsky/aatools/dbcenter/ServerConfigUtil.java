package com.missionsky.aatools.dbcenter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.commons.io.IOUtils;

import com.missionsky.aatools.ui.Connection;



/**
 * <pre>
 * 
 *  Accela Automation
 *  File: ServerConfigUtil.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2012-2014
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall ServerConfigUtil.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  May 31, 2012			john.huang				Initial.
 *  
 * </pre>
 */
public class ServerConfigUtil {
	
	private static final String FILE_CONFIGURE = "configuration.properties";
	
	private static String basePath = null; 
	
	static
	{
		Properties props = new Properties();
		try {
			String rootPath = ServerConfigUtil.class.getResource("/").getFile().toString();
			String configPath = rootPath + "/config/" + FILE_CONFIGURE;
			File file = new File(configPath);
			if(!file.exists())
			{
				file.createNewFile();
				FileInputStream fileIn = new FileInputStream(file);
				props.load(new FileInputStream(file));
				fileIn.close();
				props.setProperty("path", "F:/AA7.3.3.X/av.7.3.3.X");
				basePath = props.getProperty("path");
				FileOutputStream fos = new FileOutputStream(file);
				props.store(fos, null);
			}
			else
			{
				FileInputStream fileIn = new FileInputStream(file);
				props.load(new FileInputStream(file));
				fileIn.close();
				basePath = props.getProperty("path");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean replaceServerConfig(Connection conn) {
		File f1 = null;
		File f2 = null;
		if ("ORACLE".equalsIgnoreCase(conn.getType())) {
			f1 = new File(basePath + "/av.biz/conf/av/ServerConfig.properties.oracle");
			if (!f1.exists()) {
				f1 = new File(basePath + "/av.biz/conf/av/ServerConfig.properties");
			}
			
			f2 = new File(basePath + "/av.cfmx/conf/av/ServerConfig.properties.oracle");
			if (!f2.exists()) {
				f2 = new File(basePath + "/av.cfmx/conf/av/ServerConfig.properties");
			}
		}
		else {
			f1 = new File(basePath + "/av.biz/conf/av/ServerConfig.properties.mssql");
			if (!f1.exists()) {
				f1 = new File(basePath + "/av.biz/conf/av/ServerConfig.properties");
			}
			
			f2 = new File(basePath + "/av.cfmx/conf/av/ServerConfig.properties.mssql");
			if (!f2.exists()) {
				f2 = new File(basePath + "/av.cfmx/conf/av/ServerConfig.properties");
			}
		}
		return replaceServerConfig(f1, conn) && replaceServerConfig(f2, conn);
	}
	
	/**
	 * 
	 * av.db.description=AA Server Database
		av.db.host=10.50.0.107
		av.db.port=1521
		av.db.servicename=dbs107
		av.db.sid=dbs107
		av.db.username=aa720sp1
		av.db.password=aa720sp1
	 * @param f
	 * @param conn
	 * @return
	 */
	public static boolean replaceServerConfig(File f, Connection conn) {
		try {
			FileInputStream in = new FileInputStream(f); 
			String content = IOUtils.toString(in);
			
			content = content.replaceFirst("\\nav\\.db\\.host=([^\\r\\n]*)", "\nav.db.host=" + conn.getHost());
			content = content.replaceFirst("\\nav\\.db\\.port=([^\\r\\n]*)", "\nav.db.port=" + conn.getPort());
			content = content.replaceFirst("\\nav\\.db\\.servicename=([^\\r\\n]*)", "\nav.db.servicename=" + conn.getService());
			content = content.replaceFirst("\\nav\\.db\\.sid=([^\\r\\n]*)", "\nav.db.sid=" + conn.getService());
			content = content.replaceFirst("\\nav\\.db\\.username=([^\\r\\n]*)", "\nav.db.username=" + conn.getUser());
			content = content.replaceFirst("\\nav\\.db\\.password=([^\\r\\n]*)", "\nav.db.password=" + conn.getPass());
			
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
			out.write(content.getBytes());
			out.close();
			in.close();
			System.out.println(content);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	public static void main(String[] args) {
		Connection conn = new Connection();
		conn.setHost("1.2.3.4");
		conn.setType("ORACLE");
		ServerConfigUtil.replaceServerConfig(conn);
	}
	
}

/*
*$Log: av-env.bat,v $
*/
