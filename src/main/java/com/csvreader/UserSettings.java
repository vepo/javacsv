package com.csvreader;

public class UserSettings {
    // having these as publicly accessible members will prevent
    // the overhead of the method call that exists on properties
    public boolean caseSensitive;
    public char textQualifier;
    public boolean trimWhitespace;
    public boolean useTextQualifier;
    private char delimiter;
    private String delimiterPattern;
    private String delimiterEscaped;
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
        recordDelimiter = Letters.NULL;
        comment = Letters.POUND;
        useComments = false;
        escapeMode = CsvReader.ESCAPE_MODE_DOUBLED;
        safetySwitch = true;
        skipEmptyRecords = true;
        captureRawRecord = true;
        forceQualifier = false;

        updateDelimiter(Letters.COMMA);
    }

    /**
     * Sets the character to use as the column delimiter.
     * 
     * @param delimiter The character to use as the column delimiter.
     */
    public UserSettings withDelimiter(char delimiter) {
        updateDelimiter(delimiter);
        return this;
    }

    /**
     * Gets the character being used as the column delimiter.
     * 
     * @return The character being used as the column delimiter.
     */
    public char delimiter() {
        return delimiter;
    }

    public String delimiterPattern() {
        return delimiterPattern;
    }

    public String delimiterEscaped() {
        return delimiterEscaped;
    }

    private void updateDelimiter(char newDelimiter) {
        this.delimiterEscaped = Letters.BACKSLASH + (this.delimiterPattern = String.valueOf(this.delimiter = newDelimiter));
    }
}