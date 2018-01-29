package gamsung.traveller.model;

/**
 * Created by shin on 2018. 1. 8..
 *
 * Define. 장소 객체
 *       . 연결된 다음 장소는 next_photo_id 를 참조한다.
 */

public class Place {

    private int _id;
    private int m_route_id;
    private int m_next_place_id;
    private String m_mission;
    private String m_description;
    private int m_picture_id;
    private int m_search_id;

    public int get_id() {
        return _id;
    }

    public int getM_route_id() {
        return m_route_id;
    }

    public void setM_route_id(int m_route_id) {
        this.m_route_id = m_route_id;
    }

    public int getM_next_place_id() {
        return m_next_place_id;
    }

    public void setM_next_place_id(int m_next_place_id) {
        this.m_next_place_id = m_next_place_id;
    }

    public String getM_mission() {
        return m_mission;
    }

    public void setM_mission(String m_mission) {
        this.m_mission = m_mission;
    }

    public String getM_description() {
        return m_description;
    }

    public void setM_description(String m_description) {
        this.m_description = m_description;
    }

    public int getM_picture_id() {
        return m_picture_id;
    }

    public void setM_picture_id(int m_picture_id) {
        this.m_picture_id = m_picture_id;
    }

    public int getM_search_id() {
        return m_search_id;
    }

    public void setM_search_id(int m_search_id) {
        this.m_search_id = m_search_id;
    }
}
