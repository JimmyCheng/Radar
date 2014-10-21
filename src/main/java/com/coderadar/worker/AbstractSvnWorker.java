package com.coderadar.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.coderadar.FetchWorker;
import com.coderadar.repository.RepositoryDAO;
import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.solr.service.SolrSourceService;
import com.coderadar.util.UrlQueue;
import com.coderadar.util.WorkerPool;

public abstract class AbstractSvnWorker implements FetchWorker {
	final static Logger logger = LoggerFactory.getLogger(AbstractSvnWorker.class);

	protected UrlOperation urlOperation;

	private WorkerPool workerPool;
	protected UrlQueue urlQueue;
	protected ThreadPoolTaskExecutor taskExecutor;
	protected SolrSourceService solrSourceService;
	protected RepositoryDAO repositoryDAO;

	protected abstract void start();

	@Required
	public void setRepositoryDAO(RepositoryDAO repositoryDAO){
		this.repositoryDAO = repositoryDAO;
	}
	
	@Override
	public void run() {
		try {
			start();
		} finally {
			release();
		}
	}

	@Override
	public void release() {
		this.urlOperation = null;
		workerPool.releaseWorker(this);
		logger.info("Release worker : {}", this);
	}

	@Override
	public void setUrl(UrlOperation urlOp) {
		this.urlOperation = urlOp;
	}

	public void setWorkerPool(WorkerPool workerPool) {
		this.workerPool = workerPool;
	}

	public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setUrlQueue(UrlQueue queue) {
		this.urlQueue = queue;
	}

	public void setSolrSourceService(SolrSourceService solrSourceService) {
		this.solrSourceService = solrSourceService;
	}

}
