package com.coderadar.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.util.FileUtil;
import com.coderadar.util.UrlQueue;

public class LoadFailedEntryTask implements Runnable {
	final Logger logger = LoggerFactory.getLogger(LoadIncrementalEntryTask.class);
	private UrlQueue urlQueue;

	@Override
	public void run() {
		logger.info("Starting LoadReindexableEntryTask ...");
		List<UrlOperation> list = FileUtil.loadFailedEntries();
		if (list == null || list.isEmpty()) {
			logger.info("No file need to be reindexed .");
			return;
		}
		for (UrlOperation url : list) {
			urlQueue.put(url);
		}
	}

	public void setUrlQueue(UrlQueue queue) {
		this.urlQueue = queue;
	}
}
