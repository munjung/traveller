package gamsung.traveller.activity;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gamsung.traveller.R;
import gamsung.traveller.adapter.WidgetAdapter;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Route;
import gamsung.traveller.util.widgetItem;

public class HereAppWidgetSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_here_app_widget_select);
        int mAppWidgetId=0;
        Bundle mExtras = getIntent().getExtras();
        if (mExtras != null) {

            mAppWidgetId = mExtras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,

                    AppWidgetManager.INVALID_APPWIDGET_ID);

        }
        final int widgetid = mAppWidgetId;
        ListView listView;
        WidgetAdapter widgetAdapter;
        DataManager dataManager = DataManager.getInstance(this);


        HashMap<Integer,Route> routeHashMap = dataManager.getRouteList();
        final ArrayList<widgetItem> widgetItemArrayList = new ArrayList<>();
        for(Map.Entry<Integer,Route> e:routeHashMap.entrySet()){
            widgetItem witem = new widgetItem();
            witem.setId(e.getKey());
            witem.setRoute(e.getValue().getTitle());
            widgetItemArrayList.add(witem);
        }
        widgetAdapter = new WidgetAdapter();

        listView = (ListView)findViewById(R.id.lvwidget);
        listView.setAdapter(widgetAdapter);
        widgetAdapter.addItems(widgetItemArrayList);
        final Context context = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int idToSend = widgetItemArrayList.get(i).getId();
                Bundle bundle = new Bundle();
                bundle.putInt("idsend",idToSend);
                Intent resultValue = new Intent();
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidgetOptions(widgetid,bundle);
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetid);
                setResult(RESULT_OK,resultValue);
                finish();
            }
        });


    }
}

