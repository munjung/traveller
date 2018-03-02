package gamsung.traveller.util;

/**
 * Created by Hanbin Ju on 2018-03-02.
 */

public class widgetItem{
    private int id;
    private String route;

    public void setId(int idnum){
        id = idnum;
    }

    public void setRoute(String rtext){
        route=rtext;
    }

    public int getId(){
        return id;
    }

    public String getRoute(){
        return route;
    }
}
