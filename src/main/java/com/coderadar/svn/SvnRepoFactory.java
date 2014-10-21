package com.coderadar.svn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNSession;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnRepoFactory {
	final static Logger logger = LoggerFactory.getLogger(SvnRepoFactory.class);

	private ISVNAuthenticationManager authManager = null;
	private String svnUser;
	private String svnPwd;
	private String svnRoot;

	public void init() {
		if (authManager == null) {
			DAVRepositoryFactory.setup();
			logger.info("Init authManager with svnUser {} and svnPwd {}", svnUser, svnPwd);
			authManager = SVNWCUtil.createDefaultAuthenticationManager(svnUser, svnPwd);
			logger.info("authManager is {}", authManager);
		}
	}

	public SVNRepository newSvnRepository() {
		SVNRepository repository = null;
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(svnRoot), ISVNSession.KEEP_ALIVE);
			repository.setAuthenticationManager(authManager);
			repository.testConnection();
		} catch (SVNException e) {
			throw new RuntimeException("Can not create SVN Repository ,", e);
		}
		return repository;
	}

	public void setSvnUser(String user) {
		svnUser = user;
	}

	public void setSvnPwd(String pwd) {
		svnPwd = pwd;
	}

	public void setSvnRoot(String root) {
		svnRoot = root;
	}
}
