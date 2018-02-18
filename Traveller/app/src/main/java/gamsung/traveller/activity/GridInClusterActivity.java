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

import gamsung.traveller.R;
import gamsung.traveller.adapter.PhotoAdapter;

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

        Intent intent = getIntent();
        ArrayList<String> photolist= (ArrayList<String>)intent.getStringArrayListExtra("phototosend").clone();
        String sentname = (String)intent.getStringExtra("localname");
        TextView textView = findViewById(R.id.tvlocalname);
        textView.setText(sentname);
        PhotoAdapter adapter = new PhotoAdapter(getApplicationContext(),R.layout.gridrow,photolist);

        GridView gv = (GridView)findViewById(R.id.gvphoto);
        gv.setAdapter(adapter);
    }
}
