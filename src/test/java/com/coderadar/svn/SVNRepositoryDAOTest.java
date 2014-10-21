package com.coderadar.svn;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;

import com.coderadar.svn.SvnRepositoryDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:svnContext.xml"})
public class SVNRepositoryDAOTest{
	
	@Autowired
	SvnRepositoryDAO svnRepositoryDAO;
    
    @Test
    public void loadContentTest(){
    	//Full Path=http://code.taobao.org/svn/tomcat/tc8.0.x/java/org/apache/coyote/AbstractProcessor.java
    	//svn.root=http://code.taobao.org/svn/tomcat/
    	String content = svnRepositoryDAO.loadContent("tc8.0.x/java/org/apache/coyote/AbstractProcessor.java");
    	System.out.println(content);
    }
    
    @Test
    public void getRevisionTest(){
    	try {
			System.out.println(svnRepositoryDAO.getLastRevisionNO());
		} catch (SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    @Test 
    public void getDirTest(){
    	
		Collection<SVNDirEntry> entries = null;
		try {
			entries = svnRepositoryDAO.getDir("", -1, null, (Collection<SVNDirEntry>) null);
		} catch (SVNException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (SVNDirEntry entry : entries) {
			final String fullUrl = entry.getURL().toString();
			final String entryName = entry.getName();
			try {
				if (entry.getKind() == SVNNodeKind.FILE) {
					System.out.println("FILE:" + fullUrl + ":" + entryName);
				} else {
					System.out.println("DIR:" + fullUrl+ ":" + entryName);
				}
			} catch (Exception e) {
				System.out.println("error when when do svn fetch(dir or file), this url has been put to urlqueue, url:" + fullUrl);
			}
		}
    }
}
