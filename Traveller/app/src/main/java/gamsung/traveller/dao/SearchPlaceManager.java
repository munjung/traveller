package gamsung.traveller.dao;

import java.util.HashMap;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.SearchPlace;

/**
 * Created by shin on 2018. 1. 11..
 */

public class SearchPlaceManager {

    private final String TABLE_NAME = TableManager.SearchTable.name;
    private HashMap<Integer, SearchPlace> m_searchList;

    public SearchPlaceManager(){

    }
}
