package gamsung.traveller.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.Spot;
import gamsung.traveller.model.Route;
import gamsung.traveller.model.SearchPlace;
import gamsung.traveller.model.SpotWithCoordinate;

/**
 * Created by shin on 2018. 1. 8..
 *
 * Define. 모든 화면에서 필요한 데이터 인터페이스
 *       . 내부적으로 데이터베이스 정보와 메모리 데이터를 동기화한다.
 *       . 내부적으로 메모리 데이터를 HashMap으로 들고 있으므로 Select All Query가 발생하는 함수는 최초 1회만 수행하는 것을 권장한다.
 *       . DataManager는 Singleton Pattern이므로 getInstance()를 통해 객체를 호출할 수 있다.
 *
 *       . 고려사항 > 매번 DataManager를 new 로 각 activity에서 개별적으로 사용하도록 해야하는건가?.. SQLiteOpenHelper가 각 activity 마다 생겨도 되는지 모르겠으므로 미정
 *       .        > 데이터 사용 방식에 따라 내부 메모리를 들고 있을 필요가 없을 수 있다. 일단은 생성
 */

public class DataManager {

    private static DataManager m_instance;
    private int index;

    public static DataManager getInstance(Context context){

        if(m_instance == null)
            m_instance = new DataManager(context);

        return  m_instance;
    }

    //db interface
    private SQLiteHelper m_sqlHelper;
    private SQLiteDatabase m_db;
    private boolean m_isTransaction = false;

    //db data control interface
    private RouteManager m_routeManager;
    private SpotManager m_spotManager;
    private PhotographManager m_photoManager;
    private SearchPlaceManager m_searchManager;

    private DataManager(Context context){

        m_sqlHelper = new SQLiteHelper(context);

        m_routeManager = new RouteManager();
        m_spotManager = new SpotManager();
        m_photoManager = new PhotographManager();
        m_searchManager = new SearchPlaceManager();
    }


    public boolean getTransactionState(){
        return m_isTransaction;
    }

    public void beginTrans(){
        m_db= m_sqlHelper.beginTrans();
        m_isTransaction = true;
    }

    public void commit(){
        if(m_db != null) {
            m_sqlHelper.commit(m_db);
            m_db.close();
            m_db = null;
        }
        m_isTransaction = false;
    }

    public void rollback(){
        if(m_db != null) {
            m_sqlHelper.rollback(m_db);
            m_db.close();
            m_db = null;
        }
        m_isTransaction = false;
    }

    //route data interface
    public HashMap<Integer, Route> getRouteList(){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, Route> routeHashMap = m_routeManager.getRouteList(m_db);
        m_db.close();
        m_db = null;

        return routeHashMap;
    }

    public Route getRouteWithID(int id){

        m_db = m_sqlHelper.getReadableDatabase();
        Route route = m_routeManager.getRouteWithID(m_db, id);
        m_db.close();
        m_db = null;

        return route;
    }

    public HashMap<Integer, Route> getRouteHasToday(){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, Route> routeHashMap = m_routeManager.getRouteHasToday(m_db);
        m_db.close();
        m_db=null;

        return routeHashMap;
    }

    //검색 글자 포함한 route반환
    public HashMap<Integer, Route> getRouteWithSearch(String search_word){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, Route> routeHashMap = m_routeManager.getRouteWithSearch(m_db, search_word);
        m_db.close();
        m_db=null;

        return routeHashMap;
    }

    public boolean deleteRoute(Integer id){

        if(m_isTransaction){
            return m_routeManager.deleteRoute(m_db, id);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        boolean result = m_routeManager.deleteRoute(m_db, id);
        m_db.close();
        m_db=null;

       return result;
    }



    public long insertRoute(Route route){

        if(m_isTransaction){
            return m_routeManager.insertRoute(m_db, route);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        long id = m_routeManager.insertRoute(m_db, route);
        m_db.close();
        m_db=null;

        return id;
    }

    public int updateRoute(Route route){

        if(m_isTransaction){
            return m_routeManager.updateRoute(m_db, route);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        int count = m_routeManager.updateRoute(m_db, route);
        m_db.close();
        m_db = null;

        return count;
    }


    //place data interface

    public HashMap<Integer, Spot> getSpotList(){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, Spot> spotHashMap = m_spotManager.getSpotList(m_db);
        m_db.close();
        m_db=null;

        return spotHashMap;
    }


    public HashMap<Integer, Spot> getSpotListWithRouteId(int routeId){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, Spot> spotHashMap = m_spotManager.getSpotListWithRouteId(m_db, routeId);
        m_db.close();
        m_db=null;

        return spotHashMap;
    }


    public Spot getSpotIDWithIndexID(int index_id){

        m_db = m_sqlHelper.getReadableDatabase();
        Spot spot = m_spotManager.getSpotIDWithIndexID(m_db, index_id);
        m_db.close();
        m_db=null;

        return spot;
    }


    public boolean deleteSpot(Integer id){

        if(m_isTransaction){
            return m_spotManager.deleteSpot(m_db, id);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        boolean result = m_spotManager.deleteSpot(m_db, id);
        m_db.close();
        m_db=null;

        return result;
    }

    public long insertSpot(Spot spot){

        if(m_isTransaction){
            return m_spotManager.insertSpot(m_db, spot);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        long id = m_spotManager.insertSpot(m_db, spot);
        m_db.close();
        m_db=null;

        return id;
    }

    public long insertSpot(Spot spot, int index){

        if(m_isTransaction){
            return m_spotManager.insertSpot(m_db, spot, index);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        long id = m_spotManager.insertSpot(m_db, spot, index);
        m_db.close();
        m_db=null;

        return id;
    }

    public int updateSpot(Spot spot){

        if(m_isTransaction){
            return m_spotManager.updateSpot(m_db, spot);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        int count = m_spotManager.updateSpot(m_db, spot);
        m_db.close();
        m_db =null;

        return count;
    }

    public int updateSpotList(ArrayList<Spot> spotList){

        int count = 0;
        if(m_isTransaction){

            for (int i=0; i < spotList.size(); i++){
                count += m_spotManager.updateSpot(m_db, spotList.get(i), i);
            }
            return count;
        }

        m_db = m_sqlHelper.getWritableDatabase();
        for (int i=0; i < spotList.size(); i++){
            count += m_spotManager.updateSpot(m_db, spotList.get(i), i);
        }
        m_db.close();
        m_db=null;

        return count;
    }


    public HashMap<Integer, SpotWithCoordinate> getSpotWithCoordinateList(){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, SpotWithCoordinate> spotWithCoordinateHashMap = m_spotManager.getSpotWithCoordinateList(m_db);
        m_db.close();
        m_db =null;

        return spotWithCoordinateHashMap;
    }

    public HashMap<Integer, SpotWithCoordinate> getSpotWithCoordinateListOnRouteID(int routeId){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, SpotWithCoordinate> spotWithCoordinateHashMap = m_spotManager.getSpotWithCoordinateListOnRouteID(m_db, routeId);
        m_db.close();
        m_db=null;

        return spotWithCoordinateHashMap;
    }

    //photograph data interface
    public HashMap<Integer, Photograph> getPhotoList(){
        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, Photograph> photographHashMap = m_photoManager.getPhotoList(m_db);
        m_db.close();
        m_db = null;

        return photographHashMap;
    }

    public boolean deletePhoto(Integer id){

        if(m_isTransaction){
            return m_photoManager.deletePhoto(m_db, id);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        boolean result = m_photoManager.deletePhoto(m_db, id);
        m_db.close();
        m_db = null;

        return result;
    }

    public long insertPhoto(Photograph photo){

        if(m_isTransaction){
            return m_photoManager.insertPhoto(m_db, photo);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        long id = m_photoManager.insertPhoto(m_db, photo);
        m_db.close();
        m_db = null;

        return id;
    }

    public int updatePhoto(Photograph photo){

        if(m_isTransaction){
            return m_photoManager.updatePhoto(m_db, photo);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        int count = m_photoManager.updatePhoto(m_db, photo);
        m_db.close();
        m_db =null;

        return count;
    }

    public HashMap<Integer, Photograph> getPhotoListWithSpot(int spot_id){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, Photograph> spotWithCoordinateHashMap = m_photoManager.getPhotoListWithSpot(m_db, spot_id);
        m_db.close();
        m_db=null;

        return spotWithCoordinateHashMap;
    }

    public HashMap<String, Photograph> getPhotoListToStringWithSpot(int spot_id){

        m_db = m_sqlHelper.getWritableDatabase();
        HashMap<String, Photograph> photographHashMap = m_photoManager.getPhotoListToStringWithSpotID(m_db, spot_id);
        m_db.close();
        m_db = null;

        return photographHashMap;
    }

    public HashMap<Integer, Photograph> getPhotoListWithRoute(int route_id){
        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, Photograph> spotWithCoordinateHashMap = m_photoManager.getPhotoListWithSpot(m_db, route_id);
        m_db.close();
        m_db=null;

        return spotWithCoordinateHashMap;
    }


    //search place data interface

    public HashMap<Integer, SearchPlace> getSearchPlaceList(){

        m_db = m_sqlHelper.getReadableDatabase();
        HashMap<Integer, SearchPlace> searchPlaceHashMap = m_searchManager.getSearchPlaceList(m_db);
        m_db.close();
        m_db = null;

        return searchPlaceHashMap;
    }

    public boolean deleteSearchPlace(Integer id){

        if(m_isTransaction){
            return m_searchManager.deleteSearchPlace(m_db, id);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        boolean result = m_searchManager.deleteSearchPlace(m_db, id);
        m_db.close();
        m_db=null;

        return result;
    }

    public long insertSearchPlace(SearchPlace searchPlace){

        if(m_isTransaction){
            return m_searchManager.insertSearchPlace(m_db, searchPlace);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        long id = m_searchManager.insertSearchPlace(m_db, searchPlace);
        m_db.close();
        m_db=null;

        return id;
    }

    public int updatePhoto(SearchPlace searchPlace){

        if(m_isTransaction){
            return m_searchManager.updateSearchPlace(m_db, searchPlace);
        }

        m_db = m_sqlHelper.getWritableDatabase();
        int count = m_searchManager.updateSearchPlace(m_db, searchPlace);
        m_db.close();
        m_db = null;

        return count;
    }
}
