package gamsung.traveller.dto;

import com.google.android.gms.maps.model.LatLng;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import gamsung.traveller.dao.RouteManager;

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
        public static final String column_picture_path ="picture_path";   //대표 사진 id

        public static final String[] columns = {
                column_id,
                column_title,
                column_from_date,
                column_to_date,
                column_picture_path
        };

        public static String getCreateQuery(){

            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE " + name + " ( ");
            sb.append(column_id             + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sb.append(column_title          + " TEXT NOT NULL, ");
            sb.append(column_from_date      + " TEXT, ");
            sb.append(column_to_date        + " TEXT, ");
            sb.append(column_picture_path   + " TEXT ");
            //sb.append("FOREIGN KEY ("+ column_picture_id +") REFERENCES "+ TableManager.PictureTable.name +"("+ PictureTable.column_id + " ON UPDATE CASCADE ");

            sb.append(")");

            return sb.toString();
        }
    }

    public static class SpotTable {

        public static final String name = "Spot";

        public static final String column_id =              "spot_id";
        public static final String column_route_id =        "route_id";    //route table id
        public static final String column_next_spot_id =    "next_spot_id";
        public static final String column_picture_id =      "picture_id";   //대표 사진 id
        public static final String column_mission =         "mission";
        public static final String column_search_id =       "search_id";    //search table id
        public static final String column_category_id =     "category_id";


        public static final String[] columns = {
                column_id,
                column_route_id,
                column_next_spot_id,
                column_picture_id,
                column_mission,
                column_search_id,
                column_category_id
        };

        public static String getCreateQuery(){

            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE " + name + " ( ");
            sb.append(column_id             + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sb.append(column_route_id       + " INTEGER NOT NULL, ");
            sb.append(column_next_spot_id   + " INTEGER, ");
            sb.append(column_picture_id     + " INTEGER, ");
            sb.append(column_mission        + " TEXT NOT NULL, ");
            sb.append(column_search_id      + " INTEGER, ");
            sb.append(column_category_id    + " INTEGER, ");
            sb.append("FOREIGN KEY ("+ column_route_id +") REFERENCES "+ TableManager.RouteTable.name +"("+ RouteTable.column_id+")" + " ON DELETE CASCADE ON UPDATE CASCADE, ");
            sb.append("FOREIGN KEY ("+ column_picture_id +") REFERENCES "+ TableManager.PictureTable.name +"("+ PictureTable.column_id+") ON UPDATE CASCADE, ");
            sb.append("FOREIGN KEY ("+ column_search_id +") REFERENCES "+ TableManager.SearchTable.name +"("+ SearchTable.column_id+")");

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
            sb.append(column_id                     + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sb.append(column_place_unique_id        + " INTEGER, ");
            sb.append(column_place_name             + " TEXT, ");
            sb.append(column_place_address          + " TEXT, ");
            sb.append(column_place_attribution      + " TEXT, ");
            sb.append(column_place_phone            + " TEXT, ");
            sb.append(column_place_locale           + " TEXT, ");
            sb.append(column_place_uri              + " TEXT, ");
            sb.append(column_lat                    + " REAL, ");
            sb.append(column_lon                    + " REAL, ");
            sb.append(column_southwest_lat          + " REAL, ");
            sb.append(column_southwest_lon          + " REAL, ");
            sb.append(column_northeast_lat          + " REAL, ");
            sb.append(column_northeast_lon          + " REAL ");
            sb.append(")");

            return sb.toString();
        }
    }

    public static class PictureTable {

        public static  final String name = "Picture";

        public static final String column_id            = "picture_id";
        public static final String column_route_id      = "route_id";
        public static final String column_spot_id       = "spot_id";
        public static final String column_search_id     = "search_id";
        public static final String column_path          = "path";
        public static final String column_date          = "date";
        public static final String column_memo          = "memo";
        public static  final String[] columns = {
                column_id,
                column_route_id,
                column_spot_id,
                column_search_id,
                column_path,
                column_date,
                column_memo
        };


        public static String getCreateQuery(){

            StringBuffer sb = new StringBuffer();
            sb.append("CREATE TABLE " + name + " ( ");
            sb.append(column_id             + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
            sb.append(column_route_id       + " INTEGER, ");
            sb.append(column_spot_id        + " INTEGER, ");
            sb.append(column_search_id      + " INTEGER, ");
            sb.append(column_path           + " TEXT, ");
            sb.append(column_date           + " TEXT, ");
            sb.append(column_memo           + " TEXT, ");
            sb.append("FOREIGN KEY ("+ column_route_id +") REFERENCES "+ TableManager.RouteTable.name +"("+ RouteTable.column_id+") ON UPDATE CASCADE, ");
            sb.append("FOREIGN KEY ("+ column_spot_id +") REFERENCES "+ TableManager.SpotTable.name +"("+ SpotTable.column_id+") ON UPDATE CASCADE, ");
            sb.append("FOREIGN KEY ("+ column_search_id +") REFERENCES "+ TableManager.SearchTable.name +"("+ SearchTable.column_id+")");

            sb.append(")");

            return sb.toString();
        }
    }
}

