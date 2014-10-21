package com.coderadar.svn;

import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnClientManagerFactory {
	
	public SvnAuthManagerFactory authManager;
	
	private SVNClientManager svnClientManager;
	
	public SvnAuthManagerFactory getAuthManager() {
		return authManager;
	}

	public void setAuthManager(SvnAuthManagerFactory authManager) {
		this.authManager = authManager;
	}

	public SVNClientManager createSvnClientManager() {
		return svnClientManager;
	}

	public void init() {
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options, authManager.createSvnAuthManager());
	}
}
