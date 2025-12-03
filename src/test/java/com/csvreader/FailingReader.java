package com.csvreader;

import java.io.IOException;
import java.io.Reader;

public class FailingReader extends Reader {
	public boolean disposeCalled = false;

	public FailingReader() {
		super("");
	}

	public int read(char[] buffer, int index, int count) throws IOException {
		throw new IOException("Read failed.");
	}

	public void close() {
		disposeCalled = true;
	}
}