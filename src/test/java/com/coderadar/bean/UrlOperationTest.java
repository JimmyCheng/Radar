package com.coderadar.bean;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.coderadar.solr.bean.UrlOperation;


public class UrlOperationTest {
	@Test
	public void testNormalConditionOfFromString() {
		String str = "Update:http://test.utl";
		UrlOperation fromString = UrlOperation.fromString(str);
		assertNotNull("the result should not be null", fromString);

		str = "Delete: http://url";
		fromString = UrlOperation.fromString(str);
		assertNotNull("the result should not be null", fromString);

		str = " Update : http://url";
		fromString = UrlOperation.fromString(str);
		assertNotNull("the result should not be null", fromString);
	}

	@Test
	public void shouldReturnEmptyWhenStringIsInvalid() {
		String invalid = "Updatehttp://url";
		UrlOperation fromString = UrlOperation.fromString(invalid);
		assertNull("the result should be null", fromString);
	}

	@Test
	public void shouldReturnEmptyWhenTheStateIsAnInvalidValue() {
		String invalid = "?:http://url";
		UrlOperation fromString = UrlOperation.fromString(invalid);
		assertNull("the result should be null", fromString);
	}

}
