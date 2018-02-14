package gamsung.traveller.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.FitWindowsLinearLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.activity.TravelViewActivity;
import gamsung.traveller.adapter.TimeViewAdapter;
import gamsung.traveller.dao.SpotManager;
import gamsung.traveller.model.Spot;
import gamsung.traveller.util.DebugToast;

/**
 * Created by JKPark on 2018-01-25.
 */

public class ViewByPhotosFragment extends Fragment {
    private RecyclerView timeRecyclerView;
    private TimeViewAdapter timeViewAdapter;
    private List<Spot> spotList;
    private LinearLayoutManager linearLayoutManager;

    public ViewByPhotosFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (View)inflater.inflate(R.layout.fragment_view_by_photos, container, false);
        //ViewGroup viewGroup = (ViewGroup)view.findViewById(R.id.base_layout_photos);

        timeRecyclerView = view.findViewById(R.id.time_view_RecyclerView);
        TravelViewActivity activity = (TravelViewActivity)getActivity();
        spotList = activity.getSpotList();

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        timeViewAdapter = new TimeViewAdapter(spotList);
        timeRecyclerView.setAdapter(timeViewAdapter);
        timeRecyclerView.setLayoutManager(linearLayoutManager);
        timeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;

        //get fragment view group
        /*


        //get spot list from activity (for sync data)
        TravelViewActivity activity = (TravelViewActivity)getActivity();
        List<Spot> spotList = activity.getSpotList();
/*
        //dynamic add custom layout list
        for (int i=spotList.size()-1; i >= 0; i--) {
            Spot spot = spotList.get(i);

            View spotView = LayoutInflater.from(activity).inflate(R.layout.layout_view_by_photos_template, viewGroup);
            //mission text
            TextView tv = spotView.findViewById(R.id.txt_view_by_photos);
            tv.setText(spot.getMission());
            //edit button
            Button btn = spotView.findViewById(R.id.btn_view_by_photos);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            //picture layout
//            RecyclerView recyclerView = (RecyclerView)spotView.findViewById(R.id.recycler_view_by_photos);
//            recyclerView.setAdapter(new RecyclerViewAdapter(activity, activity.getImageListWithSpot(spot.get_id())));
//            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        }
        */




    }
}


//View Holder Adaper
class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ImageViewHolder> {

    private Context _context;
    private List<String> _items;

    public RecyclerViewAdapter(Context context, List<String> imgPathList) {

        if (imgPathList == null) {
            throw new IllegalArgumentException("route data must not be null");
        }

        this._context = context;
        this._items = imgPathList;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_view_by_photos_item, viewGroup, false);

        return new ImageViewHolder(_context, itemView);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {

        String item = _items.get(position);

        if (!TextUtils.isEmpty(item)) {

            Glide.with(_context).load(item).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    //View Holder
    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        private Context _context;
        public ImageView imageView;

        public ImageViewHolder(Context context, View itemView) {
            super(itemView);

            _context = context;

            imageView = (ImageView) itemView.findViewById(R.id.image_view_by_photos);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DebugToast.show(_context, "image clicked");
                }
            });
        }
    }
}