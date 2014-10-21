package com.coderadar.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.coderadar.FetchWorker;
import com.coderadar.enums.EntryType;
import com.coderadar.repository.RepositoryDAO;
import com.coderadar.solr.bean.UrlOperation;

public class WorkerPool implements ApplicationContextAware {
	final static Logger logger = LoggerFactory.getLogger(WorkerPool.class);

	private int queueSize;
	private ApplicationContext applicationContext;

	private final AtomicInteger directoryWorkerCount = new AtomicInteger(0);
	private final AtomicInteger fileWorkerCount = new AtomicInteger(0);
	private BlockingQueue<FetchWorker> directoryWorkerQueue;
	private BlockingQueue<FetchWorker> fileWorkerQueue;

	private static final String FILE_WORKER_BEAN_NAME = "svnFileWorker";
	private static final String DIRECTORY_WORKER_BEAN_NAME = "svnDirectoryWorker";
	
	private RepositoryDAO repositoryDAO;
	
	@Required
	private void setRepositoryDAO(RepositoryDAO helper){
		repositoryDAO = helper;
	}

	@PostConstruct
	public void initQueues() {
		int actualQueueSize = queueSize * 2;
		directoryWorkerQueue = new ArrayBlockingQueue<FetchWorker>(actualQueueSize);
		fileWorkerQueue = new ArrayBlockingQueue<FetchWorker>(actualQueueSize);
		logger.info("directoryWorkerQueue {} , fileWorkerQueue {}", directoryWorkerQueue, fileWorkerQueue);
		logger.info("WorkerPool {}", this);
	}

	/**
	 * Put a worker back to queue .
	 * 
	 * @param worker
	 */
	public void releaseWorker(FetchWorker worker) {
		logger.debug("Release worker {}", worker);
		try {
			switch (worker.type()) {
			case Directory:
				directoryWorkerQueue.put(worker);
				return;
			case File:
				fileWorkerQueue.put(worker);
				return;
			default:
				break;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Offer a new worker to handle request , if the queue is not full create a
	 * new worker.
	 * 
	 * @param type
	 * @return
	 */
	public synchronized FetchWorker offerWorker(UrlOperation url) {
		EntryType finaltype = detectEntryType(url);
		url.setType(finaltype.name());
		FetchWorker worker = null;
		switch (finaltype) {
		case Directory:
			worker = offerWorker(directoryWorkerQueue, url);
			break;
		case File:
			worker = offerWorker(fileWorkerQueue, url);
			break;
		default:
			break;
		}
		return worker;
	}

	private EntryType detectEntryType(UrlOperation url) {
		EntryType finaltype = url.getTypeEnum();
		if ((finaltype == null) || (finaltype == EntryType.AutoCheck)) {
			try {
				finaltype = getFinalEntryType(url, finaltype);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (finaltype == null) {
			throw new RuntimeException("Can not detected the entry type of URL " + url);
		}
		return finaltype;
	}

	private EntryType getFinalEntryType(UrlOperation url, EntryType type) {
		EntryType finalType = null;
		return repositoryDAO.getEntryType(url, finalType);
	}

	private FetchWorker offerWorker(BlockingQueue<FetchWorker> queue, UrlOperation url) {
		FetchWorker worker = null;
		boolean queueFulled = isQueueFulled(url.getTypeEnum());
		try {
			if (!queueFulled) {
				logger.info("{} worker queue is not fulled , create new worker , queue size {}", url.getTypeEnum(),
						queue.size());
				queue.put(createWorker(url));
			}
			worker = queue.take();
			worker.setUrl(url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return worker;
	}

	private synchronized boolean isQueueFulled(EntryType typeEnum) {
		int createdWorker = 0;
		boolean isfull = false;
		switch (typeEnum) {
		case Directory:
			createdWorker = directoryWorkerCount.get();
			logger.debug("Created Directory worker is {} , queue size is {}", createdWorker, queueSize);
			break;
		case File:
			createdWorker = fileWorkerCount.get();
			logger.debug("Created File worker is {} , queue size is {}", createdWorker, queueSize);
			break;
		default:
			break;
		}
		isfull = (createdWorker >= queueSize ? true : false);
		logger.debug("{} is fulled ? {}", typeEnum, isfull);
		return isfull;
	}

	private synchronized FetchWorker createWorker(UrlOperation url) {
		long time = System.currentTimeMillis();
		EntryType type = url.getTypeEnum();
		FetchWorker worker = null;
		switch (type) {
		case File:
			worker = applicationContext.getBean(FILE_WORKER_BEAN_NAME, FetchWorker.class);
			fileWorkerCount.incrementAndGet();
			break;
		case Directory:
			worker = applicationContext.getBean(DIRECTORY_WORKER_BEAN_NAME, FetchWorker.class);
			directoryWorkerCount.incrementAndGet();
			break;
		default:
			break;
		}
		worker.setUrl(url);
		logger.debug("Create new Worker used {}", (System.currentTimeMillis() - time));
		return worker;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void setDirectoryWorkerQueue(BlockingQueue<FetchWorker> directoryWorkerQueue) {
		this.directoryWorkerQueue = directoryWorkerQueue;
	}

	public void setFileWorkerQueue(BlockingQueue<FetchWorker> fileWorkerQueue) {
		this.fileWorkerQueue = fileWorkerQueue;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public BlockingQueue<FetchWorker> getDirectoryWorkerQueue() {
		return directoryWorkerQueue;
	}

	public BlockingQueue<FetchWorker> getFileWorkerQueue() {
		return fileWorkerQueue;
	}
}
