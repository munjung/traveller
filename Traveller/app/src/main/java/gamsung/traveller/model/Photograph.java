package gamsung.traveller.model;

import java.util.Date;

/**
 * Created by shin on 2018. 1. 8..
 *
 * Define. 사진 객체
 *       . 저장된 사진에 Tag 정보를 객체의 ID 와 동일하게 관리한다.
 */

public class Photograph {

    private int _id;
    private int route_id;
    private int spot_id;
    private int search_id;
    private String path;
    private String memo;
    private Date date;

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

    public int getSpot_id() {
        return spot_id;
    }

    public void setSpot_id(int spot_id) {
        this.spot_id = spot_id;
    }

    public int getSearch_id() {
        return search_id;
    }

    public void setSearch_id(int search_id) {
        this.search_id = search_id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
