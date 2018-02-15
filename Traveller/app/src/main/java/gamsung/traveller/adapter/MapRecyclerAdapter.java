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

    Context context;
    ArrayList<MyModel> myModels = new ArrayList<>();
    public MapRecyclerAdapter(Context context, ArrayList<MyModel> myModels){
        this.context = context;
        this.myModels = myModels;
    }
    @Override
    public VeiwHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.maprecycleritem,parent,false);
        return new VeiwHoler(view);
    }

    @Override
    public void onBindViewHolder(VeiwHoler holder, int position) {
         MyModel myModel = myModels.get(position);
         holder.imageView.setImageBitmap();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class VeiwHoler extends RecyclerView.ViewHolder {
        ImageView imageView;
        public VeiwHoler(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivRecycleitem);
        }
    }
}