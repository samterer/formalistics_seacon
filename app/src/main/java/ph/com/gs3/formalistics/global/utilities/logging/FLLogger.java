package ph.com.gs3.formalistics.global.utilities.logging;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import ph.com.gs3.formalistics.FormalisticsApplication;
import ph.com.gs3.formalistics.global.constants.ApplicationMode;

public class FLLogger {

    public static final int MAX_LOG_SIZE = 1000;

    private static ApplicationMode mode = FormalisticsApplication.APPLICATION_MODE;

    private static FLPersistentLogger persistentLogger;
    private static boolean traceEnabled = false;

    public enum LogType {
        ERROR, WARNING, DEBUG, INFORMATION
    }

//    public static final void log(LogType logType, String log) {
//        log(TAG, logType, log);
//    }

    public static void log(String source, LogType logType, String log) {

        if (traceEnabled) {
            log = log + "\nTrace:\n" + getFormattedStackTrace();
        }

        for (int i = 0; i <= log.length() / MAX_LOG_SIZE; i++) {
            int start = i * MAX_LOG_SIZE;
            int end = (i + 1) * MAX_LOG_SIZE;

            end = end > log.length() ? log.length() : end;
            logAll(source, logType, log.substring(start, end));
        }

        if (log.length() > MAX_LOG_SIZE) {
            logAll(source, logType, "~End of long log");
        }

    }

    private static void logAll(String source, LogType logType, String log) {
        switch (logType) {
            case ERROR:
                Log.e(source, log);
                if (persistentLogger != null) {
                    persistentLogger.e(source, log);
                }
                break;
            case WARNING:
                Log.w(source, log);
                if (persistentLogger != null) {
                    persistentLogger.w(source, log);
                }
                break;
            case INFORMATION:
                if (persistentLogger != null) {
                    persistentLogger.i(source, log);
                } else {
                    Log.i(source, log);
                }
                break;
            case DEBUG: {
                if (persistentLogger != null) {
                    persistentLogger.d(source, log);
                }

                if (mode == ApplicationMode.DEVELOPMENT) {

                    if (log.length() > 4000) {
                        Log.v(source, log.substring(0, 4000));
                        log(source, logType, log.substring(4000));
                    } else
                        Log.v(source, log);

                }

                // if (persistentLogger != null && mode == MODE_PRODUCTION) {
                // persistentLogger.d(log);
                // } else {
                // Log.v(source, log);
                // }
            }

            break;
        }
    }

    //<editor-fold desc="Other utility methods">
    public static String getStringStackTrace(Throwable exception) {

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();

    }

    public static String getFormattedStackTrace() {

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String row = "";
        String trace = "";

        for (StackTraceElement element : stackTraceElements) {
            if (!FLLogger.class.getName().equals(element.getClassName())
                    && element.getClassName().startsWith("ph.com.gs3.formalistics")) {
                row = "   " + element.getClassName() + "." + element.getMethodName() + " @ line "
                        + element.getLineNumber();

                trace += row + "\n";
            }

        }

        return trace + "\n";

    }
    //</editor-fold>

    //<editor-fold desc="Shortcut Methods">
    public static void w(String source, String log) {
        log(source, LogType.WARNING, log);
    }

    public static void e(String source, String log) {
        log(source, LogType.ERROR, log);
    }

    public static void d(String source, String log) {
        log(source, LogType.DEBUG, log);
    }

    public static void i(String source, String log) {
        log(source, LogType.INFORMATION, log);
    }
    //</editor-fold>

    //<editor-fold desc="Getters & Setters">
    public static void setMode(ApplicationMode mode) {
        FLLogger.mode = mode;
    }

    public static ApplicationMode getMode() {
        return FLLogger.mode;
    }

    public static FLPersistentLogger getPersistentLogger() {
        return persistentLogger;
    }

    public static void setPersistentLogger(FLPersistentLogger persistentLogger) {
        FLLogger.persistentLogger = persistentLogger;
    }

    public static boolean isTraceEnabled() {
        return traceEnabled;
    }

    public static void setTraceEnabled(boolean traceEnabled) {
        FLLogger.traceEnabled = traceEnabled;
    }
    //</editor-fold>

}
