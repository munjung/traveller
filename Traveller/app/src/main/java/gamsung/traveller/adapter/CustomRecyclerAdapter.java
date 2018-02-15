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

/**
 * Created by jekan on 2018-02-10.
 */

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> implements Serializable{

    private Context _context;
    private List<String> _items;
    private static String[] pathArr;
    //private static ArrayList<String> pathArr;

    public CustomRecyclerAdapter(Context context, List<String> imgList) {

        if (imgList == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        this._context = context;
        this._items = imgList;
    }

    @Override
    public CustomRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_edit_chlid_item, viewGroup, false);

        return new CustomViewHolder(_context, itemView);
    }


    @Override
    public void onBindViewHolder(CustomRecyclerAdapter.CustomViewHolder viewHolder, int position) {

        String item = _items.get(position);
        if (!TextUtils.isEmpty(item)) {
            Glide.with(_context).load(item).into(viewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {

        return _items == null ? 0 : _items.size();
    }

    public int addImagePath(String path){

        path = path.replace("[", "");
        path = path.replace("]", "");
        path = path.replace(" ", "");
        pathArr = path.split(",");
        // Log.d("patharrrrr", pathArr.toString());

        for(int i=0; i<pathArr.length; i++){
            _items.add(pathArr[i]);
        }

        notifyDataSetChanged();

        return _items.size();
    }

    public void addImagePath(List<String> path){
        _items.addAll(path);
    }

    public void removeImagePath(int position){

    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        Context context;

        private ImageView imageView;
        private EditText txtMemo;

        public CustomViewHolder(final Context context, View itemView) {
            super(itemView);
            this.context = context;

            imageView = (ImageView) itemView.findViewById(R.id.img_viewpager_childimage);
            txtMemo = (EditText)itemView.findViewById(R.id.txt_memo_edit);
            imageView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ImageSliderActivity.class);

            for(int i=0; i<pathArr.length; i++){
                 intent.putExtra("ImgPath", pathArr[i]);
                //intent.putParcelableArrayListExtra("ImgPath", pathArr[i]);
                Log.d("path2222", pathArr[i]);
            }


//            for(int i=0; i<pathArr.length; i++){
//                intent.putExtra("ImgPath", pathArr[i]);
//                Log.d("path2222", pathArr[i]);
//            }


            ArrayList<String> pathList = new ArrayList<String>(Arrays.asList(pathArr));
            intent.putExtra("ImgPath", pathList.toString());


            ((Activity)context).startActivity(intent);
//            ((Activity)context).startActivityForResult(intent, 2);
        }
    }
}


