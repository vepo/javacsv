package com.csvreader;

import java.util.HashMap;

public class HeadersHolder {
	public String[] Headers;

	public int Length;

	public HashMap<String, Integer> IndexByName;

	public HeadersHolder() {
		Headers = null;
		Length = 0;
		IndexByName = new HashMap<>();
	}
}