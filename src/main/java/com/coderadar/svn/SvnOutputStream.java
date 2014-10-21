package com.coderadar.svn;

import java.io.ByteArrayOutputStream;

public class SvnOutputStream extends ByteArrayOutputStream{

	final private long MAX_LENGTH = 1024*1024;
	
	private long TotalLength = 0L;
	
	@Override
	public synchronized void write(byte[] b, int off, int len) {
		TotalLength = TotalLength+b.length;
		if(TotalLength > MAX_LENGTH){
			throw(new FileTooLargeException());
		}
		super.write(b, off, len);
	}

	
}
