package com.missionsky.aatools.ui;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: Connection.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2009
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall Connection.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2009-12-28			john.huang				Initial.
 *  
 * </pre>
 */
public class Connection implements Comparable<Connection>
{
	private String name = "";
	private String type = "";
	private String host = "";
	private String port = "";
	private String service = "";
	private String user = "";
	private String pass = "";
	private boolean selected = false;
	
	// for db center
	private String AAVersion = "";
	private String date = "";
	private String comment = "";
	
	public String getHost()
	{
		return host;
	}
	public void setHost(String host)
	{
		this.host = host;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getPass()
	{
		return pass;
	}
	public void setPass(String password)
	{
		this.pass = password;
	}
	public String getPort()
	{
		return port;
	}
	public void setPort(String port)
	{
		this.port = port;
	}
	public String getService()
	{
		return service;
	}
	public void setService(String service)
	{
		this.service = service;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public String getUser()
	{
		return user;
	}
	public void setUser(String username)
	{
		this.user = username;
	}
	public boolean isSelected()
	{
		return selected;
	}
	public void setSelected(boolean selected)
	{
		this.selected = selected;
	}
	public String getAAVersion()
	{
		return AAVersion;
	}
	public void setAAVersion(String version)
	{
		AAVersion = version;
	}
	public String getComment()
	{
		return comment;
	}
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	public String getDate()
	{
		return date;
	}
	public void setDate(String date)
	{
		this.date = date;
	}
	
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Connection other = (Connection) obj;
		
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		
		return true;
	}
	public int compareTo(Connection obj)
	{
		int rs = 0;
		if (this.AAVersion != null) {
			rs = this.AAVersion.compareTo(obj.AAVersion);
		}
		
		if (rs != 0) return rs;
		
		if (this.name != null) {
			rs = this.name.compareTo(obj.name);
		}
		
		return rs;
	}
}

/*
*$Log: av-env.bat,v $
*/