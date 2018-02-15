package gamsung.traveller.util;

import java.text.ParseException;
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

    public static String convertDateToString(String format, Date date){

        return (String)android.text.format.DateFormat.format(format, date);
    }

    public static Date convertStringToDate(String strDate){
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate);
            return date;
        }
        catch (ParseException e){
            return null;
        }
    }
}
