package gamsung.traveller.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;

import java.util.ArrayList;

import gamsung.traveller.R;

/**
 * Created by Hanbin Ju on 2018-02-15.
 */

public class MapRecyclerAdapter extends RecyclerView.Adapter<MapRecyclerAdapter.VeiwHoler>{

    private String[] mDataset;
    public MapRecyclerAdapter(String[] myDataset){
        mDataset=myDataset;
    }

    public MapRecyclerAdapter() {

    }

    @Override
    public MapRecyclerAdapter.VeiwHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.maprecycleritem,parent,false);
 //       return new VeiwHoler(view);
        return null;
    }

    @Override
    public void onBindViewHolder(VeiwHoler holder, int position) {
 //        MyModel myModel = myModels.get(position);
 //        holder.imageView.setImageBitmap();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class VeiwHoler extends RecyclerView.ViewHolder {
        ImageView imageView;
        public VeiwHoler(ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }
}