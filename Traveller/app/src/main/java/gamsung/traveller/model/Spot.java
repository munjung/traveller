package gamsung.traveller.model;

import java.util.List;

/**
 * Created by shin on 2018. 1. 8..
 *
 * Define. 장소 객체
 *       . 연결된 다음 장소는 next_photo_id 를 참조한다.
 */

public class Spot {


    private int _id;
    private int route_id;
    private int next_spot_id;
    private int picture_id;
    private String picture_path;
    private String mission;
    private int search_id;
    private int section_id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getRoute_id() {
        return route_id;
    }

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public int getNext_spot_id() {
        return next_spot_id;
    }

    public void setNext_spot_id(int next_spot_id) {
        this.next_spot_id = next_spot_id;
    }

    public int getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(int picture_id) {
        this.picture_id = picture_id;
    }

    public String getPicture_path() {
        return picture_path;
    }

    public void setPicture_path(String picture_path) {
        this.picture_path = picture_path;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public int getSearch_id() {
        return search_id;
    }

    public void setSearch_id(int search_id) {
        this.search_id = search_id;
    }

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int section_id) {
        this.section_id = section_id;
    }
}
