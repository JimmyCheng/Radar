package com.coderadar.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coderadar.solr.bean.UrlOperation;

public class UrlQueue {
	final static Logger logger = LoggerFactory.getLogger(UrlQueue.class);
	private BlockingQueue<UrlOperation> urlQueue;
	private int capacity;

	@PostConstruct
	public void initQueue() {
		urlQueue = new ArrayBlockingQueue<UrlOperation>(capacity);
	}

	/**
	 * Put one URL into the queue, blocking if the queue is full.
	 * 
	 * @param url
	 */
	public void put(UrlOperation url) {
		logger.info("UrlQueue put URL {} into queue	", url);
		try {
				urlQueue.put(url);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Collection<UrlOperation> take(int size) {
		Collection<UrlOperation> result = Collections.synchronizedSet(new HashSet<UrlOperation>(size));
		urlQueue.drainTo(result, size);
		logger.info("UrlQueue take {} URLs from queue {}", result.size(), result);
		return result;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public BlockingQueue<UrlOperation> getUrlQueue() {
		return urlQueue;
	}

	public int getCapacity() {
		return capacity;
	}

}
