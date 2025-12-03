package com.csvreader;

public class UserSettings {
	// having these as publicly accessible members will prevent
	// the overhead of the method call that exists on properties
	public boolean caseSensitive;
	public char textQualifier;
	public boolean trimWhitespace;
	public boolean useTextQualifier;
	public char delimiter;
	public char recordDelimiter;
	public char comment;
	public boolean useComments;
	public int escapeMode;
	public boolean safetySwitch;
	public boolean skipEmptyRecords;
	public boolean captureRawRecord;
	public boolean forceQualifier;

	public UserSettings() {
		caseSensitive = true;
		textQualifier = Letters.QUOTE;
		trimWhitespace = true;
		useTextQualifier = true;
		delimiter = Letters.COMMA;
		recordDelimiter = Letters.NULL;
		comment = Letters.POUND;
		useComments = false;
		escapeMode = CsvReader.ESCAPE_MODE_DOUBLED;
		safetySwitch = true;
		skipEmptyRecords = true;
		captureRawRecord = true;
		forceQualifier = false;
	}
}