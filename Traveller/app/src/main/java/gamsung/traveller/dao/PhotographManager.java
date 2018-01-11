package gamsung.traveller.dao;

import java.util.HashMap;

import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.Route;

/**
 * Created by shin on 2018. 1. 11..
 */

public class PhotographManager {


    private final String TABLE_NAME = TableManager.PictureTable.name;
    private HashMap<Integer, Photograph> m_photoList;

    public PhotographManager(){

    }
}
