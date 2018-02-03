package gamsung.traveller.model;

import java.util.Date;

/**
 * Created by shin on 2018. 1. 8..
 *
 * Define. 구글에 검색된 정보를 저장한 객체
 *       . 검색으로 저장된 정보의 좌표 및 지역명 정보를 place와 연결한다.
 */

public class SearchPlace {
    private int _id;
    private int place_unique_id;
    private String place_name;
    private String place_address;
    private String place_attribution;
    private String place_phone;
    private String place_locale;
    private String place_uri;
    private double lat;
    private double lon;
    private double southwest_lat;
    private double southwest_lon;
    private double northeast_lat;
    private double northeast_lon;


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getPlace_unique_id() {
        return place_unique_id;
    }

    public void setPlace_unique_id(int place_unique_id) {
        this.place_unique_id = place_unique_id;
    }

    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getPlace_attribution() {
        return place_attribution;
    }

    public void setPlace_attribution(String place_attribution) {
        this.place_attribution = place_attribution;
    }

    public String getPlace_phone() {
        return place_phone;
    }

    public void setPlace_phone(String place_phone) {
        this.place_phone = place_phone;
    }

    public String getPlace_locale() {
        return place_locale;
    }

    public void setPlace_locale(String place_locale) {
        this.place_locale = place_locale;
    }

    public String getPlace_uri() {
        return place_uri;
    }

    public void setPlace_uri(String place_uri) {
        this.place_uri = place_uri;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getSouthwest_lat() {
        return southwest_lat;
    }

    public void setSouthwest_lat(double southwest_lat) {
        this.southwest_lat = southwest_lat;
    }

    public double getSouthwest_lon() {
        return southwest_lon;
    }

    public void setSouthwest_lon(double southwest_lon) {
        this.southwest_lon = southwest_lon;
    }

    public double getNortheast_lat() {
        return northeast_lat;
    }

    public void setNortheast_lat(double northeast_lat) {
        this.northeast_lat = northeast_lat;
    }

    public double getNortheast_lon() {
        return northeast_lon;
    }

    public void setNortheast_lon(double northeast_lon) {
        this.northeast_lon = northeast_lon;
    }

}
