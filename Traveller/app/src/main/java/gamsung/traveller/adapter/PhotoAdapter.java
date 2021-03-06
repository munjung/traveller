package gamsung.traveller.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gamsung.traveller.activity.MapRecyclerActivity;
import gamsung.traveller.R;

/**
 * Created by Hanbin Ju on 2018-02-12.
 */

public class PhotoAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList<String> photoset;
    LayoutInflater inf;

    public PhotoAdapter(Context context, int layout, ArrayList<String> photoset){
        this.context = context;
        this.layout = layout;
        this.photoset = (ArrayList<String>) photoset.clone();
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
        String imgpath = photoset.get(i);
        Glide.with(context).load(imgpath).into(iv);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 4;
//        Bitmap bm = BitmapFactory.decodeFile(imgpath,options);
//        iv.setImageBitmap(bm);
        final int prepositon = i;
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int celwidth = (width-10)/4;
        iv.setLayoutParams(new RelativeLayout.LayoutParams(celwidth,celwidth));
        iv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapRecyclerActivity.class);
                intent.putStringArrayListExtra("photoset",photoset);
                int position=prepositon;
                intent.putExtra("offset",position);
                context.startActivity(intent);
            }
        });
        return view;
    }
}