package gamsung.traveller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Adapter;

import java.util.ArrayList;

import gamsung.traveller.adapter.MapRecyclerAdapter;

public class MapRecyclerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_recycler);
        mRecyclerView = (RecyclerView) findViewById(R.id.maprecycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutmanager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutmanager);

        mAdapter = new MapRecyclerAdapter();
    }

}
