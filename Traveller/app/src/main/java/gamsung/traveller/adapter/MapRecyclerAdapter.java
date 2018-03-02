package gamsung.traveller.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL;

import gamsung.traveller.R;

/**
 * Created by Hanbin Ju on 2018-02-15.
 */

public class MapRecyclerAdapter extends RecyclerView.Adapter{

    private ArrayList<String> data;
    private Context _context;
    public MapRecyclerAdapter(Context context, ArrayList<String> data){
        this._context = context;
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
        String mydata = data.get(position);
        Glide.with(_context).load(mydata).asBitmap().into(item.imageView);


//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize=4;
//        Bitmap bm = BitmapFactory.decodeFile(mydata,options);
//        item.imageView.setImageBitmap(bm);

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