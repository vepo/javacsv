package com.csvreader;

public class ColumnBuffer {
	public char[] Buffer;

	public int Position;

	public ColumnBuffer() {
		Buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE];
		Position = 0;
	}
}