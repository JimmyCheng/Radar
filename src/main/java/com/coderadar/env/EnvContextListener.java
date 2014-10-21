package com.coderadar.env;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class EnvContextListener implements ServletContextListener {
	final static Logger logger = LoggerFactory.getLogger(EnvContextListener.class);

	private ApplicationContext applicationContext;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		logger.info("Context initialized ...");
		ServletContext servletContext = event.getServletContext();

		// debug
		@SuppressWarnings("unchecked")
		Enumeration<String> attributeNames = servletContext.getAttributeNames();
		while (attributeNames.hasMoreElements()) {
			String name = attributeNames.nextElement();
			Object value = servletContext.getAttribute(name);
			logger.info("Attribute : {} = {}", name, value);
		}
		// end debug

		applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

		EnvParameter.init();

		try {
			setupEnviromentProperties(servletContext);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	private void setupEnviromentProperties(ServletContext servletContex) throws MalformedURLException {
		// set the deploy path to system property
		String deployPath = servletContex.getRealPath("/") + File.separator;
		logger.info("Setting 'coderadar.deploy.path' to {}", deployPath);
		System.setProperty("coderadar.deploy.path", deployPath);

		setSolrHome(deployPath);

		setAppDataPath(deployPath);

		// set the solr.api.url
	}

	private void setAppDataPath(String deployPath) {
		// set the appdata path
		String appdataPath = deployPath + EnvParameter.get("app.data.directory");
		logger.info("Setting 'app.data.directory' to {}", appdataPath);
		System.setProperty("app.data.directory", appdataPath);
	}

	private void setSolrHome(String deployPath) {
		// set the solr home enviroment parameter
		String solrHomePath = EnvParameter.get("solr.home.relative");
		String fullSolrHomePath = "";
		if (solrHomePath.startsWith("/")) {
			fullSolrHomePath = solrHomePath;
		} else {
			fullSolrHomePath = deployPath + File.separator + solrHomePath;
		}
		logger.info("Setting 'solr.solr.home' to {}", fullSolrHomePath);
		System.setProperty("solr.solr.home", fullSolrHomePath);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
	}
}
