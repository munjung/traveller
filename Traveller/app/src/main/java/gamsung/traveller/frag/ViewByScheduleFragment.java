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
import gamsung.traveller.R;
import gamsung.traveller.activity.EditLocationActivity;
import gamsung.traveller.activity.MainActivity;
import gamsung.traveller.activity.SplashActivity;

/**
 * Created by JKPark on 2018-01-25.
 */

public class ViewByScheduleFragment extends Fragment {
    ViewGroup rootView;
    NestedScrollView scrollView;
    RelativeLayout layoutBase; //layout where lists are being drawn on
    ScheduleServiceAnimated scheduleService;
    private View referenceView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) { //if rootview is not loaded, load.
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_by_schedule, container, false);

            layoutBase = rootView.findViewById(R.id.base_layout_schedule);
            scrollView = rootView.findViewById(R.id.scroll_schedule);

            scheduleService = new ScheduleServiceAnimated(rootView, R.layout.layout_single_schedule, scrollView, layoutBase, getContext(), true);
            scheduleService.clickEditSchedule = editSchedule;
            scheduleService.clickRemoveSelectedSchedule = clickRemoveSchedule;

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
                        numItem = 0;
                        if (numItem == 0) { //draw first screen if data is not available
                            scheduleService.drawFirstScreen_Coordinator(startScheduling);
                        } else { //load if data is available

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
            scheduleService.initSchedule(createNewSchedule, editSchedule);
        }
    };

    View.OnClickListener createNewSchedule = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            scheduleService.addSchedule(createNewSchedule, editSchedule);
            Intent i = new Intent(rootView.getContext(),EditLocationActivity.class);
            i.putExtra("TAG_ACTIVITY","create");
            Toast.makeText(rootView.getContext(), "View ID: " + view.getTag(), Toast.LENGTH_SHORT).show();
            startActivity(i);
        }
    };

    View.OnClickListener editSchedule = new View.OnClickListener(){
        @Override
        public void onClick(View view) {

            Intent i = new Intent(rootView.getContext(),EditLocationActivity.class);
            i.putExtra("TAG_ACTIVITY","edit");
            Toast.makeText(rootView.getContext(), "View ID: " + view.getTag(), Toast.LENGTH_SHORT).show();
            startActivity(i);
        }
    };

    View.OnClickListener clickRemoveSchedule = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(!scheduleService.removeSchedule(((View)view.getParent()).getId())){ //if less than 2 remaining
                //removeSchedule returns false when the first screen needs to be drawn.
                layoutBase.removeAllViews();
                scheduleService.listSchedule.clear();
                scheduleService.drawFirstScreen_Coordinator(startScheduling);
            }
        }
    };

}