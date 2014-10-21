package com.coderadar.svn;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.coderadar.enums.EntryType;
import com.coderadar.enums.IndexOperation;
import com.coderadar.repository.RepositoryDAO;
import com.coderadar.solr.bean.UrlOperation;
import com.coderadar.util.StrUtil;
import com.coderadar.util.UrlQueue;

public class SvnRepositoryDAO extends RepositoryDAO {

	final static Logger logger = LoggerFactory.getLogger(SvnRepositoryDAO.class);

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private SvnRepositoryPool svnRepositoryPool;

	private SvnClientManagerFactory svnClientManagerFactory;

	@Required
	public void setSvnRepositoryPool(SvnRepositoryPool pool) {
		svnRepositoryPool = pool;
	}

	public SvnRepositoryPool getSvnRepositoryPool() {
		return svnRepositoryPool;
	}

	@Required
	public void setSvnClientManagerFactory(SvnClientManagerFactory factory) {
		svnClientManagerFactory = factory;
	}

	@Override
	public String loadContent(String path) {
		SvnOutputStream out = new SvnOutputStream();
		boolean fileTooLarge = false;
		try {
			getRepository().getFile(path, -2L, null, out);
		} catch (SVNException e) {
			e.printStackTrace();
		} catch (FileTooLargeException e) {
			fileTooLarge = true;
		}

		StringBuffer sb = new StringBuffer();
		if (fileTooLarge) {
			sb = new StringBuffer("(LargeFile) This file is more than 1M, so will not be indexed.");
		} else {
			readContent(out, sb);
		}

		return sb.toString();
	}

	private void readContent(SvnOutputStream out, StringBuffer sb) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(
				out.toByteArray())));

		String s;
		long length = 0;

		try {
			while ((s = bufferedReader.readLine()) != null) {
				sb.append(s).append(LINE_SEPARATOR);
				length = length + s.length();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
				out.close();
				out = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private SVNRepository getRepository() {
		return svnRepositoryPool.getSvnRepository();
	}

	@Override
	public EntryType getEntryType(UrlOperation url, EntryType defaultType) {
		SVNNodeKind entry;
		EntryType finalType = defaultType;
		try {
			entry = getRepository().checkPath(StrUtil.getSvnRelativePath(url.getUrl()), -1);
			if (entry == SVNNodeKind.DIR) {
				finalType = EntryType.Directory;
			} else if (entry == SVNNodeKind.FILE) {
				finalType = EntryType.File;
			}
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return finalType;
	}

	@Override
	public List<UrlOperation> listDirctory(UrlOperation directoryDirPath) {
		List<UrlOperation> targetUrlList = new ArrayList<UrlOperation>();
		try {
			Collection<SVNDirEntry> entries = loadEntries(directoryDirPath);
			Iterator<SVNDirEntry> iterator = entries.iterator();

			while (iterator.hasNext()) {
				SVNDirEntry entry = iterator.next();
				SVNNodeKind nodeKind = entry.getKind();
				// inherit the operation from the parent URL.
				String urlString = entry.getURL().toString();

				UrlOperation svnUrl = new UrlOperation(directoryDirPath.getOperationEnum(), urlString,
						System.currentTimeMillis());

				if (nodeKind == SVNNodeKind.FILE) {
					svnUrl.setType(EntryType.File.name());
					targetUrlList.add(svnUrl);
				} else if (nodeKind == SVNNodeKind.DIR) {
					svnUrl.setType(EntryType.Directory.name());
					targetUrlList.add(svnUrl);
				} else {
					logger.warn("Note kind of URL : {} is {} , not File or Directory ", svnUrl.getUrl(), nodeKind);
					continue;
				}
			}
		} catch (SVNException e) {
			logger.error("Error when listDirctory", e);
		}
		return targetUrlList;
	}

	private Collection<SVNDirEntry> loadEntries(UrlOperation url) throws SVNException {
		Collection<SVNDirEntry> entries = Collections.synchronizedList(new ArrayList<SVNDirEntry>());
		String fullUrl = url.getUrl();
		String relativeUrl = StrUtil.getSvnRelativePath(url.getUrl());

		long time = System.currentTimeMillis();
		getRepository().getDir(relativeUrl, -1, null, entries);
		logger.info("Time used count # use repository to getDir used {}", (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		logger.info("There are {} children for URL : {}", entries.size(), fullUrl.toString());
		return entries;
	}

	@Override
	public long loadIncrement(Long start, UrlQueue urlQueue, String repositoryURL) {
		HistoryLogEntryHandler handler = new HistoryLogEntryHandler();

		long end = -1;

		long nextRevision = -1;

		long tmpRevision;

		boolean bContinue = true;

		try {
			getHistoryEntries(svnClientManagerFactory.createSvnClientManager(), repositoryURL, start, end, handler);
		} catch (SVNException e) {
			bContinue = false;
		}

		if (bContinue) {
			List<SVNLogEntry> logEntries = handler.getSvnLogEntry();
			for (SVNLogEntry logEntry : logEntries) {
				Map<String, SVNLogEntryPath> changedPaths = logEntry.getChangedPaths();
				tmpRevision = logEntry.getRevision();
				if (tmpRevision > nextRevision) {
					nextRevision = tmpRevision;
				}
				Iterator<Map.Entry<String, SVNLogEntryPath>> iter = changedPaths.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, SVNLogEntryPath> entry = iter.next();
					String url = getFullUrl(entry.getKey(), repositoryURL);
					SVNLogEntryPath logEntryPath = entry.getValue();
					if ((logEntryPath.getKind() == SVNNodeKind.FILE)) {
						UrlOperation uo = new UrlOperation(getIndexOperationType(logEntryPath.getType()), url,
								new Date().getTime());
						uo.setType(EntryType.File.name());
						urlQueue.put(uo);
					} else if ((logEntryPath.getKind() == SVNNodeKind.DIR)
							&& (logEntryPath.getType() == SVNLogEntryPath.TYPE_DELETED)) {
						UrlOperation uo = new UrlOperation(IndexOperation.Delete, url, new Date().getTime());
						uo.setType(EntryType.Directory.name());
						urlQueue.put(uo);
					} else {
						logger.info("modified entry not suit to updated, entry url:{}", url);
					}

				}
			}
		}
		return nextRevision;
	}

	private String getFullUrl(String path, String repositoryURL) {
		String relativePath = path;
		String pathSeparator = "/";
		if (repositoryURL.endsWith(pathSeparator) && path.startsWith(pathSeparator)) {
			relativePath = StringUtils.substringAfter(path, pathSeparator);
		}
		return repositoryURL + relativePath;
	}

	private void getHistoryEntries(SVNClientManager clientManager, String repositoryURL, long start, long end,
			ISVNLogEntryHandler handler) throws SVNException {

		SVNLogClient logClient = clientManager.getLogClient();
		SVNRevision startRevision = SVNRevision.create(start);
		SVNRevision endRevision = null;
		if (end == -1) {
			endRevision = SVNRevision.HEAD;
		} else {
			endRevision = SVNRevision.create(end);
		}
		logClient.doLog(SVNURL.parseURIEncoded(repositoryURL), new String[] {}, SVNRevision.HEAD, startRevision,
				endRevision, false, true, 100, handler);
	}

	private IndexOperation getIndexOperationType(char type) {
		switch (type) {
		case SVNLogEntryPath.TYPE_DELETED:
			return IndexOperation.Delete;
		default:
			return IndexOperation.Update;
		}
	}

	private class HistoryLogEntryHandler implements ISVNLogEntryHandler {
		private final List<SVNLogEntry> history = new ArrayList<SVNLogEntry>();

		@Override
		public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
			history.add(logEntry);
		}

		public List<SVNLogEntry> getSvnLogEntry() {
			return history;
		}
	}

	public SVNNodeKind checkPath(String path, long revision) throws SVNException {
		SVNNodeKind nodeKind = null;
		nodeKind = getRepository().checkPath("", -1);
		return nodeKind;
	}

	public long getLastRevisionNO() throws SVNException {
		return getRepository().info(".", -1).getRevision();
	}

	@SuppressWarnings("unchecked")
	public Collection<SVNDirEntry> getDir(String path, long revision, SVNProperties properties, Collection<SVNDirEntry> collection) throws SVNException{
		return getRepository().getDir(path, revision, properties, collection);
	}
}
