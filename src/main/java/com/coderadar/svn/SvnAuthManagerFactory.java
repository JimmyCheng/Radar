package com.coderadar.svn;

import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SvnAuthManagerFactory {
	public String svnUser;
	public String svnPwd;
	
	private ISVNAuthenticationManager defaultAuthenticationManager ;
	

	public String getSvnUser() {
		return svnUser;
	}

	public void setSvnUser(String svnUser) {
		this.svnUser = svnUser;
	}

	public String getSvnPwd() {
		return svnPwd;
	}

	public void setSvnPwd(String svnPwd) {
		this.svnPwd = svnPwd;
	}
	
	public void init(){
		defaultAuthenticationManager = SVNWCUtil.createDefaultAuthenticationManager(svnUser, svnPwd);
	}

	
	public ISVNAuthenticationManager createSvnAuthManager() {
		return defaultAuthenticationManager; 
	}
}
