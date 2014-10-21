package com.coderadar.worker;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coderadar.enums.EntryType;
import com.coderadar.enums.IndexOperation;
import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.util.FileUtil;

public class SvnDirectoryWorker extends AbstractSvnWorker {

	final static Logger logger = LoggerFactory.getLogger(SvnDirectoryWorker.class);

	@Override
	public void start() {
		long currentTimeMillis = System.currentTimeMillis();
		logger.info("Start fetching {} at {} ", urlOperation, currentTimeMillis);
		try {
			if ((urlOperation.getOperationEnum() == IndexOperation.Delete) && (urlOperation.getTypeEnum() == EntryType.Directory)) {
				solrSourceService.deleteDirByUrl(urlOperation.getUrl());
			} else {
				listDirectory(urlOperation);
			}
		} catch (Exception e) {
			logger.warn("Failed when fetch URL {} , the exception is :", urlOperation);
			e.printStackTrace();
			logger.info("Save failed URL to failed list ");
			FileUtil.saveFailedList(urlOperation);
		} finally {
			logger.info("Full doFetch method used {}", (System.currentTimeMillis() - currentTimeMillis));
		}
	}

	private void listDirectory(UrlOperation url) {

		List<UrlOperation> targetUrlList = getTargetList(url);
		for (UrlOperation urlOperationTmp : targetUrlList) {
			urlQueue.put(urlOperationTmp);
		}
	}

	private List<UrlOperation> getTargetList(UrlOperation url) {
		return repositoryDAO.listDirctory(url);
	}

	@Override
	public EntryType type() {
		return EntryType.Directory;
	}

}
