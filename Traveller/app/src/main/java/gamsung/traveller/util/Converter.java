package gamsung.traveller.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shin on 2018. 1. 11..
 */

public class Converter {

    public static String SqlDateFormat = "yyyy-MM-dd HH:mm:ss";

    public static String convertSqlDateFormat(Date date){

        return new SimpleDateFormat(SqlDateFormat).format(date);
    }
}
