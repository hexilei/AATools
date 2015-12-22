package com.missionsky.aatools.ui;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * File utils
 * 
 * @author john.huang
 *
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class FileUtil {
	public static File loadFile(String fn) {
		String path = System.getProperty("user.dir");
		File file = new File(path + "/" + fn);
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					return null;
				}
			} catch (IOException e) {
				return null;
			}
		}
		return file;
	}

	public static byte[] loadBytesFromFile(File file) {
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			InputStream in = new FileInputStream(file);
			byte[] buf = new byte[8192];
			while (true) {
				int l = in.read(buf);
				if (l < 0)
					break;
				bout.write(buf, 0, l);
			}

			return bout.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean saveBytesToFile(File file, byte[] data) {
		try {
			FileOutputStream out = new FileOutputStream(file);
			out.write(data);
			out.flush();
			out.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 
	 * @return Connection File
	 */
	public static File loadConnectionFile(String fileName, boolean usingUserDir)
	{
		File file = null;
		if(!usingUserDir)
		{
			String rootPath = FileUtil.class.getResource("/").getFile().toString();
			String configPath = rootPath + "/config";
			String connPath = configPath + "/" + fileName;
			File configDir = new File(configPath);
			if(!configDir.exists())
			{
				if(!configDir.mkdir())
				{
					return null;
				}
			}
			file = new File(connPath);
			if(!file.exists())
			{
				try {
					file.createNewFile();
				} catch (IOException e) {
					//
				}
			}
			return file;
		}
		else
		{
			String path = System.getProperty("user.dir");
			file = new File(path + "/" + fileName);
			if(!file.exists())
			{
				try {
					file.createNewFile();
				} catch (IOException e) {
					//
				}
			}
			return file;
		}
	}
}

/*
 * $Log: av-env.bat,v $
 */