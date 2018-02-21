package gamsung.traveller.frag;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;
import gamsung.traveller.model.Spot;


/**
 * Created by JKPark on 2018-01-31.
 */
public class ScheduleServiceAnimated extends ScheduleService {
    long DRAGDROP_ANIMATION_DURATION, DRAGDROP_WAITING_TIME;
    public View.OnClickListener clickEditSchedule;
    private Queue<View> queueCircleAnimated= new LinkedList<>();
    private int startID, endID;
    private boolean isScheduleMoved;
    private int temp_int = 0;
    public ScheduleServiceAnimated(ViewGroup rootView, @LayoutRes int layoutSingle, NestedScrollView scrollView,
                                   RelativeLayout layoutBase, Context appContext, List<Spot> spotList, boolean isDragDrop) {
        super(rootView, layoutSingle, scrollView, layoutBase, appContext, spotList, isDragDrop);
        super.dragListener = this.scheduleDragListener;
        clickEditSchedule = null;
        DRAGDROP_ANIMATION_DURATION = 1500;
        DRAGDROP_WAITING_TIME = 1000;
        isScheduleMoved = false;
        scrollView.getViewTreeObserver().addOnScrollChangedListener(scrollListener);
    }
    ViewTreeObserver.OnScrollChangedListener scrollListener = new ViewTreeObserver.OnScrollChangedListener() {
        @Override
        public void onScrollChanged() {

            int y_coordinate = 0;
            View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
            int diff = (int)(view.getBottom() - (scrollView.getHeight() + scrollView.getY()));
            if (scrollView.getY() == 0) y_coordinate = scrollView.getScrollY();
            Log.d("Test", y_coordinate + ".");
            //if (temp_int++ % 2 == 0) listSchedule.get(0).circleImage.animate().x(300);
            //else listSchedule.get(0).circleImage.animate().x(0);

        }
    };

    View.OnDragListener scheduleDragListener = new View.OnDragListener(){
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            NestedScrollView scrollView;
            int action = dragEvent.getAction();
            View localParent = (View)((View)dragEvent.getLocalState()).getParent();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    startID = -1;
                    isScheduleMoved = false;
                    return true;

                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    //tHandler.removeCallbacksAndMessages(null); //cancel animation + messages when moved to the next

                    view.setBackgroundColor(Color.TRANSPARENT);
                    //view.setVisibility(View.VISIBLE);
                    Log.d("DragDrop", "Exited entered");
                    return true;
                case DragEvent.ACTION_DROP:
                    //switch of data happens
                    //tHandler.removeCallbacksAndMessages(null);

                    //view.setVisibility(View.VISIBLE);
                    ClipData.Item item = dragEvent.getClipData().getItemAt(0);
                    String dragData =item.getText().toString();

                    Log.d("DragDrop", "Ended entered");
                    view.setBackgroundColor(Color.TRANSPARENT);
                    //if schedule is already moved, animation is not necessary.
                    if (!isScheduleMoved) {
                        moveSchedule(toListIdx(Integer.parseInt(dragData)), toListIdx((int) view.getTag()));
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (startID == -1) {
                        startID = toListIdx((int)view.getTag());
                    }
                    endID = toListIdx((int)view.getTag());
                    view.setBackgroundColor(Color.YELLOW);
                    //tHandler.postDelayed(runnableTimeCounter, DRAGDROP_WAITING_TIME); //animation takes a place after the waiting time.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    //cancel animaton if necessary
                    endID = toListIdx((int)view.getTag());
                    //tHandler.removeCallbacksAndMessages(null);
                    isScheduleMoved = false;
                    isEditing = false;
                    return true;

                default:
                    break;
            }
            return false;
        }
    };

    @Override
    public void removeSchedule(int view_id) {
        /* involves the entire schedules to be animated
        Lower than the selected index
        -Texts: fade out --> fade in
        -Circle Images: left -> right OR right - left
        The selected index
        -fade out --> delete
        Higher than the selected index
        -Move STRAIGHT UP and replace the selected index.*/
        int delete_idx = toListIdx(view_id);
        int listSize = listSchedule.size();
        for (int idx = 0; idx < listSize; idx++){
            boolean isLeft = getLeftVisbility(idx);
            if (idx < delete_idx){ //circle to side way
                listSchedule.get(idx).circleImage.animate().x(coordinateInformation.circleX[isLeft ? 1 : 0]).setDuration(DRAGDROP_ANIMATION_DURATION);
                listSchedule.get(idx).view.animate().alpha(0).setDuration(DRAGDROP_ANIMATION_DURATION/2);
            }
            else if (idx == delete_idx){
                RemoveAnimation removeAnimation = new RemoveAnimation(view_id);
                removeAnimation.setData(false);
                listSchedule.get(idx).view.animate().alpha(0).setDuration(DRAGDROP_ANIMATION_DURATION/2).setListener(removeAnimation);
                listSchedule.get(idx).circleImage.animate().alpha(0).setDuration(DRAGDROP_ANIMATION_DURATION/2);
            }
            else{
                listSchedule.get(idx).view.animate().yBy(-coordinateInformation.layout_height).setDuration(DRAGDROP_ANIMATION_DURATION);
                if (idx < listSchedule.size() - 1) listSchedule.get(idx).circleImage.animate().yBy(-coordinateInformation.layout_height).setDuration(DRAGDROP_ANIMATION_DURATION);
                //bottom empty circle also animates
            }

            if (isLeft)
                listSchedule.get(idx).view.findViewById(R.id.btn_delete_schedule_left).setVisibility(View.GONE);
            else
                listSchedule.get(idx).view.findViewById(R.id.btn_delete_schedule_right).setVisibility(View.GONE);

            //optiotnal
            if (idx < listSchedule.size() - 1){
                listSchedule.get(idx).circleImage.bringToFront();
            }
        }
        isEditing = false;
    }


    @Override
    public void moveSchedule(int idxA, int idxB) {
        int low, high;
        boolean isMoveDown;
        TextsFadeOutAnimationListener textsFadeOutAnimationListener = new TextsFadeOutAnimationListener();
        Toast.makeText(appContext, "Start: " + (startID) + ", " + "End: " + (endID), Toast.LENGTH_SHORT).show();
        if (startID == - 1 || idxA == idxB) {
            return;
        }
        if (startID < endID){
            low = startID;
            high = endID;
            isMoveDown = false;
        }
        else{
            low = endID;
            high = startID;
            isMoveDown = true;
        }

        Boolean isLeft;
        textsFadeOutAnimationListener.setData(idxA, idxB, low, high, isMoveDown);
        for (int i = 0; i < listSchedule.size() - 1; i++)
            listSchedule.get(i).view.setOnDragListener(null); //ignore drag drop during animation

        for (int idx = low; idx <= high; idx++){
            /*Start of drawing views*/
            listSchedule.get(idx).circleImage.bringToFront();


            isLeft = getLeftVisbility(idx);
            View referenceView = listSchedule.get(idx).view;

            //setting view animation
            if (idx == low)
                referenceView.animate().setListener(textsFadeOutAnimationListener);
            referenceView.animate().setDuration(DRAGDROP_ANIMATION_DURATION/2).alpha(0);

            if ((idx == high && isMoveDown) || (idx == low && !isMoveDown)) {
                if (high - low < 3) {
                    if (isMoveDown) {
                        isLeft = getLeftVisbility(low);
                        listSchedule.get(idx).circleImage.animate().yBy(-coordinateInformation.layout_height * (high - low)).
                                x(coordinateInformation.circleX[isLeft ? 0 : 1]).setDuration(DRAGDROP_ANIMATION_DURATION);
                    } else {
                        isLeft = getLeftVisbility(high);
                        listSchedule.get(idx).circleImage.animate().yBy(+(coordinateInformation.layout_height * (high - low)))
                                .x(coordinateInformation.circleX[isLeft ? 0 : 1]).setDuration(DRAGDROP_ANIMATION_DURATION);
                    }
                }
                else{
                    queueCircleAnimated.clear();
                    queueCircleAnimated.add(listSchedule.get(idx).circleImage);
                    listSchedule.get(idx).circleImage.animate().alpha(0).setDuration(DRAGDROP_ANIMATION_DURATION/2);
                }
                continue;
            }

            //setting circle animations
            listSchedule.get(idx).circleImage.animate().setDuration(DRAGDROP_ANIMATION_DURATION);
            if (isMoveDown){ //if moving down
                listSchedule.get(idx).circleImage.animate().x(coordinateInformation.circleX[isLeft ? 1: 0]).yBy(+coordinateInformation.layout_height);
            }
            else{
                listSchedule.get(idx).circleImage.animate().x(coordinateInformation.circleX[isLeft ? 1: 0]).yBy(-coordinateInformation.layout_height);
            }
            //end of setting destination coordinates//
            listSchedule.get(idx).circleImage.setOnClickListener(null);
        }

        for (int idx = 0; idx < listSchedule.size() - 1; idx++){
            View referenceView = listSchedule.get(idx).view;
            if (getLeftVisbility(idx))
                referenceView.findViewById(R.id.btn_delete_schedule_left).setVisibility(View.GONE);
            else
                referenceView.findViewById(R.id.btn_delete_schedule_right).setVisibility(View.GONE);
        }
        //super.moveSchedule(idxA, idxB);
    }

    class TextsFadeOutAnimationListener implements Animator.AnimatorListener{
        int idxStart, idxEnd, low, high;
        boolean isMoveDown;
        public void setData(int idxStart, int idxEnd, int low, int high, boolean isMoveDown){
            this.idxStart = idxStart;
            this.idxEnd = idxEnd;
            this.low = low;
            this.high = high;
            this.isMoveDown = isMoveDown;
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }
        @Override
        public void onAnimationEnd(Animator animator) {
            //assumed that editing is on.
            TextsFadeInAnimationListener textsFadeInAnimationListener = new TextsFadeInAnimationListener();
            int idx;
            //if same, the animation is implementing removal.
            ScheduleServiceAnimated.super.moveSchedule(idxStart, idxEnd);

            if (!queueCircleAnimated.isEmpty()){ //if not empty, greater than four circles are being moved.
                View circleImage = queueCircleAnimated.remove();
                idx = toListIdx((int)circleImage.getTag());
                circleImage.setX(coordinateInformation.circleX[getLeftVisbility(idx) ? 0 : 1]);
                circleImage.setY(coordinateInformation.layout_height * idx + coordinateInformation.first_margin);
                circleImage.animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION/2);
            }

            for (idx = low; idx <= high; idx++) {
                View referenceView = listSchedule.get(idx).view;
                if (getLeftVisbility(idx)) {
                    //referenceView.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
                    referenceView.animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION/2);
                    if (idx == low)
                        referenceView.animate().setListener(textsFadeInAnimationListener);
                } else {
                    //referenceView.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE); //can be ignored
                    referenceView.animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION/2);
                    if (idx == low)
                        referenceView.animate().setListener(textsFadeInAnimationListener);
                }
            }

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }
        @Override
        public void onAnimationRepeat(Animator animator) {

        }

    }

    class TextsFadeInAnimationListener implements Animator.AnimatorListener{

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) { //this is the end of animation
            //remove animations from layout
            for (int idx = 0; idx < listSchedule.size() - 1; idx++) {
                if (getLeftVisbility(idx)) {
                    //listSchedule.get(idx).view.findViewById(R.id.circleimageview_left).setVisibility(View.VISIBLE);
                    listSchedule.get(idx).view.animate().setListener(null);
                }
                else {
                    //listSchedule.get(idx).view.findViewById(R.id.circleimageview_right).setVisibility(View.VISIBLE);
                    listSchedule.get(idx).view.animate().setListener(null);
                }
                listSchedule.get(idx).view.setOnDragListener(scheduleDragListener); //reactivate drag drop listener
                listSchedule.get(idx).circleImage.setOnClickListener(clickEditSchedule);
            }
            isEditing = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    class RemoveAnimation implements Animator.AnimatorListener{
        final int view_id;
        private boolean isFadeIn = false;
        public RemoveAnimation(int selectedData) {
            this.view_id = selectedData;
        }

        public void setData(boolean isFadeIn){
            this.isFadeIn = isFadeIn;
        }
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            //after the animation is finished perform the removal
            int idxDelete = toListIdx(view_id);
            if (!isFadeIn) {
                RemoveAnimation removeAnimation = new RemoveAnimation(view_id);
                removeAnimation.setData(true);
                if (idxDelete == 0)
                    listSchedule.get(idxDelete).view.animate().alpha(0).setListener(removeAnimation);
                else listSchedule.get(0).view.animate().alpha(1).setListener(removeAnimation);

                for (int i = 0; i < idxDelete; i++) {
                    listSchedule.get(i).view.animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION / 2);
                    if (getLeftVisbility(i)){
                        listSchedule.get(i).view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
                        listSchedule.get(i).view.findViewById(R.id.contents_left).setVisibility(View.INVISIBLE);
                        listSchedule.get(i).view.findViewById(R.id.title_right).setVisibility(View.VISIBLE);
                        listSchedule.get(i).view.findViewById(R.id.contents_right).setVisibility(View.VISIBLE);
                    }
                    else{
                        listSchedule.get(i).view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
                        listSchedule.get(i).view.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
                        listSchedule.get(i).view.findViewById(R.id.title_left).setVisibility(View.VISIBLE);
                        listSchedule.get(i).view.findViewById(R.id.contents_left).setVisibility(View.VISIBLE);
                    }
                }
            }
            else {
                listSchedule.get(0).view.animate().setListener(null);
                ScheduleServiceAnimated.super.removeSchedule(view_id);
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}
