// This is a custom class that handles dates and time to be formatted to ISO 8601[1]
// and what the system locale decides.
//
// [1]
// https://vi.wikipedia.org/wiki/ISO_8601
// https://en.wikipedia.org/wiki/ISO_8601

package com.tdtu.mywallet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateTimeUtils {
    public static String formatToISO8601Date(Calendar calendar) {
        SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.US);
        return iso8601DateFormat.format(calendar.getTime());
    }

    public static String formatToUserLocaleDate(String iso8601Date) {
        SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd",
                Locale.US);
        SimpleDateFormat userLocaleDateFormat = new SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault());

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(iso8601DateFormat.parse(iso8601Date));
            return userLocaleDateFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return iso8601Date;
        }
    }

    public static String formatToISO8601Time(Calendar calendar) {
        SimpleDateFormat iso8601TimeFormat = new SimpleDateFormat("HH:mm:ss",
                Locale.US);
        return iso8601TimeFormat.format(calendar.getTime());
    }

    public static String formatToUserLocaleTime(String iso8601Time) {
        SimpleDateFormat iso8601TimeFormat = new SimpleDateFormat("HH:mm:ss",
                Locale.US);
        SimpleDateFormat userLocaleTimeFormat = new SimpleDateFormat("hh:mm a",
                Locale.getDefault());

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(iso8601TimeFormat.parse(iso8601Time));
            return userLocaleTimeFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return iso8601Time;
        }
    }
}
