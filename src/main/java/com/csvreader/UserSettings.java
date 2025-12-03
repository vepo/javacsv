package com.csvreader;

public class UserSettings {
    // having these as publicly accessible members will prevent
    // the overhead of the method call that exists on properties
    private char textQualifier;
    private String textQualifierPattern;
    private String textQualifierEscaped;

    private char delimiter;
    private String delimiterPattern;
    private String delimiterEscaped;

    private EscapeMode escapeMode;
    private boolean trimWhitespace;

    public boolean useTextQualifier;
    public char recordDelimiter;
    public char comment;
    public boolean useComments;
    public boolean safetySwitch;
    public boolean skipEmptyRecords;
    public boolean captureRawRecord;
    public boolean forceQualifier;

    public UserSettings() {
        trimWhitespace = true;
        useTextQualifier = true;
        recordDelimiter = Letters.NULL;
        comment = Letters.POUND;
        useComments = false;
        escapeMode = EscapeMode.DOUBLED;
        safetySwitch = true;
        skipEmptyRecords = true;
        captureRawRecord = true;
        forceQualifier = false;

        updateDelimiter(Letters.COMMA);
        updateTextQualifier(Letters.QUOTE);
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

    /**
     * Gets the character to use as a text qualifier in the data.
     * 
     * @return The character to use as a text qualifier in the data.
     */
    public char textQualifier() {
        return this.textQualifier;
    }

    /**
     * Sets the character to use as a text qualifier in the data.
     * 
     * @param textQualifier The character to use as a text qualifier in the data.
     */
    public UserSettings withTextQualifier(char newTextQualifier) {
        updateTextQualifier(newTextQualifier);
        return this;
    }

    public String textQualifierEscaped() {
        return this.textQualifierEscaped;
    }

    public String textQualifierPattern() {
        return this.textQualifierPattern;
    }

    public EscapeMode escapeMode() {
        return this.escapeMode;
    }

    public UserSettings withEscapeMode(EscapeMode escapeMode) {
        this.escapeMode = escapeMode;
        updateTextQualifier(textQualifier);
        return this;
    }

    /**
     * Gets whether leading and trailing whitespace characters are being trimmed
     * from non-textqualified column data. Default is true.
     * 
     * @return Whether leading and trailing whitespace characters are being
     *         trimmed from non-textqualified column data.
     */
    public boolean trimWhitespace() {
        return trimWhitespace;
    }

    /**
     * Sets whether leading and trailing whitespace characters should be trimmed
     * from non-textqualified column data or not. Default is true.
     * 
     * @param trimWhitespace Whether leading and trailing whitespace characters
     *                       should be trimmed from non-textqualified column data or
     *                       not.
     */
    public UserSettings withTrimWhitespace(boolean trimWhitespace) {
        this.trimWhitespace = trimWhitespace;
        return this;
    }

    private void updateTextQualifier(char newTextQualifier) {
        if (this.escapeMode == EscapeMode.BACKSLASH) {
            this.textQualifierEscaped = Letters.BACKSLASH + (this.textQualifierPattern = String.valueOf(this.textQualifier = newTextQualifier));
        } else {
            this.textQualifierEscaped = newTextQualifier + (this.textQualifierPattern = String.valueOf(this.textQualifier = newTextQualifier));
        }
    }

    private void updateDelimiter(char newDelimiter) {
        this.delimiterEscaped = Letters.BACKSLASH + (this.delimiterPattern = String.valueOf(this.delimiter = newDelimiter));
    }
}