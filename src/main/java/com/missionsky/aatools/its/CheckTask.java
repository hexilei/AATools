package com.missionsky.aatools.its;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: CheckTask.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2013
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall CheckTask.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  Jun 24, 2013			john.huang				Initial.
 *  
 * </pre>
 */
public class CheckTask extends TimerTask
{
	private Display display;
	private String name;
	public CheckTask(Display display, String name) {
		this.display = display;
		this.name = name;
	}
	
//	@Override
	public void run()
	{
		String[] res = getBugIDs(name);
		String msg = "";
		String criticals = res[0];
		if (criticals.trim().length() > 0) {
			msg += "你有以下Critical Cases今天尚未加comment：\r\n" + criticals + "\r\n";
		}
		System.out.println("Critical cases: " + criticals);
		
		String twoDaysCases = "";
		for (int i = 1; i < res.length; i++) {
			if (check2DaysCases(res[i])) {
				twoDaysCases += res[i] + "\r\n";
				System.out.println(res[i] + " is a 2 days case");
			}
			else {
				System.out.println(res[i] + " is not a 2 days case");
			}
		}
		
		if (twoDaysCases.trim().length() > 0) {
			msg += "你有以下Cases可能已两天未加comment了：\r\n" + twoDaysCases;
		}
		if (msg.trim().length() > 0) {
			showMessage(msg);
		}
	}
	
	public String[] getBugIDs(String name) {
		HttpClient client = new DefaultHttpClient();
		HttpHost target = new HttpHost("10.50.0.81", 8088, "http");
		
		HttpGet req = new HttpGet("/buglist.cgi?assigned_to=" + name + "%40beyondsoft.com&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED");
//		HttpGet req = new HttpGet("/buglist.cgi?assigned_to=" + name + "%40beyondsoft.com&chfieldfrom=2013-06-11&chfieldto=Now");
		String criticals = " ";
		ArrayList<String> results = new ArrayList<String>();
		try {
			HttpResponse rsp = client.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			
			String resp = EntityUtils.toString(entity);
			int start = resp.indexOf("href=\"show_bug.cgi?id=");
			while (start >= 0) {
				int idEnd = resp.indexOf("\">", start);
				String buzId = resp.substring(start + "href=\"show_bug.cgi?id=".length(), idEnd);
				int bugEnd = resp.indexOf("</tr>", idEnd);
				
				int criPos = resp.indexOf("<td style=\"white-space: nowrap\">Cri", start);
				if (criPos > 0 && criPos < bugEnd) {
					criticals += buzId + "\r\n";
				}
				results.add(buzId);
				start = resp.indexOf("href=\"show_bug.cgi?id=", idEnd);
			}
		} catch (Exception e) {
			showErrorMessage("Get Critical bugs failed: " + e.getMessage());
			e.printStackTrace();
		}
		
		results.add(0, criticals);
		return results.toArray(new String[]{});
	}
	
	public boolean check2DaysCases(String id) {
		HttpClient client = new DefaultHttpClient();
		HttpHost target = new HttpHost("10.50.0.81", 8088, "http");
		HttpGet req = new HttpGet("/show_bug.cgi?id=" + id);
		try {
			HttpResponse rsp = client.execute(target, req);
			HttpEntity entity = rsp.getEntity();
			
			String resp = EntityUtils.toString(entity);
			int start = resp.indexOf("<span class=\"comment_rule\">-------</span>");
			if (start > 0) {
				int end = resp.indexOf("<span class=\"comment_rule\">-------</span>", start + 15);
				String s = resp.substring(start, end);
				int dateEndPos = s.indexOf("</i>");
				int dateBeginPos = s.lastIndexOf("</a>", dateEndPos);
				String date = s.substring(dateBeginPos + "</a>".length(), dateEndPos);
				date = date.trim();
				date = date.substring(0, date.indexOf(" "));
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Date d = df.parse(date);
				Date curD = new Date();
				Calendar cal1 = Calendar.getInstance();
				cal1.setTime(d);
				Calendar cal2 = Calendar.getInstance();
				cal2.setTime(curD);
				int d1 = cal1.get(Calendar.DAY_OF_YEAR);
				int d2 = cal2.get(Calendar.DAY_OF_YEAR);
				if (d2 - d1 >= 1) {
					return true;
				}
			}
		} catch (Exception e) {
			showErrorMessage("Get Critical bugs failed: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public void showErrorMessage(final String message) 
	{
		display.syncExec(new Thread() {
			public void run() {
				MessageDialog.openInformation(
					display.getActiveShell(),
					"Error",
					message);
			}
		});
		
	}
	
	public void showMessage(final String message) 
	{
		display.syncExec(new Thread() {
			public void run() {
				MessageDialog.openInformation(
					display.getActiveShell(),
					"温馨提示",
					message);
			}
		});
	}
	
	public static void main(String[] args)
	{
		CheckTask task = new CheckTask(null, "john.huang");
		task.run();
	}
}

/*
*$Log: av-env.bat,v $
*/