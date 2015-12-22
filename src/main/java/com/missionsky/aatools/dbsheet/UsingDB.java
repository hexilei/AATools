package com.missionsky.aatools.dbsheet;

import java.util.Date;

public class UsingDB {
	
	private String customer;
	
	private String product;
	
	private String type;
	
	private String DBIP;
	
	private String SID;
	
	private String port;
	
	private String user;
	
	private String password;
	
	private Date dbCreated;
	
	private String comment;
	
	private String version;

	public String getCustomer() {
		return customer;
	}

	public UsingDB setCustomer(String customer) {
		this.customer = customer;
		return this;
	}

	public String getProduct() {
		return product;
	}

	public UsingDB setProduct(String product) {
		this.product = product;
		return this;
	}

	public String getType() {
		return type;
	}

	public UsingDB setType(String type) {
		this.type = type;
		return this;
	}

	public String getDBIP() {
		return DBIP;
	}

	public UsingDB setDBIP(String dBIP) {
		DBIP = dBIP;
		return this;
	}

	public String getSID() {
		return SID;
	}

	public UsingDB setSID(String sID) {
		SID = sID;
		return this;
	}

	public String getPort() {
		return port;
	}

	public UsingDB setPort(String port) {
		this.port = port;
		return this;
	}

	public String getUser() {
		return user;
	}

	public UsingDB setUser(String user) {
		this.user = user;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UsingDB setPassword(String password) {
		this.password = password;
		return this;
	}

	public Date getDbCreated() {
		return dbCreated;
	}

	public UsingDB setDbCreated(Date dbCreated) {
		this.dbCreated = dbCreated;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public UsingDB setComment(String comment) {
		this.comment = comment;
		return this;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("|");
		sb.append(this.getCustomer()).append("|").append(this.getVersion()).append("|").append(this.getType()).append("|").append(this.getDBIP())
		.append("|").append(this.getPort()).append("|").append(this.getSID()).append("|").append(this.getUser()).append("|").append(this.getPassword())
		.append("|").append(this.getComment()).append("|\r\n");
		return sb.toString();
	}
	
}
