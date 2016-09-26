package com.veracloud.logging;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;

final class LogFactory {

	static {
		final LogManager logManager = LogManager.getLogManager();
		final InputStream is = LogFactory.class.getResourceAsStream("/logging.properties");
		if (is != null) {
  		try {
  			logManager.readConfiguration(is);
  		} catch (SecurityException e) {
  			e.printStackTrace();
  		} catch (IOException e) {
  			e.printStackTrace();
  		} finally {
  				try {
  					is.close();
  				} catch (IOException e) {
  				  e.printStackTrace();
  				}
  		}
		} else {
		  System.err.println("Can't load resource: /logging.properties");
		}
	}

	// key: name (String), value: a Log instance
	private static final Map<String, Log> loggerMap = new ConcurrentHashMap<String, Log>();

	public static Log getLog(Class<?> cls) {
		return getLog(cls.getName());
	}

	public static Log getLog(String name) {
		Log logger = loggerMap.get(name);
		if (logger != null) {
			return logger;
		} else {
			Log newInstance = new Log(name);
			Log oldInstance = loggerMap.putIfAbsent(name, newInstance);
			return oldInstance == null ? newInstance : oldInstance;
		}
	}
}
