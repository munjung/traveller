package gamsung.traveller.frag;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.DragEvent;
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

    LinearLayout layoutBase; //layout where lists are being drawn on
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_view_by_schedule, container, false);

        layoutBase = rootView.findViewById(R.id.base_layout_schedule);
        for (int i = 0; i < 10; i++)
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

            layoutSchedule.findViewById(R.id.circleimageview_right).setOnLongClickListener(longClickedCircle);
        }
        else{
            layoutSchedule.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);

            layoutSchedule.findViewById(R.id.circleimageview_left).setOnLongClickListener(longClickedCircle);
        }
        adjustContents(layoutSchedule, listSchedule.size());
        //layoutSchedule.setId(View.generateViewId());
        listSchedule.add(layoutSchedule);

        layoutSchedule.setTag(listSchedule.size());
        layoutBase.addView(layoutSchedule);
        layoutSchedule.setOnDragListener(scheduleDragListener);
    }


    LinearLayout.OnDragListener scheduleDragListener = new LinearLayout.OnDragListener(){
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            NestedScrollView scrollView;
            int action = dragEvent.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("DragDrop", "Started entered");
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //where animation happens

                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    //나중에 수정
                    //automatically scrolls at the edges of the scroll view
                    scrollView = rootView.findViewById(R.id.scroll_schedule);
                    int topDropZone = layoutBase.getTop();
                    int bottomDropZone = layoutBase.getBottom();

                    int scrollY = scrollView.getScrollY();
                    int scrollHeight = scrollView.getHeight();
                    Log.d("Scroll Y: ", scrollY + scrollHeight + ".Bottom: " + bottomDropZone + ".Top: " + topDropZone);
                    Log.d("Drag Point: ", dragEvent.getX() +", " + dragEvent.getY());
                    if (bottomDropZone > scrollY + scrollHeight - 100)
                        scrollView.smoothScrollBy(0, 30);

                    if (topDropZone < scrollY + 100)
                        scrollView.smoothScrollBy(0, -30);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    //cancel animation if necessary
                    Log.d("DragDrop", "Exited entered");
                    return true;
                case DragEvent.ACTION_DROP:
                    //switch of data happens
                    ClipData.Item item = dragEvent.getClipData().getItemAt(0);
                    String dragData =item.getText().toString();

                    swapSchedules(Integer.parseInt(dragData) - 1, Integer.parseInt(view.getTag().toString()) - 1);
                    Log.d("selected data index: ", dragData);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    //cancel animaton if necessary
                    Log.d("DragDrop", "Ended entered");
                    return true;

                default:
                    break;
            }
            return false;
        }
    };
    public void swapSchedules(int idxA, int idxB){
        int numTag;

        adjustContents(listSchedule.get(idxA), idxB);
        adjustContents(listSchedule.get(idxB), idxA);

        //switch tags of the two parameters
        numTag = Integer.parseInt(listSchedule.get(idxA).getTag().toString());
        listSchedule.get(idxA).setTag(Integer.parseInt(listSchedule.get(idxB).getTag().toString()));
        listSchedule.get(idxB).setTag(numTag);

        Collections.swap(listSchedule, idxA, idxB);
    }
    CircleImageView.OnLongClickListener longClickedCircle = new CircleImageView.OnLongClickListener(){
        @Override
        public boolean onLongClick(View view) {
            View parentView = (View)view.getParent();
            ClipData.Item item = new ClipData.Item((CharSequence) parentView.getTag().toString());
            String[] mimeType = {ClipDescription.MIMETYPE_TEXT_PLAIN};

            ClipData data = new ClipData(parentView.getTag().toString(), mimeType, item); //pass on the tag of the selected layout

            view.setBackgroundColor(Color.TRANSPARENT);
            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, dragShadowBuilder, view, 0);

            return true;
        }
    };



    public void adjustContents(View view, int tempNum){
        //update the contents of widgets on a schedule layout
        TextView textTitle, textContents;
        CircleImageView circleImageView;

        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                textTitle = view.findViewById(R.id.title_right);
                textContents = view.findViewById(R.id.contents_right);
                circleImageView = view.findViewById(R.id.circleimageview_right);
            }
            else{
                textTitle = view.findViewById(R.id.title_left);
                textContents = view.findViewById(R.id.contents_left);
                circleImageView = view.findViewById(R.id.circleimageview_left);
            }
            setContents(textTitle, textContents, circleImageView, tempNum);
        }
    }
    private void setContents(TextView textTitle, TextView textContents, CircleImageView circleImageView, int idx) {
        textTitle.setText("Title " + idx);
        textContents.setText("Here is the text view for contents" + idx);
        circleImageView.setImageResource(R.color.colorPrimary);
        circleImageView.setOnLongClickListener(longClickedCircle);
    };
    private void toggleVisbility(View view){
        if (visbilityCheck(view) == 0){ //if left is visible
            view.findViewById(R.id.circleimageview_right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.title_right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.contents_right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.contents_left).setVisibility(View.INVISIBLE);
        }
        else{
            view.findViewById(R.id.circleimageview_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.title_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.contents_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
        }
    }
    private int visbilityCheck(View view){
        //return 0 if left is visible, 1 if right is visible.
        CircleImageView circleimage;
        circleimage = view.findViewById(R.id.circleimageview_right);

        if (circleimage.getVisibility() == View.INVISIBLE && view.findViewById(R.id.circleimageview_left).getVisibility() == View.INVISIBLE)
            //if both taps are invisible
            return 2;

        if (circleimage.getVisibility() == View.VISIBLE)
            return 1;
        else
            return 0;
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
