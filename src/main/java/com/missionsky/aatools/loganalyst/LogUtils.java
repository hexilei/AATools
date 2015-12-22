package com.missionsky.aatools.loganalyst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: LogUtils.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2011
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: LogUtils.java 72642 2009-01-01 20:01:57Z ACHIEVO\frank.gui $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,			&lt;Who&gt;,			&lt;What&gt;
 *  2011-1-17		frank.gui		Initial.
 * 
 * </pre>
 */
public class LogUtils {

	/**
	 * TODO.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			reduceDiagnostics("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void reduceDiagnostics(String fileName) throws IOException {
		//fileName="E:\\Bugs\\ITS\\ITS 6594\\Logs 110111\\Biz\\server.log";
		File file = new File(fileName);
		if (!file.exists())
			throw new FileNotFoundException("Given file " + fileName
					+ " doesn't exists.");
		
		String newFileName="";
		int dotIndex =fileName.lastIndexOf('.');
		if(dotIndex>0)
		{
			newFileName = fileName.substring(0, dotIndex)+"_noDiagnostics"+fileName.substring(dotIndex);
		}
		else
			newFileName = fileName+"_noDiagnostics";
		File newFile = new File(newFileName);
		if(newFile.exists())
			newFile.delete();
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		FileOutputStream fos = new FileOutputStream(newFile);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		String line = null;
		while ((line = br.readLine()) != null) {
			if(line.indexOf("[com.accela.diagnostics.DiagnosticThread]")<0)
				bw.write(line+"\r\n");
		}
		bw.close();
		fos.close();
		br.close();
		fis.close();

	}

	public static String reduceDiagnostics(File f) {
		try
		{
			List lines = FileUtils.readLines(f);
			List<LogModel> logs = new ArrayList<LogModel>();
			StringBuffer output = new StringBuffer();
			
			for (int i = 0; i < lines.size(); i++) {
				String line = (String) lines.get(i);
				if(line.indexOf("[com.accela.diagnostics.DiagnosticThread]")<0) {
					output.append(line).append("\r\n");
				}
			}
			
			return output.toString();
		}
		catch (IOException e)
		{
			return e.getMessage();
		}
	}
}

/*
 * $Log: av-env.bat,v $
 */