package es.upm.miw.bantumi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public String dateToString(Date date) {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return outputDateFormat.format(date);
    }

    public int dateToUnixTimestamp(Date date) {
        return (int) (date.getTime() / 1000);
    }

    public String unixTimestampToString(String unixTimestamp) {
        Long unixTimestampLong = Long.parseLong(unixTimestamp);
        Date date = new Date(unixTimestampLong * 1000);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return outputDateFormat.format(date);
    }
}
