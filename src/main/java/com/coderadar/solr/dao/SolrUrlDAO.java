package com.coderadar.solr.dao;

import java.io.IOException;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coderadar.solr.bean.UrlBean;

public class SolrUrlDAO {

	final static Logger logger = LoggerFactory.getLogger(SolrUrlDAO.class);

	private SolrServer solrUrlServer;

	public SolrServer getSolrUrlServer() {
		return solrUrlServer;
	}

	public void setSolrUrlServer(SolrServer solrUrlServer) {
		this.solrUrlServer = solrUrlServer;
	}
	
	public void deleteByUrl(String svnUrl) {
		deleteByQuery("url:" + svnUrl.replace(":", "\\:"));
	}

	public void deleteByQuery(String query) {
		try {
			solrUrlServer.deleteByQuery(query);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
	
	public QueryResponse sendQuery(SolrQuery query) {
		QueryResponse rsp = null;
		try{
			rsp = solrUrlServer.query(query, SolrRequest.METHOD.POST);
		}catch (Exception e) {
			logger.error("query:" + query, e);
		}
		
		return rsp;		
	}
	
	public void update(UrlBean fileUrl) {
		try {
			logger.info("update to solr: {}", fileUrl);
			solrUrlServer.addBean(fileUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void batchUpdate(Collection<UrlBean> fileUrlList) {
		try {
			logger.info("Batch update {} to solr: {}", fileUrlList.size());
			solrUrlServer.addBeans(fileUrlList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void commit() {
		try {
			solrUrlServer.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
