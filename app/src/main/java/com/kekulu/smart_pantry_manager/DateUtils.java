package com.kekulu.smart_pantry_manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static long getDaysUntilExpiry(String expiryStr) {
        if (expiryStr == null || expiryStr.trim().isEmpty()) {
            return Long.MAX_VALUE;
        }

        String trimmed = expiryStr.trim();
        SimpleDateFormat[] formats = new SimpleDateFormat[]{
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                new SimpleDateFormat("d/M/yyyy", Locale.getDefault()),
                new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        };

        for (SimpleDateFormat sdf : formats) {
            try {
                sdf.setLenient(false);
                Date expiryDate = sdf.parse(trimmed);
                if (expiryDate != null) {
                    Calendar today = Calendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    Calendar exp = Calendar.getInstance();
                    exp.setTime(expiryDate);
                    exp.set(Calendar.HOUR_OF_DAY, 0);
                    exp.set(Calendar.MINUTE, 0);
                    exp.set(Calendar.SECOND, 0);
                    exp.set(Calendar.MILLISECOND, 0);

                    long diffMs = exp.getTimeInMillis() - today.getTimeInMillis();
                    return diffMs / (1000 * 60 * 60 * 24);
                }
            } catch (ParseException ignored) {
            }
        }
        return Long.MAX_VALUE;
    }
}
