package com.missionsky.aatools.fidmanage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import com.missionsky.aatools.dbproxy.ConnectionUtil;
import com.missionsky.aatools.ui.Connection;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: FIDUtil.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall FIDUtil.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-3-25			john.huang				Initial.
 *  
 * </pre>
 */
public class FIDUtil
{
	private static String GET_MODULES = "SELECT pg.module_name " + 
										"FROM    PPROV_GROUP pg, rserv_prov rp, aaversion av, aaversion_module amo " + 
										"WHERE 	pg.serv_prov_code = ? " + 
										"AND		pg.status = 'ENABLE' " + 
										"AND 	rp.serv_prov_code = pg.serv_prov_code " + 
										"AND     av.aa_version = rp.aarelease_version " + 
										"AND     amo.aa_version_nbr = av.aa_version_nbr " + 
										"AND     amo.module_name = pg.module_name " + 
										"AND     av.rec_status = 'A' " + 
										"AND     amo.rec_status = 'A' " + 
										"GROUP BY amo.SOLUTION_NAME, pg.module_name " + 
										"ORDER BY amo.SOLUTION_NAME, pg.module_name";
	
	private static String GET_SELECTED_GROUP = "SELECT	pug.group_seq_nbr,pug.module_name,pg.disp_text " + 
										"FROM    PUSER_GROUP pug, PPROV_GROUP pg " + 
										"WHERE 	pug.serv_prov_code = ? " + 
										"AND		pug.serv_prov_code = pg.serv_prov_code " + 
										"AND		pug.group_seq_nbr = pg.group_seq_nbr " + 
										"AND		pug.user_name = ? " + 
										"AND		pug.module_name = ? " + 
										"AND		pug.rec_status = 'A' " + 
										"AND		pg.status LIKE 'ENABLE' " + 
										"ORDER BY pg.disp_text";
	
	private static String GET_FIDS = "SELECT 	RMENUITEM.FUNCTION_GROUP FUNCTION_GROUP, RMENUITEM.FUNCTION_TYPE FUNCTION_TYPE, RMENUITEM.FUNCTION_SUBTYPE FUNCTION_SUBTYPE, RMENUITEM.FUNCTION_CATEGORY FUNCTION_CATEGORY,       " + 
								   	"XGROUP_MENUITEM_MODULE.SERV_PROV_CODE, XGROUP_MENUITEM_MODULE.GROUP_SEQ_NBR,  " + 
									"XGROUP_MENUITEM_MODULE.MENUITEM_CODE AS txtMenuItemCode, XGROUP_MENUITEM_MODULE.READ_STAT as ACLRead,  " + 
									"XGROUP_MENUITEM_MODULE.ADD_STAT as ACLAdd, XGROUP_MENUITEM_MODULE.EDIT_STAT as ACLEdit,  " + 
									"XGROUP_MENUITEM_MODULE.DEL_STAT as ACLDelete,RMENUITEM.DISP_TEXT as txtMnItem,UPPER(RMENUITEM.DISP_TEXT) as upperMnItem,  " + 
									"RMENUITEM.FUNCTION_NAME FUNCTION_NAME, RMENUITEM.FUNCTION_VERSION, RMENUITEM.FUNCTION_GROUP_NBR FUNCTION_GROUP_NBR " + 
									"FROM	XGROUP_MENUITEM_MODULE, RMENUITEM,PPROV_MENUITEM_MODULE, " + 
										"RSERV_PROV rp, AAVERSION aa, AAVERSION_MODULE amo, AAVERSION_MENUITEM ami " + 
									"WHERE	PPROV_MENUITEM_MODULE.MENUITEM_CODE = RMENUITEM.MENUITEM_CODE " + 
									"AND		XGROUP_MENUITEM_MODULE.MENUITEM_CODE = PPROV_MENUITEM_MODULE.MENUITEM_CODE " + 
									"AND		PPROV_MENUITEM_MODULE.MODULE_NAME = ? " + 
									"AND		XGROUP_MENUITEM_MODULE.GROUP_SEQ_NBR = ? " + 
									"AND		XGROUP_MENUITEM_MODULE.serv_prov_code = ? " + 
									"AND		PPROV_MENUITEM_MODULE.serv_prov_code = ? " + 
									"AND		XGROUP_MENUITEM_MODULE.STATUS = 'ENABLE' " + 
									"AND		RMENUITEM.STATUS = 'ENABLE' " + 
									"AND		RMENUITEM.DISPLAY_FLAG = 'Y' " + 
									"AND		PPROV_MENUITEM_MODULE.STATUS='ENABLE' " + 
									"AND		PPROV_MENUITEM_MODULE.REC_STATUS='A' " + 
									"AND		PPROV_MENUITEM_MODULE.READ_STAT = 'Y' " + 
									"AND		rp.SERV_PROV_CODE = PPROV_MENUITEM_MODULE.SERV_PROV_CODE " + 
									"AND 	aa.AA_VERSION = rp.AARELEASE_VERSION " + 
									"AND		aa.REC_STATUS = 'A' " + 
									"AND		amo.AA_VERSION_NBR = aa.AA_VERSION_NBR " + 
									"AND		amo.MODULE_NAME = PPROV_MENUITEM_MODULE.MODULE_NAME " + 
									"AND		amo.REC_STATUS = 'A' " + 
									"AND		ami.MENUITEM_CODE = RMENUITEM.MENUITEM_CODE " + 
									"AND		ami.AA_VERSION_NBR = amo.AA_VERSION_NBR " + 
									"AND		ami.MODULE_NAME = amo.MODULE_ALIAS " + 
									"AND		ami.REC_STATUS = 'A' " + 
									"UNION  " + 
									"SELECT  RMENUITEM.FUNCTION_GROUP FUNCTION_GROUP, RMENUITEM.FUNCTION_TYPE FUNCTION_TYPE, RMENUITEM.FUNCTION_SUBTYPE FUNCTION_SUBTYPE, RMENUITEM.FUNCTION_CATEGORY FUNCTION_CATEGORY, " + 
										"PPROV_MENUITEM_MODULE.SERV_PROV_CODE,  4113 AS GROUP_SEQ_NBR,  " + 
										"PPROV_MENUITEM_MODULE.MENUITEM_CODE AS txtMenuItemCode, " + 
										"'N' as ACLRead, 'N' as ACLAdd, 'N' as ACLEdit, 'N' as ACLDelete,  " + 
										"RMENUITEM.DISP_TEXT as txtMnItem,UPPER(RMENUITEM.DISP_TEXT) as upperMnItem,  " + 
										"RMENUITEM.FUNCTION_NAME FUNCTION_NAME, RMENUITEM.FUNCTION_VERSION, RMENUITEM.FUNCTION_GROUP_NBR FUNCTION_GROUP_NBR " + 
									"FROM	PPROV_MENUITEM_MODULE, RMENUITEM, " + 
									   	"RSERV_PROV rp, AAVERSION aa, AAVERSION_MODULE amo, AAVERSION_MENUITEM ami " + 
									"WHERE 	PPROV_MENUITEM_MODULE.MENUITEM_CODE = RMENUITEM.MENUITEM_CODE " + 
									"AND		PPROV_MENUITEM_MODULE.serv_prov_code = ? " + 
									"AND		PPROV_MENUITEM_MODULE.MODULE_NAME = ? " + 
									"AND		PPROV_MENUITEM_MODULE.STATUS = 'ENABLE' " + 
									"AND		PPROV_MENUITEM_MODULE.REC_STATUS='A' " + 
									"AND		PPROV_MENUITEM_MODULE.READ_STAT = 'Y' " + 
									"AND		RMENUITEM.STATUS = 'ENABLE' " + 
									"AND		RMENUITEM.DISPLAY_FLAG = 'Y' " + 
									"AND		PPROV_MENUITEM_MODULE.MENUITEM_CODE NOT IN  " + 
										"(SELECT  MENUITEM_CODE  " + 
											"FROM  XGROUP_MENUITEM_MODULE  " + 
											"WHERE GROUP_SEQ_NBR=? " + 
											"AND	  serv_prov_code = ? " + 
											") " + 
									"AND		rp.SERV_PROV_CODE = PPROV_MENUITEM_MODULE.SERV_PROV_CODE " + 
									"AND    	aa.AA_VERSION = rp.AARELEASE_VERSION " + 
									"AND    	aa.REC_STATUS = 'A' " + 
									"AND  	amo.AA_VERSION_NBR = aa.AA_VERSION_NBR " + 
									"AND   	amo.MODULE_NAME = PPROV_MENUITEM_MODULE.MODULE_NAME " + 
									"AND   	amo.REC_STATUS = 'A' " + 
									"AND    	ami.MENUITEM_CODE = RMENUITEM.MENUITEM_CODE " + 
									"AND   	ami.AA_VERSION_NBR = amo.AA_VERSION_NBR " + 
									"AND    	ami.MODULE_NAME = amo.MODULE_ALIAS " + 
									"AND    	ami.REC_STATUS = 'A' " + 
									"UNION " + 
									"SELECT 	RMENUITEM.FUNCTION_GROUP FUNCTION_GROUP, RMENUITEM.FUNCTION_TYPE FUNCTION_TYPE, RMENUITEM.FUNCTION_SUBTYPE FUNCTION_SUBTYPE, RMENUITEM.FUNCTION_CATEGORY FUNCTION_CATEGORY,       " + 
									   	"XGROUP_MENUITEM_MODULE.SERV_PROV_CODE, XGROUP_MENUITEM_MODULE.GROUP_SEQ_NBR,  " + 
									   	"XGROUP_MENUITEM_MODULE.MENUITEM_CODE AS txtMenuItemCode, XGROUP_MENUITEM_MODULE.READ_STAT as ACLRead,  " + 
										"XGROUP_MENUITEM_MODULE.ADD_STAT as ACLAdd, XGROUP_MENUITEM_MODULE.EDIT_STAT as ACLEdit,  " + 
										"XGROUP_MENUITEM_MODULE.DEL_STAT as ACLDelete,RMENUITEM.DISP_TEXT as txtMnItem,UPPER(RMENUITEM.DISP_TEXT) as upperMnItem,  " + 
										"RMENUITEM.FUNCTION_NAME FUNCTION_NAME, RMENUITEM.FUNCTION_VERSION, RMENUITEM.FUNCTION_GROUP_NBR FUNCTION_GROUP_NBR " + 
									"FROM	XGROUP_MENUITEM_MODULE, RMENUITEM,PPROV_MENUITEM_MODULE, " + 
										"RSERV_PROV rp, AAVERSION aa, AAVERSION_MODULE amo " + 
									"WHERE	PPROV_MENUITEM_MODULE.MENUITEM_CODE = RMENUITEM.MENUITEM_CODE " + 
									"AND		XGROUP_MENUITEM_MODULE.MENUITEM_CODE = PPROV_MENUITEM_MODULE.MENUITEM_CODE " + 
									"AND		RMENUITEM.MENUITEM_CODE LIKE '6%' " + 
									"AND		PPROV_MENUITEM_MODULE.MODULE_NAME = ? " + 
									"AND		XGROUP_MENUITEM_MODULE.GROUP_SEQ_NBR = ? " + 
									"AND		XGROUP_MENUITEM_MODULE.serv_prov_code = ? " + 
									"AND		PPROV_MENUITEM_MODULE.serv_prov_code = ? " + 
									"AND		XGROUP_MENUITEM_MODULE.STATUS = 'ENABLE' " + 
									"AND		RMENUITEM.STATUS = 'ENABLE' " + 
									"AND		RMENUITEM.DISPLAY_FLAG = 'Y' " + 
									"AND		PPROV_MENUITEM_MODULE.STATUS='ENABLE' " + 
									"AND		PPROV_MENUITEM_MODULE.REC_STATUS='A' " + 
									"AND		PPROV_MENUITEM_MODULE.READ_STAT = 'Y' " + 
									"AND		rp.SERV_PROV_CODE = PPROV_MENUITEM_MODULE.SERV_PROV_CODE " + 
									"AND 	aa.AA_VERSION = rp.AARELEASE_VERSION " + 
									"AND    	aa.REC_STATUS = 'A' " + 
									"AND 	amo.AA_VERSION_NBR = aa.AA_VERSION_NBR " + 
									"AND   	amo.MODULE_NAME = PPROV_MENUITEM_MODULE.MODULE_NAME " + 
									"AND   	amo.REC_STATUS = 'A' " + 
									"UNION  " + 
									"SELECT  RMENUITEM.FUNCTION_GROUP FUNCTION_GROUP, RMENUITEM.FUNCTION_TYPE FUNCTION_TYPE, RMENUITEM.FUNCTION_SUBTYPE FUNCTION_SUBTYPE, RMENUITEM.FUNCTION_CATEGORY FUNCTION_CATEGORY, " + 
										"PPROV_MENUITEM_MODULE.SERV_PROV_CODE,  4113 AS GROUP_SEQ_NBR,  " + 
										"PPROV_MENUITEM_MODULE.MENUITEM_CODE AS txtMenuItemCode, " + 
										"'N' as ACLRead, 'N' as ACLAdd, 'N' as ACLEdit, 'N' as ACLDelete,  " + 
										"RMENUITEM.DISP_TEXT as txtMnItem,UPPER(RMENUITEM.DISP_TEXT) as upperMnItem,  " + 
										"RMENUITEM.FUNCTION_NAME FUNCTION_NAME, RMENUITEM.FUNCTION_VERSION, RMENUITEM.FUNCTION_GROUP_NBR FUNCTION_GROUP_NBR " + 
									"FROM  	PPROV_MENUITEM_MODULE, RMENUITEM, " + 
										"RSERV_PROV rp, AAVERSION aa, AAVERSION_MODULE amo " + 
									"WHERE 	PPROV_MENUITEM_MODULE.MENUITEM_CODE = RMENUITEM.MENUITEM_CODE " + 
									"AND  	RMENUITEM.MENUITEM_CODE LIKE '6%' " + 
									"AND		PPROV_MENUITEM_MODULE.serv_prov_code = ? " + 
									"AND		PPROV_MENUITEM_MODULE.MODULE_NAME = ? " + 
									"AND		PPROV_MENUITEM_MODULE.STATUS = 'ENABLE' " + 
									"AND		PPROV_MENUITEM_MODULE.REC_STATUS='A' " + 
									"AND		PPROV_MENUITEM_MODULE.READ_STAT = 'Y' " + 
									"AND		RMENUITEM.STATUS = 'ENABLE' " + 
									"AND		RMENUITEM.DISPLAY_FLAG = 'Y' " + 
									"AND		PPROV_MENUITEM_MODULE.MENUITEM_CODE NOT IN  " + 
										"(SELECT  MENUITEM_CODE  " + 
											"FROM  XGROUP_MENUITEM_MODULE  " + 
											"WHERE GROUP_SEQ_NBR=? " + 
											"AND	  serv_prov_code = ? " + 
											") " + 
									"AND 	rp.SERV_PROV_CODE = PPROV_MENUITEM_MODULE.SERV_PROV_CODE " + 
									"AND		aa.AA_VERSION = rp.AARELEASE_VERSION " + 
									"AND		aa.REC_STATUS = 'A' " + 
									"AND		amo.AA_VERSION_NBR = aa.AA_VERSION_NBR " + 
									"AND		amo.MODULE_NAME = PPROV_MENUITEM_MODULE.MODULE_NAME " + 
									"AND		amo.REC_STATUS = 'A' " + 
									"ORDER BY	FUNCTION_GROUP, FUNCTION_TYPE, FUNCTION_SUBTYPE, FUNCTION_CATEGORY, FUNCTION_NAME";
	
	private static String UPDATE_FID_FULL = "update XGROUP_MENUITEM_MODULE " + 
								"set READ_STAT = 'Y', ADD_STAT = 'Y', DEL_STAT = 'Y', EDIT_STAT = 'Y' " + 
								"where serv_prov_code = ? " + 
								"and GROUP_SEQ_NBR = ? " + 
								"and menuitem_code = ?";
   
	private static String UPDATE_FID_READ = "update XGROUP_MENUITEM_MODULE " + 
								"set READ_STAT = 'Y', ADD_STAT = 'N', DEL_STAT = 'N', EDIT_STAT = 'N' " + 
								"where serv_prov_code = ? " + 
								"and GROUP_SEQ_NBR = ? " + 
								"and menuitem_code = ?";
	
	private static String UPDATE_FID_NONE = "update XGROUP_MENUITEM_MODULE " + 
								"set READ_STAT = 'N', ADD_STAT = 'N', DEL_STAT = 'N', EDIT_STAT = 'N' " + 
								"where serv_prov_code = ? " + 
								"and GROUP_SEQ_NBR = ? " + 
								"and menuitem_code = ?";
   
	private static String errMessage = "";
	
	public static String[] getModules(Connection connection, String agency) {
    	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
		
		ArrayList<String> modules = new ArrayList<String>();
    	try
		{
			java.sql.Connection conn = ConnectionUtil.createDriverConnection(dbType, host, port, scheme, user, password);
			PreparedStatement stat = conn.prepareStatement(GET_MODULES);
			stat.setString(1, agency);
			
			ResultSet rs = stat.executeQuery();
			while (rs.next()) {
				modules.add(rs.getString(1));
			}
		}
		catch (Exception e)
		{
			errMessage = e.getMessage();
		}
		
		return modules.toArray(new String[0]);
	}
	
	public static ArrayList<String[]> getSelectedGroups(Connection connection, String agency, String userID) {
		userID = userID.trim().toUpperCase();
		agency = agency.trim().toUpperCase();
		
		String[] modules = getModules(connection, agency);
		ArrayList<String[]> selectedGroups = new ArrayList<String[]>();
		for (int i = 0; i < modules.length; i++)
		{
			String[] group = getSelectedGroupByModule(connection, userID, agency, modules[i]);
			if (group[0] != null) {
				selectedGroups.add(group);
			}
		}
		
		return selectedGroups;
	}
	
	public static String[] getSelectedGroupByModule(Connection connection, String userID, String agency, String module) {
	  	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
		
		String[] group = new String[3];
		
    	try
		{
			java.sql.Connection conn = ConnectionUtil.createDriverConnection(dbType, host, port, scheme, user, password);
			PreparedStatement stat = conn.prepareStatement(GET_SELECTED_GROUP);
			stat.setString(1, agency);
			stat.setString(2, userID);
			stat.setString(3, module);
			ResultSet rs = stat.executeQuery();
			if (rs.next()) {
				group[0] = rs.getString(1);
				group[1] = rs.getString(2);
				group[2] = rs.getString(3);
			}
		}
		catch (Exception e)
		{
			errMessage = e.getMessage();
		}
		return group;
	}
	
	public static ArrayList<FIDModel> getFIDs(Connection connection, String agency, String module, String groupID) {
		agency = agency.toUpperCase();
		
	  	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
		
		ArrayList<FIDModel> fids = new ArrayList<FIDModel>();
    	try
		{
			java.sql.Connection conn = ConnectionUtil.createDriverConnection(dbType, host, port, scheme, user, password);
			PreparedStatement stat = conn.prepareStatement(GET_FIDS);
			stat.setString(1, module);
			stat.setString(2, groupID);
			stat.setString(3, agency);
			stat.setString(4, agency);
			stat.setString(5, agency);
			stat.setString(6, module);
			stat.setString(7, groupID);
			stat.setString(8, agency);
			stat.setString(9, module);
			stat.setString(10, groupID);
			stat.setString(11, agency);
			stat.setString(12, agency);
			stat.setString(13, agency);
			stat.setString(14, module);
			stat.setString(15, groupID);
			stat.setString(16, agency);
			
			ResultSet rs = stat.executeQuery();
			boolean r = false;
			boolean a = false;
			boolean e = false;
			boolean d = false;
			
			while (rs.next()) {
				FIDModel fid = new FIDModel();
				fid.funGroup = rs.getString(1); // Function group
				fid.funType = rs.getString(2); // function type
				fid.subType = rs.getString(3); // function sub type
				fid.funCategory = rs.getString(4); // function category
				fid.agency = rs.getString(5); // agency
				fid.groupID = rs.getString(6); // group id
				fid.code = rs.getString(7); // XGROUP_MENUITEM_MODULE.MENUITEM_CODE
				
				
				r = "Y".equalsIgnoreCase(rs.getString(8)); // XGROUP_MENUITEM_MODULE.READ_STAT
				a =	"Y".equalsIgnoreCase(rs.getString(9)); // XGROUP_MENUITEM_MODULE.ADD_STAT
				e = "Y".equalsIgnoreCase(rs.getString(10)); // XGROUP_MENUITEM_MODULE.EDIT_STAT
				d =	"Y".equalsIgnoreCase(rs.getString(11)); // XGROUP_MENUITEM_MODULE.DEL_STAT as ACLDelete
				
				if (r && a && e && d) {
					fid.access = "FULL";
				}
				else if (r) {
					fid.access = "READ";
				}
				else {
					fid.access = "NONE";
				}
				
				fid.name = rs.getString(12);   // RMENUITEM.DISP_TEXT
				//fid[12] = rs.getString(13); // uppper name
				fid.funName = rs.getString(14); // function name
				fid.funVer = rs.getString(15); // function ver
				fid.funGroupNbr = rs.getString(16); // function group nbr
				
				fids.add(fid);
			}
		}
		catch (Exception e)
		{
			errMessage = e.getMessage();
		}
		return fids;
	}
	
	public static boolean updateFID(Connection connection, FIDModel model) {
	  	String dbType = connection.getType();
		String host = connection.getHost();
		String port = connection.getPort();
		String scheme = connection.getService();
		String user = connection.getUser();
		String password = connection.getPass();
		
    	try
		{
			java.sql.Connection conn = ConnectionUtil.createDriverConnection(dbType, host, port, scheme, user, password);
			PreparedStatement stat;
			if ("FULL".equals(model.access)) {
				stat = conn.prepareStatement(UPDATE_FID_FULL);
			}
			else if ("READ".equalsIgnoreCase(model.access)) {
				stat = conn.prepareStatement(UPDATE_FID_READ);
			}
			else {
				stat = conn.prepareStatement(UPDATE_FID_NONE);
			}
			
			stat.setString(1, model.agency);
			stat.setString(2, model.groupID);
			stat.setString(3, model.code);
			int rs = stat.executeUpdate();
			if (rs > 0) {
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
}

/*
*$Log: av-env.bat,v $
*/