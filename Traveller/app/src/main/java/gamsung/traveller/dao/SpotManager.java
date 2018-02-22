package gamsung.traveller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Spot;
import gamsung.traveller.model.SpotWithCoordinate;

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
        return _getSpotListWithRouteId(dbHelper, routeId);
    }

    public Spot getLastIndexSpot(SQLiteHelper dbHelper){
        return _getLastIndexSpot(dbHelper);
    }

    public HashMap<Integer, SpotWithCoordinate> getSpotWithCoordinateListOnRouteID(SQLiteHelper dbHelper, Integer route_id){
        return _getSpotWithCoordinateListOnRouteID(dbHelper, route_id);
    }

    public boolean deleteSpot(SQLiteHelper dbHelper, Integer id){

        if(!_deleteSpot(dbHelper, id))
            return false;

        if (m_spotMap.containsKey(id)) {
            m_spotMap.remove(id);
        }

        return true;
    }

    public boolean deleteSpotWithRouteID(SQLiteHelper dbHelper, Integer route_id){

        if(!_deleteSpotWithRouteID(dbHelper, route_id))
            return false;

//        if (m_spotMap.containsKey(id)) {
//            m_spotMap.remove(id);
//        }

        return true;
    }



    public long insertSpot(SQLiteHelper dbHelper, Spot spot, Integer index){

        long rowId = _insertSpot(dbHelper, spot, index);
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

    public int updateSpotIndex(SQLiteHelper dbHelper, Integer before_index, Integer after_index){

        int count = _updateSpotIndex(dbHelper, before_index, after_index);
        return count;
    }



    private HashMap<Integer, Spot> _getSpotList(SQLiteHelper dbHelper){

        HashMap<Integer, Spot> placeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" ORDER BY " + TableManager.SpotTable.column_index_id + " ASC");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Spot spot = new Spot();
                spot.set_id(c.getInt(0));                          //id
                spot.setRoute_id(c.getInt(1));                   //route_id
                spot.setIndex_id(c.getInt(2));                   //index_id
                spot.setPicture_path(c.getString(3));           //picture
                spot.setMission(c.getString(4));                 //mission
                spot.setSearch_id(c.getInt(5));                  //search
                spot.setCategory_id(c.getInt(6));                //category (Eat,buy..)

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
        sb.append(" ORDER BY " + TableManager.SpotTable.column_index_id + " ASC");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Spot spot = new Spot();
                spot.set_id(c.getInt(0));                          //id
                spot.setRoute_id(c.getInt(1));                   //route_id
                spot.setIndex_id(c.getInt(2));                  //index
                spot.setPicture_path(c.getString(3));           //picture
                spot.setMission(c.getString(4));                //mission
                spot.setSearch_id(c.getInt(5));                 //search
                spot.setCategory_id(c.getInt(6));               //category(eat, buy,,,)

                placeMap.put(spot.get_id(), spot);
            }
            c.close();
        }
        db.close();

        return placeMap;
    }

    private Spot _getLastIndexSpot(SQLiteHelper dbHelper) {
        Spot spot = new Spot();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME );
        sb.append(" ORDER BY " + TableManager.SpotTable.column_id + " DESC LIMIT 1");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null  && c.getCount() > 0){
            while (c.moveToNext()){

                spot.set_id(c.getInt(0));                          //id
                spot.setRoute_id(c.getInt(1));                   //route_id
                spot.setIndex_id(c.getInt(2));
                spot.setPicture_id(c.getInt(3));                 //picture
                spot.setMission(c.getString(4));                 //mission
                spot.setSearch_id(c.getInt(5));                  //search
            }
            c.close();
        }
        db.close();

        return spot;
    }

    private HashMap<Integer, SpotWithCoordinate> _getSpotWithCoordinateListOnRouteID(SQLiteHelper dbHelper, Integer route_id) {
        HashMap<Integer, SpotWithCoordinate> spotList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * ");
        //sb.append("SELECT " + TableManager.SearchTable.column_lat + " , " + TableManager.SearchTable.column_lon );
        sb.append(" FROM " + TABLE_NAME);
        sb.append(" JOIN " + TableManager.SearchTable.name);
        sb.append(" ON " + TableManager.SpotTable.name + "." +TableManager.SpotTable.column_search_id + " = " + TableManager.SearchTable.name + "." + TableManager.SearchTable.column_id );
        sb.append(" WHERE " + TableManager.SpotTable.column_route_id + " = " + route_id);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                SpotWithCoordinate spot = new SpotWithCoordinate();
                spot.set_id(c.getInt(c.getColumnIndex(TableManager.SpotTable.column_id)));
                spot.setRoute_id(c.getInt(c.getColumnIndex(TableManager.SpotTable.column_route_id)));
                spot.setIndex_id(c.getInt(c.getColumnIndex(TableManager.SpotTable.column_index_id)));
                spot.setPicture_path(c.getString(c.getColumnIndex(TableManager.SpotTable.column_picture_path)));
                spot.setMission(c.getString(c.getColumnIndex(TableManager.SpotTable.column_mission)));
                spot.setSearch_id(c.getInt(c.getColumnIndex(TableManager.SpotTable.column_search_id)));
                spot.setLat(c.getDouble(c.getColumnIndex(TableManager.SearchTable.column_lat)));
                spot.setLon(c.getDouble(c.getColumnIndex(TableManager.SearchTable.column_lon)));

                spotList.put(spot.get_id(), spot);
            }
            c.close();
        }
        db.close();

        return spotList;
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

    private long _insertSpot(SQLiteHelper dbHelper, Spot spot, Integer index){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_route_id, spot.getRoute_id());
        values.put(TableManager.SpotTable.column_index_id, index);
        values.put(TableManager.SpotTable.column_picture_path, spot.getPicture_path());
        values.put(TableManager.SpotTable.column_mission, spot.getMission());
        values.put(TableManager.SpotTable.column_search_id, spot.getSearch_id());
        values.put(TableManager.SpotTable.column_category_id, spot.getCategory_id());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    private int _updateSpot(SQLiteHelper dbHelper, Spot spot){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_route_id, spot.getRoute_id());
        values.put(TableManager.SpotTable.column_index_id, spot.getIndex_id());
        values.put(TableManager.SpotTable.column_picture_path, spot.getPicture_path());
        values.put(TableManager.SpotTable.column_mission, spot.getMission());
        values.put(TableManager.SpotTable.column_search_id, spot.getSearch_id());
        values.put(TableManager.SpotTable.column_category_id, spot.getCategory_id());


        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.SpotTable.column_id + " = " + spot.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);
        db.close();

        return count;
    }

    private int _updateSpotIndex(SQLiteHelper dbHelper, Integer before_index, Integer after_index){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_index_id, after_index);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.SpotTable.column_index_id + " = " + before_index;

        int count = db.update(TABLE_NAME, values, selection, null);
        db.close();

        return count;
    }

    public boolean _deleteSpotWithRouteID(SQLiteHelper dbHelper, Integer id) {
        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.SpotTable.column_route_id + " = " + id);

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
}

