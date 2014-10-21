package com.coderadar.solr.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.coderadar.enums.EntryType;
import com.coderadar.enums.IndexOperation;
import com.coderadar.repository.RepositoryDAO;
import com.coderadar.solr.bean.SourceBean;
import com.coderadar.solr.bean.UrlBean;
import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.solr.dao.SolrSourceDAO;
import com.coderadar.util.FileUtil;
import com.coderadar.util.StrUtil;
import com.coderadar.util.UrlUtil;

public class SolrSourceService {
	private final Logger logger = LoggerFactory.getLogger(SolrSourceService.class);

	private UrlUtil urlUtil;
	
	private SolrSourceDAO solrSourceDAO;
	
	private RepositoryDAO repositoryDAO;
	
	private static long lastSubmitTime = 0L;

	@Required
	public void setSolrSourceDAO(SolrSourceDAO solrSourceDAO) {
		this.solrSourceDAO = solrSourceDAO;
	}
	
	@Required
	public void setRepositoryDAO(RepositoryDAO repositoryDAO) {
		this.repositoryDAO = repositoryDAO;
	}
	
	@Required
	public void setUrlUtil(UrlUtil urlUtil) {
		this.urlUtil = urlUtil;
	}
	
	/**
	 * The cache used to store the source info.
	 */
	private static final Collection<SourceBean> SOURCEINFO_CACHE = Collections
			.synchronizedSet(new HashSet<SourceBean>());

	private static final int CACHE_SIZE = 200;

	private static final long SOLR_SUBMIT_INTERVAL = 5000;

	public void deleteAll() {
		solrSourceDAO.deleteByQuery("*:*");
		solrSourceDAO.commit();
	}
	
	public boolean saveSource(String fullUrl) {
		try {
			String relativeURL = StrUtil.getSvnRelativePath(fullUrl);
			logger.debug("Building Source Info for {}, relative path is {}", fullUrl, relativeURL);

			String name = extractFileName(fullUrl);
			SourceBean sourceInfo = new SourceBean(name, relativeURL, fullUrl);
			sourceInfo.setContent(loadContent(relativeURL));
			sourceInfo.setFileType(urlUtil.getFileType(fullUrl));;
			sourceInfo.setRelease(urlUtil.getRelease(fullUrl));
			save(sourceInfo);
			
			return true;
		} catch (Exception e) {
			logger.error("Building SourceInfo for {} throws exception " + fullUrl, e);
			return false;
		}
	}
	
	public boolean saveSource(UrlBean urlBean) {
		String fullUrl = null;
		try {
			fullUrl= urlBean.getUrl();
			String relativeURL = StrUtil.getSvnRelativePath(fullUrl);
			logger.debug("Building Source Info for {}, relative path is {}", fullUrl, relativeURL);

			String name = extractFileName(fullUrl);
			SourceBean sourceInfo = new SourceBean(name, relativeURL, fullUrl);
			sourceInfo.setContent(loadContent(relativeURL));
			sourceInfo.setFileType(urlBean.getFileType());;
			sourceInfo.setRelease(urlBean.getRelease());
			save(sourceInfo);
			
			return true;
		} catch (Exception e) {
			logger.error("Building SourceInfo for {} throws exception " + fullUrl, e);
			return false;
		}
	}

	private String loadContent(String relativeURL) {
		logger.debug("Load content of : {}", relativeURL);
		return repositoryDAO.loadContent(relativeURL);
	}
	
	private synchronized void save(SourceBean sourceInfo) {
		if (sourceInfo != null) {
			logger.info("Save source info for URL : {}", sourceInfo.getSvnUrl());
			SOURCEINFO_CACHE.add(sourceInfo);
		}

		logger.debug("sourceInfoCache size: {}", SOURCEINFO_CACHE.size());
		if (needSubmit()) {
			submitToSolr();
		}
	}

	/**
	 * Extract the file name from a URL.
	 * 
	 * @param url
	 * @return
	 */
	private static String extractFileName(String url) {
		if (url.endsWith("/")) {
			return "";
		}
		return url.substring(url.lastIndexOf("/") + 1);
	}
	

	public void deleteDirByUrl(String svnUrl) {
		solrSourceDAO.deleteByUrl(svnUrl + "*");
		solrSourceDAO.commit();
	}

	public void deleteByUrl(String svnUrl) {
		solrSourceDAO.deleteByUrl(svnUrl);
		solrSourceDAO.commit();
	}

	private void submitToSolr() {
		try {
			solrSourceDAO.batchUpdate(SOURCEINFO_CACHE);
			solrSourceDAO.commit();
			lastSubmitTime = System.currentTimeMillis();
		} catch (Throwable t) {
			logger.warn("Failed when committing SouceInfor in cache to solr exception is : ");
			t.printStackTrace();
			saveFailedUrls();
		} finally {
			SOURCEINFO_CACHE.clear();
		}
	}

	private void saveFailedUrls() {
		UrlOperation[] urls = new UrlOperation[SOURCEINFO_CACHE.size()];
		int index = 0;

		Iterator<SourceBean> iterator = SOURCEINFO_CACHE.iterator();
		while (iterator.hasNext()) {
			SourceBean si = iterator.next();
			UrlOperation uo = new UrlOperation();
			uo.setOperation(IndexOperation.Update.name());
			uo.setType(EntryType.File.name());
			uo.setUrl(si.getSvnUrl());
			urls[index] = uo;
			index++;
			iterator.remove();
		}
		FileUtil.saveFailedList(urls);
	}

	private boolean needSubmit() {
		boolean cacheIsFull = SOURCEINFO_CACHE.size() >= CACHE_SIZE;
		boolean flushTimeArrived = (System.currentTimeMillis() - lastSubmitTime) >= SOLR_SUBMIT_INTERVAL;
		boolean cacheContainsElement = SOURCEINFO_CACHE.size() > 0;

		boolean b = (cacheIsFull || flushTimeArrived) && cacheContainsElement;

		StringBuffer log = new StringBuffer();
		log.append("flushTimeArrived = ").append(flushTimeArrived).append(",cacheIsFull=").append(cacheIsFull)
				.append(",cacheContainsElement=").append(cacheContainsElement).append(", final result=").append(b);
		logger.info(" Check if need submit SourceInfo : {}", log.toString());

		return b;
	}
	
	public <T> List<T> simpleSearch(SolrQuery q, Class<T> clazz) {
		return solrSourceDAO.simpleSearch(q, clazz);
	}
	
	public <T> List<T> simpleSearch(String query, Integer start, Integer rows, Class<T> clazz) {
		return solrSourceDAO.simpleSearch(query, start, rows, clazz);
	}
	
	public List<SourceBean> search(String query, Integer start, Integer rows) {
		return solrSourceDAO.search(query, start, rows);
	}

	public List<String> facet(String query, String facetField) {
		return solrSourceDAO.facet(query, facetField);
	}
	
	public long count(String query) {
		return solrSourceDAO.count(query);
	}
	
	
}
