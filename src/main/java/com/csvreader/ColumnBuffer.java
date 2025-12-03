package com.csvreader;

public class ColumnBuffer {
	public char[] buffer;

	public int position;

	public ColumnBuffer() {
		buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE];
		position = 0;
	}
}