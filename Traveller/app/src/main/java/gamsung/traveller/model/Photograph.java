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
    private int m_route_id;
    private int m_place_id;
    private int m_search_id;
    private String m_tag;
    private Date m_date;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getM_route_id() {
        return m_route_id;
    }

    public void setM_route_id(int m_route_id) {
        this.m_route_id = m_route_id;
    }

    public int getM_place_id() {
        return m_place_id;
    }

    public void setM_place_id(int m_place_id) {
        this.m_place_id = m_place_id;
    }

    public int getM_search_id() {
        return m_search_id;
    }

    public void setM_search_id(int m_search_id) {
        this.m_search_id = m_search_id;
    }

    public String getM_tag() {
        return m_tag;
    }

    public void setM_tag(String m_tag) {
        this.m_tag = m_tag;
    }

    public Date getM_date() {
        return m_date;
    }

    public void setM_date(Date m_date) {
        this.m_date = m_date;
    }
}
