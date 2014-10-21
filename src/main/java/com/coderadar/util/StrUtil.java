package com.coderadar.util;

import com.coderadar.env.EnvParameter;

public class StrUtil {

	/**
	 * Get the relative path of a full SVN URL.
	 * 
	 * @param fullURL
	 * @return
	 */
	public static String getSvnRelativePath(String fullURL) {
		if (fullURL == null) {
			return null;
		}
		if (!fullURL.toLowerCase().startsWith("http")) {
			return fullURL;
		}
		if (!fullURL.startsWith(EnvParameter.get("svn.root"))) {
			return null;
		}

		String result = fullURL.substring(EnvParameter.get("svn.root").length());
		if (!result.startsWith("/")) {
			result = "/" + result;
		}
		return result;
	}

	/**
	 * Remove the generic part of a class defination.
	 * 
	 * @param str
	 * @return
	 */
	public static String removeGeneric(String string) {
		int indexOf = string.indexOf("<");
		if (indexOf > 0) {
			return string.substring(0, indexOf);
		} else {
			return string;
		}
	}
}
