package com.coderadar.svn;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;

import com.coderadar.enums.IndexOperation;
import com.coderadar.solr.bean.UrlBean;
import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.util.FileUtil;
import com.coderadar.worker.fullIndex.AbstractFullIndexMgr;

public class SvnFullIndexMgrImpl extends AbstractFullIndexMgr {

	private final Logger logger = LoggerFactory.getLogger(SvnFullIndexMgrImpl.class);

	protected static AtomicLong dirCount = new AtomicLong(0);

	protected final ExecutorService executorService = Executors.newFixedThreadPool(100);

	protected final AtomicInteger threadRunning = new AtomicInteger(0);

	private SvnRepositoryDAO svnRepositoryDAO;

	@Required
	public void setSvnRepositoryDAO(SvnRepositoryDAO svnRepositoryDAO) {
		this.svnRepositoryDAO = svnRepositoryDAO;
	}

	public SvnRepositoryDAO getSvnRepositoryDAO() {
		return svnRepositoryDAO;
	}

	@Override
	protected void doFullIndex() {
		iterateAll();
		putFailListToUrlQueue();
	}

	private void putFailListToUrlQueue() {
		Iterator<UrlOperation> iterator = failList.iterator();
		while (iterator.hasNext()) {
			UrlOperation uotmp = iterator.next();
			urlQueue.put(uotmp);
		}
	}

	private void iterateAll() {

		long latestRevision = 0L;
		try {
			SVNNodeKind nodeKind = svnRepositoryDAO.checkPath("", -1);
			if (nodeKind == SVNNodeKind.NONE) {
				logger.error("There is no entry at the initial path.");
				return;
			} else if (nodeKind == SVNNodeKind.FILE) {
				logger.error("The entry at the initial path is a file while a directory is expected.");
				return;
			}

			listEntries("");

			latestRevision = svnRepositoryDAO.getLastRevisionNO();
			FileUtil.updateRevision(latestRevision);

			try {
				Thread.sleep(10000);
				while (threadRunning.get() > 0) {
					logger.info("+++++++++++++++++++++++++++++++++++++++++++++failList size: " + failList.size());
					logger.info("threadRunning = " + threadRunning.get());
					Thread.sleep(60000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (SVNException e) {
			logger.error("error when init full index", e);
		}
	}

	private void listEntries(String path) throws SVNException {

		Collection<SVNDirEntry> entries = svnRepositoryDAO.getDir(path, -1, null, (Collection<SVNDirEntry>) null);

		for (SVNDirEntry entry : entries) {
			final String fullUrl = entry.getURL().toString();
			final String entryName = entry.getName();
			try {
				if (entry.getKind() == SVNNodeKind.FILE) {
					handleFile(fullUrl);
				} else {
					handleDir(path, entryName, fullUrl);
				}
			} catch (Exception e) {
				UrlOperation uo = new UrlOperation(IndexOperation.Update, fullUrl, System.currentTimeMillis());
				failList.add(uo);
				logger.error("error when when do svn fetch(dir or file), this url has been put to urlqueue, url:"
						+ fullUrl, e);
			}
		}
	}

	private void handleFile(final String fullUrl) {
		String fileType = urlUtil.getFileType(fullUrl);
		String release = urlUtil.getRelease(fullUrl);
		
		if(urlUtil.isFileTypeExcluded(fileType)){
			logger.info("File {} is excluded.", fullUrl);
			return;
		}
		
		logger.debug("Handling file {}", fullUrl);
		UrlBean fileUrl = new UrlBean(fullUrl, IndexOperation.Update.name());
		fileUrl.setFileType(fileType);
		fileUrl.setRelease(release);		
		
		solrUrlService.save(fileUrl);
	}

	private void handleDir(final String path, final String entryName, final String fullUrl) throws SVNException {
		if(urlUtil.isDirExcluded(path)){
			logger.info("Dir {} is excluded.", fullUrl);
			return;
		}
		
		logger.debug("Handling dir {}:{}", fullUrl, entryName);
		
		if (threadRunning.get() < 100) {
			threadRunning.incrementAndGet();
			logger.info("dir count" + dirCount.incrementAndGet());
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					try {
						listEntries((path.equals("")) ? entryName : path + "/" + entryName);
					} catch (SVNException e) {
						e.printStackTrace();
					} finally {
						threadRunning.decrementAndGet();
					}
				}
			});
		} else {
			listEntries((path.equals("")) ? entryName : path + "/" + entryName);
			logger.info("dir count" + dirCount.incrementAndGet());
		}
	}
}
