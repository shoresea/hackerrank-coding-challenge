package com.hackerrank.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class DateTimeUtil {
    public static Timestamp getCurrentTimeInUTC() {
        return new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis());
    }

    public static Timestamp getRelativeTimeInUTC(Long differenceInSeconds) {
        return new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis() + differenceInSeconds * 1000l);
    }
}
