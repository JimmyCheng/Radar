package com.coderadar.solr.service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import com.coderadar.enums.EntryType;
import com.coderadar.solr.bean.UrlBean;
import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.solr.dao.SolrUrlDAO;
import com.coderadar.util.FileUtil;

public class SolrUrlService {
	private final Logger logger = LoggerFactory.getLogger(SolrUrlService.class);

	private SolrUrlDAO solrUrlDAO;

	private static long lastSubmitTime = 0L;

	/**
	 * The cache used to store the source info.
	 */
	private static final Collection<UrlBean> FileUrl_CACHE = Collections.synchronizedSet(new HashSet<UrlBean>());

	private static final int CACHE_SIZE = 200;

	private static final long SOLR_SUBMIT_INTERVAL = 5000;

	@Required
	public void setSolrUrlDAO(SolrUrlDAO solrUrlDAO) {
		this.solrUrlDAO = solrUrlDAO;
	}

	/**
	 * Save the file from SVN to solr.
	 * 
	 * @param entryName
	 * @param fullUrl
	 */
	public void deleteAll() {
		solrUrlDAO.deleteByQuery("*:*");
		solrUrlDAO.commit();
	}
	
	
	public synchronized void save(UrlBean fileUrl) {
		logger.info("Save fileUrl info for URL : {}", fileUrl);
		if (fileUrl != null) {
			FileUrl_CACHE.add(fileUrl);
		}

		logger.debug("sourceInfoCache size: {}", FileUrl_CACHE.size());
		if (needSubmit()) {
			submitToFileUrlSolr();
		}
	}

	public void update(UrlBean fileUrl) {
		solrUrlDAO.update(fileUrl);
		solrUrlDAO.commit();
	}

	private void submitToFileUrlSolr() {
		try {
			solrUrlDAO.batchUpdate(FileUrl_CACHE);
			solrUrlDAO.commit();
			lastSubmitTime = System.currentTimeMillis();
		} catch (Throwable t) {
			logger.warn("Failed when committing fileUrls in cache to solr, exception is : ");
			t.printStackTrace();
			saveFailedUrls();
		} finally {
			FileUrl_CACHE.clear();
		}
	}

	private void saveFailedUrls() {
		UrlOperation[] urls = new UrlOperation[FileUrl_CACHE.size()];
		int index = 0;

		Iterator<UrlBean> iterator = FileUrl_CACHE.iterator();
		while (iterator.hasNext()) {
			UrlBean si = iterator.next();
			UrlOperation uo = new UrlOperation();
			uo.setOperation(si.getOperation());
			uo.setType(EntryType.File.name());
			uo.setUrl(si.getUrl());
			urls[index] = uo;
			index++;
			iterator.remove();
		}
		FileUtil.saveFailedList(urls);
	}

	private boolean needSubmit() {
		boolean cacheIsFull = FileUrl_CACHE.size() >= CACHE_SIZE;
		boolean flushTimeArrived = System.currentTimeMillis() - lastSubmitTime >= SOLR_SUBMIT_INTERVAL;
		boolean cacheContainsElement = FileUrl_CACHE.size() > 0;

		boolean b = (cacheIsFull || flushTimeArrived) && cacheContainsElement;

		StringBuffer log = new StringBuffer();
		log.append("flushTimeArrived = ").append(flushTimeArrived).append(",cacheIsFull=").append(cacheIsFull)
				.append(",cacheContainsElement=").append(cacheContainsElement).append(", final result=").append(b);
		logger.info(" Check if need submit SourceInfo : {}", log.toString());

		return b;
	}

	public List<UrlBean> searchOrderByStoredTime(String query, Integer start, Integer rows) {
		List<UrlBean> beans = null;
		String orderFieldName = "storedTime";
		SolrQuery sq = new SolrQuery();
		sq.setQuery(query);
		sq.setSortField(orderFieldName, ORDER.asc);
		sq.setStart(start);
		sq.setRows(rows);
		QueryResponse rsp = solrUrlDAO.sendQuery(sq);
		if (rsp != null) {
			beans = rsp.getBeans(UrlBean.class);
		}
		return beans;
	}

	public long count(String query) {
		long numberFound = -1;
		String orderFieldName = "storedTime";
		SolrQuery sq = new SolrQuery();
		sq.setQuery(query);
		sq.setSortField(orderFieldName, ORDER.asc);
		sq.setStart(0);
		sq.setRows(0);
		QueryResponse rsp = solrUrlDAO.sendQuery(sq);

		if (rsp != null) {
			numberFound = rsp.getResults().getNumFound();
		}
		return numberFound;
	}
}
