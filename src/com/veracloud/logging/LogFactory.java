package com.veracloud.logging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LogFactory {
  //key: name (String), value: a Log instance
  private static final Map<String, Log> loggerMap = new ConcurrentHashMap<String, Log>();
  
  public static Log get(Class<?> cls) {
    return get(cls.getName());
  }
  
  public static Log get(String name) {
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
