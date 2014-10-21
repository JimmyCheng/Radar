package com.coderadar.solr.bean;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.beans.Field;
import com.coderadar.SolrBean;
import com.coderadar.env.EnvParameter;

public class SourceBean implements SolrBean {
	private static final long serialVersionUID = 8867006045996407918L;

	/**
	 * The SVN URL
	 */
	@Field("svnUrl")
	private String svnUrl;

	/**
	 * The Fetch Time.
	 */
	@Field("fetchTime")
	private long fetchTime;

	// The short name of this source file.
	@Field("name")
	private String name;

	/*
	 * The SVN path of this source.
	 */
	@Field("path")
	private String path;

	private String encodedPath;

	@Field("content")
	private String content;

	@Field("release")
	private String release;

	@Field("fileType")
	private String fileType;

	private String match;

	/**
	 * Default constructor.
	 */
	public SourceBean() {
	}

	/**
	 * Constructor .
	 * @param name
	 * @param path
	 * @param svnUrl
	 */
	public SourceBean(String name, String path, String svnUrl) {
		this.name = name;
		this.path = path;
		this.svnUrl = svnUrl;
		this.fetchTime = System.currentTimeMillis();
	}

	public String getName() {
		return name;
	}

	public String getSvnUrl() {
		return svnUrl;
	}

	public String getContent() {
		return content;
	}

	public String getPath() {
		return path;
	}

	/**
	 * Return the URL of SVN viewVc
	 * @return
	 */
	public String getViewVc() {

		String root = EnvParameter.get("svn.root");
		String viewvc = EnvParameter.get("svn.viewvc");
		
		return svnUrl.replace(root, viewvc);
	}

	private String safeContent(String input) {
		String output = "";
		if(StringUtils.isNotBlank(input)){
			
			output = input.replaceAll("<li>", "%li%");
			output = output.replaceAll("</li>", "%/li%");
			output = output.replaceAll("<em>", "%em%");
			output = output.replaceAll("</em>", "%/em%");
			output = output.replaceAll("<", "&lt;").replaceAll(">", "&gt;");//replace < and >
			// replace back li and em tag, this is for highlight the match
			output = output.replaceAll("%li%", "<li>");
			output = output.replaceAll("%/li%", "</li>");
			output = output.replaceAll("%em%", "<em>");
			output = output.replaceAll("%/em%", "</em>");
		}

		return output;
	}

	public String getMatch() {
		return safeContent(match);
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "SourceInfo [svnUrl=" + svnUrl + ", content size=" + content.length() + "]";
	}

	public long getFetchTime() {
		return fetchTime;
	}

	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
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

	public void setSvnUrl(String svnUrl) {
		this.svnUrl = svnUrl;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
		try {
			this.encodedPath = URLEncoder.encode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			this.encodedPath = path;
		}
	}

	public String getEncodedPath() {
		if (StringUtils.isEmpty(encodedPath)) {
			try {
				this.encodedPath = URLEncoder.encode(path, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				this.encodedPath = path;
			}
		}
		return encodedPath;
	}
}
