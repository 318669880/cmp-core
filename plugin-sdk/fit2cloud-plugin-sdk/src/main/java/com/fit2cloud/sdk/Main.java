package com.fit2cloud.sdk;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class Main {

	public static void main(String[] args) throws IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		System.out.println(IOUtils.toString(classLoader.getResourceAsStream("plugin.properties")));
	}

}
