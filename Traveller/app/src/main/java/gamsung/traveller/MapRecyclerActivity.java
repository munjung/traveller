package gamsung.traveller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import java.util.ArrayList;

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
        mRecyclerView = (RecyclerView) findViewById(R.id.maprecycler);

        mLayoutmanager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutmanager);

        mAdapter = new MapRecyclerAdapter(data);
        mRecyclerView.setAdapter(mAdapter);

        data.addAll(photolist);


    }

}

