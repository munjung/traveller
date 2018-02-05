package gamsung.traveller.frag;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import gamsung.traveller.R;

/**
 * Created by JKPark on 2018-01-25.
 */

public class ViewByScheduleFragment extends Fragment {
    ViewGroup rootView;
    NestedScrollView scrollView;
    LinearLayout layoutBase; //layout where lists are being drawn on
    ScheduleServiceAnimated scheduleService;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int numItem;

        if (rootView == null) { //if rootview is not loaded, load.
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_by_schedule, container, false);

            layoutBase = rootView.findViewById(R.id.base_layout_schedule);
            scrollView = rootView.findViewById(R.id.scroll_schedule);

            scheduleService = new ScheduleServiceAnimated(rootView, R.layout.layout_single_schedule, scrollView, layoutBase, getContext(), true);
            numItem = 0;
            if (numItem == 0) {
                scheduleService.drawFirstScreen_Coordinator(startScheduling);
            } else {

            }
        }
        return rootView;
    }
    View.OnClickListener startScheduling = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            scheduleService.startSchedule(createNewSchedule, editSchedule);
        }
    };

    View.OnClickListener createNewSchedule = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            scheduleService.createNewSchedule(createNewSchedule, editSchedule);
        }
    };

    View.OnClickListener editSchedule = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            //동그라미 클릭시 일단 삭제 시험
            View viewParent = (View)view.getParent();
            Toast.makeText(getContext(), "Clicked TagID: " + viewParent.getTag().toString() + " Idx: " +
                    scheduleService.toListIdx((int)viewParent.getTag()), Toast.LENGTH_SHORT).show();
            layoutBase.removeView(viewParent);
            scheduleService.listSchedule.remove(scheduleService.toListIdx((int)viewParent.getTag()));
            for (int i = 0; i < scheduleService.listSchedule.size() - 1; i++){
                scheduleService.setVisbility(scheduleService.listSchedule.get(i).view, scheduleService.getLeftVisbility(i));
            }
        }
    };


}