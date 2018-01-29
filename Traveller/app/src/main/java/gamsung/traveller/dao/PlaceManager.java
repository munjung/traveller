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

/**
 * Created by shin on 2018. 1. 11..
 */

public class PlaceManager {

    private final String TABLE_NAME = TableManager.PlaceTable.name;
    private HashMap<Integer, Place> m_placeMap;

    public PlaceManager(){

        m_placeMap = new HashMap<>();
    }

}
