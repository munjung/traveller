package gamsung.traveller.dao;

import java.util.HashMap;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Place;

/**
 * Created by shin on 2018. 1. 11..
 */

public class PlaceManager {

    private final String TABLE_NAME = TableManager.PlaceTable.name;
    private HashMap<Integer, Place> m_routeList;

    public PlaceManager(){

    }
}
