package com.missionsky.aatools.logplus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import com.missionsky.aatools.ui.FileUtil;


/**
 * <pre>
 * 
 *  Accela Automation
 *  File: LogsUtil.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2011
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall LogsUtil.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  Mar 30, 2011			john.huang				Initial.
 *  
 * </pre>
 */
public class LogsUtil
{
	public final static String path = "D:/AALog++/AALogger.class.btrace";
	public static boolean reset() {
		try
		{
			File f = new File(path);
			if (!f.exists()) {
				return false;
			}
			
			if (!f.canWrite()) {
				return false;
			}
			
			return FileUtil.saveBytesToFile(f, new byte[] {});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	public static String[] populate() {
		StringBuilder txtJSP = new StringBuilder();
		StringBuilder txtSelect = new StringBuilder();
		StringBuilder txtIDU = new StringBuilder();
		StringBuilder txtReq = new StringBuilder();
		StringBuilder txtSession = new StringBuilder();
		StringBuilder txtBizDomain = new StringBuilder();
		StringBuilder txtFID = new StringBuilder();
		StringBuilder txtWS = new StringBuilder();
		StringBuilder txtReport = new StringBuilder();
			
		try
		{
			InputStream in = new BufferedInputStream(new FileInputStream(path));
			List<String> lines = (List<String>)IOUtils.readLines(in, "GBK");
			in.close();
			
			boolean bSel = false;
			boolean bSQLEnd = true;
			for (String line : lines)
			{
				if (line.startsWith("--JSP: ")) {
					txtJSP.append(line).append("\r\n");
				}
				
				if (line.startsWith("--Action: ")) {
					txtJSP.append("\r\n").append(line).append("\r\n");
				}
				
				if (line.startsWith("--SQL Select")) {
					bSel = true;
					bSQLEnd = false;
				}
				
				if (line.startsWith("--SQL IDU")) {
					bSel = false;
					bSQLEnd = false;
				}
				
				if (line.startsWith("--SQL End")) {
					if (bSel) {
						txtSelect.append("\r\n");
					}
					else {
						txtIDU.append("\r\n");
					}
					bSel = false;
					bSQLEnd = true;
				}
				
				if (!line.startsWith("--") && !bSQLEnd) {
					if (bSel) {
						txtSelect.append(line).append("\r\n");
					}
					else {
						txtIDU.append(line).append("\r\n");
					}
				}
				
				if (line.startsWith("--Request")) {
					if (line.indexOf("portlet_instance") < 0 && line.indexOf("org.apache.") < 0) {
						txtReq.append(line).append("\r\n");
					}
				}
				
				if (line.startsWith("--ModuleSession")) {
					if (line.indexOf("org.apache.") < 0) {
						txtSession.append(line).append("\r\n");
					}
				}
				
				if (line.startsWith("--HttpSession")) {
					if (line.indexOf("org.apache.") < 0) {
						txtSession.append(line).append("\r\n");
					}
				}
				
				if (line.startsWith("--getBizDomain")) {
					int pos = line.indexOf(":");
					txtBizDomain.append(line.substring(pos + 1)).append("\r\n");
				}
				
				if (line.startsWith("--FID: ")) {
					txtFID.append(line).append("\r\n");
				}
				
				if (line.startsWith("--GIS GovXML: ")) {
					txtWS.append(line).append("\r\n");
				}
			}
			
			return new String[] {txtJSP.toString(), txtSelect.toString(), txtIDU.toString(), txtReq.toString(), txtSession.toString(), txtBizDomain.toString(), txtFID.toString(), txtWS.toString(), txtReport.toString()};
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}

/*
*$Log: av-env.bat,v $
*/