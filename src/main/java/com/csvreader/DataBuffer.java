package com.csvreader;

public class DataBuffer {
	public char[] Buffer;

	public int Position;

	// / <summary>
	// / How much usable data has been read into the stream,
	// / which will not always be as long as Buffer.Length.
	// / </summary>
	public int Count;

	// / <summary>
	// / The position of the cursor in the buffer when the
	// / current column was started or the last time data
	// / was moved out to the column buffer.
	// / </summary>
	public int ColumnStart;

	public int LineStart;

	public DataBuffer() {
		Buffer = new char[StaticSettings.MAX_BUFFER_SIZE];
		Position = 0;
		Count = 0;
		ColumnStart = 0;
		LineStart = 0;
	}
}