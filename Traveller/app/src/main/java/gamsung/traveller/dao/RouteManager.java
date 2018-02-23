package gamsung.traveller.dao;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Route;
import gamsung.traveller.util.Converter;

/**
 * Created by shin on 2018. 1. 11..
 *
 * Define. Route 데이터를 저장하는 데이터베이스를 관리한다. (Table Name = ROUTE)
 *       . 내부적으로 Route 데이터를 메모리로 관리한다.
 */

public class RouteManager {

    private final String TABLE_NAME = TableManager.RouteTable.name;

    public RouteManager(){

    }

    public HashMap<Integer, Route> getRouteList(SQLiteDatabase db){

        return _getRouteList(db);
    }

    public Route getRouteWithID(SQLiteDatabase db, int id){

        return _getRouteWithID(db, id);
    }

    public HashMap<Integer, Route> getRouteHasToday(SQLiteDatabase db){

        return _getRouteHasToday(db, new Date());
    }

    public HashMap<Integer, Route> getRouteWithSearch(SQLiteDatabase db, String search_word){

        return _getRouteWithSearch(db, search_word);
    }

    public boolean deleteRoute(SQLiteDatabase db, Integer id){

        if(!_deleteRoute(db, id))
            return false;

        return true;
    }

    public long insertRoute(SQLiteDatabase db, Route route){

        long rowId = _insertRoute(db, route);
        if(rowId > 0)
            route.set_id((int)rowId);

        return rowId;
    }

    public int updateRoute(SQLiteDatabase db, Route route){

        int count = _updateRoute(db, route);

        return count;
    }


    private HashMap<Integer, Route> _getRouteList(SQLiteDatabase db){

        HashMap<Integer, Route> routeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Route route = new Route();
                route.set_id(c.getInt(0));                                                  //id
                route.setTitle(c.getString(1));                                             //title
                route.setFromDate(Converter.convertStringToDate(c.getString(2)));          //from date
                route.setToDate(Converter.convertStringToDate(c.getString(3)));            //to date
                route.setPicturPath(c.getString(4));                                        //picture path

                routeMap.put(route.get_id(), route);
            }
            c.close();
        }

        return routeMap;
    }

    private Route _getRouteWithID(SQLiteDatabase db, int id){

        Route route = new Route();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.RouteTable.column_id + " = " + id);

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                route.set_id(c.getInt(0));                                                  //id
                route.setTitle(c.getString(1));                                             //title
                route.setFromDate(Converter.convertStringToDate(c.getString(2)));          //from date
                route.setToDate(Converter.convertStringToDate(c.getString(3)));            //to date
                route.setPicturPath(c.getString(4));                                        //picture path
            }
            c.close();
        }

        return route;
    }

    private HashMap<Integer, Route> _getRouteHasToday(SQLiteDatabase db, Date today){

        HashMap<Integer, Route> routeMap = new HashMap<>();


        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE strftime('%s', '" + today + "') >= strftime('%s', " + TableManager.RouteTable.column_from_date + ")" +
                  " AND " + "strftime('%s', '"+ today + "') <= strftime('%s' , " + TableManager.RouteTable.column_to_date + ")");

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){
                Route route = new Route();
                route.set_id(c.getInt(0));                                                  //id
                route.setTitle(c.getString(1));                                             //title
                route.setFromDate(Converter.convertStringToDate(c.getString(2)));          //from date
                route.setToDate(Converter.convertStringToDate(c.getString(3)));            //to date
                route.setPicturPath(c.getString(4));                                        //picture path
            }
            c.close();
        }

        return routeMap;
    }

    private HashMap<Integer, Route> _getRouteWithSearch(SQLiteDatabase db, String search_word){

        HashMap<Integer, Route> routeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.RouteTable.column_title + " LIKE '%" + search_word + "%'");

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){
                Route route = new Route();
                route.set_id(c.getInt(0));                                                  //id
                route.setTitle(c.getString(1));                                             //title
                route.setFromDate(Converter.convertStringToDate(c.getString(2)));          //from date
                route.setToDate(Converter.convertStringToDate(c.getString(3)));            //to date
                route.setPicturPath(c.getString(4));                                        //picture path
            }
            c.close();
        }

        return routeMap;
    }



    private boolean _deleteRoute(SQLiteDatabase db, Integer id){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.RouteTable.column_id + " = " + id);

        try {
            db.execSQL(sb.toString());
        }
        catch (Exception ex){
            Log.e("delete route", ex.getMessage());
            return false;
        }

        return true;
    }

    private long _insertRoute(SQLiteDatabase db, Route route){

        ContentValues values = new ContentValues();
        values.put(TableManager.RouteTable.column_title, route.getTitle());                                             //title
        values.put(TableManager.RouteTable.column_from_date, Converter.convertSqlDateFormat(route.getFromDate()));      //from
        values.put(TableManager.RouteTable.column_to_date, Converter.convertSqlDateFormat(route.getToDate()));          //to
        values.put(TableManager.RouteTable.column_picture_path, route.getPicturePath());

        long rowId = db.insert(TABLE_NAME, null, values);

        return rowId;
    }

    private int _updateRoute(SQLiteDatabase db, Route route){

        ContentValues values = new ContentValues();
        values.put(TableManager.RouteTable.column_title, route.getTitle());                                             //title
        values.put(TableManager.RouteTable.column_from_date, Converter.convertSqlDateFormat(route.getFromDate()));      //from
        values.put(TableManager.RouteTable.column_to_date, Converter.convertSqlDateFormat(route.getToDate()));          //to
        values.put(TableManager.RouteTable.column_picture_path, route.getPicturePath());

        String selection = TableManager.RouteTable.column_id + " = " + route.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);

        return count;
    }
}
