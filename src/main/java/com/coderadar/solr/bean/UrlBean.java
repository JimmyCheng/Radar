package com.coderadar.solr.bean;

import org.apache.solr.client.solrj.beans.Field;

import com.coderadar.SolrBean;

public class UrlBean implements SolrBean {
	private static final long serialVersionUID = 8867006045996407918L;

	/**
	 * The SVN URL
	 */
	@Field("url")
	private String url;

	/**
	 * The stored Time.
	 */
	@Field("storedTime")
	private long storedTime;

	/**
	 * The operation needed for the url. update/delete
	 */
	@Field("operation")
	private String operation;
	
	/**
	 * Mark if this url has been handled, that is to say, has been updated to source solr.
	 * Default value is false;
	 */
	@Field("updated")
	private boolean updated = false;
	
	@Field("release")
	private String release;

	@Field("fileType")
	private String fileType;
	
	//for used of finding beans from solr
	public UrlBean(){}
	
	public UrlBean(final String url, final String operation){
		this.url = url;
		this.operation = operation;
		this.storedTime = System.currentTimeMillis();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getStoredTime() {
		return storedTime;
	}

	public void setStoredTime(long storedTime) {
		this.storedTime = storedTime;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}	
	
}
