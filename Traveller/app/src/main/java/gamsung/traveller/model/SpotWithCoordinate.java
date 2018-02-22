package gamsung.traveller.model;

/**
 * Created by RYOON on 2018-02-21.
 */

public class SpotWithCoordinate extends Spot {
    private double lat;
    private double lon;

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

}
