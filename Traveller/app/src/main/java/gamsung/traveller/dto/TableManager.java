package gamsung.traveller.dto;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shin on 2018. 1. 8..
 */

public class TableManager {


    public static class TopicTable {

        public static final String name = "Topic";

        public static final String column_id =      "topic_id";
        public static final String column_name =    "name";
        public static final String[] columns = {
                column_id,
                column_name
        };

        public static final String createQuery = "";
    }

    public static class RouteTable {

        public static final String name = "Route";

        public static final String column_id =          "route_id";
        public static final String column_topic_id =    "topic_id";    //topic table id
        public static final String column_name =        "name";
        public static final String[] columns = {
                column_id,
                column_topic_id,
                column_name
        };

        public static final String createQuery = "";
    }

    public static class PlaceTable {

        public static final String name = "Place";

        public static final String column_id =              "place_id";
        public static final String column_next_place_id =   "next_place_id";
        public static final String column_route_id =        "route_id";    //route table id
        public static final String column_date =            "date";
        public static final String column_mission =         "mission";
        public static final String column_description =     "descripiton";
        public static final String column_picture_id =      "picture_id";   //picture talbe id
        public static final String column_search_id =       "search_id";    //search table id
        public static final String[] columns = {
                column_id,
                column_next_place_id,
                column_route_id,
                column_date,
                column_mission,
                column_description,
                column_picture_id,
                column_search_id
        };

        public static final String createQuery = "";
    }

    public static class SearchTable {

        public static final String name = "Search";

        public static final String column_id = "search_id";
        public static final String column_place_id = "place_id";
//        public static final String column_google_arg = "google";
        public static final String[] columns = { "" };

        public static final String createQuery = "";
    }

    public static class PictureTable {

        public static  final String name = "Picture";

        public static final String column_id = "picture_id";
        public static final String column_topic_id = "topic_id";
        public static final String column_route_id = "route_id";
        public static final String column_place_id = "place_id";
        public static final String column_text = "text";
        public static final String column_date = "date";
        public static final String column_search_id = "search_id";
        public static  final String[] columns = {
                column_id,
                column_topic_id,
                column_route_id,
                column_place_id,
                column_search_id,
                column_text,
                column_date
        };

        public static  final String createQuery = "";
    }
}
