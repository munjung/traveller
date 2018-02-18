package gamsung.traveller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Spot;

/**
 * Created by shin on 2018. 1. 11..
 */

public class SpotManager {

    private final String TABLE_NAME = TableManager.SpotTable.name;
    private HashMap<Integer, Spot> m_spotMap;

    public SpotManager(){

        m_spotMap = new HashMap<>();
    }


    public HashMap<Integer, Spot> getSpotList(SQLiteHelper dbHelper){

        m_spotMap.clear();
        m_spotMap.putAll(_getSpotList(dbHelper));

        return m_spotMap;
    }

    public HashMap<Integer, Spot> getSpotListWithRouteId(SQLiteHelper dbHelper, int routeId){

        m_spotMap.clear();
        m_spotMap.putAll(_getSpotListWithRouteId(dbHelper, routeId));

        return m_spotMap;
    }

    public boolean deleteSpot(SQLiteHelper dbHelper, Integer id){

        if(!_deleteSpot(dbHelper, id))
            return false;

        if (m_spotMap.containsKey(id)) {
            m_spotMap.remove(id);
        }

        return true;
    }

    public long insertSpot(SQLiteHelper dbHelper, Spot spot){

        long rowId = _insertSpot(dbHelper, spot);
        if(rowId > 0)
            spot.set_id((int)rowId);

        return rowId;
    }

    public int updateSpot(SQLiteHelper dbHelper, Spot spot){

        int count = _updateSpot(dbHelper, spot);
        if(count > 0)
            m_spotMap.put(spot.get_id(), spot);

        return count;
    }


    private HashMap<Integer, Spot> _getSpotList(SQLiteHelper dbHelper){

        HashMap<Integer, Spot> placeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Spot spot = new Spot();
                spot.set_id(c.getInt(0));                          //id
                spot.setRoute_id(c.getInt(1));                   //route_id
                spot.setNext_spot_id(c.getInt(2));              //next_place
                spot.setPicture_id(c.getInt(3));                 //picture
                spot.setMission(c.getString(4));                 //mission
                spot.setSearch_id(c.getInt(5));                  //search

                placeMap.put(spot.get_id(), spot);
            }
            c.close();
        }
        db.close();

        return placeMap;
    }

    private HashMap<Integer, Spot> _getSpotListWithRouteId(SQLiteHelper dbHelper, int routeId){

        HashMap<Integer, Spot> placeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.SpotTable.column_route_id + " = " + routeId);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Spot spot = new Spot();
                spot.set_id(c.getInt(0));                          //id
                spot.setRoute_id(c.getInt(1));                   //route_id
                spot.setNext_spot_id(c.getInt(2));              //next_place
                spot.setPicture_id(c.getInt(3));                 //picture
                spot.setMission(c.getString(4));                 //mission
                spot.setSearch_id(c.getInt(5));                  //search

                placeMap.put(spot.get_id(), spot);
            }
            c.close();
        }
        db.close();

        return placeMap;
    }


    private boolean _deleteSpot(SQLiteHelper dbHelper, Integer id){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.SpotTable.column_id + " = " + id);

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

    private long _insertSpot(SQLiteHelper dbHelper, Spot spot){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_route_id, spot.getRoute_id());
        values.put(TableManager.SpotTable.column_next_spot_id, spot.getNext_spot_id());
        values.put(TableManager.SpotTable.column_picture_id, spot.getPicture_id());
        values.put(TableManager.SpotTable.column_mission, spot.getMission());
        values.put(TableManager.SpotTable.column_search_id, spot.getSearch_id());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    private int _updateSpot(SQLiteHelper dbHelper, Spot spot){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_route_id, spot.getRoute_id());
        values.put(TableManager.SpotTable.column_next_spot_id, spot.getNext_spot_id());
        values.put(TableManager.SpotTable.column_picture_id, spot.getPicture_id());
        values.put(TableManager.SpotTable.column_mission, spot.getMission());
        values.put(TableManager.SpotTable.column_search_id, spot.getSearch_id());


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.RouteTable.column_id + " = " + spot.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);
        db.close();

        return count;
    }

}

