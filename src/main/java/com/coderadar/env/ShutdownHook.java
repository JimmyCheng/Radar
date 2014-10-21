package com.coderadar.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class ShutdownHook extends Thread {
	final static Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

	private TaskExecutor entryFetchTaskExecutor;

	private TaskExecutor indexMakeTaskExecutor;

	@Override
	public void run() {
		stopThreads();
	}

	private void stopThreads() {
		logger.info("Shutting down EntryFetchTaskExecutor ...");
		if (entryFetchTaskExecutor instanceof ThreadPoolTaskExecutor) {
			ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) entryFetchTaskExecutor;
			threadPoolTaskExecutor.shutdown();
		}
		if (indexMakeTaskExecutor instanceof ThreadPoolTaskExecutor) {
			ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) indexMakeTaskExecutor;
			threadPoolTaskExecutor.shutdown();
		}
	}

	public void setEntryFetchTaskExecutor(TaskExecutor entryFetchTaskExecutor) {
		this.entryFetchTaskExecutor = entryFetchTaskExecutor;
	}

	public void setIndexMakeTaskExecutor(TaskExecutor indexMakeTaskExecutor) {
		this.indexMakeTaskExecutor = indexMakeTaskExecutor;
	}

}
