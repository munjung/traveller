package gamsung.traveller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gamsung.traveller.R;
import gamsung.traveller.adapter.PhotoAdapter;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Photograph;

/**
 * 그리드뷰로 사진들을 보여주는 액티비티.
 * 현재 11번 화면에 해당하고 12번도 겸용으로 쓰이도록 할 예정.
 * 누르면 리사이클러뷰로 이동한다.
 */
public class GridInClusterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_in_cluster);

        ArrayList<String> photolist = new ArrayList<>();
        TextView textView = findViewById(R.id.tvlocalname);

        Intent intent = getIntent();
        int routeId = intent.getIntExtra(MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_ID,-1);
        if(routeId==-1) {
            photolist = (ArrayList<String>) intent.getStringArrayListExtra("phototosend").clone();

            String sentname = (String) intent.getStringExtra("localname");

            textView.setText(sentname);
        }
        else{
            DataManager dataManager = DataManager.getInstance(this);
            HashMap<Integer,Photograph> photomap = dataManager.getPhotoListWithRoute(routeId);
            for(Map.Entry<Integer,Photograph> e : photomap.entrySet()){
                photolist.add(e.getValue().getPath());
            }
            String sentname = intent.getStringExtra(MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_TITLE);
            textView.setText(sentname);
        }
           PhotoAdapter adapter = new PhotoAdapter(getApplicationContext(), R.layout.gridrow, photolist);
            GridView gv = (GridView) findViewById(R.id.gvphoto);
            gv.setAdapter(adapter);

    }
}

