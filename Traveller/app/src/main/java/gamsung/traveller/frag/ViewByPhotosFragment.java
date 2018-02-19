package gamsung.traveller.frag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.FitWindowsLinearLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.activity.TravelViewActivity;
import gamsung.traveller.adapter.TimeViewAdapter;
import gamsung.traveller.dao.DataManager;
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
    private List<Integer> deletedSpotID, editedSpotID;
    private boolean isOrderChanged;
    private LinearLayoutManager linearLayoutManager;
    private DataManager dataManager;
    TravelViewActivity activity;
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

        dataManager = DataManager.getInstance(getActivity());

        timeRecyclerView = view.findViewById(R.id.time_view_RecyclerView);
        activity = (TravelViewActivity)getActivity();

        if (activity.getChangeMade() || spotList == null) spotList = new ArrayList<>(dataManager.getSpotListWithRouteId(activity.getRoute_id()).values());
        activity.setChangeMade(false);

        deletedSpotID = activity.getDeletedSpotID();
        editedSpotID = activity.getEditedSpotID();
        isOrderChanged = activity.isOrderChanged();

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        timeViewAdapter = new TimeViewAdapter(spotList, null);
        timeRecyclerView.setAdapter(timeViewAdapter);
        timeRecyclerView.setLayoutManager(linearLayoutManager);
        timeRecyclerView.setItemAnimator(new DefaultItemAnimator());


        PhotoTouchHelperCallback touchHelperCallback = new PhotoTouchHelperCallback(timeViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
        touchHelper.attachToRecyclerView(timeRecyclerView);
        timeViewAdapter.setCallback(clickListener);
        return view;

    }
    TimeViewAdapter.ClickListener clickListener = new TimeViewAdapter.ClickListener() {
        @Override
        public void onClickDelete(final int position) {
            final int pos = position;
            final Spot targetSpot = spotList.get(pos);
            AlertDialog.Builder alert_delete = new AlertDialog.Builder(getContext());
            alert_delete.setMessage("일정을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deletedSpotID.add(targetSpot.get_id());
                    dataManager.deleteSpot(targetSpot.get_id());
                    spotList.remove(pos);
                    timeViewAdapter.updateColorGab();
                    timeViewAdapter.notifyItemRemoved(pos);
                    timeViewAdapter.notifyItemRangeRemoved(0, spotList.size());
                    activity.setChangeMade(true);
                    //timeViewAdapter.notifyDataSetChanged();
                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    timeViewAdapter.notifyItemChanged(pos);
                }
            });
            timeRecyclerView.scrollToPosition(pos);
            AlertDialog alert = alert_delete.create();
            alert.show();
        }

        @Override
        public void onClickEdit(int position) {
            spotList.get(position).setMission("Mission edited: " + position);
            int spotID = spotList.get(position).get_id();
            boolean isExist = false;
            dataManager.updateSpot(spotList.get(position));
            //avoid overlaps
            for (int idx : editedSpotID){
                if (idx == spotID){
                    isExist = true;
                    break;
                }
            }
            if (!isExist) editedSpotID.add(spotID);
            timeViewAdapter.notifyDataSetChanged();

            activity.setChangeMade(true);
        }

        @Override
        public void notifyOrderChanged(int oldPos, int newPos) {
            activity.setOrderChanged(true);
            activity.setChangeMade(true);
        }
    };
}

