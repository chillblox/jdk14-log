package com.veracloud.logging;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * API for sending log output to {@link java.util.logging.Logger}.
 */
public final class Log {
  private static final String SELF = Log.class.getName();

  private final Logger mLogger;

  // WARN: Log constructor should have only package access so
  // that only LogFactory be able to create one.
  Log(String name) {
    mLogger = Logger.getLogger(name);
  }

  // ---------------------------
  // TRACE
  // ---------------------------

  /**
   * Is this logger instance enabled for the FINEST level?
   * 
   * @return True if this Logger is enabled for level FINEST, false otherwise.
   */
  public boolean isTraceEnabled() {
    return mLogger.isLoggable(Level.FINEST);
  }

  /**
   * Log a message at level FINEST according to the specified format and
   * arguments.
   * 
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void t(String format, Object... args) {
    if (mLogger.isLoggable(Level.FINEST)) {
      log(SELF, Level.FINEST, String.format(format, args), null);
    }
  }

  /**
   * Log an exception (throwable) at level FINEST with an accompanying message
   * according to the specified format and arguments.
   * 
   * @param t
   *          the exception (throwable) to log
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void t(Throwable t, String format, Object... args) {
    if (mLogger.isLoggable(Level.FINEST)) {
      log(SELF, Level.FINEST, String.format(format, args), t);
    }
  }

  // ---------------------------
  // DEBUG
  // ---------------------------

  /**
   * Is this logger instance enabled for the FINE level?
   * 
   * @return True if this Logger is enabled for level FINE, false otherwise.
   */
  public boolean isDebugEnabled() {
    return mLogger.isLoggable(Level.FINE);
  }

  /**
   * Log a message at level FINE according to the specified format and
   * arguments.
   * 
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void d(String format, Object... args) {
    if (mLogger.isLoggable(Level.FINE)) {
      log(SELF, Level.FINE, String.format(format, args), null);
    }
  }

  /**
   * Log an exception (throwable) at level FINE with an accompanying message
   * according to the specified format and arguments.
   * 
   * @param t
   *          the exception (throwable) to log
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void d(Throwable t, String format, Object... args) {
    if (mLogger.isLoggable(Level.FINE)) {
      log(SELF, Level.FINE, String.format(format, args), t);
    }
  }

  // ---------------------------
  // INFO
  // ---------------------------

  /**
   * Is this logger instance enabled for the INFO level?
   * 
   * @return True if this Logger is enabled for the INFO level, false otherwise.
   */
  public boolean isInfoEnabled() {
    return mLogger.isLoggable(Level.INFO);
  }

  /**
   * Log a message at level INFO according to the specified format and
   * arguments.
   * 
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void i(String format, Object... args) {
    if (mLogger.isLoggable(Level.INFO)) {
      log(SELF, Level.INFO, String.format(format, args), null);
    }
  }

  /**
   * Log an exception (throwable) at level INFO with an accompanying message
   * according to the specified format and arguments.
   * 
   * @param t
   *          the exception (throwable) to log
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void i(Throwable t, String format, Object... args) {
    if (mLogger.isLoggable(Level.INFO)) {
      log(SELF, Level.INFO, String.format(format, args), t);
    }
  }

  // ---------------------------
  // WARNING
  // ---------------------------

  /**
   * Is this logger instance enabled for the WARNING level?
   * 
   * @return True if this Logger is enabled for the WARNING level, false
   *         otherwise.
   */
  public boolean isWarnEnabled() {
    return mLogger.isLoggable(Level.WARNING);
  }

  /**
   * Log a message at level WARNING according to the specified format and
   * arguments.
   * 
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void w(String format, Object... args) {
    if (mLogger.isLoggable(Level.WARNING)) {
      log(SELF, Level.WARNING, String.format(format, args), null);
    }
  }

  /**
   * Log an exception (throwable) at level WARNING with an accompanying message
   * according to the specified format and arguments.
   * 
   * @param t
   *          the exception (throwable) to log
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void w(Throwable t, String format, Object... args) {
    if (mLogger.isLoggable(Level.WARNING)) {
      log(SELF, Level.WARNING, String.format(format, args), t);
    }
  }

  // ---------------------------
  // ERROR
  // ---------------------------

  /**
   * Is this logger instance enabled for level SEVERE?
   * 
   * @return True if this Logger is enabled for level SEVERE, false otherwise.
   */
  public boolean isErrorEnabled() {
    return mLogger.isLoggable(Level.SEVERE);
  }

  /**
   * Log a message at level SEVERE according to the specified format and
   * arguments.
   * 
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void e(String format, Object... args) {
    if (mLogger.isLoggable(Level.SEVERE)) {
      log(SELF, Level.SEVERE, String.format(format, args), null);
    }
  }

  /**
   * Log an exception (throwable) at level SEVERE with an accompanying message
   * according to the specified format and arguments.
   * 
   * @param t
   *          the exception (throwable) to log
   * @param format
   *          the format string
   * @param args
   *          the arguments
   */
  public void e(Throwable t, String format, Object... args) {
    if (mLogger.isLoggable(Level.SEVERE)) {
      log(SELF, Level.SEVERE, String.format(format, args), t);
    }
  }

  // ---------------------------
  // Implementation
  // ---------------------------

  private void log(String callerFQCN, Level level, String msg, Throwable t) {
    LogRecord record = new LogRecord(level, msg);
    record.setThrown(t);
    fillCallerData(callerFQCN, record);
    mLogger.log(record);
  }

  private void fillCallerData(String callerFQCN, LogRecord record) {
    StackTraceElement[] steArray = new Throwable().getStackTrace();

    int selfIndex = -1;
    for (int i = 0; i < steArray.length; i++) {
      final String className = steArray[i].getClassName();
      if (className.equals(callerFQCN)) {
        selfIndex = i;
        break;
      }
    }

    int found = -1;
    for (int i = selfIndex + 1; i < steArray.length; i++) {
      final String className = steArray[i].getClassName();
      if (!(className.equals(callerFQCN))) {
        found = i;
        break;
      }
    }

    if (found != -1) {
      StackTraceElement ste = steArray[found];
      // setting the class name has the side effect of setting
      // the needToInferCaller variable to false.
      record.setSourceClassName(ste.getClassName());
      record.setSourceMethodName(ste.getMethodName());
    }
  }
}
