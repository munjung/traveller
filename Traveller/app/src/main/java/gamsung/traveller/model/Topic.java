package gamsung.traveller.model;

/**
 * Created by shin on 2018. 1. 8..
 */

public class Topic {

    private long _id;
    private String name;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
