package gamsung.traveller.frag;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.activity.EditLocationActivity;
import gamsung.traveller.activity.EmptyTravelActivity;
import gamsung.traveller.activity.MainActivity;
import gamsung.traveller.activity.SplashActivity;
import gamsung.traveller.activity.TravelViewActivity;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Spot;

/**
 * Created by JKPark on 2018-01-25.
 */

public class ViewByScheduleFragment extends Fragment {
    private static final int REQUEST_EDIT= 0;
    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_INIT = 2;
    private static final int RESULT_EDIT = 3;
    private static final int RESULT_ADD = 4;
    private int route_id;
    ViewGroup rootView;
    NestedScrollView scrollView;
    RelativeLayout layoutBase; //layout where lists are being drawn on
    ScheduleServiceAnimated scheduleService;
    private View referenceView;
    private List<Spot> spotList;
    private List<Integer> deletedSpotID, editedSpotID;
    private boolean isOrderChanged;
    private TravelViewActivity activity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (TravelViewActivity)getActivity();
        deletedSpotID = activity.getDeletedSpotID();
        editedSpotID = activity.getEditedSpotID();
        isOrderChanged = activity.isOrderChanged();
        route_id = activity.getRoute_id();


        if (activity.getChangeMade() || spotList == null) spotList = activity.refreshSpotList();
        activity.setChangeMade(false);

        if (rootView == null) { //if rootview is not loaded, load.
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_by_schedule, container, false);

            layoutBase = rootView.findViewById(R.id.base_layout_schedule);
            scrollView = rootView.findViewById(R.id.scroll_schedule);

            scheduleService = new ScheduleServiceAnimated(rootView, R.layout.layout_single_schedule, scrollView, layoutBase, getContext(), spotList, true);

            scheduleService.clickEditSchedule = editSchedule;
            scheduleService.clickRemoveSelectedSchedule = clickRemoveSchedule;
            scheduleService.startScheduling = startScheduling;
            scheduleService.editSchedule = editSchedule;
            scheduleService.createNewSchedule = createNewSchedule;

            //draw referenceView for coordinate information
            LayoutInflater layoutInflater = (LayoutInflater) rootView.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layoutSchedule = layoutInflater.inflate(R.layout.layout_single_schedule, null);
            layoutSchedule.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            layoutBase.addView(layoutSchedule);
            referenceView = layoutSchedule;
            referenceView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() { //some unnecessary calls are made here
                    int numItem;
                    if(scheduleService.initCoordInformation(referenceView)) {
                        layoutBase.removeView(referenceView);
                        numItem = spotList.size();
                        if (numItem == 0) { //draw first screen if data is not available
                            scheduleService.drawFirstScreen_Coordinator();
                        } else { //load if data is available
                            scheduleService.load_Spots();
                        }
                        referenceView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
            //end of calculation of coordinates.
        }

        if (editedSpotID.size() >  0 || deletedSpotID.size() > 0 || isOrderChanged) {
            scheduleService.update_spots(spotList);
            scheduleService.updateSchedule(deletedSpotID, editedSpotID, isOrderChanged);
            activity.setOrderChanged(false);
        }
        return rootView;
    }


    View.OnClickListener startScheduling = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            Intent i = new Intent(rootView.getContext(),EditLocationActivity.class);
            Toast.makeText(rootView.getContext(), "View ID: " + view.getTag(), Toast.LENGTH_SHORT).show();
            i.putExtra("TAG_ACTIVITY", "create");
            i.putExtra("route id", route_id);
            startActivityForResult(i, REQUEST_INIT);

            activity.setChangeMade(true);
        }
    };

    View.OnClickListener createNewSchedule = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            Toast.makeText(rootView.getContext(), "View ID: " + view.getTag(), Toast.LENGTH_SHORT).show();

            Intent i = new Intent(rootView.getContext(),EditLocationActivity.class);
            i.putExtra("TAG_ACTIVITY","create");
            i.putExtra("route id", route_id);
            startActivityForResult(i, REQUEST_ADD);

            activity.setChangeMade(true);

        }
    };

    View.OnClickListener editSchedule = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            int idx = scheduleService.toListIdx((int)view.getId());
            Toast.makeText(rootView.getContext(), "Index: " + idx + ", " + spotList.get(idx).getMission(), Toast.LENGTH_SHORT).show();

            int view_idx = scheduleService.toListIdx((int)view.getId());

            Intent i = new Intent(rootView.getContext(),EditLocationActivity.class);
            i.putExtra("TAG_ACTIVITY","edit");
            i.putExtra("route id", route_id);
            i.putExtra("spot id", scheduleService.listSchedule.get(view_idx).spot_ID);
            startActivityForResult(i, REQUEST_EDIT);

            activity.setChangeMade(true);
        }
    };

    View.OnClickListener clickRemoveSchedule = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int view_id = ((View)view.getParent()).getId();
            AlertDialog.Builder alert_delete = new AlertDialog.Builder(getContext());
            alert_delete.setMessage("일정을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //assumed that the indexes for spotlist and listschedule are synchronized
                    int idx_view = scheduleService.toListIdx(view_id);
                    activity.deleteSpotFromDB(scheduleService.listSchedule.get(idx_view).spot_ID);
                    //spotList.remove(scheduleService.toListIdx(scheduleService.listSchedule.get(idx_view).spot_ID)); //temporarily manual deletion of list.
                    Toast.makeText(getContext(), "Deleted spot ID: " + scheduleService.listSchedule.get(idx_view).spot_ID + " route id: " + route_id, Toast.LENGTH_SHORT).show();
                    if (scheduleService.listSchedule.size() > 2){
                        scheduleService.removeSchedule(view_id);
                    }
                    else{
                        layoutBase.removeAllViews();
                        scheduleService.listSchedule.clear();
                        spotList.clear();
                        scheduleService.drawFirstScreen_Coordinator();
                    }

                    activity.setChangeMade(true);

                }
            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        AlertDialog alert = alert_delete.create();
        alert.show();
        }
    };

        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        spotList = activity.refreshSpotList();
        scheduleService.update_spots(spotList);
        if (requestCode == REQUEST_ADD){
            //temporary creating spots

            //the difference in the size between schedules and spot are number of items being created.
            int list_total = scheduleService.listSchedule.size() - 1; //minus for the last circle image view
            int num_added = spotList.size() - list_total;
            scheduleService.isEditing = false;
            processAdditionalSchedules(num_added, list_total);
        }
        else if (requestCode == REQUEST_INIT){

            if (spotList.size() > 0) {
                scheduleService.initSchedule();
                int list_total = scheduleService.listSchedule.size() - 1;
                int num_added = spotList.size() - list_total;
                processAdditionalSchedules(num_added, list_total);
            }
        }
        else if (requestCode == REQUEST_EDIT){
            int spot_id;
            try {
                spot_id = data.getExtras().getInt("spot_id", -1);
            } catch(NullPointerException e) {return;}
            if (spot_id == -1) return;

            int spot_total = spotList.size();
            for (int idx = 0; idx < spot_total; idx++){
                if (spotList.get(idx).get_id() == spot_id){
                    activity.updateSpotFromDB(spotList.get(idx));
                    break;
                }
            }
            editedSpotID.add(spot_id);
            scheduleService.updateSchedule(deletedSpotID, editedSpotID, false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processAdditionalSchedules(int num_added, int list_total){
        for (int idx = 0; idx < num_added; idx++){
            Spot newSpot = spotList.get(list_total + idx);
            scheduleService.addSchedule(newSpot);
        }
    }

    public void force_update(){
        spotList = activity.refreshSpotList();
        scheduleService.update_spots(spotList);
        if (scheduleService.listSchedule.size() == 0){
            scheduleService.load_Spots();
        }
        else{
            int list_total = scheduleService.listSchedule.size() - 1; //minus for the last circle image view
            int num_added = spotList.size() - list_total;
            scheduleService.isEditing = false;
            processAdditionalSchedules(num_added, list_total);
        }
    }
}

