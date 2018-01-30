package gamsung.traveller.frag;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;

/**
 * Created by JKPark on 2018-01-25.
 */

public class ViewByScheduleFragment extends Fragment {
    ArrayList<View> listSchedule = new ArrayList<>();
    ViewGroup rootView;
    NestedScrollView scrollView;
    LinearLayout layoutBase; //layout where lists are being drawn on
    ScheduleService scheduleService;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int numItem;

        if (rootView == null) { //if rootview is not loaded, load.
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_view_by_schedule, container, false);

            layoutBase = rootView.findViewById(R.id.base_layout_schedule);
            scrollView = rootView.findViewById(R.id.scroll_schedule);

            scheduleService = new ScheduleService(rootView, R.layout.layout_single_schedule, scrollView, layoutBase, getContext());
            numItem = 0;
            if (numItem == 0) {
                scheduleService.drawFirstScreen_Coordinator();
                View circleView = scheduleService.listSchedule.get(0);
                circleView.setOnClickListener(startScheduling);
            } else {

            }

            return rootView;
        }
        return rootView;

    }

    View.OnClickListener startScheduling = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            ArrayList listSchedule = scheduleService.listSchedule;
            layoutBase.removeAllViews();
            listSchedule.remove(0); //remove the empty circle from the view.

            CoordinatorLayout.LayoutParams coordParms = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
            coordParms.gravity = Gravity.TOP;
            scrollView.setLayoutParams(coordParms);

            for (int i = 0; i < 2; i++) {
                listSchedule.add(scheduleService.createScheduleView(listSchedule.size()));
                layoutBase.addView((View)listSchedule.get(i));
            }
            scheduleService.setScheduleView(0);
        }
    };

}
