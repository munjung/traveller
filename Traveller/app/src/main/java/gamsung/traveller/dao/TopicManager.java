package gamsung.traveller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Topic;

/**
 * Created by shin on 2018. 1. 8..
 */

public class TopicManager {

    private static String tableName = TableManager.TopicTable.name;
    private static String[] columns = TableManager.TopicTable.columns;

    public static List<Topic> getTopics(SQLiteHelper dbHelper){

        List<Topic> topicList = new ArrayList<>();

        StringBuffer sb = new StringBuffer();
        sb.append("SELECT * FROM " + tableName);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery(sb.toString(), null);
        if(c != null){
            while (c.moveToNext()){

                Topic topic = new Topic();
                topic.set_id(c.getLong(0));      //_id
                topic.setName(c.getString(1));  //Name

                topicList.add(topic);
            }
            c.close();
        }
        db.close();

        return topicList;
    }

    public static void deleteTopic(SQLiteHelper dbHelper, String name){

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM " + tableName);
        sb.append(" WHERE NAME = " + name);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(sb.toString());
        db.close();
    }

    public static long insertTopic(SQLiteHelper dbHelper, Topic topic){

        ContentValues values = new ContentValues();
        values.put(TableManager.TopicTable.column_name, topic.getName());   //Name

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(tableName, null, values);
        db.close();

        return rowId;
    }

    public static int updateTopic(SQLiteHelper dbHelper, Topic topic){

        ContentValues values = new ContentValues();
        values.put(TableManager.TopicTable.column_name, topic.getName());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = TableManager.TopicTable.column_id + "=" + topic.get_id();
        int count = db.update(tableName, values, selection, null);
        db.close();

        return count;
    }
}
