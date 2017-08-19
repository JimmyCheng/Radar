package com.coderadar.solr.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;

import com.coderadar.SolrBean;
import com.coderadar.enums.EntryType;
import com.coderadar.enums.IndexOperation;


//TODO: Jimmy: this is not a solrbean, right?
public class UrlOperation implements SolrBean {
	private static final long serialVersionUID = -5552268802048878771L;

	@Field("type")
	private String type = EntryType.AutoCheck.name();

	@Field("operation")
	private String operation = IndexOperation.NoAction.name();

	@Field("url")
	private String url;

	@Field("fetchTime")
	private long fetchTime = 0l;

	@Field("fetched")
	private boolean fetched;

	public UrlOperation() {
	}

	public UrlOperation(IndexOperation operation, String url, long fetchTime) {
		super();
		this.operation = operation.name();
		this.fetchTime = fetchTime;
		try {
			this.url = URLDecoder.decode(url, "utf8").trim();
		} catch (UnsupportedEncodingException e) {
			this.url  = url;
			e.printStackTrace();
		};
		
	}

	public static UrlOperation fromString(String s) {
		// the valid string should match this format: [state]:[url]
		// where [state] is a single char
		int pos = s.indexOf(":");
		if (pos == -1) {
			return null;
		}
		
		String opAndUrl = StringUtils.substringAfter(s, ":");
		String op= StringUtils.substringBefore(s, ":");
		String url = StringUtils.substringAfter(opAndUrl, ":");
		
		IndexOperation valueOf = null;
		try {
			valueOf = IndexOperation.valueOf(op.trim());
		} catch (Exception e) {
			return null;
		}
		// TODO : the fetch time should be wrong.
		return new UrlOperation(valueOf, url.trim(), 0l);
	}

	@Override
	public String toString() {
		return new StringBuilder(type).append(":").append(operation).append(":").append(url).toString();
	}

	public IndexOperation getOperationEnum() {
		return IndexOperation.valueOf(operation);
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		try {
			this.url = URLDecoder.decode(url, "utf8").trim();
		} catch (UnsupportedEncodingException e) {
			this.url  = url;
			e.printStackTrace();
		};
	}

	public EntryType getTypeEnum() {
		return EntryType.valueOf(type);
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getOperation() {
		return operation;
	}

	public long getFetchTime() {
		return fetchTime;
	}

	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}

	public boolean isFetched() {
		return fetched;
	}

	public void setFetched(boolean fetched) {
		this.fetched = fetched;
	}
	
}
