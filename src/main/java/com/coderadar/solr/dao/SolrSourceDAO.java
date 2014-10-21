package com.coderadar.solr.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.coderadar.solr.bean.SourceBean;
import com.coderadar.util.SolrUtil;

public class SolrSourceDAO {

	final static Logger logger = LoggerFactory.getLogger(SolrSourceDAO.class);

	private SolrServer solrSourceServer;
	
	public SolrServer getSolrSourceServer() {
		return solrSourceServer;
	}

	@Required
	public void setSolrSourceServer(SolrServer solrSourceServer) {
		this.solrSourceServer = solrSourceServer;
	}
	
	public void commit() {
		try {
			solrSourceServer.commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteDirByUrl(String svnUrl) {
		String dirSvnUrl = svnUrl+"*";
		deleteByQuery("svnUrl:" + dirSvnUrl.replace(":", "\\:"));
	}
	public void deleteByUrl(String svnUrl) {
		deleteByQuery("svnUrl:" + svnUrl.replace(":", "\\:"));
	}

	public void deleteByQuery(String query) {
		try {
			solrSourceServer.deleteByQuery(query);
		} catch (SolrServerException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public <T> List<T> simpleSearch(SolrQuery q, Class<T> clazz) {
		List<T> beans = new ArrayList<T>();
		try {
			QueryResponse rsp = solrSourceServer.query(q, SolrRequest.METHOD.POST);
			beans = rsp.getBeans(clazz);
		} catch (Exception e) {
			logger.error("query:" + q, e);
		}
		return beans;
	}

	public <T> List<T> simpleSearch(String query, Integer start, Integer rows, Class<T> clazz) {
		List<T> beans = new ArrayList<T>();
		try {
			SolrQuery sq = new SolrQuery();
			sq.setQuery(query);
			sq.setStart(start);
			sq.setRows(rows);
			QueryResponse rsp = solrSourceServer.query(sq, SolrRequest.METHOD.POST);
			beans = rsp.getBeans(clazz);
		} catch (Exception e) {
			logger.error("query:" + query, e);
		}
		return beans;
	}

	public List<SourceBean> search(String query, Integer start, Integer rows) {
		List<SourceBean> beans = new ArrayList<SourceBean>();
		try {
			SolrQuery sq = new SolrQuery();
			sq.setQuery(query);
			sq.setStart(start);
			sq.setRows(rows);
			sq.addHighlightField("name");
			sq.addHighlightField("content");
			sq.setHighlight(true);
			sq.setHighlightFragsize(200);
			//			sq.setHighlightRequireFieldMatch(true);
			sq.setHighlightSimplePre("<em>");
			sq.setHighlightSimplePost("</em>");
			QueryResponse rsp = solrSourceServer.query(sq, SolrRequest.METHOD.POST);
			beans = rsp.getBeans(SourceBean.class);
			Map<String, Map<String, List<String>>> highlighting = rsp.getHighlighting();
			setHightLigth(beans, highlighting);
		} catch (Exception e) {
			logger.error("query:" + query, e);
		}
		return beans;
	}

	public List<String> facet(String query, String facetField) {
		List<String> facetNameList = new ArrayList<String>();
		try {
			SolrQuery sq = new SolrQuery();
			sq.setQuery(query);
			sq.setRows(0);
			sq.setFacetMinCount(1);
			sq.setFacet(true);
			sq.addFacetField(facetField);

			QueryResponse rsp = solrSourceServer.query(sq, SolrRequest.METHOD.POST);
			List<FacetField> facetFields = rsp.getFacetFields();
			if (facetFields != null) {
				for (FacetField facetFieldTmp : facetFields) {
					List<Count> values = facetFieldTmp.getValues();
					for (Count count : values) {
						facetNameList.add(count.getName());
					}
				}
			}
		} catch (Exception e) {
			logger.error("facetQuery:" + query, e);
		}
		return facetNameList;
	}

	public List<SourceBean> searchOrderByFetchTime(String query, Integer start, Integer rows) {
		List<SourceBean> beans = new ArrayList<SourceBean>();
		try {
			String orderFieldName = "fetchTime";
			SolrQuery sq = new SolrQuery();
			sq.setQuery(query);
			sq.setSortField(orderFieldName, ORDER.desc);
			sq.setStart(start);
			sq.setRows(rows);
			sq.addHighlightField("name");
			sq.addHighlightField("content");
			sq.setHighlight(true);
			sq.setHighlightFragsize(200);
			//			sq.setHighlightRequireFieldMatch(true);
			sq.setHighlightSimplePre("<em>");
			sq.setHighlightSimplePost("</em>");
			QueryResponse rsp = solrSourceServer.query(sq, SolrRequest.METHOD.POST);
			beans = rsp.getBeans(SourceBean.class);
			Map<String, Map<String, List<String>>> highlighting = rsp.getHighlighting();
			setHightLigth(beans, highlighting);
		} catch (Exception e) {
			logger.error("query:" + query, e);
		}
		return beans;
	}

	private void setHightLigth(List<SourceBean> sourceInfoList, Map<String, Map<String, List<String>>> highLightMap) {
		String contentFiled = "content";
		String nametFiled = "name";
		for (SourceBean sourceInfo : sourceInfoList) {
			Map<String, List<String>> highFieldMap = highLightMap.get(sourceInfo.getSvnUrl());
			if (null != highFieldMap && highFieldMap.size() > 0) {

				List<String> hightList = highFieldMap.get(contentFiled);
				if (hightList == null) {
					hightList = new ArrayList<String>();
				}
				List<String> nameHightList = highFieldMap.get(nametFiled);
				if (nameHightList != null) {
					hightList.addAll(nameHightList);
				}
				StringBuffer hightLightHtml = new StringBuffer();
				for (String hl : hightList) {
					hightLightHtml.append(SolrUtil.safeHighLightContent(hl));
				}
				sourceInfo.setMatch(hightLightHtml.toString());
			}
		}
	}

	public long count(String query) {
		try {
			SolrQuery sq = new SolrQuery();
			sq.setQuery(query);
			QueryResponse rsp = solrSourceServer.query(sq, SolrRequest.METHOD.POST);
			return rsp.getResults().getNumFound();
		} catch (Exception e) {
			logger.error("query:" + query, e);
		}
		return 0;
	}

	// TODO:use cache to store bean
	public void update(SourceBean bean) {
		try {
			solrSourceServer.addBean(bean);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void batchUpdate(Collection<?> sourceInfoPojoList) {
		try {
			logger.info("Batch update {} to solr: {}", sourceInfoPojoList.size(), sourceInfoPojoList);
			solrSourceServer.addBeans(sourceInfoPojoList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
