package gamsung.traveller.frag;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;


class CoordViews{
    int circleX[] = new int[2];
    int circleY[] = new int[2];
}

/**
 * Created by JKPark on 2018-01-31.
 */
public class ScheduleServiceAnimated extends ScheduleService {
    long DRAGDROP_ANIMATION_DURATION, DRAGDROP_WAITING_TIME;
    public View.OnClickListener clickEditSchedule, clickRemoveSchedule;

    private ArrayList<CoordViews> coordViewsList = new ArrayList<>(); //뷰의 위치 저장
    private Queue<View> queueCircleAnimated= new LinkedList<>();
    private int startID, endID;
    private boolean isScheduleMoved;
    private Handler tHandler = new Handler();
    private int single_height;

    public ScheduleServiceAnimated(ViewGroup rootView, int layoutSingle, NestedScrollView scrollView, RelativeLayout layoutBase, Context appContext, boolean isDragDrop) {
        super(rootView, layoutSingle, scrollView, layoutBase, appContext, isDragDrop);
        super.longClickedCircle = this.longClickStartEditing;
        super.dragListener = this.scheduleDragListener;
        clickEditSchedule = null;
        DRAGDROP_ANIMATION_DURATION = 3000;
        DRAGDROP_WAITING_TIME = 1000;
        isScheduleMoved = false;
        single_height = 0;
    }

    @Override
    public void createNewSchedule(@Nullable View.OnClickListener clickCreateNew, @Nullable View.OnClickListener clickEdit) {
        //animation before creating of new schedule
        View viewReference;
        while (listSchedule.size() > coordViewsList.size()) {
             viewReference = listSchedule.get(coordViewsList.size()).view;
            if (viewReference == null) //if null throw exception
                return;
            if (single_height == 0)
                single_height = viewReference.getHeight();
            CoordViews coordViews = new CoordViews();

            coordViews.circleX[0] = getRelativeLeft(viewReference.findViewById(R.id.circleimageview_left), layoutBase);
            coordViews.circleX[1] = getRelativeLeft(viewReference.findViewById(R.id.circleimageview_right), layoutBase);
            coordViews.circleY[0] = getRelativeTop(viewReference.findViewById(R.id.circleimageview_left), layoutBase);
            coordViews.circleY[1] = getRelativeTop(viewReference.findViewById(R.id.circleimageview_right), layoutBase);

            coordViewsList.add(coordViews);
        }

        super.createNewSchedule(clickCreateNew, clickEdit);
    }

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
                    tHandler.removeCallbacksAndMessages(null); //cancel animation + messages when moved to the next

                    view.setBackgroundColor(Color.TRANSPARENT);
                    //view.setVisibility(View.VISIBLE);
                    Log.d("DragDrop", "Exited entered");
                    return true;
                case DragEvent.ACTION_DROP:
                    //switch of data happens
                    tHandler.removeCallbacksAndMessages(null);

                    //view.setVisibility(View.VISIBLE);
                    ClipData.Item item = dragEvent.getClipData().getItemAt(0);
                    String dragData =item.getText().toString();

                    Log.d("DragDrop", "Ended entered");
                    for (int i = 0; i < listSchedule.size(); i++){
                        listSchedule.get(i).view.findViewById(R.id.circleimageview_left).clearAnimation();
                        listSchedule.get(i).view.findViewById(R.id.circleimageview_right).clearAnimation();
                    }
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
                    view.setBackgroundColor(Color.GRAY);
                    tHandler.postDelayed(runnableTimeCounter, DRAGDROP_WAITING_TIME); //animation takes a place after the waiting time.
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    //cancel animaton if necessary
                    endID = toListIdx((int)view.getTag());
                    //tHandler.removeCallbacksAndMessages(null);
                    isScheduleMoved = false;
                    return true;

                default:
                    break;
            }
            return false;
        }
    };


    View.OnLongClickListener longClickStartEditing = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View view) { //Start Editing
            for (int idx = 0; idx < listSchedule.size() - 1; idx++){ //show delete buttons
                View referenceView = listSchedule.get(idx).view;
                if (getLeftVisbility(idx)){
                    referenceView.findViewById(R.id.btn_delete_schedule_left).setVisibility(View.VISIBLE);
                    referenceView.findViewById(R.id.circleimageview_left).setOnLongClickListener(clickStartDragging);
                }
                else {
                    referenceView.findViewById(R.id.btn_delete_schedule_right).setVisibility(View.VISIBLE);
                    referenceView.findViewById(R.id.circleimageview_right).setOnLongClickListener(clickStartDragging);
                }
            }
            /*start shaking
            Animation aniShake = AnimationUtils.loadAnimation(appContext, R.anim.shake);
            for (int idx = 0; idx < listSchedule.size() - 1; idx++){
                CircleImageView circleImageView;
                if (getLeftVisbility(idx))
                    circleImageView = listSchedule.get(idx).view.findViewById(R.id.circleimageview_left);
                else
                    circleImageView = listSchedule.get(idx).view.findViewById(R.id.circleimageview_right);
                circleImageView.startAnimation(aniShake);
                circleImageView.animate().
            }
*/
            return true;
        }
    };

    private CircleImageView createCircleImage(int idxOriginal){
        Boolean isLeft = getLeftVisbility(idxOriginal);
        CircleImageView circleCopy = new CircleImageView(appContext);
        circleCopy.setX(coordViewsList.get(idxOriginal).circleX[isLeft ? 0 : 1]);;
        circleCopy.setY(coordViewsList.get(idxOriginal).circleY[isLeft ? 0 : 1]);
        circleCopy.setBorderWidth(BORDER_WIDTH);
        circleCopy.setBorderColor(Color.BLACK);
        circleCopy.setImageResource(R.color.colorPrimaryDark);
        RelativeLayout.LayoutParams circleSize = new RelativeLayout.LayoutParams((int)toDp(IMAGE_SIZE), (int)toDp(IMAGE_SIZE));
        circleCopy.setLayoutParams(circleSize);

        return circleCopy;
    }
    int TEMP_ID;
    @Override
    public boolean removeSchedule(int view_id) {
        /*
        Lower than the selected index
        -Texts: fade out --> fade in
        -Circle Images: left -> right OR right - left
        The selected index
        -fade out --> delete
        Higher than the selected index
        -Move STRAIGHT UP and replace the selected index.
        */
        boolean isLeft;
        int total = listSchedule.size();
        TEMP_ID = view_id;
        int delete_idx = toListIdx(view_id);
        int height = listSchedule.get(0).view.getHeight();
        for (int i = 0; i < total; i++){
            View referenceView = listSchedule.get(i).view;
            isLeft = getLeftVisbility(i);
            if (i < delete_idx) {
                CircleImageView circleCopy = createCircleImage(i);
                layoutBase.addView(circleCopy);
                queueCircleAnimated.add(circleCopy);
                //setting circle animations
                circleCopy.animate().x(coordViewsList.get(i).circleX[isLeft ? 1 : 0]).setDuration(DRAGDROP_ANIMATION_DURATION);

                //setting text animations
                if (isLeft) {
                    referenceView.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
                } else {
                    referenceView.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
                }
                referenceView.animate().setDuration(DRAGDROP_ANIMATION_DURATION/2).alpha(0);

                if (i == 0){
                    TextsFadeOutAnimationListener fadeOut = new TextsFadeOutAnimationListener();
                    fadeOut.setData(0, 0, 0, delete_idx - 1, true);
                    referenceView.animate().setListener(fadeOut);
                }
            }
            else if (i == delete_idx){ //if the target
                FinishRemoveAnimation fin = new FinishRemoveAnimation(view_id);
                referenceView.animate().setDuration(DRAGDROP_ANIMATION_DURATION).setListener(fin).alpha(0);
            }
            else if (i > delete_idx){
                referenceView.animate().setDuration(DRAGDROP_ANIMATION_DURATION).yBy(-height);
            }
        }
        if (listSchedule.size() > 2)
            return true;
        else
            return false;
        //return super.removeSchedule(view_id);
    }

    View.OnLongClickListener clickStartDragging = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View view) {
            View parentView = (View) view.getParent();
            ClipData.Item item = new ClipData.Item((CharSequence) parentView.getTag().toString());
            String[] mimeType = {ClipDescription.MIMETYPE_TEXT_PLAIN};

            ClipData data = new ClipData(parentView.getTag().toString(), mimeType, item); //pass on the tag of the selected layout

            view.setBackgroundColor(Color.TRANSPARENT);

            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, dragShadowBuilder, view, 0);
            return true;
        }
    };

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

            isLeft = getLeftVisbility(idx);
            View referenceView = listSchedule.get(idx).view;

            //setting view animation
            if (idx == low)
                referenceView.animate().setListener(textsFadeOutAnimationListener);
            referenceView.animate().setDuration(DRAGDROP_ANIMATION_DURATION/2).alpha(0);

            if (isMoveDown && idx == high) continue; //no circle animation for the target that is being moved
            if (!isMoveDown && idx == low) continue; //no circle animation

            if (isLeft)
                referenceView.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
            else
                referenceView.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);


            CircleImageView circleCopy = createCircleImage(idx);
            layoutBase.addView(circleCopy);
            queueCircleAnimated.add(circleCopy);

            //setting circle animations
            circleCopy.animate().setDuration(DRAGDROP_ANIMATION_DURATION);
            if (isMoveDown){ //if moving down
                circleCopy.animate().y(coordViewsList.get(idx + 1).circleY[isLeft ? 1 : 0]).x(coordViewsList.get(idx + 1).circleX[isLeft ? 1 : 0]);
            }
            else{
                circleCopy.animate().y(coordViewsList.get(idx - 1).circleY[isLeft ? 1 : 0]).x(coordViewsList.get(idx - 1).circleX[isLeft ? 1 : 0]);
            }
            //end of setting destination coordinates//
        }


        for (int idx = 0; idx < listSchedule.size() - 1; idx++){
            View referenceView = listSchedule.get(idx).view;
            if (getLeftVisbility(idx))
                referenceView.findViewById(R.id.btn_delete_schedule_left).setVisibility(View.INVISIBLE);
            else
                referenceView.findViewById(R.id.btn_delete_schedule_right).setVisibility(View.INVISIBLE);
        }
        //super.moveSchedule(idxA, idxB);
    }

    private Runnable runnableTimeCounter = new Runnable() {
        @Override
        public void run() {
            if (startID == endID) //no necessary animation needs to be made.
                return;
            moveSchedule(startID, endID);
            isScheduleMoved = true;
            startID = endID;
        }
    };


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

            //if same, the animation is implementing removal.
            if (idxStart != idxEnd )ScheduleServiceAnimated.super.moveSchedule(idxStart, idxEnd);

            for (int idx = low; idx <= high; idx++) {
                View referenceView = listSchedule.get(idx).view;
                if (getLeftVisbility(idx)) {
                    referenceView.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
                    referenceView.animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION/2);
                    if (idx == low)
                        referenceView.animate().setListener(textsFadeInAnimationListener);
                } else {
                    referenceView.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE); //can be ignored
                    referenceView.animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION/2);
                    if (idx == low)
                        referenceView.animate().setListener(textsFadeInAnimationListener);
                }

                if (idxStart == idxEnd){
                    ScheduleServiceAnimated.super.setVisbility(referenceView, !getLeftVisbility(idx));
                    if (!getLeftVisbility(idx))
                        referenceView.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
                    else
                        referenceView.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
                    continue;
                }

                //Specific animation for changing orders
                if (isMoveDown && idx == low){
                    if (getLeftVisbility(idx))
                        referenceView.findViewById(R.id.circleimageview_left).setVisibility(View.VISIBLE);
                    else
                        referenceView.findViewById(R.id.circleimageview_right).setVisibility(View.VISIBLE);
                }
                else if (!isMoveDown && idx == high){
                    if (getLeftVisbility(idx))
                        referenceView.findViewById(R.id.circleimageview_left).setVisibility(View.VISIBLE);
                    else
                        referenceView.findViewById(R.id.circleimageview_right).setVisibility(View.VISIBLE);
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
                    listSchedule.get(idx).view.findViewById(R.id.circleimageview_left).setVisibility(View.VISIBLE);
                    listSchedule.get(idx).view.animate().setListener(null);
                }
                else {
                    listSchedule.get(idx).view.findViewById(R.id.circleimageview_right).setVisibility(View.VISIBLE);
                    listSchedule.get(idx).view.animate().setListener(null);
                }
                listSchedule.get(idx).view.setOnDragListener(scheduleDragListener); //reactivate drag drop listener
                listSchedule.get(idx).view.setOnClickListener(clickEditSchedule);
            }
            while(queueCircleAnimated.size() > 0){ //clearing queue after its use
                queueCircleAnimated.peek().clearAnimation();
                layoutBase.removeView(queueCircleAnimated.remove());
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    class FinishRemoveAnimation implements Animator.AnimatorListener{
        final int view_id;
        public FinishRemoveAnimation(int selectedData) {
            this.view_id = selectedData;
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}
