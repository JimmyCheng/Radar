package com.coderadar.worker.fullIndex;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.solr.service.SolrSourceService;
import com.coderadar.solr.service.SolrUrlService;
import com.coderadar.util.UrlQueue;
import com.coderadar.util.UrlUtil;

public abstract class AbstractFullIndexMgr {

	protected UrlUtil urlUtil;

	protected SolrSourceService solrSourceService;
	
	protected SolrUrlService solrUrlService;

	protected UrlQueue urlQueue;

	protected List<UrlOperation> failList = new LinkedList<UrlOperation>();

	@Required
	public void setUrlUtil(UrlUtil urlUtil) {
		this.urlUtil = urlUtil;
	}

	@Required
	public void setSolrUrlService(SolrUrlService solrUrlService) {
		this.solrUrlService = solrUrlService;
	}
	
	@Required
	public void setSolrSourceService(SolrSourceService solrSourceService) {
		this.solrSourceService = solrSourceService;
	}
	
	@Required
	public void setUrlQueue(UrlQueue urlQueue) {
		this.urlQueue = urlQueue;
	}

	public void init() {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(15000);  //jimmy:should update to 15000
 				} catch (InterruptedException e) {
					// do nothing
				}
				startIndex();
			}

		}.start();
	}

	private void startIndex() {
		//clearAllData();  //Clear all the data and start from beginning, usally for debugging.
		
		if (solrUrlService.count("*:*") < 0) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				//do nothing
			}
		}

		// only do full Index when fileUrl is empty, that is to say the first time
		if (solrUrlService.count("*:*") == 0) {
			new Thread() {

				@Override
				public void run() {
					doFullIndex();
				}
			}.start();

		}
	}
	
	//For debugging only, will be removed.
	private void clearAllData(){
		solrUrlService.deleteAll();
		solrSourceService.deleteAll();
	}

	protected abstract void doFullIndex();
}
