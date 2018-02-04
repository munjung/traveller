package gamsung.traveller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Place;
import gamsung.traveller.model.Route;
import gamsung.traveller.util.Converter;

import static gamsung.traveller.dto.TableManager.PlaceTable.column_section_id;

/**
 * Created by shin on 2018. 1. 11..
 */

public class PlaceManager {

    private final String TABLE_NAME = TableManager.PlaceTable.name;
    private HashMap<Integer, Place> m_placeMap;

    public PlaceManager(){

        m_placeMap = new HashMap<>();
    }


    public HashMap<Integer, Place> getPlaceList(SQLiteHelper dbHelper){

        m_placeMap.clear();
        m_placeMap.putAll(_getPlaceList(dbHelper));

        return m_placeMap;
    }

    public boolean deletePlace(SQLiteHelper dbHelper, Integer id){

        if(!_deletePlace(dbHelper, id))
            return false;

        if (m_placeMap.containsKey(id)) {
            m_placeMap.remove(id);
        }

        return true;
    }

    public long insertPlace(SQLiteHelper dbHelper, Place place){

        long rowId = _insertPlace(dbHelper, place);
        if(rowId > 0)
            place.set_id((int)rowId);

        return rowId;
    }

    public int updatePlace(SQLiteHelper dbHelper, Place place){

        int count = _updatePlace(dbHelper, place);
        if(count > 0)
            m_placeMap.put(place.get_id(), place);

        return count;
    }


    private HashMap<Integer, Place> _getPlaceList(SQLiteHelper dbHelper){

        HashMap<Integer, Place> placeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        //sb.append("WHERE " + TableManager.PlaceTable.column_route_id + " = " + id);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Place place = new Place();
                place.set_id(c.getInt(0));                          //id
                place.setRoute_id(c.getInt(1));                   //route_id
                place.setNext_place_id(c.getInt(2));              //next_place
                place.setPicture_id(c.getInt(3));                 //picture
                place.setMission(c.getString(4));                 //mission
                place.setSearch_id(c.getInt(5));                  //search

                placeMap.put(place.get_id(), place);
            }
            c.close();
        }
        db.close();

        return placeMap;
    }

    private boolean _deletePlace(SQLiteHelper dbHelper, Integer id){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.PlaceTable.column_id + " = " + id);

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(sb.toString());
            db.close();
        }
        catch (Exception ex){
            Log.e("delete place", ex.getMessage());
            return false;
        }

        return true;
    }

    private long _insertPlace(SQLiteHelper dbHelper, Place place){

        ContentValues values = new ContentValues();
        values.put(TableManager.PlaceTable.column_route_id, place.getRoute_id());
        values.put(TableManager.PlaceTable.column_next_place_id, place.getNext_place_id());
        values.put(TableManager.PlaceTable.column_picture_id, place.getPicture_id());
        values.put(TableManager.PlaceTable.column_mission, place.getMission());
        values.put(TableManager.PlaceTable.column_search_id, place.getSearch_id());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    private int _updatePlace(SQLiteHelper dbHelper, Place place){

        ContentValues values = new ContentValues();
        values.put(TableManager.PlaceTable.column_route_id, place.getRoute_id());
        values.put(TableManager.PlaceTable.column_next_place_id, place.getNext_place_id());
        values.put(TableManager.PlaceTable.column_picture_id, place.getPicture_id());
        values.put(TableManager.PlaceTable.column_mission, place.getMission());
        values.put(TableManager.PlaceTable.column_search_id, place.getSearch_id());


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.RouteTable.column_id + " = " + place.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);
        db.close();

        return count;
    }

}

