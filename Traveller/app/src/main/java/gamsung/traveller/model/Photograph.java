package gamsung.traveller.model;

/**
 * Created by shin on 2018. 1. 8..
 *
 * Define. 사진 객체
 *       . 저장된 사진에 Tag 정보를 객체의 ID 와 동일하게 관리한다.
 */

public class Photograph {

    private int _id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
