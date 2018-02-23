package gamsung.traveller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.design.widget.TabLayout;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.Route;
import gamsung.traveller.util.Converter;

/**
 * Created by shin on 2018. 1. 11..
 */

public class PhotographManager {


    private final String TABLE_NAME = TableManager.PictureTable.name;

    public PhotographManager(){
    }

    public HashMap<Integer, Photograph> getPhotoList(SQLiteDatabase db){

        return _getphotoList(db);
    }

    public boolean deletePhoto(SQLiteDatabase db, Integer id){

        if(!_deletePhoto(db, id))
            return false;

        return true;
    }

    public long insertPhoto(SQLiteDatabase db, Photograph photo){

        long rowId = _insertPhoto(db, photo);
        if(rowId > 0)
            photo.set_id((int)rowId);

        return rowId;
    }

    public int updatePhoto(SQLiteDatabase db, Photograph photo){

        int count = _updatePhoto(db, photo);

        return count;
    }


    public HashMap<Integer, Photograph> getPhotoListWithSpot(SQLiteDatabase db, Integer spot_id){

        return _getPhotoListWithSpot(db, spot_id);
    }

    public HashMap<Integer, Photograph> getPhotoListWithRoute(SQLiteDatabase db, Integer route_id){

        return _getPhotoListWithRoute(db, route_id);
    }


    private HashMap<Integer, Photograph> _getphotoList(SQLiteDatabase db){

        HashMap<Integer, Photograph> photoList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){
                Photograph photo = new Photograph();
                photo.set_id(c.getInt(0));
                photo.setRoute_id(c.getInt(1));
                photo.setSpot_id(c.getInt(2));
                photo.setSearch_id(c.getInt(3));
                photo.setPath(c.getString(4));
                if(c.getString(5) != null) photo.setDate(Converter.convertStringToDate(c.getString(5)));
                photo.setMemo(c.getString(6));

                photoList.put(photo.get_id(), photo);
            }
            c.close();
        }

        return photoList;
    }

    private boolean _deletePhoto(SQLiteDatabase db, Integer id){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.PictureTable.column_id + " = " + id);

        try {
            db.execSQL(sb.toString());
        }
        catch (Exception ex){
            Log.e("delete photo", ex.getMessage());
            return false;
        }
        return true;
    }

    private long _insertPhoto(SQLiteDatabase db, Photograph photo){

        ContentValues values = new ContentValues();
        values.put(TableManager.PictureTable.column_route_id, photo.getRoute_id());
        values.put(TableManager.PictureTable.column_spot_id, photo.getSpot_id());
        values.put(TableManager.PictureTable.column_search_id, photo.getSearch_id());
        values.put(TableManager.PictureTable.column_path, photo.getPath());
        if(photo.getDate() != null) values.put(TableManager.PictureTable.column_date, Converter.convertSqlDateFormat(photo.getDate()));
        values.put(TableManager.PictureTable.column_memo, photo.getMemo());

        long rowId = db.insert(TABLE_NAME, null, values);

        return rowId;
    }

    private int _updatePhoto(SQLiteDatabase db, Photograph photo){

        ContentValues values = new ContentValues();
        values.put(TableManager.PictureTable.column_route_id, photo.getRoute_id());
        values.put(TableManager.PictureTable.column_spot_id, photo.getSpot_id());
        values.put(TableManager.PictureTable.column_search_id, photo.getSearch_id());
        values.put(TableManager.PictureTable.column_path, photo.getPath());
        if(photo.getDate() != null) values.put(TableManager.PictureTable.column_date, Converter.convertSqlDateFormat(photo.getDate()));
        values.put(TableManager.PictureTable.column_memo, photo.getMemo());

        String selection = TableManager.PictureTable.column_id + " = " + photo.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);

        return count;
    }

    public HashMap<Integer, Photograph> _getPhotoListWithSpot(SQLiteDatabase db, Integer spot_id){

        HashMap<Integer, Photograph> photoList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.PictureTable.column_spot_id + " = " + spot_id);  //이렇게 사용하는게 좋아요

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){
                Photograph photo = new Photograph();
                photo.set_id(c.getInt(0));
                photo.setRoute_id(c.getInt(1));
                photo.setSpot_id(c.getInt(2));
                photo.setSearch_id(c.getInt(3));
                photo.setPath(c.getString(4));
                if(c.getString(5) != null) photo.setDate(Converter.convertStringToDate(c.getString(5)));
                photo.setMemo(c.getString(6));

                photoList.put(photo.get_id(), photo);

            }
            c.close();
        }

        return photoList;
    }

    public HashMap<Integer, Photograph> _getPhotoListWithRoute(SQLiteDatabase db, Integer route_id){

        HashMap<Integer, Photograph> photoList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.PictureTable.column_route_id + " = " + route_id);    //이렇게 사용하는게 좋아요

        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){
                Photograph photo = new Photograph();
                photo.set_id(c.getInt(0));
                photo.setRoute_id(c.getInt(1));
                photo.setSpot_id(c.getInt(2));
                photo.setSearch_id(c.getInt(3));
                photo.setPath(c.getString(4));
                if(c.getString(5) != null) photo.setDate(Converter.convertStringToDate(c.getString(5)));
                photo.setMemo(c.getString(6));

                photoList.put(photo.get_id(), photo);
            }
            c.close();
        }

        return photoList;
    }
}



