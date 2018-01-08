package gamsung.traveller.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gamsung.traveller.dto.TableManager;

/**
 * Created by shin on 2018. 1. 8..
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Traveller";
    private static final int VERSION = 1;


    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(TableManager.TopicTable.createQuery);
        db.execSQL(TableManager.RouteTable.createQuery);
        db.execSQL(TableManager.PlaceTable.createQuery);
        db.execSQL(TableManager.SearchTable.createQuery);
        db.execSQL(TableManager.PictureTable.createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TableManager.TopicTable.name);
        db.execSQL("DROP TABLE IF EXISTS " + TableManager.RouteTable.name);
        db.execSQL("DROP TABLE IF EXISTS " + TableManager.PlaceTable.name);
        db.execSQL("DROP TABLE IF EXISTS " + TableManager.SearchTable.name);
        db.execSQL("DROP TABLE IF EXISTS " + TableManager.PictureTable.name);

        onCreate(db);
    }

//    public void excuteQuery(String query){
//
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(query);
//        db.close();
//    }
//
//    public Cursor excuteReader(String query){
//
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        return db.rawQuery(query, null);
//    }
}
