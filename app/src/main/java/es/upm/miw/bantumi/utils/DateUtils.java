package es.upm.miw.bantumi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public String dateToString(Date date) {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return outputDateFormat.format(date);
    }
}
