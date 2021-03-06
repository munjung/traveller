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
    private static final int VERSION = 8;


    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys= 1;");
        }

        db.execSQL(TableManager.RouteTable.getCreateQuery());
        db.execSQL(TableManager.SpotTable.getCreateQuery());
        db.execSQL(TableManager.SearchTable.getCreateQuery());
        db.execSQL(TableManager.PictureTable.getCreateQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TableManager.RouteTable.name);
        db.execSQL("DROP TABLE IF EXISTS " + TableManager.SpotTable.name);
        db.execSQL("DROP TABLE IF EXISTS " + TableManager.SearchTable.name);
        db.execSQL("DROP TABLE IF EXISTS " + TableManager.PictureTable.name);

        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = 1;");
    }

    public SQLiteDatabase beginTrans(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        return db;
    }

    public void commit(SQLiteDatabase db){

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void rollback(SQLiteDatabase db){
        db.endTransaction();
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
