package com.coderadar.repository;

import java.util.List;


import com.coderadar.enums.EntryType;
import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.util.UrlQueue;

public abstract class RepositoryDAO {
	
	public abstract String loadContent(String path);
	
	public abstract EntryType getEntryType(UrlOperation url, EntryType defaultType);

	public abstract List<UrlOperation> listDirctory(UrlOperation directoryDirPath);

	public abstract long loadIncrement(Long start, UrlQueue urlQueue,String repositoryURL);
}
