package gamsung.traveller.dto;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shin on 2018. 1. 8..
 *
 * Define. 데이터베이스 테이블 정의 클래스
 *       . 각 테이블의 명칭 및 컬럼명을 명시한다.
 */

public class TableManager {

    public static class RouteTable {

        public static final String name = "Route";

        public static final String column_id =          "route_id";
        public static final String column_title =       "title";
        public static final String column_from_date =   "from_date";
        public static final String column_to_date =     "to_date";
        public static final String column_picture_id =  "picture_id";   //대표 사진 id

        public static final String[] columns = {
                column_id,
                column_title,
                column_from_date,
                column_to_date,
                column_picture_id
        };

        public static String getCreateQuery(){

            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE " + name + " ( ");
            sb.append(column_id         + " INTEGER PRIMARY KEY AUTOINCREMENT ");
            sb.append(column_title      + " TEXT ");
            sb.append(column_from_date  + " DATETIME ");
            sb.append(column_to_date    + " DATETIME ");
            sb.append(column_picture_id + " INTEGER ");
            sb.append(")");

            return sb.toString();
        }
    }

    public static class PlaceTable {

        public static final String name = "Place";

        public static final String column_id =              "place_id";
        public static final String column_route_id =        "route_id";    //route table id
        public static final String column_next_place_id =   "next_place_id";
        public static final String column_picture_id =      "picture_id";   //대표 사진 id
        public static final String column_search_id =       "search_id";    //search table id
        public static final String column_mission =         "mission";
        public static final String column_description =     "descripiton";
        public static final String column_picture_list =    "picture_list"; //연결된 사진 리스트

        public static final String[] columns = {
                column_id,
                column_next_place_id,
                column_route_id,
                column_picture_id,
                column_picture_list,
                column_search_id,
                column_mission,
                column_description
        };

        public static String getCreateQuery(){

            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE " + name + " ( ");
            sb.append(column_id             + " INTEGER PRIMARY KEY AUTOINCREMENT ");
            sb.append(column_route_id       + " INTEGER ");
            sb.append(column_next_place_id  + " INTEGER ");
            sb.append(column_picture_id     + " INTEGER ");
            sb.append(column_picture_list   + " TEXT ");
            sb.append(column_search_id      + " INTEGER ");
            sb.append(column_mission        + " TEXT ");
            sb.append(column_description    + " TEXT ");
            sb.append(")");

            return sb.toString();
        }
    }

    public static class SearchTable {

        public static final String name = "Search";

        public static final String column_id =                  "search_id";
        public static final String column_place_unique_id =     "unique_id";
        public static final String column_place_name =          "name";
        public static final String column_place_address =       "address";
        public static final String column_place_attribution =   "attribution";
        public static final String column_place_phone =         "phone";
        public static final String column_place_locale =        "locale";
        public static final String column_place_uri =           "uri";
        public static final String column_lat =                 "lat";
        public static final String column_lon =                 "lon";
        public static final String column_southwest_lat =       "southwest_lat";
        public static final String column_southwest_lon =       "southwest_lon";
        public static final String column_northeast_lat =       "northeast_lat";
        public static final String column_northeast_lon =       "northeast_lon";

        public static final String[] columns = {
                column_id,
                column_place_unique_id,
                column_place_name,
                column_place_address,
                column_place_attribution,
                column_place_phone,
                column_place_locale,
                column_place_uri,
                column_lat,
                column_lon,
                column_southwest_lat,
                column_southwest_lon,
                column_northeast_lat,
                column_northeast_lon
        };

        public static String getCreateQuery(){

            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE " + name + " ( ");
            sb.append(column_id                     + " INTEGER PRIMARY KEY AUTOINCREMENT ");
            sb.append(column_place_unique_id        + " INTEGER ");
            sb.append(column_place_name             + " TEXT ");
            sb.append(column_place_address          + " TEXT ");
            sb.append(column_place_attribution      + " TEXT ");
            sb.append(column_place_phone            + " TEXT ");
            sb.append(column_place_locale           + " TEXT ");
            sb.append(column_place_uri              + " TEXT ");
            sb.append(column_lat                    + " REAL ");
            sb.append(column_lon                    + " REAL ");
            sb.append(column_southwest_lat          + " REAL ");
            sb.append(column_southwest_lon          + " REAL ");
            sb.append(column_northeast_lat          + " REAL ");
            sb.append(column_northeast_lon          + " REAL ");
            sb.append(")");

            return sb.toString();
        }
    }

    public static class PictureTable {

        public static  final String name = "Picture";

        public static final String column_id = "picture_id";
        public static final String column_route_id = "route_id";
        public static final String column_place_id = "place_id";
        public static final String column_search_id = "search_id";
        public static final String column_text = "text";
        public static final String column_date = "date";
        public static  final String[] columns = {
                column_id,
                column_route_id,
                column_place_id,
                column_search_id,
                column_text,
                column_date
        };


        public static String getCreateQuery(){

            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE " + name + " ( ");
            sb.append(column_id             + " INTEGER PRIMARY KEY AUTOINCREMENT ");
            sb.append(column_route_id       + " INTEGER ");
            sb.append(column_place_id       + " INTEGER ");
            sb.append(column_search_id      + " INTEGER ");
            sb.append(column_text           + " TEXT ");
            sb.append(column_date           + " DATETIME ");
            sb.append(")");

            return sb.toString();
        }
    }
}
