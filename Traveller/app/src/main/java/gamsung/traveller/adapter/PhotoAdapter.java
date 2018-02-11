package gamsung.traveller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import gamsung.traveller.R;

/**
 * Created by Hanbin Ju on 2018-02-12.
 */

public class PhotoAdapter extends BaseAdapter {
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