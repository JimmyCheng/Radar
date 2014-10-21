package com.coderadar.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.coderadar.enums.EntryType;
import com.coderadar.enums.IndexOperation;

public class SvnFileWorker extends AbstractSvnWorker {
	
	private final Logger logger = LoggerFactory.getLogger(SvnFileWorker.class);
	
	@Override
	public void start() {
		long currentTimeMillis = System.currentTimeMillis();
		logger.info("Start makeindex for {}", urlOperation);
		IndexOperation operation = urlOperation.getOperationEnum();
		try {
			switch (operation) {
			case Delete:
				solrSourceService.deleteByUrl(urlOperation.getUrl());
				break;
			case Update:
				solrSourceService.saveSource(urlOperation.getUrl());
				break;
			default:
				logger.warn("No action will be taken for URL : {}", urlOperation);
				break;
			}
		} finally {
			logger.info("Time used count : indexing {} used {}", urlOperation, (System.currentTimeMillis() - currentTimeMillis));
		}
	}

	@Override
	public EntryType type() {
		return EntryType.File;
	}
}
