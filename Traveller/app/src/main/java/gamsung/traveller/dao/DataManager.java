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
    private SQLiteDatabase m_transactionDB;
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
        m_transactionDB= m_sqlHelper.beginTrans();
        m_isTransaction = true;
    }

    public void commit(){
        if(m_transactionDB != null) {
            m_sqlHelper.commit(m_transactionDB);
            m_transactionDB.close();
            m_transactionDB = null;
        }
        m_isTransaction = false;
    }

    public void rollback(){
        if(m_transactionDB != null) {
            m_sqlHelper.rollback(m_transactionDB);
            m_transactionDB.close();
            m_transactionDB = null;
        }
        m_isTransaction = false;
    }

    //route data interface
    public HashMap<Integer, Route> getRouteList(){
        return m_routeManager.getRouteList(m_sqlHelper);
    }

    public Route getRouteWithID(int id){
        return m_routeManager.getRouteWithID(m_sqlHelper, id);
    }

    public HashMap<Integer, Route> getRouteHasToday(){
        return m_routeManager.getRouteHasToday(m_sqlHelper);
    }

    //검색 글자 포함한 route반환
    public HashMap<Integer, Route> getRouteWithSearch(String search_word){
        return m_routeManager.getRouteWithSearch(m_sqlHelper, search_word);
    }

    public boolean deleteRoute(Integer id){
//        m_spotManager.deleteSpotWithRouteID(m_sqlHelper, id);
       return m_routeManager.deleteRoute(m_sqlHelper, id);
    }



    public long insertRoute(Route route){
        return m_routeManager.insertRoute(m_sqlHelper, route);
    }

    public int updateRoute(Route route){
        return m_routeManager.updateRoute(m_sqlHelper, route);
    }


    //place data interface

    public HashMap<Integer, Spot> getSpotList(){
        return m_spotManager.getSpotList(m_sqlHelper);
    }


    public HashMap<Integer, Spot> getSpotListWithRouteId(int routeId){
        return m_spotManager.getSpotListWithRouteId(m_sqlHelper, routeId);
    }

    public Spot getLastIndexSpot(){
        return m_spotManager.getLastIndexSpot(m_sqlHelper);
    }

    public Spot getSpotIDWithIndexID(int index_id){
        return m_spotManager.getSpotIDWithIndexID(m_sqlHelper, index_id);
    }

    public HashMap<Integer, SpotWithCoordinate> getSpotWithCoordinateListOnRouteID(int routeId){
        return m_spotManager.getSpotWithCoordinateListOnRouteID(m_sqlHelper, routeId);
    }

    public boolean deleteSpot(Integer id){
       return m_spotManager.deleteSpot(m_sqlHelper, id);
    }
    

    public long insertSpot(Spot spot){

        Spot lastspot = getLastIndexSpot();
        if(lastspot == null){
            index = 1;
        }else{
            index = lastspot.get_id()+1;
        }

       return  m_spotManager.insertSpot(m_sqlHelper, spot, index);
    }

    public int updateSpot(Spot spot){
        return m_spotManager.updateSpot(m_sqlHelper, spot);
    }

    public int updateSpotIndex(Integer id, Integer after_index){
        return m_spotManager.updateSpotIndex(m_sqlHelper, id, after_index);
    }

    public int updateSpotList(ArrayList<Spot> spotList){

        int count = 0;
        for (int i=1; i <= spotList.size(); i++){

            count += m_spotManager.updateSpot(m_sqlHelper, spotList.get(i), i);
        }

        return count;
    }



    //photograph data interface
    public HashMap<Integer, Photograph> getPhotoList(){
        return m_photoManager.getPhotoList(m_sqlHelper);
    }

    public boolean deletePhoto(Integer id){
        return m_photoManager.deletePhoto(m_sqlHelper, id);
    }

    public long insertPhoto(Photograph photo){
        return m_photoManager.insertPhoto(m_sqlHelper, photo);
    }

    public int updatePhoto(Photograph photo){
       return m_photoManager.updatePhoto(m_sqlHelper, photo);
    }

    public HashMap<Integer, Photograph> getPhotoListWithSpot(int spot_id){
        return m_photoManager.getPhotoListWithSpot(m_sqlHelper, spot_id);
    }

    public HashMap<Integer, Photograph> getPhotoListWithRoute(int route_id){
        return m_photoManager.getPhotoListWithRoute(m_sqlHelper, route_id);
    }


    //search place data interface

    public HashMap<Integer, SearchPlace> getSearchPlaceList(){
        return m_searchManager.getSearchPlaceList(m_sqlHelper);
    }

    public boolean deleteSearchPlace(Integer id){
       return m_searchManager.deleteSearchPlace(m_sqlHelper, id);
    }

    public long insertSearchPlace(SearchPlace searchPlace){
        return m_searchManager.insertSearchPlace(m_sqlHelper, searchPlace);
    }

    public int updatePhoto(SearchPlace searchPlace){
       return m_searchManager.updateSearchPlace(m_sqlHelper, searchPlace);
    }
}
