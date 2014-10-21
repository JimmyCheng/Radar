package com.coderadar.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.coderadar.solr.bean.SourceBean;
import com.coderadar.solr.service.SolrSourceService;

@Controller
@RequestMapping(value = "/view")
public class FileViewerController{

    @Autowired    
	private SolrSourceService solrSourceService;
	
	@RequestMapping(method = RequestMethod.GET)
	public String viewFile(Model model, @RequestParam("query") String query, @RequestParam("path") String path) {
		if (StringUtils.isBlank(path)) {
			return null;
		}
		
		String decoded = null;
		try {
			decoded = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SourceBean sourceInfo = getSourceInfo(decoded);
		if (sourceInfo == null) {
			return null;
		}
       
		//for syntax highlighter. set hightlight number
		String matchLines = "[";
		Scanner scanner = new Scanner(sourceInfo.getContent());
	    int lineNum = 0;
	    while (scanner.hasNextLine()) {
	        String line = scanner.nextLine();
	        lineNum++;
	        if(line.contains(query)) { 
	        	matchLines = matchLines.concat(lineNum + ",");
	        }
	    }
	    matchLines = matchLines.concat("]");
	    scanner.close();
	    
	    //set filetype
	    String fileType = sourceInfo.getFileType();
	    String syntaxSupportType = "java:xml:sql:cpp:php:js";
	    if(!syntaxSupportType.contains(fileType)){
	    	fileType = "plain";
	    }
		
		String content = sourceInfo.getContent().replace("<", "&lt;").replace(">", "&gt;");
		sourceInfo.setContent(content);
		
		model.addAttribute("sourceInfo", sourceInfo);
		model.addAttribute("matchLines", matchLines);
		model.addAttribute("fileType", fileType);
		
		return "fileview";
	}

	private SourceBean getSourceInfo(String classId) {
//		SolrSourceDAO solrSourceDAO = applicationContext.getBean("solrSourceDAO", SolrSourceDAO.class);
//		List<SourceBean> sources = solrSourceDAO.simpleSearch("path:" + classId, 0, 10, SourceBean.class);
		
		List<SourceBean> sources = solrSourceService.simpleSearch("path:" + classId, 0, 10, SourceBean.class);
		
		if (CollectionUtils.isEmpty(sources)) {
			return null;
		}
		return sources.get(0);
	}
}
