package gamsung.traveller.frag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.util.Log;
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
import java.util.Collections;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.activity.EditLocationActivity;
import gamsung.traveller.activity.TravelViewActivity;
import gamsung.traveller.adapter.TimeViewAdapter;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.dao.SpotManager;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.Spot;
import gamsung.traveller.util.DebugToast;

/**
 * Created by JKPark on 2018-01-25.
 */

public class ViewByPhotosFragment extends Fragment {
    private static final int REQUEST_EDIT= 0;
    private RecyclerView timeRecyclerView;
    private TimeViewAdapter timeViewAdapter;
    private List<Integer> originalPos = new ArrayList<>();
    private List<Integer> updatedPos = new ArrayList<>();
    private List<Spot> spotList;
    private boolean isOrderChanged;
    private List<Integer> deletedSpotID, editedSpotID;
    private LinearLayoutManager linearLayoutManager;
    private int last_idx = -1;
    TravelViewActivity activity;
    private static final int RESULT_EDIT = 503;

    public List<Integer> getOriginalPos(){
        return originalPos;
    }
    public List<Integer> getUpdatedPos(){
        updatedPos.clear();
        for (int orgPos : originalPos){
            int idx = 0;
            for (Spot curSpot : spotList){
                if (curSpot.getIndex_id() == orgPos) break;
                idx++;
            }
            updatedPos.add(originalPos.get(idx));
        }

        return updatedPos;
    }


    @Override
    public void onDestroy() { //update order when being destoryed
        activity.updateSpotlistToDB((ArrayList<Spot>) spotList);
        super.onDestroy();
    }

    private void updateLists(){
        spotList = activity.refreshSpotList();

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
        activity = (TravelViewActivity)getActivity();

        if (activity.getChangeMade() || spotList == null) updateLists();
        originalPos.clear();
        for (Spot spot : spotList){
            originalPos.add(spot.getIndex_id());
        }
        activity.setChangeMade(false);

        deletedSpotID = activity.getDeletedSpotID();
        editedSpotID = activity.getEditedSpotID();
        isOrderChanged = activity.isOrderChanged();

        linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        timeViewAdapter = new TimeViewAdapter(spotList, null, activity.getDataManager());
        timeRecyclerView.setAdapter(timeViewAdapter);
        timeRecyclerView.setLayoutManager(linearLayoutManager);
        timeRecyclerView.setItemAnimator(new DefaultItemAnimator());


        PhotoTouchHelperCallback touchHelperCallback = new PhotoTouchHelperCallback(timeViewAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(touchHelperCallback);
        touchHelper.attachToRecyclerView(timeRecyclerView);
        timeViewAdapter.setCallback(clickListener);
        return view;

    }

    public List<Spot> getSpotList(){
        return  this.spotList;
    }
    TimeViewAdapter.ClickListener clickListener = new TimeViewAdapter.ClickListener() {
        private ArrayList<Photograph> photoList;
        private DataManager dataManager;
        @Override
        public void onClickDelete(final int position) {
            final int pos = position;
            final Spot targetSpot = spotList.get(pos);
            final AlertDialog.Builder alert_delete = new AlertDialog.Builder(getContext());
            alert_delete.setMessage("일정을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deletedSpotID.add(targetSpot.get_id());
                    activity.deleteSpotFromDB(targetSpot.get_id());
                    spotList.remove(pos);
                    timeViewAdapter.updateColorGab();
                    timeViewAdapter.notifyItemRemoved(pos);
                    timeViewAdapter.notifyItemRangeRemoved(0, spotList.size());
                    activity.setChangeMade(true);
                    if (spotList.size() == 0) {
                        activity.restartActivity();
                    }
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
            Intent i = new Intent(getContext(), EditLocationActivity.class);
            activity.updateSpotlistToDB((ArrayList<Spot>) spotList);

            Spot targetSpot = spotList.get(position);
            i.putExtra("TAG_ACTIVITY", "edit");
            i.putExtra("route id", targetSpot.getRoute_id());
            i.putExtra("spot id", targetSpot.get_id());
            i.putExtra("spot index", targetSpot.getIndex_id());
            startActivityForResult(i, REQUEST_EDIT);
            last_idx = position;
        }

        @Override
        public void notifyOrderChanged(int oldPos, int newPos) {
            activity.setOrderChanged(true);
            activity.setChangeMade(true);
        }

        @Override
        public String getPlaceName(int searchID) {
            return activity.getSearchPlaceFromDB(searchID);
        }

    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_EDIT){
            spotList = activity.refreshSpotList();
            int spot_id;

            try{
                spot_id = data.getExtras().getInt("spot_id", -1);
            }catch(NullPointerException e){return;}

            if (spot_id == -1) return; //no spot_id returns => edit fails


            boolean isExist = false;
            for (int idx : editedSpotID){ //update editedSpotID, but avoid overlaps
                if (idx == spot_id){
                isExist = true;
                break;
                }
            }
            if (!isExist)editedSpotID.add(spot_id);

//            timeViewAdapter.notifyDataSetChanged();
            timeViewAdapter.refreshSpotlist(spotList);
//            if (last_idx != -1) timeViewAdapter.notifyItemChanged(last_idx);
            timeViewAdapter.notifyDataSetChanged();
            activity.setChangeMade(true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void forceUpdate(){
        if (activity == null) return;
        spotList = activity.refreshSpotList();
        timeViewAdapter.refreshSpotlist(spotList);
        timeViewAdapter.notifyDataSetChanged();
    }
}

