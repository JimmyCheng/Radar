package com.coderadar.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coderadar.solr.bean.UrlOperation;


public class FileUtil {

	private static final String FAILED_LIST = "failed.list";
	private static final String REVISION_TXT = "revision.txt";
	private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private static final String APPDATA_PATH = System.getProperty("app.data.directory");
	final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * Update the revision number to revision.txt
	 * 
	 * @param revisonNo
	 */
	public static void updateRevision(long revisonNo) {
		String fullpath = getFullPath(REVISION_TXT);
		BufferedWriter writer = newWriter(fullpath, false);
		try {
			writer.write(String.valueOf(revisonNo));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeWriter(writer);
		}
	}

	/**
	 * Load revision number from revision.txt
	 * 
	 * @return
	 */
	public static Long loadRevision() {
		String fullpath = getFullPath(REVISION_TXT);
		BufferedReader fileReader = newReader(fullpath);
		try {
			String revisionStr = fileReader.readLine();
			if (StringUtils.isNumeric(revisionStr)) {
				return Long.valueOf(revisionStr);
			} else {
				return new Long(Integer.MAX_VALUE);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeReader(fileReader);
		}
		return null;
	}

	/**
	 * Save the URLs - which failed to index into SOLR - into file for retry.
	 * 
	 * This method need consider the multiply threads read/write the same file
	 * at the same time .
	 * 
	 * @param failedUrls
	 */
	public static void saveFailedList(UrlOperation... failedUrls) {
		Lock writeLock = rwLock.writeLock();
		writeLock.lock();
		String fullpath = getFullPath(FAILED_LIST);
		BufferedWriter newWriter = newWriter(fullpath, true);
		try {
			for (UrlOperation s : failedUrls) {
				newWriter.write(s.toString());
				newWriter.newLine();
			}
			newWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeWriter(newWriter);
			writeLock.unlock();
		}
	}

	/**
	 * Load the failed file list.
	 * 
	 * This method need consider multiply threads read/write the same file .
	 * 
	 * @return
	 */
	public static List<UrlOperation> loadFailedEntries() {
		List<UrlOperation> result = new ArrayList<UrlOperation>();
		Lock writeLock = rwLock.writeLock();
		
		writeLock.lock();
		String fullpath = getFullPath(FAILED_LIST);
		BufferedReader reader = newReader(fullpath);
		
		if (reader == null) {
			return null;
		}
		String s;
		try {
			int recordCount = 0;
			while ((s = reader.readLine()) != null) {
				recordCount++;
				UrlOperation failRecord = UrlOperation.fromString(s);
				if(failRecord!=null){
					result.add(failRecord);
				}
			}
			closeReader(reader);
			//backup old fail list and new fresh file
			if(recordCount > 0){
				File failFile = new File(fullpath);
				failFile.renameTo(new File(fullpath+"."+System.currentTimeMillis()));
				new File(fullpath).createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
			writeLock.unlock();
		}
		return result;
	}


	private static String getFullPath(String relativePath) {
		String fullpath = APPDATA_PATH + File.separator + relativePath;
		return fullpath;
	}

	private static BufferedReader newReader(String filename) {
		File file = new File(filename);
		if (!file.exists()) {
			return null;
		}
		BufferedReader fileReader = null;
		try {
			fileReader = new BufferedReader(new FileReader(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileReader;
	}

	private static void closeReader(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static BufferedWriter newWriter(String filename, boolean append) {
		BufferedWriter writer = null;
		try {
			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(file, append));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer;
	}

	private static void closeWriter(Writer writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
