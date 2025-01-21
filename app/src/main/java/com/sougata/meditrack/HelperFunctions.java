package com.sougata.meditrack;

import android.content.Context;
import android.util.TypedValue;

import java.util.Calendar;

public class HelperFunctions {
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String isExpired(String time) {
        String[] s = time.split(" ");
        int h24Clock = s[1].equals("AM") ? 0 : 12;
        String[] s2 = s[0].split(":");
        int h = Integer.parseInt(s2[0]);
        int m = Integer.parseInt(s2[1]);
        if (h == 12) {
            h = 0;
        }
        h += h24Clock;
        Calendar calendar = Calendar.getInstance();
        int ch = calendar.get(Calendar.HOUR_OF_DAY);
        int cm = calendar.get(Calendar.MINUTE);
        if (ch > h) {
            return "Expired for Today";
        }
        if (cm > m) {
            h -= 1;
            m += 60;
        }
        int resultH = h - ch;
        if (resultH < 0 || h < 0) {
            return "Expired for Today";
        }
        int resultM = m - cm;
        return "In " + pad(resultH) + ":" + pad(resultM) + " Hour";
    }

    public static String pad(int num) {
        if (num < 10) {
            return "0" + String.valueOf(num);
        } else {
            return String.valueOf(num);
        }
    }

    public static String get12hTime(int h, int m) {
        String meridian = "AM", hour, min;
        if (h >= 12) {
            meridian = "PM";
        }
        h = h % 12;
        if (h == 0) {
            h = 12;
        }
        if (h < 10) {
            hour = "0" + h;
        } else {
            hour = String.valueOf(h);
        }
        if (m < 10) {
            min = "0" + m;
        } else {
            min = String.valueOf(m);
        }
        return hour + ":" + min + " " + meridian;
    }

    public static String timeToString(long time) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
        long t = c.getTimeInMillis() + time;
        c.setTimeInMillis(t);
        return get12hTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    public static Calendar timeMillisToHM(long time) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 0, 0, 0);
        long t = c.getTimeInMillis() + time;
        c.setTimeInMillis(t);
        return c;
    }

    public static String calendarToDate(Calendar c) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DATE);
        return pad(d) + "/" + pad(m + 1) + "/" + y;
    }
}
