package com.coderadar.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlUtil {
	final static Logger logger = LoggerFactory.getLogger(UrlUtil.class);
	
	private List<String> releaseList = new ArrayList<String>();
	private List<String> excludeFileList = new ArrayList<String>();
	private List<String> excludeDirList = new ArrayList<String>();

	public void setReleaseList(List<String> list) {
		this.releaseList = list;
	}

	public List<String> getReleaseList() {
		return releaseList;
	}	
	
	public void setExcludeFileList(List<String> list) {
		this.excludeFileList = list;
	}

	public List<String> getExcludeFileList() {
		return excludeFileList;
	}

	public void setExcludeDirList(List<String> list) {
		this.excludeDirList = list;
	}

	public List<String> getExcludeDirList() {
		return excludeDirList;
	}

	public boolean isDirExcluded(String url) {
		for (String s: excludeDirList) {
			if (url.indexOf(s) != -1) {
				return true;
			}
		}
		return false;
	}

	public boolean isFileTypeExcluded(String type) {
		return excludeFileList.contains(type);
	}
	
	public String getFileType(String url){
		String type = StringUtils.substringAfterLast(url, ".").toLowerCase();
		
		//in case of url = "1.26/properties/key" get type ="26/properties/key"
		if(type.indexOf('/') != -1) {
			type = "unknown";
		}
		return type;
	}
	
	public String getRelease(String url){
		for (String release : releaseList) {
			if (url.contains(release)) {
				return release;
			}
		}
		return "unknown";
	}
}
