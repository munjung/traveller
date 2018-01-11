package gamsung.traveller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Route;
import gamsung.traveller.util.Converter;

/**
 * Created by shin on 2018. 1. 11..
 */

public class RouteManager {

    private static String tableName = TableManager.RouteTable.name;
    private static String[] columns = TableManager.RouteTable.columns;

    public static List<Route> getRouteList(SQLiteHelper dbHelper){

        List<Route> routeList = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + tableName);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Route route = new Route();
                route.set_id(c.getInt(0));                          //id
                route.setTitle(c.getString(1));                     //title
                route.setFromDate(new Date(c.getLong(2) * 1000));   //from date
                route.setToDate(new Date(c.getLong(3) * 1000));     //to date

                routeList.add(route);
            }
            c.close();
        }
        db.close();

        return routeList;
    }

    public static void deleteRoute(SQLiteHelper dbHelper, String name){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + tableName);
        sb.append(" WHERE NAME = " + name);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sb.toString());
        db.close();
    }

    public static long insertRoute(SQLiteHelper dbHelper, Route route){

        ContentValues values = new ContentValues();
        values.put(TableManager.RouteTable.column_title, route.getTitle());                                             //title
        values.put(TableManager.RouteTable.column_from_date, Converter.convertSqlDateFormat(route.getFromDate()));      //from
        values.put(TableManager.RouteTable.column_to_date, Converter.convertSqlDateFormat(route.getToDate()));          //to

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(tableName, null, values);
        db.close();

        return rowId;
    }

    public static int updateRoute(SQLiteHelper dbHelper, Route route){

        ContentValues values = new ContentValues();
        values.put(TableManager.RouteTable.column_title, route.getTitle());                                             //title
        values.put(TableManager.RouteTable.column_from_date, Converter.convertSqlDateFormat(route.getFromDate()));      //from
        values.put(TableManager.RouteTable.column_to_date, Converter.convertSqlDateFormat(route.getToDate()));          //to

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.RouteTable.column_id + "=" + route.get_id();
        int count = db.update(tableName, values, selection, null);
        db.close();

        return count;
    }
}
