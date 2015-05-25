package common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Long on 5/17/2015.
 */
public class DateUtils {
    public static String getDateTimeString(long milliseconds) {
        Date then = new Date(milliseconds);
        Date now = new Date();

        DateFormat format = new SimpleDateFormat("HH:mm");

        String date = format.format(then);

        int daysDiff = getDaysDiff(then, now);
        if (daysDiff == 1) {
            date = "昨天 " + date;
        }
        else if(daysDiff > 1){
            format = new SimpleDateFormat("MM月dd日");
            date = format.format(then) + " " + date;
        }

        return date;
    }

    private static int getDaysDiff(Date date1, Date date2) {
        long millisecondsDiff = date2.getTime() - date1.getTime();
        int daysDiff = (int) (millisecondsDiff / 1000 / 3600 / 24);

        return Math.abs(daysDiff);
    }
}
