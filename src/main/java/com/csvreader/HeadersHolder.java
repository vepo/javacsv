package com.csvreader;

import java.util.HashMap;

public class HeadersHolder {
	public String[] headers;
	public int length;
	public HashMap<String, Integer> indexByName;

	public HeadersHolder() {
		headers = null;
		length = 0;
		indexByName = new HashMap<>();
	}
}