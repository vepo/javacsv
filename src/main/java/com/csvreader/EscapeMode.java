package com.csvreader;

public enum EscapeMode {
    /**
     * Double up the text qualifier to represent an occurance of the text qualifier.
     */
    DOUBLED,
    /**
     * Use a backslash character before the text qualifier to represent an occurance
     * of the text qualifier.
     */
    BACKSLASH
}
