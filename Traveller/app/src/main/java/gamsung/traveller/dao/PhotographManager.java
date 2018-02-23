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
    private HashMap<Integer, Photograph> m_photoList;

    public PhotographManager(){
        m_photoList = new HashMap<>();
    }

    public HashMap<Integer, Photograph> getPhotoList(SQLiteHelper dbHelper){

        m_photoList.clear();
        m_photoList.putAll(_getphotoList(dbHelper));

        return m_photoList;
    }

    public boolean deletePhoto(SQLiteHelper dbHelper, Integer id){

        if(!_deletePhoto(dbHelper, id))
            return false;

        if (m_photoList.containsKey(id)) {
            m_photoList.remove(id);
        }

        return true;
    }

    public long insertPhoto(SQLiteHelper dbHelper, Photograph photo){

        long rowId = _insertPhoto(dbHelper, photo);
        if(rowId > 0)
            photo.set_id((int)rowId);

        return rowId;
    }

    public int updatePhoto(SQLiteHelper dbHelper, Photograph photo){

        int count = _updatePhoto(dbHelper, photo);
        if(count > 0)
            m_photoList.put(photo.get_id(), photo);

        return count;
    }



//    public List<String> getPhotoListWithSpot(int spot_id){
//        return new ArrayList<>();
//    }
//
//    public List<String> getPhotoListWithRoute(int route_id){
//        return new ArrayList<>();
//    }

    public HashMap<Integer, Photograph> getPhotoListWithSpot(SQLiteHelper dbHelper, Integer spot_id){

        m_photoList.clear();
        m_photoList.putAll(_getPhotoListWithSpot(dbHelper, spot_id));

        return m_photoList;
    }

    public HashMap<String, Photograph> getPhotoListToStringWithSpot(SQLiteHelper dbHelper, Integer spot_id){

        return _getPhotoListToStringWithSpot(dbHelper, spot_id);
    }

    public HashMap<Integer, Photograph> getPhotoListWithRoute(SQLiteHelper dbHelper, Integer route_id){

        m_photoList.clear();
        m_photoList.putAll(_getPhotoListWithRoute(dbHelper, route_id));

        return m_photoList;
    }


    private HashMap<Integer, Photograph> _getphotoList(SQLiteHelper dbHelper){

        HashMap<Integer, Photograph> photoList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
        db.close();

        return photoList;
    }

    private boolean _deletePhoto(SQLiteHelper dbHelper, Integer id){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.PictureTable.column_id + " = " + id);

        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(sb.toString());
            db.close();

        }
        catch (Exception ex){
            Log.e("delete photo", ex.getMessage());
            return false;
        }
        return true;
    }

    private long _insertPhoto(SQLiteHelper dbHelper, Photograph photo){

        ContentValues values = new ContentValues();
        values.put(TableManager.PictureTable.column_route_id, photo.getRoute_id());
        values.put(TableManager.PictureTable.column_spot_id, photo.getSpot_id());
        values.put(TableManager.PictureTable.column_search_id, photo.getSearch_id());
        values.put(TableManager.PictureTable.column_path, photo.getPath());
        if(photo.getDate() != null) values.put(TableManager.PictureTable.column_date, Converter.convertSqlDateFormat(photo.getDate()));
        values.put(TableManager.PictureTable.column_memo, photo.getMemo());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(TABLE_NAME, null, values);
        db.close();

        return rowId;
    }

    private int _updatePhoto(SQLiteHelper dbHelper, Photograph photo){

        ContentValues values = new ContentValues();
        values.put(TableManager.PictureTable.column_route_id, photo.getRoute_id());
        values.put(TableManager.PictureTable.column_spot_id, photo.getSpot_id());
        values.put(TableManager.PictureTable.column_search_id, photo.getSearch_id());
        values.put(TableManager.PictureTable.column_path, photo.getPath());
        if(photo.getDate() != null) values.put(TableManager.PictureTable.column_date, Converter.convertSqlDateFormat(photo.getDate()));
        values.put(TableManager.PictureTable.column_memo, photo.getMemo());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.PictureTable.column_id + " = " + photo.get_id();
        int count = db.update(TABLE_NAME, values, selection, null);
        db.close();

        return count;
    }

    public HashMap<Integer, Photograph> _getPhotoListWithSpot(SQLiteHelper dbHelper, Integer spot_id){

        HashMap<Integer, Photograph> photoList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.PictureTable.column_spot_id + " = " + spot_id);  //이렇게 사용하는게 좋아요
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
        db.close();

        return photoList;
    }

    public HashMap<String, Photograph> _getPhotoListToStringWithSpot(SQLiteHelper dbHelper, Integer spot_id){

        HashMap<String, Photograph> photoList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.PictureTable.column_spot_id + " = " + spot_id);  //이렇게 사용하는게 좋아요
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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

                photoList.put(photo.getPath(), photo);

            }
            c.close();
        }
        db.close();

        return photoList;
    }

    public HashMap<Integer, Photograph> _getPhotoListWithRoute(SQLiteHelper dbHelper, Integer route_id){
        //List<Photograph> photoList = new ArrayList<Photograph>();

        HashMap<Integer, Photograph> photoList = new HashMap<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + TABLE_NAME);
        sb.append(" WHERE " + TableManager.PictureTable.column_route_id + " = " + route_id);    //이렇게 사용하는게 좋아요
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
        db.close();

        return photoList;
    }
}



