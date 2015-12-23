package com.missionsky.aatools.v360text;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;



/**
 * <pre>
 * 
 *  Accela Automation
 *  File: V360TextGenerator.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall V360TextGenerator.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  Nov 23, 2010			john.huang				Initial.
 *  
 * </pre>
 */
public class V360TextGenerator
{
	public final static String[] paths = {
		"D:\\AA6.7.0\\main-dev\\ui\\portlets\\src\\ui\\WEB-INF\\sql\\text\\",
		"D:\\AA7.0.0\\main-dev\\ui\\portlets\\src\\ui\\WEB-INF\\sql\\text\\",
		"D:\\AA7.0.5\\main-dev\\sys_config_data\\text\\",
		"D:\\AA7.1.0\\main-dev\\sys_config_data\\text\\",
		"D:\\AA7.2.0\\main-dev\\sys_config_data\\text\\",
	};
	
	public final static String[] files = {
		"v360text.csv",
		"v360text_ar_ae.csv",
		"v360text_en_au.csv",
		"v360text_es_mx.csv",
		"v360text_zh_cn.csv",
		"v360text_zh_tw.csv",
		"v360text_fr_ca.csv",
		"v360text_vi_vn.csv",
		"v360text_pt_pt.csv"
	};
	
	public final static String[] value_suffix = {
		"",
		"عل ",
		"_AU",
		"_ES",
		"_中文",
		"_繁體",
		"_FR",
		"_VN",
		""
	};
	
	public final static byte[][] value_suffix1 = {
		"".getBytes()
		
	};
	
	public final static String[] suffix = {
		",US,en",
		",AE,ar",
		",AU,en",
		",MX,es",
		",CN,zh",
		",TW,zh",
		",CA,fr",
		",VN,vi"
	};
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		for (int i = 0; i < files.length; i++) {
			addToFile("", files[i], new boolean[] {false, false, false, true});
		}
	}
	
	public static List<String> retrieveInfo(String strKey, boolean[] target) throws Exception {
		List<String> infoList = new ArrayList<String>();
		String tempValue = "";
		for (int i = 0; i < target.length; i++) {
			if (target[i]) {
				File f = new File(paths[i] + files[0]);
				if (!f.exists()) {
					continue;
				}
				InputStream in = new FileInputStream(f);
				List lines = IOUtils.readLines(in, "UTF-8");	
				
				for(int j=0; j< lines.size(); j++)
				{
					String line = (String)lines.get(j);
					if(line.indexOf("\"")>0)
					{
						String[] marks = line.split("\"");
						tempValue = marks[1] ;
						line = line.replace(marks[1], marks[1].replaceAll(",", "."));
					}
					String[] temps = line.split(",");
					if(temps[0].equalsIgnoreCase(strKey))
					{	
						infoList.add(0, temps[0]);
						if(tempValue != "")
						{
							infoList.add(1, tempValue);
						}
						else
						{
							infoList.add(1, temps[1]);
						}
						infoList.add(2, temps[2]);
						infoList.add(3, temps[3]);
						infoList.add(4, temps[4]);
						infoList.add(5, temps[5]);
						break;
					}
					else
					{
						tempValue = "";
					}
				}
				in.close();
			}
		}
		return infoList;
	}
	
	public static boolean updateText(String strKey, String strValue, String category, String recDate, String recName, boolean popCommit, boolean[] target, boolean flag) throws Exception {
		// update files by using svn command
		if (popCommit) {
			for (int i = 0; i < target.length; i++) {
				if (target[i]) {
					updateBySVN(paths[i]);
				}
			}
		}
		
		String orgValue = strValue;
		for (int i = 0; i < files.length; i++) 
		{
			StringBuilder stringBuilder = new StringBuilder();
			String valueSuffix = value_suffix[i];
			if (strValue.equals("nbsp;")) {
				valueSuffix = "";
			}
			if (files[i].indexOf("ar_ae") > 0) {
				stringBuilder.append("\"").append(valueSuffix).append(strValue).append("\"");
			}
			else {
				stringBuilder.append("\"").append(strValue).append(valueSuffix).append("\"");
			}
			strValue = stringBuilder.toString();
			flag = updateToFile(strKey, strValue, category, recDate, recName, files[i], target, flag);
			strValue = orgValue;
		}
	
		// commit files by using svn command
		if (popCommit && flag) 
		{
			for (int i = 0; i < target.length; i++) {
				if (target[i]) {
					commitBySVN(paths[i]);
				}
			}
		}
		
		return flag;
	}
	
	public static boolean updateToFile(String key, String value, String category, String recDate, String recName, String fn, boolean[] target, boolean flag) throws Exception {
		for (int i = 0; i < target.length; i++) {
			if (target[i]) {
				File f = new File(paths[i] + fn);
				if (!f.exists()) {
					continue;
				}
				InputStream in = new FileInputStream(f);
				List lines;
				if (fn.indexOf("zh_cn") > 0 /*|| fn.indexOf("fr_ca") > 0*/) {
					lines = IOUtils.readLines(in, "GBK");
				}
				else {
					lines = IOUtils.readLines(in, "UTF-8");
				}
				
				for(int j=0; j< lines.size(); j++)
				{
					String line = (String)lines.get(j);
					if(line.indexOf("\"")>0)
					{
						String[] marks = line.split("\"");
						line = line.replace(marks[1], marks[1].replaceAll(",", "."));
						
					}
					String[] temps = line.split(",");
					if(temps[0].equalsIgnoreCase(key))
					{	
						line = line.replace(temps[1], value);
						if(category != "")
						{	
							line = line.replaceFirst(temps[2], category);
						}
						if(recDate != "")
						{
							line = line.replace(temps[3], recDate);
						}
						if(recName != "")
						{
							StringBuilder tempString = new StringBuilder(line);
							int beginIndex = line.lastIndexOf(temps[4]);
							int endIndex = beginIndex + temps[4].length();
							line = tempString.replace(beginIndex, endIndex, recName).toString();					
						}
						lines.set(j,line);
						flag = true ;
						break;
					}
				}
				
				OutputStream out = new FileOutputStream(f);
				if (fn.indexOf("zh_cn") > 0 /*|| fn.indexOf("fr_ca") > 0*/) {
					IOUtils.writeLines(lines, null, out, "GBK");
				}
				else {
					IOUtils.writeLines(lines, null, out, "UTF-8");
				}
				
				in.close();
				out.flush();
				out.close();
			}
		}
		return flag ;
	}
	
	public static boolean addText(String strKey, String strValue, String category, String recDate, String recName, String recStatus, boolean popCommit, boolean[] target) throws Exception {
		// update files by using svn command
		if (popCommit) {
			for (int i = 0; i < target.length; i++) {
				if (target[i]) {
					updateBySVN(paths[i]);
				}
			}
		}
		
		for (int i = 0; i < files.length; i++) {
			StringBuilder builder = new StringBuilder();
			//workflow.email.at.label,"at_ÖÐÎÄ",Portlet - Process Email,11/19/2010,ADMIN,A,CN,zh
			
			builder.append(strKey).append(",");
			String valueSuffix = value_suffix[i];
			if (strValue.equals("nbsp;")) {
				valueSuffix = "";
			}
			if (files[i].indexOf("ar_ae") > 0) {
				builder.append("\"").append(valueSuffix).append(strValue).append("\"").append(",");
			}
			else {
				builder.append("\"").append(strValue).append(valueSuffix).append("\"").append(",");
			}
			builder.append(category).append(",");
			builder.append(recDate).append(",");
			builder.append(recName).append(",");
			builder.append(recStatus).append(suffix[i]);
			
			addToFile(builder.toString(), files[i], target);
		}
		
		// commit files by using svn command
		if (popCommit) {
			for (int i = 0; i < target.length; i++) {
				if (target[i]) {
					commitBySVN(paths[i]);
				}
			}
		}
		
		return true;
	}
	
	public static void addToFile(String v, String fn, boolean[] target) throws Exception {
		for (int i = 0; i < target.length; i++) {
			if (target[i]) {
				File f = new File(paths[i] + fn);
				if (!f.exists()) {
					continue;
				}
				InputStream in = new FileInputStream(f);
				List lines;
				if (fn.indexOf("zh_cn") > 0 /*|| fn.indexOf("fr_ca") > 0*/) {
					lines = IOUtils.readLines(in, "GBK");
				}
				else {
					lines = IOUtils.readLines(in, "UTF-8");
				}
				lines.add(lines.size(), v);
				
				OutputStream out = new FileOutputStream(f);
				if (fn.indexOf("zh_cn") > 0 /*|| fn.indexOf("fr_ca") > 0*/) {
					IOUtils.writeLines(lines, null, out, "GBK");
				}
				else {
					IOUtils.writeLines(lines, null, out, "UTF-8");
				}
				
				in.close();
				out.flush();
				out.close();
			}
		}
	}
	
	public static void updateBySVN(String path) {
		String program = "TortoiseProc /command:update /path:\"" + path + "\" /closeonend:3";
		try
		{
			Process p = Runtime.getRuntime().exec(program);
			p.waitFor();
			p.destroy();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void commitBySVN(final String path) {
		new Thread() {
			public void run() {
				String program = "TortoiseProc /command:commit /path:\"" + path + "\"";
				try
				{
					Process p = Runtime.getRuntime().exec(program);
					p.waitFor();
					p.destroy();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}.start();
	}
}

/*
*$Log: av-env.bat,v $
*/