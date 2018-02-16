package gamsung.traveller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.util.ArrayList;

import gamsung.traveller.R;
import gamsung.traveller.adapter.MapRecyclerAdapter;

public class MapRecyclerActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutmanager;
    MapRecyclerAdapter mAdapter;
    ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_recycler);
        Intent intent = getIntent();
        ArrayList<String> photolist = (ArrayList<String>) intent.getStringArrayListExtra("photoset").clone();
        int offset = intent.getIntExtra("offset",0);
        mRecyclerView = (RecyclerView) findViewById(R.id.maprecycler);

        mLayoutmanager = new LinearLayoutManager(this);
        mLayoutmanager.setOrientation(LinearLayoutManager.HORIZONTAL);

        mRecyclerView.setLayoutManager(mLayoutmanager);
        mRecyclerView.scrollToPosition(offset);
        mAdapter = new MapRecyclerAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        data.addAll(photolist);


    }

}

