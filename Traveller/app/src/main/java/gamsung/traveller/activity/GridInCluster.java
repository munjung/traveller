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

class PhotoAdapter extends BaseAdapter{
    Context context;
    int layout;
    ArrayList<Integer> photoset;
    LayoutInflater inf;

    public PhotoAdapter(Context context, int layout, ArrayList<Integer> photoset){
        this.context = context;
        this.layout = layout;
        this.photoset = (ArrayList<Integer>) photoset.clone();
        inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return photoset.size();
    }

    @Override
    public Object getItem(int i) {
        return photoset.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null)
            view = inf.inflate(layout,null);
        ImageView iv =(ImageView)view.findViewById(R.id.ivphotoplace);
        iv.setImageResource(photoset.get(i));
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int celwidth = (width-10)/4;
        iv.setLayoutParams(new RelativeLayout.LayoutParams(celwidth,celwidth));
        return view;
    }
}