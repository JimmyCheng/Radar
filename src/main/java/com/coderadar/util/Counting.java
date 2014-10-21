package com.coderadar.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Counting {
	final static Logger logger = LoggerFactory.getLogger(Counting.class);

	private static final AtomicInteger remainDirectoryUrls = new AtomicInteger(0);
	private static final AtomicInteger remainFileUrls = new AtomicInteger(0);

	public int updateRemainDirectoryUrls(int number) {
		return remainDirectoryUrls.addAndGet(number);
	}

	public int updateRemainFileUrls(int number) {
		return remainFileUrls.addAndGet(number);
	}
}
