package com.coderadar.svn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;

public class SvnRepositoryPool {
	
	final static Logger logger = LoggerFactory.getLogger(SvnRepositoryPool.class);
	
	private List<SVNRepository> repList = new ArrayList<SVNRepository>(30);
	
	private Map<SVNRepository, SVNClientManager> svnClientManagerMap = new HashMap<SVNRepository, SVNClientManager>();
	
	private int index = 0;
	private int poolSize;
	private SvnRepoFactory svnRepoFactory; 
	
	@Required
	public void setPoolSize(int size) {
		poolSize = size;
	}
	
	public int getPoolSize() {
		return poolSize;
	}
	
	@Required
	public void setSvnRepoFactory(SvnRepoFactory factory) {
		svnRepoFactory = factory;
	}
	
	public SvnRepoFactory getSvnRepoFactory(){
		return svnRepoFactory;
	}
	
	private void init() {
		for (int i = 0; i < poolSize; i++) {
			logger.info("Initializing svn pool: {}", i);
			SVNRepository newSvnRepository = svnRepoFactory.newSvnRepository();
			repList.add(i, newSvnRepository);
			
			SVNClientManager newClientManager = SVNClientManager.newInstance(null, 
					newSvnRepository.getAuthenticationManager());
			svnClientManagerMap.put(newSvnRepository, newClientManager);
		}
	}
	
	public synchronized SVNRepository getSvnRepository(){
		index ++ ;
		if(repList.size() == 0 ){
			init();
		}
		if(index == repList.size()){
			index = 0;
		}
		return repList.get(index);
	}
	
	public synchronized SVNClientManager getSVNClient(SVNRepository svnRepository){
		return svnClientManagerMap.get(svnRepository);
	}
}
