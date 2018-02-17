package gamsung.traveller.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import gamsung.traveller.R;
import gamsung.traveller.activity.CustomGalleryActivity;
import gamsung.traveller.activity.ImageSliderActivity;
import gamsung.traveller.model.Photograph;

/**
 * Created by jekan on 2018-02-10.
 */

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> implements Serializable{

    private Context _context;
    private ArrayList<Photograph> _items;

    public CustomRecyclerAdapter(Context context, ArrayList<Photograph> photoList) {

        if (photoList == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        this._context = context;
        this._items = photoList;
    }

    @Override
    public CustomRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_edit_chlid_item, viewGroup, false);

        return new CustomViewHolder(_context, itemView);
    }


    @Override
    public void onBindViewHolder(CustomRecyclerAdapter.CustomViewHolder viewHolder, int position) {

        String imgPath = _items.get(position).getPath();
        if (!TextUtils.isEmpty(imgPath)) {
            Glide.with(_context).load(imgPath).into(viewHolder.imageView);
        }

        String memo = _items.get(position).getMemo();
        viewHolder.txtMemo.setText(memo);
    }

    @Override
    public int getItemCount() {

        return _items == null ? 0 : _items.size();
    }



    public int addImagePath(String path){

        path = path.replace("[", "");
        path = path.replace("]", "");
        path = path.replace(" ", "");
        String[] pathArr = path.split(",");
        for(int i=0; i<pathArr.length; i++){

            Photograph photograph = new Photograph();
            photograph.setPath(pathArr[i]);

            _items.add(photograph);

            notifyItemChanged(_items.size()-1);
        }

        return _items.size();
    }

    public ArrayList<Photograph> getPhotoList(){
        return _items;
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        Context context;

        private ImageView imageView;
        private EditText txtMemo;

        public CustomViewHolder(final Context context, View itemView) {
            super(itemView);
            this.context = context;

            imageView = (ImageView) itemView.findViewById(R.id.img_viewpager_childimage);
            txtMemo = (EditText)itemView.findViewById(R.id.txt_memo_edit);
        }
    }
}


