package gamsung.traveller.model;

import java.util.Date;

/**
 * Created by shin on 2018. 1. 8..
 *
 * Define. 경로 객체 (연결된 Spot 객체들의 부모 객체)
 */

public class Route {

    private int _id;
    private String title;
    private Date fromDate;
    private Date toDate;
    private String picturePath;

    public void set_id(Integer _id) {
        this._id = _id;
    }

    public int get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturPath(String picturePath) {
        this.picturePath = picturePath;
    }
}
