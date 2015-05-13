package ph.com.gs3.formalistics.global.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ph.com.gs3.formalistics.model.api.API;

public class DateUtilities {

    // @formatter:off
    public static final SimpleDateFormat SERVER_DATE_FORMAT = new SimpleDateFormat(API.SERVER_DATE_FORMAT, Locale.ENGLISH);
    public static final SimpleDateFormat SERVER_DATE_TIME_FORMAT = new SimpleDateFormat(API.SERVER_DATE_TIME_FORMAT, Locale.ENGLISH);
    public static final SimpleDateFormat SERVER_TIME_FORMAT = new SimpleDateFormat(API.SERVER_TIME_FORMAT, Locale.ENGLISH);

    public static final SimpleDateFormat DEFAULT_DISPLAY_DATE_ONLY_FORMAT = new SimpleDateFormat("MMM/dd/yyyy", Locale.ENGLISH);
    public static final SimpleDateFormat DEFAULT_DISPLAY_TIME_ONLY_FORMAT = new SimpleDateFormat("hh:mm:a", Locale.ENGLISH);
    public static final SimpleDateFormat DEFAULT_DISPLAY_DATE_TIME_FORMAT = new SimpleDateFormat("MMM/dd/yyyy hh:mm:a", Locale.ENGLISH);

    public static final SimpleDateFormat WIDGET_DISPLAY_DATE_ONLY_FORMAT = new SimpleDateFormat("MMM dd", Locale.ENGLISH);
    public static final SimpleDateFormat WIDGET_DISPLAY_TIME_ONLY_FORMAT = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);

    public static final SimpleDateFormat LOG_DATE_ONLY_FORMAT = new SimpleDateFormat("yyyy_MM_dd", Locale.ENGLISH);

    public static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
    // @formatter:on

    @SuppressWarnings("deprecation")
    public static boolean isDateToday(Date date) {

        Date today = new Date();
        return date.getDate() == today.getDate() && date.getMonth() == today.getMonth() && date.getYear() == today.getYear();

    }

    public static Date parseToServerDate(String rawDate) throws ParseException {

        return SERVER_DATE_TIME_FORMAT.parse(rawDate);

    }

    public static String getServerFormattedCurrentDateTime() {

        Date currentDate = new Date();
        return SERVER_DATE_TIME_FORMAT.format(currentDate);

    }

    public static String getServerFormattedCurrentDate() {

        Date currentDate = new Date();
        return SERVER_DATE_FORMAT.format(currentDate);

    }

    public static String getCurrentTimeStamp() {

        Date currentDate = new Date();
        return TIMESTAMP_FORMAT.format(currentDate);

    }
}
