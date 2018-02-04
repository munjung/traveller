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
    private HashMap<Integer, Route> m_routeMap;

    public RouteManager(){

        m_routeMap = new HashMap<>();
    }

    public void doSync(SQLiteHelper dbHelper){

        m_routeMap.clear();
        m_routeMap.putAll(_getRouteList(dbHelper));
    }

    public HashMap<Integer, Route> getRouteList(SQLiteHelper dbHelper){

        if(m_routeMap.size() > 0) {
            return m_routeMap;
        }

        m_routeMap.putAll(_getRouteList(dbHelper));
        return m_routeMap;
    }

    public boolean deleteRoute(SQLiteHelper dbHelper, Integer id){

        if(!_deleteRoute(dbHelper, id))
            return false;

        if (m_routeMap.containsKey(id)) {
            m_routeMap.remove(id);
        }

        return true;
    }

    public long insertRoute(SQLiteHelper dbHelper, Route route){

        long rowId = _insertRoute(dbHelper, route);
        if(rowId > 0)
            route.set_id((int)rowId);

        return rowId;
    }

    public int updateRoute(SQLiteHelper dbHelper, Route route){

        int count = _updateRoute(dbHelper, route);
        if(count > 0)
            m_routeMap.put(route.get_id(), route);

        return count;
    }


    private HashMap<Integer, Route> _getRouteList(SQLiteHelper dbHelper){

        HashMap<Integer, Route> routeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Route route = new Route();
                route.set_id(c.getInt(0));                          //id
                route.setTitle(c.getString(1));                     //title
                route.setArea(c.getString(2));                      //area
                route.setFromDate(new Date(c.getLong(3) * 1000));   //from date
                route.setToDate(new Date(c.getLong(4) * 1000));     //to date

                routeMap.put(route.get_id(), route);
            }
            c.close();
        }
        db.close();

        return routeMap;
    }

    private boolean _deleteRoute(SQLiteHelper dbHelper, Integer id){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.RouteTable.column_id + " = " + id);

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(sb.toString());
            db.close();
        }
        catch (Exception ex){
            Log.e("delete route", ex.getMessage());
            return false;
        }

        return true;
    }

    private long _insertRoute(SQLiteHelper dbHelper, Route route){

        ContentValues values = new ContentValues();
        values.put(TableManager.RouteTable.column_title, route.getTitle());                                             //title
        values.put(TableManager.RouteTable.column_area, route.getArea());                                               //area
        values.put(TableManager.RouteTable.column_from_date, Converter.convertSqlDateFormat(route.getFromDate()));      //from
        values.put(TableManager.RouteTable.column_to_date, Converter.convertSqlDateFormat(route.getToDate()));          //to

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    private int _updateRoute(SQLiteHelper dbHelper, Route route){

        ContentValues values = new ContentValues();
        values.put(TableManager.RouteTable.column_title, route.getTitle());                                             //title
        values.put(TableManager.RouteTable.column_area, route.getArea());                                               //area
        values.put(TableManager.RouteTable.column_from_date, Converter.convertSqlDateFormat(route.getFromDate()));      //from
        values.put(TableManager.RouteTable.column_to_date, Converter.convertSqlDateFormat(route.getToDate()));          //to

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.RouteTable.column_id + " = " + route.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);
        db.close();

        return count;
    }
}
