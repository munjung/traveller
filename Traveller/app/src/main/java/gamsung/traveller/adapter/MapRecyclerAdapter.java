package gamsung.traveller.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.ArrayList;

import gamsung.traveller.R;

/**
 * Created by Hanbin Ju on 2018-02-15.
 */

public class MapRecyclerAdapter extends RecyclerView.Adapter{

    private ArrayList<String> data;

    public MapRecyclerAdapter(ArrayList<String> data){
        this.data=data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_recycler_item,parent,false);
        return new ListitemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ListitemViewHolder item = (ListitemViewHolder) holder;
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        String mydata = path+"/yeogi/"+data.get(position);
        Bitmap bm = BitmapFactory.decodeFile(mydata);
        item.imageView.setImageBitmap(bm);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public static class ListitemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ListitemViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivRecycleitem);
        }
    }
}