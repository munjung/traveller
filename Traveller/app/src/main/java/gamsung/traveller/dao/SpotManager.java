package gamsung.traveller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Spot;
import gamsung.traveller.model.SpotWithCoordinate;

/**
 * Created by shin on 2018. 1. 11..
 */

public class SpotManager {

    private final String TABLE_NAME = TableManager.SpotTable.name;


    public SpotManager(){

    }


    public HashMap<Integer, Spot> getSpotList(SQLiteDatabase db){

        return _getSpotList(db);
    }

    public HashMap<Integer, Spot> getSpotListWithRouteId(SQLiteDatabase db, int routeId){
        return _getSpotListWithRouteId(db, routeId);
    }

    public Spot getLastIndexSpot(SQLiteDatabase db){
        return _getLastIndexSpot(db);
    }
        
    public Spot getSpotIDWithIndexID(SQLiteDatabase db, Integer index_id){
        return _getSpotIDWithIndexID(db, index_id);
    }

    public HashMap<Integer, SpotWithCoordinate> getSpotWithCoordinateList(SQLiteDatabase db){
        return _getSpotWithCoordinateListOnRouteID(db);
    }
    
    public HashMap<Integer, SpotWithCoordinate> getSpotWithCoordinateListOnRouteID(SQLiteDatabase db, Integer route_id){
        return _getSpotWithCoordinateListOnRouteID(db, route_id);
    }

    public boolean deleteSpot(SQLiteDatabase db, Integer id){

        return _deleteSpot(db, id);
    }

    public boolean deleteSpotWithRouteID(SQLiteDatabase db, Integer route_id){

        return _deleteSpotWithRouteID(db, route_id);
    }


    public long insertSpot(SQLiteDatabase db, Spot spot){

        long rowId = _insertSpot(db, spot);
        if(rowId > 0)
            spot.set_id((int)rowId);

        return rowId;
    }

    public long insertSpot(SQLiteDatabase db, Spot spot, Integer index){

        long rowId = _insertSpot(db, spot, index);
        if(rowId > 0)
            spot.set_id((int)rowId);

        return rowId;
    }

    public int updateSpot(SQLiteDatabase db, Spot spot){

        int count = _updateSpot(db, spot);

        return count;
    }

    public int updateSpot(SQLiteDatabase db, Spot spot, int index){

        int count = _updateSpot(db, spot, index);

        return count;
    }

    public int updateSpotIndex(SQLiteDatabase db, Integer id, Integer after_index){

        int count = _updateSpotIndex(db, id, after_index);

        return count;
    }



    private HashMap<Integer, Spot> _getSpotList(SQLiteDatabase db){

        HashMap<Integer, Spot> placeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" ORDER BY " + TableManager.SpotTable.column_index_id + " ASC");

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

        return placeMap;
    }

    private HashMap<Integer, Spot> _getSpotListWithRouteId(SQLiteDatabase db, int routeId){

        HashMap<Integer, Spot> placeMap = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.SpotTable.column_route_id + " = " + routeId);
        sb.append(" ORDER BY " + TableManager.SpotTable.column_index_id + " ASC");

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

        return placeMap;
    }

    private Spot _getLastIndexSpot(SQLiteDatabase db) {
        Spot spot = new Spot();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME );
        sb.append(" ORDER BY " + TableManager.SpotTable.column_id + " DESC LIMIT 1");

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
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

        return spot;
    }

    private Spot _getSpotIDWithIndexID(SQLiteDatabase db, Integer index_id) {
        Spot spot = new Spot();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME );
        sb.append(" WHERE " + TableManager.SpotTable.column_index_id + " = " + index_id);

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null  && c.getCount() > 0){
            while (c.moveToNext()){

                spot.set_id(c.getInt(0));                          //id
                spot.setRoute_id(c.getInt(1));                   //route_id
                spot.setIndex_id(c.getInt(2));                   //index_id
                spot.setPicture_path(c.getString(3));           //picture
                spot.setMission(c.getString(4));                 //mission
                spot.setSearch_id(c.getInt(5));                  //search
                spot.setCategory_id(c.getInt(6));                //category (Eat,buy..)
            }
            c.close();
        }

        return spot;
    }

    private HashMap<Integer, SpotWithCoordinate> _getSpotWithCoordinateListOnRouteID(SQLiteDatabase db) {
        HashMap<Integer, SpotWithCoordinate> spotList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * ");
        sb.append(" FROM " + TABLE_NAME);
        sb.append(" JOIN " + TableManager.SearchTable.name);
        sb.append(" ON " + TableManager.SpotTable.name + "." +TableManager.SpotTable.column_search_id + " = " + TableManager.SearchTable.name + "." + TableManager.SearchTable.column_id );


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

        return spotList;
    }



    private HashMap<Integer, SpotWithCoordinate> _getSpotWithCoordinateListOnRouteID(SQLiteDatabase db, Integer route_id) {
        HashMap<Integer, SpotWithCoordinate> spotList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * ");
        sb.append(" FROM " + TABLE_NAME);
        sb.append(" JOIN " + TableManager.SearchTable.name);
        sb.append(" ON " + TableManager.SpotTable.name + "." +TableManager.SpotTable.column_search_id + " = " + TableManager.SearchTable.name + "." + TableManager.SearchTable.column_id );
        sb.append(" WHERE " + TableManager.SpotTable.column_route_id + " = " + route_id);

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

        return spotList;
    }



    private boolean _deleteSpot(SQLiteDatabase db, Integer id){


        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.SpotTable.column_id + " = " + id);

        try {
            db.execSQL(sb.toString());
        }
        catch (Exception ex){
            Log.e("delete place", ex.getMessage());
            return false;
        }

        return true;
    }

    public boolean _deleteSpotWithRouteID(SQLiteDatabase db, Integer id) {
        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.SpotTable.column_route_id + " = " + id);

        try {
            db.execSQL(sb.toString());
        }
        catch (Exception ex){
            Log.e("delete place", ex.getMessage());
            return false;
        }

        return true;
    }


    private long _insertSpot(SQLiteDatabase db, Spot spot){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_route_id, spot.getRoute_id());
        values.put(TableManager.SpotTable.column_index_id, spot.getIndex_id());
        values.put(TableManager.SpotTable.column_picture_path, spot.getPicture_path());
        values.put(TableManager.SpotTable.column_mission, spot.getMission());
        values.put(TableManager.SpotTable.column_search_id, spot.getSearch_id());
        values.put(TableManager.SpotTable.column_category_id, spot.getCategory_id());

        long rowId = db.insert(TABLE_NAME, null, values);

        return rowId;
    }

    private long _insertSpot(SQLiteDatabase db, Spot spot, Integer index){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_route_id, spot.getRoute_id());
        values.put(TableManager.SpotTable.column_index_id, index);
        values.put(TableManager.SpotTable.column_picture_path, spot.getPicture_path());
        values.put(TableManager.SpotTable.column_mission, spot.getMission());
        values.put(TableManager.SpotTable.column_search_id, spot.getSearch_id());
        values.put(TableManager.SpotTable.column_category_id, spot.getCategory_id());

        long rowId = db.insert(TABLE_NAME, null, values);

        return rowId;
    }

    private int _updateSpot(SQLiteDatabase db, Spot spot){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_route_id, spot.getRoute_id());
        values.put(TableManager.SpotTable.column_index_id, spot.getIndex_id());
        values.put(TableManager.SpotTable.column_picture_path, spot.getPicture_path());
        values.put(TableManager.SpotTable.column_mission, spot.getMission());
        values.put(TableManager.SpotTable.column_search_id, spot.getSearch_id());
        values.put(TableManager.SpotTable.column_category_id, spot.getCategory_id());


        String selection = TableManager.SpotTable.column_id + " = " + spot.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);

        return count;
    }

    private int _updateSpot(SQLiteDatabase db, Spot spot, int index){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_index_id, index);

        String selection = TableManager.SpotTable.column_id + " = " + spot.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);

        return count;
    }




    private int _updateSpotIndex(SQLiteDatabase db, Integer id, Integer after_index){

        ContentValues values = new ContentValues();
        values.put(TableManager.SpotTable.column_index_id, after_index);

        String selection = TableManager.SpotTable.column_id + " = " + id;
        int count = db.update(TABLE_NAME, values, selection, null);

        return count;
    }


}

