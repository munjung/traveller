package gamsung.traveller.activity;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.util.HashMap;

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

        DataManager dataManager = DataManager.getInstance(context);
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.here_app_widget);
/*
        HashMap<Integer,Spot> spotlist = dataManager.getSpotListWithRouteId(0);
        HashMap<Integer,SearchPlace> placelist = dataManager.getSearchPlaceList();
        views.setTextViewText(R.id.tvTodowidget,spotlist.get(0).getMission());

        int searchid = spotlist.get(0).getSearch_id();
        views.setTextViewText(R.id.tvTodolocal,placelist.get(searchid).getPlace_address());
        views.setTextViewText(R.id.tvTodonext,spotlist.get(0).getMission());

*/
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
}

