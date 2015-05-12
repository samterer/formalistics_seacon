package ph.com.gs3.formalistics.global.utilities.logging;

import android.content.Context;

import java.io.FileOutputStream;
import java.util.Date;

import ph.com.gs3.formalistics.global.utilities.DateUtilities;

public class FLPersistentLogger {

    public static final String ERROR_LOG_FILE = "error";
    public static final String WARNING_LOG_FILE = "warning";
    public static final String INFORMATION_LOG_FILE = "information";
    public static final String DEBUG_LOG_FILE = "debug";

    private final Context context;

    public FLPersistentLogger(Context context) {
        this.context = context;
    }

    public void e(String source, String message) {

        String logFileName = ERROR_LOG_FILE + "_"
                + DateUtilities.LOG_DATE_ONLY_FORMAT.format(new Date());
        log(logFileName, source + "\n" + message);

    }

    public void w(String source, String message) {
        String logFileName = WARNING_LOG_FILE + "_"
                + DateUtilities.LOG_DATE_ONLY_FORMAT.format(new Date());
        log(logFileName, source + "\n" + message);
    }

    public void i(String source, String message) {
        String logFileName = INFORMATION_LOG_FILE + "_"
                + DateUtilities.LOG_DATE_ONLY_FORMAT.format(new Date());
        log(logFileName, source + "\n" + message);
    }

    public void d(String source, String message) {
        String logFileName = DEBUG_LOG_FILE + "_"
                + DateUtilities.LOG_DATE_ONLY_FORMAT.format(new Date());
        log(logFileName, source + "\n" + message);
    }

    public void log(String file, String message) {

        FileOutputStream outputStream;

        try {

            String messageWithDate = DateUtilities.DEFAULT_DISPLAY_TIME_ONLY_FORMAT
                    .format(new Date())
                    + "\n"
                    + message
                    + "\n------------------------------------------------------\n\n";

            outputStream = context.openFileOutput(file, Context.MODE_APPEND);
            outputStream.write(messageWithDate.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
