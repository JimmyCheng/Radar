package com.coderadar.worker.fileUrlToSourceSolr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.coderadar.solr.bean.UrlBean;
import com.coderadar.solr.service.SolrUrlService;


public class FileSaveWorkGenerator {

	private final Logger logger = LoggerFactory.getLogger(FileSaveWorkGenerator.class);

	final long threadHold = 10000;

	private ArrayBlockingQueue<UrlBean> fileUrlQueue;

	private SolrUrlService solrUrlService;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	public void setFileUrlQueue(ArrayBlockingQueue<UrlBean> fileUrlQueue) {
		this.fileUrlQueue = fileUrlQueue;
	}
	
	@Required
	public void setSolrUrlService(SolrUrlService solrUrlService) {
		this.solrUrlService = solrUrlService;
	}

	public void start() {

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				try {
					if (shouldDoQuickFind()) {
						startPushQuickFind();
					} else {
						startPush();
					}
				} catch (Exception e) {
					logger.error("error when push file url to fileUrlQueue");
				}

			}
		});
	}

	private void startPush() throws InterruptedException {
		Integer start = 0;
		Integer rows = 200;
		List<UrlBean> fileList = solrUrlService.searchOrderByStoredTime("*:*", start, rows);
		while (CollectionUtils.isNotEmpty(fileList)) {
			for (UrlBean fileUrl : fileList) {
				if (!fileUrl.isUpdated()) {
					fileUrlQueue.put(fileUrl);
				}
			}
			start = start + rows;
			System.out.println("fetch update url, start:" + start);
			fileList = solrUrlService.searchOrderByStoredTime("*:*", start, rows);
		}
	}

	private void startPushQuickFind() throws InterruptedException {
		Integer start = 0;
		Integer rows = 200;
		List<UrlBean> fileList = solrUrlService.searchOrderByStoredTime("updated:false", start, rows);
		List<UrlBean> targetList = new ArrayList<UrlBean>();
		while (CollectionUtils.isNotEmpty(fileList)) {
			for (UrlBean fileUrl : fileList) {
				targetList.add(fileUrl);
			}
			start = start + rows;
			fileList = solrUrlService.searchOrderByStoredTime("updated:false", start, rows);
		}

		for (UrlBean fileUrl : targetList) {
			fileUrlQueue.put(fileUrl);
		}
	}

	private boolean shouldDoQuickFind() {
		long needUpdatedcount = solrUrlService.count("updated:false");
		return needUpdatedcount < threadHold;
	}
}
