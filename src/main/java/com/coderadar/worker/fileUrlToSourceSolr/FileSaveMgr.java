package com.coderadar.worker.fileUrlToSourceSolr;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;

import com.coderadar.solr.bean.UrlBean;
import com.coderadar.solr.service.SolrUrlService;


public class FileSaveMgr {
	private ArrayBlockingQueue<UrlBean> fileUrlQueue;
	private SolrUrlService solrUrlService;
	private FileSaveWorkGenerator fileSaveWorkGenerator;
	private FileSaveWorkConsumer fileSaveWorkConsumer;

	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	public void setFileUrlQueue(ArrayBlockingQueue<UrlBean> queue) {
		this.fileUrlQueue = queue;
	}

	public void setSolrUrlService(SolrUrlService solrUrlService) {
		this.solrUrlService = solrUrlService;
	}

	public void setFileSaveWorkGenerator(FileSaveWorkGenerator generator) {
		this.fileSaveWorkGenerator = generator;
	}

	public void setFileSaveWorkConsumer(FileSaveWorkConsumer consumer) {
		this.fileSaveWorkConsumer = consumer;
	}

	public void init() {
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				boolean existNeedUpdateFiles = existNeedUpdateFiles();
				boolean lastSourceIndexIsDone = lastSourceIndexIsDone();
				System.out.println("existNeedUpdateFiles:" + existNeedUpdateFiles + ";lastSourceIndexIsDone:"
						+ lastSourceIndexIsDone);
				if (existNeedUpdateFiles && lastSourceIndexIsDone) {
					doSourceIndex();
				}
			}

		}, 1, 30, TimeUnit.MINUTES);
	}

	private void doSourceIndex() {
		fileSaveWorkGenerator.start();
		fileSaveWorkConsumer.start();
	}

	private boolean lastSourceIndexIsDone() {
		boolean isDone = true;
		for (int i = 0; i < 5; i++) {
			if ((fileUrlQueue != null) && !fileUrlQueue.isEmpty()) {
				isDone = false;
				break;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isDone;
	}

	private boolean existNeedUpdateFiles() {
		Integer start = 0;
		Integer rows = 1;
		List<UrlBean> results = solrUrlService.searchOrderByStoredTime("updated:false", start, rows);
		return CollectionUtils.isNotEmpty(results);
	}
}
