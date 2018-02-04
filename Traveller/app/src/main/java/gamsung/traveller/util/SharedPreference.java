package gamsung.traveller.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by shin on 2018. 1. 25..
 */

public class SharedPreference {

    private static String splitter = ";";

    public static String getValue(Context context, String key, String tag){

        SharedPreferences sp = context.getSharedPreferences(key, context.MODE_PRIVATE);
        String val = sp.getString(tag, null);

        return val;
    }

    public static void setValue(Context context, String key, String tag, String value)
    {
        SharedPreferences sp = context.getSharedPreferences(key, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(tag, value);
        editor.commit();
    }

    public static void clearValue(Context context, String key, String tag)
    {
        SharedPreferences sp = context.getSharedPreferences(key, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.clear();
        editor.commit();
    }

    public static List<String> getValueList(Context context, String key, String tag){

        SharedPreferences sp = context.getSharedPreferences(key, context.MODE_PRIVATE);
        String val = sp.getString(tag, null);

        List<String> valueList = new ArrayList<>();
        if(val != null && val != "")
        {
            String[] temp = val.split(splitter);
            valueList = Arrays.asList(temp);
        }

        return valueList;
    }

    public static void setValueList(Context context, String key, String tag, String value)
    {
        SharedPreferences sp = context.getSharedPreferences(key, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String val = getValue(context, key, tag);
        val += value;
        val += splitter;

        editor.putString(tag, val);
        editor.commit();
    }
}
