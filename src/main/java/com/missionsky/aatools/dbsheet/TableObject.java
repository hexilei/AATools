package com.missionsky.aatools.dbsheet;

/**
 * <pre>
 * 
 *  Accela Automation
 *  File: TableObject.java
 * 
 *  Accela, Inc.
 *  Copyright (C): 2011
 * 
 *  Description:
 *  TODO
 * 
 *  Notes:
 * 	$Id: V360CodeTemplates.xml 72642 2007-07-10 20:01:57Z vernon.crandall TableObject.java,v 1.4 2007/05/16 07:46:52 achievo_ken_wen Exp $ 
 * 
 *  Revision History
 *  &lt;Date&gt;,		&lt;Who&gt;,			&lt;What&gt;
 *  Jan 18, 2011			john.huang				Initial.
 *  
 * </pre>
 */
public class TableObject
{
	private String name;
	private String dataType;
	private String dataLen;
	private String nullable;
	private String pk_flag;
	private String description;
	
	public String getDataLen()
	{
		return dataLen;
	}
	public void setDataLen(String dataLen)
	{
		this.dataLen = dataLen;
	}
	public String getDataType()
	{
		return dataType;
	}
	public void setDataType(String dataType)
	{
		this.dataType = dataType;
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
	public String getNullable()
	{
		return nullable;
	}
	public void setNullable(String nullable)
	{
		this.nullable = nullable;
	}
	public String getPk_flag()
	{
		return pk_flag;
	}
	public void setPk_flag(String pk_flag)
	{
		this.pk_flag = pk_flag;
	}
	
	
}

/*
*$Log: av-env.bat,v $
*/