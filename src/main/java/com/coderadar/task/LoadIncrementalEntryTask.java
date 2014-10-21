package com.coderadar.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.coderadar.repository.RepositoryDAO;
import com.coderadar.util.FileUtil;
import com.coderadar.util.UrlQueue;

public class LoadIncrementalEntryTask implements Runnable {

	final static Logger logger = LoggerFactory.getLogger(LoadIncrementalEntryTask.class);

	private UrlQueue urlQueue;

	private String repositoryURL;

	private RepositoryDAO repositoryDAO;
	
	@Required
	private void setRepositoryDAO(RepositoryDAO repositoryDAO) {
		this.repositoryDAO = repositoryDAO;
	}
	
	@Override
	public void run() {
		loadIncrement();
	}

	private void loadIncrement() {
		logger.info("Start run LoadIncrementalEntryTask ...");

		Long start = FileUtil.loadRevision();

		if (start == null || start.longValue() < 0) {
			return;
		}

		long nextRevision = loadFromServer(start, urlQueue, repositoryURL);

		if (nextRevision != -1) {
			FileUtil.updateRevision(nextRevision);
		}

		logger.info("Finish run LoadIncrementalEntryTask.");
	}

	private long loadFromServer(Long start, UrlQueue urlQueue, String repositoryURL) {
		return repositoryDAO.loadIncrement(start, urlQueue, repositoryURL);
	}

	public void setRepositoryURL(String repositoryURL) {
		this.repositoryURL = repositoryURL;
	}

	public void setUrlQueue(UrlQueue queue) {
		this.urlQueue = queue;
	}

};
