/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coderadar.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.coderadar.solr.bean.SourceBean;
import com.coderadar.solr.service.SolrSourceService;

@Controller
public class MainController{

	final Logger logger = Logger.getLogger(MainController.class.getName());

	//TODO: this should be configurable in Management.
	private static final int ROW_PER_PAGE = 5;

	private static final String SEARCH_BY_NAME = "search-by-name";
	private static final String SEARCH_BY_CONTENT = "search-by-content";
	private static final String RELEASE = "release";
	private static final String FILE_TYPE = "fileType";
	
	@Autowired
	private SolrSourceService solrSourceService;

    @RequestMapping(value= "/")
    public String firstPage() {
        return "index";
    }
    
    @RequestMapping(value= "/about")
    public String about(){
        return "error";
    }
    
    @RequestMapping(value= "/login")
    public String login(){
        return "error";
    } 

    @RequestMapping(value= "/management")
    public String management(){
        return "error";
    } 
    
    @RequestMapping(value= "/aboutme")
    public String aboutMe(){
        return "aboutme";
    }   
   
    @RequestMapping(value= "/find")
    public String findSource(HttpServletRequest request, Model model){
		long enter = System.currentTimeMillis();
		findSources(request, model);
		double used = ((double) System.currentTimeMillis() - (double) enter) / 1000;
		logger.info("Time used :" + String.valueOf(used));
		
		model.addAttribute("timeUsed", String.valueOf(used));
        return "result";
        }

	private String getSolrString(HttpServletRequest request, Model model) {
		String solrSql = "";
		String q = request.getParameter("q");
		
		model.addAttribute("queryText", q);
		
		logger.info(" param q = " + q);
		if (StringUtils.isBlank(q)) {
			return solrSql;
		}
		String filterByName = "";
		String filterByContent = "";

		String searchOptions = request.getParameter("search-options");
		logger.info(" param search-options = " + searchOptions);
		if (searchOptions != null) {
			if (searchOptions.contains(SEARCH_BY_NAME)) {
				filterByName = SEARCH_BY_NAME;
			}
			if (searchOptions.contains(SEARCH_BY_CONTENT)) {
				filterByContent = SEARCH_BY_CONTENT;
			}
		}

		String searchOptionsType = request.getParameter("search-options-type");
		if (StringUtils.isBlank(searchOptionsType)) {
			searchOptionsType = "OR";
		}
		searchOptionsType = " " + searchOptionsType + " ";
		logger.info(" param search-options-type = " + searchOptionsType);

		logger.info(" searchByName =" + filterByName + ", searchByContent=" + filterByContent);

		String path = request.getParameter("path");

		// compose the name and content condition
		String escapeQueryCulprits = com.coderadar.util.SolrUtil.escapeQueryCulprits(q);

		String byName = "", byContent = "", andPath = "";
		if (StringUtils.isNotBlank(filterByName)) {
			byName = "name:" + escapeQueryCulprits;
		}
		if (StringUtils.isNotBlank(filterByContent)) {
			byContent = "content:" + escapeQueryCulprits;
		}
		if (StringUtils.isNotBlank(path)) {
			andPath = "path:" + path + "*";
		}

		if (StringUtils.isNotBlank(byName) && StringUtils.isNotBlank(byContent) && StringUtils.isNotBlank(andPath)) {
			solrSql = "(" + byName + searchOptionsType + byContent + ") AND " + andPath;
		} else {
			if (StringUtils.isNotBlank(byName) && StringUtils.isNotBlank(byContent)) {
				solrSql = byName + searchOptionsType + byContent;
			} else if (StringUtils.isNotBlank(byName) && StringUtils.isNotBlank(andPath)) {
				solrSql = byName + " AND " + andPath;
			} else if (StringUtils.isNotBlank(byContent) && StringUtils.isNotBlank(andPath)) {
				solrSql = byContent + " AND " + andPath;
			} else if (StringUtils.isNotBlank(byName)) {
				solrSql = byName;
			} else if (StringUtils.isNotBlank(andPath)) {
				solrSql = andPath;
			} else if (StringUtils.isNotBlank(byContent)) {
				solrSql = byContent;
			} else {
				// defalut , query all data.
				solrSql = "name:" + escapeQueryCulprits + searchOptionsType + " content:" + escapeQueryCulprits;
			}
		}
		// filter options
		String filters = getFilters(request);
		if (StringUtils.isNotBlank(filters)) {
			solrSql = "( " + solrSql + " )" + " AND " + filters;
		}
		logger.info("Solr String :" + solrSql);
		return solrSql;
	}

	private String getFilters(HttpServletRequest request) {
		String selectedFilterOptions = request.getParameter("selected-filter-options");
		if (StringUtils.isBlank(selectedFilterOptions)) {
			return "";
		}
		String[] filters = selectedFilterOptions.split(",");
		List<String> releaseOpt = new ArrayList<String>();
		List<String> fileTypeOpt = new ArrayList<String>();
		for (String filter : filters) {
			if (StringUtils.isNotBlank(filter)) {
				if (filter.startsWith(RELEASE)) {
					releaseOpt.add(filter);
				} else if (filter.startsWith(FILE_TYPE)) {
					fileTypeOpt.add(filter);
				} else {
					logger.error("Error filter option :" + filter);
				}
			}
		}

		List<String> filterResult = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(fileTypeOpt)) {
			String fileTypeStr = "(" + StringUtils.join(fileTypeOpt, " OR ") + ")";
			filterResult.add(fileTypeStr);
		}
		if (!CollectionUtils.isEmpty(releaseOpt)) {
			String releaseStr = "(" + StringUtils.join(releaseOpt, " OR ") + ")";
			filterResult.add(releaseStr);
		}
		return StringUtils.join(filterResult, " AND ");
	}

	private void findSources(HttpServletRequest request, Model model) {
		String path = request.getParameter("path");
		model.addAttribute("path", path);

		String q = request.getParameter("q");
		model.addAttribute("queryString", q);

		String searchOptions = request.getParameter("search-options");
		model.addAttribute("searchOptions", searchOptions);
		
		String selectedFilterOptions = request.getParameter("selected-filter-options");
		model.addAttribute("selectedFilterOptions", selectedFilterOptions);
		
		String searchOptionsType = request.getParameter("search-options-type");
		if (StringUtils.isBlank(searchOptionsType)) {
			searchOptionsType = "OR";
		}
		model.addAttribute("searchOptionsType", searchOptionsType);
		
		String pageStr = request.getParameter("page");
		if (!NumberUtils.isNumber(pageStr)) {
			pageStr = "1";
		}
		int page = Integer.valueOf(pageStr);
		if (page < 1) {
			page = 1;
		}

		String solrString = getSolrString(request, model);

		int start = (page - 1) * ROW_PER_PAGE;
		List<SourceBean> sourceList = new ArrayList<SourceBean>();
		if (StringUtils.isNotBlank(solrString)) {

			sourceList = solrSourceService.search(solrString, start, ROW_PER_PAGE);
			long count = solrSourceService.count(solrString);
			if ((sourceList != null) && !sourceList.isEmpty()) {
				this.setFacet(solrString, request, model);
			}

			model.addAttribute("sourceInfoList", sourceList);
			model.addAttribute("count", count);
		}
		
		model.addAttribute("pageRecordCount", sourceList.size());
		model.addAttribute("page", page);
		model.addAttribute("rows", ROW_PER_PAGE);
		model.addAttribute("pagesize", ROW_PER_PAGE);
	}

	private void setFacet(String query, HttpServletRequest request, Model model) {
		String release = "release";
		String fileType = "fileType";
		List<String> facetRelease = solrSourceService.facet(query, release);
		List<String> facetFileType = solrSourceService.facet(query, fileType);
		sort(facetRelease);
		sort(facetFileType);

		model.addAttribute("releases", facetRelease);
		model.addAttribute("fileTypes", facetFileType);		
	}

	private void sort(List<String> sortlist) {
		if (!CollectionUtils.isEmpty(sortlist)) {
			Collections.sort(sortlist);
			Collections.sort(sortlist, Collections.reverseOrder());
		}
	}

	public static void main(String[] args) {
		List<String> sortList = new ArrayList<String>();
		sortList.add("tc-5.5.x");
		sortList.add("tc-6.0.x");
		sortList.add("tc-7.0.x");
		sortList.add("tc-8.0.x");
		sortList.add("trunk");
		Collections.sort(sortList);
		Collections.sort(sortList, Collections.reverseOrder());
		System.out.println("");
	}
}
