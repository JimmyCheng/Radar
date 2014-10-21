package com.coderadar.env;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EnvParameter {

	private static final String ENV_PROPERTIES = "env.properties";

	static Log logger = LogFactory.getLog(EnvParameter.class);

	private static Properties props = null;

	final private static Integer lock = new Integer(0);

	public static String get(String key) {
		if (props == null) {
			reload();
		}
		synchronized (lock) {
			return props.getProperty(key);
		}
	};

	public synchronized static String get(String key, String defaultValue) {
		synchronized (lock) {
			return props.getProperty(key, defaultValue);
		}
	};

	public synchronized static void init() {
		reload();
	}

	public static void reload() {

		logger.info("load properties from" + ENV_PROPERTIES); // jimmy.
		Properties propsTmp = new Properties();
		InputStream inputStream = EnvParameter.class.getClassLoader().getResourceAsStream(ENV_PROPERTIES);
		try {
			propsTmp.load(inputStream);
		} catch (IOException e) {
			logger.error("" + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				logger.error("" + e);
			}
		}

		synchronized (lock) {
			props = propsTmp;
		}
	}

}
