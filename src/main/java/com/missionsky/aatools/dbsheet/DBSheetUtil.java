package com.missionsky.aatools.dbsheet;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.missionsky.aatools.dbproxy.ConnectionUtil;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: DBSheetUtil.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010-2014
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall DBSheetUtil.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-1-21			john.huang				Initial.
 *  
 * </pre>
 */
public class DBSheetUtil
{
	private static String ERR_MSG = "";
	
	private static final String DB_HOST = "192.168.0.156";
	
	private static final String DB_PORT = "1521";
	
	private static final String SID = "dbs156";
	
	private static final String DB_USER = "dbmread";
	
	private static final String DB_PWD =  "dbmread";
	
	public static ArrayList<AAObject> search(String keywords, String type) {
		String dbType = "ORACLE";
		String host = "192.168.0.110";
		String port = "1521";
		String scheme = "bptprod0727";
		String user = "accela";
		String password = "accela";
		
		keywords = keywords.toUpperCase();
		type = type.toUpperCase();
		
		// parse keywords
		StringTokenizer token = new StringTokenizer(keywords.trim(), " ");
		String sql = "";
		if (type.equalsIgnoreCase("COLUMN")) {
			sql = "select table_name as type, column_name as name, column_desc as description "
						+ " from aa_data_dic "
						+ " where ";
			
			StringBuilder condition = new StringBuilder(" (1=1"); 
			
			while (token.hasMoreTokens()) {
				String key = token.nextToken();
				condition.append(" and upper(column_name) like '%").append(key).append("%'");
			}
			
			condition.append(") and rownum <= 100");
			
			sql += condition.toString();
 
		}
		else /*if (type.equalsIgnoreCase("TABLE"))*/ {
			sql = "   select 'TABLE' as type, object_name as name, object_desc as description"
						+ " from aa_objects"
						+ " where ";

			StringBuilder condition = new StringBuilder(" (1=1"); 
			while (token.hasMoreTokens()) {
				String key = token.nextToken();
				condition.append(" and (");
				condition.append(" upper(object_name) like '%").append(key).append("%'");
				condition.append(" or upper(object_desc) like '%").append(key).append("%'");
				condition.append(" )");
			}
			
			condition.append(") and object_type='TABLE' and rownum <= 100");
			
			sql += condition.toString();
		}
		
    	try
		{
    		java.sql.Connection conn = ConnectionUtil.createDriverConnection(dbType, host, port, scheme, user, password);
			ResultSet rs = conn.createStatement().executeQuery(sql);
			ArrayList<AAObject> results = new ArrayList<AAObject>();
			while (rs.next()) {
				AAObject obj = new AAObject();
				obj.setObjectType(type);
				obj.setName(rs.getString("name"));
				obj.setType(rs.getString("type"));
				obj.setDescription(rs.getString("description"));
				results.add(obj);
			}
			
			return results;
		}
		catch (Exception e)
		{
			ERR_MSG = e.getMessage();
			return null;
		}
	}
	
	public static ArrayList<TableObject> desc(String table) {
		String dbType = "ORACLE";
		String host = "192.168.0.110";
		String port = "1521";
		String scheme = "bptprod0727";
		String user = "accela";
		String password = "accela";
		
		// parse keywords
		String sql = "";
		sql = "select table_name as type, column_name as name, data_type, data_length, nullable, pk_flag, column_desc as description "
					+ " from aa_data_dic "
					+ " where table_name = '" + table + "'";
		
    	try
		{
    		java.sql.Connection conn = ConnectionUtil.createDriverConnection(dbType, host, port, scheme, user, password);
			ResultSet rs = conn.createStatement().executeQuery(sql);
			ArrayList<TableObject> results = new ArrayList<TableObject>();
			while (rs.next()) {
				TableObject obj = new TableObject();
				obj.setName(rs.getString("name"));
				obj.setDataType(rs.getString("data_type"));
				obj.setDataLen(rs.getString("data_length"));
				obj.setNullable(rs.getString("nullable"));
				obj.setPk_flag(rs.getString("pk_flag"));
				obj.setDescription(rs.getString("description"));
				results.add(obj);
			}
			
			return results;
		}
		catch (Exception e)
		{
			ERR_MSG = e.getMessage();
			return null;
		}
	}
	
	public static void main(String[] args) {
		String dbType = "ORACLE";
		String host = "10.50.0.60";
		String port = "1522";
		String scheme = "dbs6u";
		String user = "aaqa";
		String password = "aaqa";
		
		// parse keywords
		String sql = "update BAPPSPECTABLE_VALUE A set row_index=60000 "
						+ " WHERE A.SERV_PROV_CODE = 'SACRAMENTO'"
						+ " AND A.B1_PER_ID1 = '10CAP'"
						+ " AND A.B1_PER_ID2 = '00000'"
						+ " AND A.B1_PER_ID3 = '000C8'"
						+ " AND a.table_name = 'SIGN BOARD'";
    	try
		{
    		java.sql.Connection conn = ConnectionUtil.createDriverConnection(dbType, host, port, scheme, user, password);
			int count = conn.createStatement().executeUpdate(sql);
			System.out.println("Count: " + count);
		}
		catch (Exception e)
		{
			ERR_MSG = e.getMessage();
			System.out.println(e.getMessage());
		}
	}
	
	public static String getLastErr() {
		return ERR_MSG;
	}
	
	public static List<UsingDB> getAvaliableDBs(String agency)
	{
		List<UsingDB> dbs = new ArrayList<UsingDB>();
		String SQL = "SELECT CUSTOMER, PRODUCT, DB_TYPE, VERSION_ORI, DB_IP, DB_SID, DB_NAME, DB_PORT, DB_USER, DB_PASS, DB_CREATED, DB_USAGE, VERSION_ORI, VERSION_CUR FROM DB_SHEET"
				+ " WHERE DB_STATUS = 'USING' AND LOWER(DB_NAME) LIKE '%"+agency+"%'";
		try {
			java.sql.Connection conn = ConnectionUtil.createDriverConnection("ORACLE", DB_HOST, DB_PORT, SID, DB_USER, DB_PWD);
			PreparedStatement statement = conn.prepareStatement(SQL);//
			ResultSet rs = statement.executeQuery();
			while(rs.next())
			{
				UsingDB DB = new UsingDB();
				String version = (null == rs.getString("VERSION_CUR") || "".equals(rs.getString("VERSION_CUR"))) ? rs.getString("VERSION_ORI") : rs.getString("VERSION_CUR");
				DB.setComment(rs.getString("DB_USAGE")).setCustomer(rs.getString("CUSTOMER")).setDbCreated(rs.getDate("DB_CREATED")).setDBIP(rs.getString("DB_IP"))
				.setPassword(rs.getString("DB_PASS")).setPort(rs.getString("DB_PORT")).setProduct(rs.getString("PRODUCT")).setSID(rs.getString("DB_SID"))
				.setType(rs.getString("DB_TYPE")).setUser(rs.getString("DB_USER")).setVersion(version);
				dbs.add(DB);
			}
		} catch (Exception e) {
			ERR_MSG = e.getMessage();
			System.out.println(e.getMessage());
		}
		return dbs;
	}
}

/*
*$Log: av-env.bat,v $
*/
