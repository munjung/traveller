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

import java.util.ArrayList;

import gamsung.traveller.MapRecyclerActivity;
import gamsung.traveller.R;

import static android.support.v4.content.ContextCompat.startActivity;

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
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String imgpath = path+"/testing/"+photoset.get(i);
        Bitmap bm = BitmapFactory.decodeFile(imgpath);
        iv.setImageBitmap(bm);
        int width = context.getResources().getDisplayMetrics().widthPixels;
        int celwidth = (width-10)/4;
        iv.setLayoutParams(new RelativeLayout.LayoutParams(celwidth,celwidth));
        iv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapRecyclerActivity.class);
                intent.putStringArrayListExtra("photoset",photoset);
                context.startActivity(intent);
            }
        });
        return view;
    }
}