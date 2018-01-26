package gamsung.traveller.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import gamsung.traveller.R;

/**
 * Created by JKPark on 2018-01-25.
 */

public class ViewByScheduleFragment extends Fragment {
    ArrayList<View> listSchedule = new ArrayList<>();
    ;
    LinearLayout layoutBase; //layout where lists are being drawn on
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_view_by_schedule, container, false);

        layoutBase = rootView.findViewById(R.id.base_layout_schedule);

        for (int i = 0; i < 15; i++)
            addSchedule();

        return rootView;
    }

    public void addSchedule(){
        LayoutInflater layoutInflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutSchedule = layoutInflater.inflate(R.layout.layout_single_schedule, null);

        if (listSchedule.size() % 2 == 0){
            layoutSchedule.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.contents_left).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
        }
        else{
            layoutSchedule.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
        }
        //layoutSchedule.setId(View.generateViewId());
        listSchedule.add(layoutSchedule);
        layoutBase.addView(layoutSchedule);
    }

    private int getRelativeLeft(View view, View root){
        //absolute x-coordinate relative to a root view
        if (view.getParent() == root)
            return view.getLeft();
        else
            return  view.getLeft() + getRelativeLeft((View)view.getParent(), root);
    }
    private int getRelativeTop(View view, View root){
        //absolute y-coordinate relative to a root view
        if (view.getParent() == root)
            return view.getTop();
        else
            return view.getTop() + getRelativeTop((View)view.getParent(), root);
    }
}
