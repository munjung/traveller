package gamsung.traveller.dao;

import android.content.Context;

import java.util.List;

import gamsung.traveller.model.Route;

/**
 * Created by shin on 2018. 1. 8..
 */

public class DataManager {

    private static SQLiteHelper m_sqlHelper;
    private static DataManager m_instance;

    public static DataManager getInstance(Context context){

        if(m_instance == null)
            m_instance = new DataManager(context);

        return  m_instance;
    }

    private DataManager(Context context){

        m_sqlHelper = new SQLiteHelper(context);
    }


    /*
     * Data
     */




    /*
     * Route
     */
    public List<Route> getRouteList(){

        return RouteManager.getRouteList(m_sqlHelper);
    }

    public void deleteRoute(String name){

        RouteManager.deleteRoute(m_sqlHelper, name);
    }

    public void insertRoute(Route route){

        int id = (int)RouteManager.insertRoute(m_sqlHelper, route);
        if(id > 0)
            route.set_id(id);
    }

    public void updateRoute(Route route){

        RouteManager.updateRoute(m_sqlHelper, route);
    }



    /*
     * Place
     */


    /*
     * Photograph
     */



    /*
     * SearchPlace
     */
}
