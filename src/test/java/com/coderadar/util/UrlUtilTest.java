package com.coderadar.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.coderadar.util.UrlUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:com/coderadar/util/UrlUtil.xml"})
public class UrlUtilTest {
	
    @Autowired
    UrlUtil urlUtil;
    
	@Test
	public void includeFileTypeTest(){
		String url = "http://code.taobao.org/svn/tomcat/tc8.0.x/java/org/apache/coyote/AbstractProcessor.doc";
		String type = urlUtil.getFileType(url);
		assertTrue(urlUtil.isFileTypeExcluded(type));
		
		url = "http://code.taobao.org/svn/tomcat/tc8.0.x/java/org/apache/coyote/AbstractProcessor.java";
		type = urlUtil.getFileType(url);
		assertTrue(!urlUtil.isFileTypeExcluded(type));
	}
	
	@Test
	public void includeDirTest(){
		String url = "http://code.taobao.org/svn/tomcat/tc3.0.x/java/org/apache/coyote/AbstractProcessor.java";
		assertTrue(urlUtil.isDirExcluded(url));
		
		url = "http://code.taobao.org/svn/tomcat/tc8.0.x/java/org/apache/coyote/AbstractProcessor.java";
		assertTrue(!urlUtil.isDirExcluded(url));
	}
	
	@Test
	public void getFileTypeTest(){
		String url = "http://code.taobao.org/svn/tomcat/tc3.0.x/java/org/apache/coyote/AbstractProcessor.java";
		assertTrue(urlUtil.getFileType(url).equals("java"));
		
		url = "http://code.taobao.org/svn/tomcat/tc8.0.x/java/org/apache/coyote/AbstractProcessor.properties";
		assertTrue(urlUtil.getFileType(url).equals("properties"));
	}
	
	@Test
	public void getReleaseTest(){
		String url = "http://code.taobao.org/svn/tomcat/tc3.0.x/java/org/apache/coyote/AbstractProcessor.java";
		assertTrue(urlUtil.getRelease(url).equals("unknown"));
		
		url = "http://code.taobao.org/svn/tomcat/tc8.0.x/java/org/apache/coyote/AbstractProcessor.properties";
		assertTrue(urlUtil.getRelease(url).equals("tc8.0.x"));
	}
	
}
