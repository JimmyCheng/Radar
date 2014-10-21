package com.coderadar.util;

import org.apache.commons.lang.StringUtils;

public class SolrUtil {

	private SolrUtil(){};

	public static String safeHighLightContent(String input) {
		String output = input;
		output = output.replaceAll("<em>", "%em%");
		output = output.replaceAll("</em>", "%/em%");
		output = output.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		// replace back li and em tag, this is for highlight the match
		output = output.replaceAll("%em%", "<em>");
		output = output.replaceAll("%/em%", "</em>");

		return output;
	}

	public static String escapeQueryCulprits(String s) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isNotBlank(s)) {

			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				// These characters are part of the query syntax and must be
				// escaped
				if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^'
						|| c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*'
						|| c == '?' || c == '|' || c == '&' || c == ';') {
					sb.append('\\');
				}
				if (Character.isWhitespace(c)) {
					sb.append(" \\ ");
				}
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
