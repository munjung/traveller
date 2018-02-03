package gamsung.traveller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.SearchPlace;
import gamsung.traveller.util.Converter;

/**
 * Created by shin on 2018. 1. 11..
 */

public class SearchPlaceManager {

    private final String TABLE_NAME = TableManager.SearchTable.name;
    private HashMap<Integer, SearchPlace> m_searchList;

    public SearchPlaceManager(){
        m_searchList = new HashMap<>();
    }

    public HashMap<Integer, SearchPlace> getSearchPlaceList(SQLiteHelper dbHelper){

        m_searchList.clear();
        m_searchList.putAll(_getSearchPlaceList(dbHelper));

        return m_searchList;
    }

    public boolean deleteSearchPlace(SQLiteHelper dbHelper, Integer id){

        if(!_deleteSearchPlace(dbHelper, id))
            return false;

        if (m_searchList.containsKey(id)) {
            m_searchList.remove(id);
        }
        return true;
    }

    public long insertSearchPlace(SQLiteHelper dbHelper, SearchPlace searchPlace){

        long rowId = _insertSearchPlace(dbHelper, searchPlace);
        if(rowId > 0)
            searchPlace.set_id((int)rowId);

        return rowId;
    }

    public int updateSearchPlace(SQLiteHelper dbHelper, SearchPlace searchPlace){

        int count = _updateSearchPlace(dbHelper, searchPlace);
        if(count > 0)
            m_searchList.put(searchPlace.get_id(), searchPlace);

        return count;
    }


    private HashMap<Integer, SearchPlace> _getSearchPlaceList(SQLiteHelper dbHelper){

        HashMap<Integer, SearchPlace> searchPlaceHashMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                SearchPlace searchPlace = new SearchPlace();
                searchPlace.set_id(c.getInt(0));
                searchPlace.setPlace_unique_id(c.getInt(1));
                searchPlace.setPlace_name(c.getString(2));
                searchPlace.setPlace_address(c.getString(3));
                searchPlace.setPlace_attribution(c.getString(4));
                searchPlace.setPlace_phone(c.getString(5));
                searchPlace.setPlace_locale(c.getString(6));
                searchPlace.setPlace_uri(c.getString(7));
                searchPlace.setLat(c.getDouble(8));
                searchPlace.setLon(c.getDouble(9));
                searchPlace.setSouthwest_lat(c.getDouble(10));
                searchPlace.setSouthwest_lon(c.getDouble(11));
                searchPlace.setNortheast_lat(c.getDouble(12));
                searchPlace.setNortheast_lon(c.getDouble(13));

                searchPlaceHashMap.put(searchPlace.get_id(), searchPlace);
            }
            c.close();
        }
        db.close();

        return searchPlaceHashMap;
    }

    private boolean _deleteSearchPlace(SQLiteHelper dbHelper, Integer id){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.SearchTable.column_id + " = " + id);

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(sb.toString());
            db.close();
        }
        catch (Exception ex){
            Log.e("delete searchPlace", ex.getMessage());
            return false;
        }

        return true;
    }

    private long _insertSearchPlace(SQLiteHelper dbHelper, SearchPlace searchPlace){

        ContentValues values = new ContentValues();

        values.put(TableManager.SearchTable.column_place_unique_id, searchPlace.getPlace_unique_id());
        values.put(TableManager.SearchTable.column_place_name, searchPlace.getPlace_name());
        values.put(TableManager.SearchTable.column_place_address, searchPlace.getPlace_address());
        values.put(TableManager.SearchTable.column_place_attribution, searchPlace.getPlace_attribution());
        values.put(TableManager.SearchTable.column_place_phone, searchPlace.getPlace_phone());
        values.put(TableManager.SearchTable.column_place_locale, searchPlace.getPlace_locale());
        values.put(TableManager.SearchTable.column_place_uri, searchPlace.getPlace_uri());
        values.put(TableManager.SearchTable.column_lat, searchPlace.getLat());
        values.put(TableManager.SearchTable.column_lon, searchPlace.getLon());
        values.put(TableManager.SearchTable.column_southwest_lat, searchPlace.getSouthwest_lat());
        values.put(TableManager.SearchTable.column_southwest_lon, searchPlace.getSouthwest_lon());
        values.put(TableManager.SearchTable.column_northeast_lat, searchPlace.getNortheast_lat());
        values.put(TableManager.SearchTable.column_northeast_lon, searchPlace.getNortheast_lon());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    private int _updateSearchPlace(SQLiteHelper dbHelper, SearchPlace searchPlace){

        ContentValues values = new ContentValues();
        values.put(TableManager.SearchTable.column_place_unique_id, searchPlace.getPlace_unique_id());
        values.put(TableManager.SearchTable.column_place_name, searchPlace.getPlace_name());
        values.put(TableManager.SearchTable.column_place_address, searchPlace.getPlace_address());
        values.put(TableManager.SearchTable.column_place_attribution, searchPlace.getPlace_attribution());
        values.put(TableManager.SearchTable.column_place_phone, searchPlace.getPlace_phone());
        values.put(TableManager.SearchTable.column_place_locale, searchPlace.getPlace_locale());
        values.put(TableManager.SearchTable.column_place_uri, searchPlace.getPlace_uri());
        values.put(TableManager.SearchTable.column_lat, searchPlace.getLat());
        values.put(TableManager.SearchTable.column_lon, searchPlace.getLon());
        values.put(TableManager.SearchTable.column_southwest_lat, searchPlace.getSouthwest_lat());
        values.put(TableManager.SearchTable.column_southwest_lon, searchPlace.getSouthwest_lon());
        values.put(TableManager.SearchTable.column_northeast_lat, searchPlace.getNortheast_lat());
        values.put(TableManager.SearchTable.column_northeast_lon, searchPlace.getNortheast_lon());


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.SearchTable.column_id + " = " + searchPlace.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);
        db.close();

        return count;
    }
}
