package com.csvreader;

public class RawRecordBuffer {
	public char[] Buffer;

	public int Position;

	public RawRecordBuffer() {
		Buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE
				* StaticSettings.INITIAL_COLUMN_COUNT];
		Position = 0;
	}
}