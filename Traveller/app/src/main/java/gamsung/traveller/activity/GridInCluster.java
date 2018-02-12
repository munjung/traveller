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

public class GridInCluster extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_in_cluster);

        Intent intent = getIntent();
        ArrayList<Integer> photolist= (ArrayList<Integer>)intent.getIntegerArrayListExtra("phototosend").clone();

        PhotoAdapter adapter = new PhotoAdapter(getApplicationContext(),R.layout.gridrow,photolist);

        GridView gv = (GridView)findViewById(R.id.gvphoto);
        gv.setAdapter(adapter);
    }
}

