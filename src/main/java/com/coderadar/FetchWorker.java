package com.coderadar;

import com.coderadar.enums.EntryType;
import com.coderadar.solr.bean.UrlOperation;

public interface FetchWorker extends Runnable {
	@Override
	public void run();

	public void setUrl(UrlOperation url);

	/**
	 * Release the worker
	 */
	public void release();

	/**
	 * Get the type of this worker.
	 * 
	 * @return
	 */
	public EntryType type();
}
