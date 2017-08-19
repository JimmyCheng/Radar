package com.coderadar.task;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.coderadar.FetchWorker;
import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.util.UrlQueue;
import com.coderadar.util.WorkerPool;

public class SvnFetcherTask implements Runnable {
	final static Logger logger = LoggerFactory.getLogger(SvnFetcherTask.class);

	private ThreadPoolTaskExecutor taskExecutor;

	private UrlQueue urlQueue;

	private WorkerPool workerPool;

	/**
	 * Used to paused the thread manually fron the control page.
	 */
	private volatile boolean paused = false;

	@Override
	public void run() {
		if (paused) {
			logger.info("SvnFetcherTask is paused");
			return;
		}
		logger.info("There are {} SvnFetcher task running ....", taskExecutor.getActiveCount());

		int avaliableTaskCount = getAvaliableTaskCount();

		Collection<UrlOperation> urls = urlQueue.take(avaliableTaskCount);
		Iterator<UrlOperation> iterator = urls.iterator();
		while (iterator.hasNext()) {
			FetchWorker worker = workerPool.offerWorker(iterator.next());
			taskExecutor.execute(worker);
			iterator.remove();
		}
	}

	private int getAvaliableTaskCount() {
		int activeCount = taskExecutor.getActiveCount();
		int corePoolSize = taskExecutor.getCorePoolSize();
		int avaliable = corePoolSize - activeCount;
		logger.info("Activing Threads : {} , avaliable Threads :{} .", activeCount, avaliable);
		return avaliable;
	}

	public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.taskExecutor = taskExecutor;
	}

	public void setUrlQueue(UrlQueue queue) {
		this.urlQueue = queue;
	}

	public void setWorkerPool(WorkerPool workerPool) {
		this.workerPool = workerPool;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

}
