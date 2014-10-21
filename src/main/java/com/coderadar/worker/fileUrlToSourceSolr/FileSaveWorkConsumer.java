package com.coderadar.worker.fileUrlToSourceSolr;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.coderadar.solr.bean.UrlBean;
import com.coderadar.solr.service.SolrSourceService;
import com.coderadar.solr.service.SolrUrlService;


public class FileSaveWorkConsumer {

	private final Logger logger = LoggerFactory.getLogger(FileSaveWorkConsumer.class);

	private ArrayBlockingQueue<UrlBean> fileUrlQueue;

	private SolrSourceService solrSourceService;
	
	private SolrUrlService solrUrlService;

	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(30);

	public void setFileUrlQueue(ArrayBlockingQueue<UrlBean> fileUrlQueue) {
		this.fileUrlQueue = fileUrlQueue;
	}	
	
	public SolrSourceService getSolrSourceService() {
		return solrSourceService;
	}

	@Required
	public void setSolrSourceService(SolrSourceService solrSourceService) {
		this.solrSourceService = solrSourceService;
	}

	public SolrUrlService getSolrUrlService() {
		return solrUrlService;
	}

	@Required
	public void setSolrUrlService(SolrUrlService solrUrlService) {
		this.solrUrlService = solrUrlService;
	}
	
	public void start() {
		for (int i = 0; i < 30; i++) {
			executorService.scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					String url = "";
					try {
						UrlBean fileUrl = fileUrlQueue.poll(5, TimeUnit.SECONDS);
						while (fileUrl != null) {
							save(fileUrl);
							fileUrl = fileUrlQueue.poll(5, TimeUnit.SECONDS);
						}
					} catch (Throwable e) {
						logger.error("error when save fileUlr, url:" + url, e);
					}
					
				}
			}, 1, 5, TimeUnit.SECONDS);
		}
	}

	public void save(UrlBean urlBean) {
		if(solrSourceService.saveSource(urlBean)){
			urlBean.setUpdated(true);
			solrUrlService.update(urlBean);
		}
	}
}
