package com.missionsky.aatools.dbsheet;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: AAObject.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2010
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall AAObject.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  2010-1-21			john.huang				Initial.
 *  
 * </pre>
 */
public class AAObject
{
	private String name;
	private String description;
	private String type;
	private String objectType;
	
	public String getObjectType()
	{
		return objectType;
	}
	public void setObjectType(String objectType)
	{
		this.objectType = objectType;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getType()
	{
		return type;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	
}

/*
*$Log: av-env.bat,v $
*/