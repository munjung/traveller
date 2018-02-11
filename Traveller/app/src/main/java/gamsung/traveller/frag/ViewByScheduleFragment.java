package gamsung.traveller.frag;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.activity.EditLocationActivity;
import gamsung.traveller.activity.MainActivity;
import gamsung.traveller.activity.SplashActivity;
import gamsung.traveller.activity.TravelViewActivity;
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
    ViewGroup rootView;
    NestedScrollView scrollView;
    RelativeLayout layoutBase; //layout where lists are being drawn on
    ScheduleServiceAnimated scheduleService;
    private View referenceView;
    List<Spot> spotList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TravelViewActivity activity = (TravelViewActivity)getActivity();
        spotList = activity.getSpotList();
        Spot temp = spotList.get(0);
        temp.getRoute_id();

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
                public void onGlobalLayout() { //some unncessary calls are made here
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
        return rootView;
    }
    View.OnClickListener startScheduling = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            Intent i = new Intent(rootView.getContext(),EditLocationActivity.class);
            Toast.makeText(rootView.getContext(), "View ID: " + view.getTag(), Toast.LENGTH_SHORT).show();
            bundle.putInt("route id", spotList.get(0).getRoute_id());
            startActivityForResult(i, REQUEST_INIT, bundle);
        }
    };

    View.OnClickListener createNewSchedule = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            Intent i = new Intent(rootView.getContext(),EditLocationActivity.class);
            i.putExtra("TAG_ACTIVITY","create");
            Toast.makeText(rootView.getContext(), "View ID: " + view.getTag(), Toast.LENGTH_SHORT).show();
            bundle.putInt("route id", spotList.get(0).getRoute_id());
            startActivityForResult(i,REQUEST_ADD);
        }
    };

    View.OnClickListener editSchedule = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Bundle bundle = new Bundle();
            Intent i = new Intent(rootView.getContext(),EditLocationActivity.class);
            int idx = scheduleService.toListIdx((int)view.getTag());
            Toast.makeText(rootView.getContext(), "Index: " + idx + ", " + spotList.get(idx).getMission(), Toast.LENGTH_SHORT).show();
            bundle.putInt("spot list", (int)view.getTag());
            startActivityForResult(i, REQUEST_EDIT, bundle);

        }
    };

    View.OnClickListener clickRemoveSchedule = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!scheduleService.removeSchedule(((View)view.getParent()).getId())){ //if less than 2 remaining
                //removeSchedule returns false when the first screen needs to be drawn.
                layoutBase.removeAllViews();
                scheduleService.listSchedule.clear();
                scheduleService.drawFirstScreen_Coordinator();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD){
            Spot tempSpot = new Spot();
            tempSpot.set_id(spotList.size() - 1);
            tempSpot.setMission("Temp mission" + tempSpot.get_id());
            tempSpot.setPicture_id(0);
            scheduleService.addSchedule(tempSpot);
        }
        else if (requestCode == REQUEST_INIT){
            Spot tempSpot = new Spot();
            tempSpot.set_id(110);
            tempSpot.setMission("First mission");
            tempSpot.setPicture_id(0);
            scheduleService.initSchedule(tempSpot);
        }
        else if (requestCode == REQUEST_EDIT){

            //Spot editedSPot = new Spot();
            //editedSPot.setMission("Hi");
            //
            //scheduleService.editSchedule((int)REQUEST_EDIT, editedSPot);
            //수정된 view_id값을 다시 editSchedule에다가 보내면 수정완료!
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}

