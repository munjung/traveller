package gamsung.traveller.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.SearchPlace;
import gamsung.traveller.model.Spot;

/**
 * Implementation of App Widget functionality.
 */
public class HereAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        String mission = "미션 정보 없음";
        String place = "위치 정보 없음";
        String nextmisson = "다음 장소 정보 없음";
        DataManager dataManager = DataManager.getInstance(context);
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.here_app_widget);

        Spot curSpot = new Spot();
        HashMap<Integer,Spot> spotlist = new HashMap<>();
        spotlist = dataManager.getSpotListWithRouteId(1);

        Iterator it = sortByValue(spotlist).iterator();
        HashMap<Integer,SearchPlace> placelist = dataManager.getSearchPlaceList();

        while(it.hasNext()){
            int temp = (int)it.next();
            if(dataManager.getPhotoListWithSpot(temp).size()==0) {
                mission = spotlist.get(temp).getMission();
                place = placelist.get(spotlist.get(temp).getSearch_id()).getPlace_address();
                int newtemp = (int)it.next();
                nextmisson = spotlist.get(newtemp).getMission();
                break;
            }
        }

        Intent intent = new Intent(context,CameraActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        views.setOnClickPendingIntent(R.id.widget_camera,pendingIntent);


        views.setTextViewText(R.id.tvTodowidget,mission);
        views.setTextViewText(R.id.tvTodolocal,place);
        views.setTextViewText(R.id.tvTodonext,nextmisson);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static List sortByValue(final Map<Integer,Spot> map){
        List<Integer> list =new ArrayList<>();
        list.addAll(map.keySet());

        Collections.sort(list, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Object v1 = map.get(o1).getIndex_id();
                Object v2 = map.get(o2).getIndex_id();

                return ((Comparable)v2).compareTo(v1);
            }
        });
        Collections.reverse(list);
        return list;
    }
}



