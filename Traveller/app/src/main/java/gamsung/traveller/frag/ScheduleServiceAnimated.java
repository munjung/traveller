package gamsung.traveller.frag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;



class CoordViews{
    int circleX[] = new int[2];
    int circleY[] = new int[2];
    int titleX[] = new int[2];
    int titleY[] = new int[2];
    int contentsX[] = new int[2];
    int contentsY[] = new int[2];
}

/**
 * Created by JKPark on 2018-01-31.
 */
public class ScheduleServiceAnimated extends ScheduleService {
    long DRAGDROP_ANIMATION_DURATION, DRAGDROP_WAITING_TIME;
    ArrayList<CoordViews> coordViewsList = new ArrayList<>(); //뷰의 위치 저장
    Queue<View> queueCircleAnimated= new LinkedList<>();
    Queue<Integer> queueIdxHistory = new LinkedList<>();
    private int startID, endID;
    private boolean isScheduleMoved;
    private Handler visHandler = new Handler();
    private Handler tHandler = new Handler();
    private RunnableVisOff runnableVisOff = new RunnableVisOff();


    public ScheduleServiceAnimated(ViewGroup rootView, int layoutSingle, NestedScrollView scrollView, LinearLayout layoutBase, Context appContext, boolean isDragDrop) {
        super(rootView, layoutSingle, scrollView, layoutBase, appContext, isDragDrop);
        super.longClickedCircle = this.longClickListener;
        super.dragListener = this.scheduleDragListener;
        DRAGDROP_ANIMATION_DURATION = 2000;
        DRAGDROP_WAITING_TIME = 1000;
        isScheduleMoved = false;
    }

    @Override
    public void createNewSchedule(@Nullable View.OnClickListener clickCreateNew, @Nullable View.OnClickListener clickEdit) {
        //animation before creating of new schedule


        while (listSchedule.size() > coordViewsList.size()) {
            View viewReference = listSchedule.get(coordViewsList.size()).view;
            if (viewReference == null) //if null throw exception
                return;
            CoordViews coordViews = new CoordViews();

            coordViews.circleX[0] = getRelativeLeft(viewReference.findViewById(R.id.circleimageview_left), layoutBase);
            coordViews.circleX[1] = getRelativeLeft(viewReference.findViewById(R.id.circleimageview_right), layoutBase);
            coordViews.contentsX[0] = getRelativeLeft(viewReference.findViewById(R.id.contents_left), layoutBase);
            coordViews.contentsX[1] = getRelativeLeft(viewReference.findViewById(R.id.contents_right), layoutBase);
            coordViews.titleX[0] = getRelativeLeft(viewReference.findViewById(R.id.title_left), layoutBase);
            coordViews.titleX[1] = getRelativeLeft(viewReference.findViewById(R.id.title_right), layoutBase);

            coordViews.circleY[0] = getRelativeTop(viewReference.findViewById(R.id.circleimageview_left), layoutBase);
            coordViews.circleY[1] = getRelativeTop(viewReference.findViewById(R.id.circleimageview_right), layoutBase);
            coordViews.contentsY[0] = getRelativeTop(viewReference.findViewById(R.id.contents_left), layoutBase);
            coordViews.contentsY[1] = getRelativeTop(viewReference.findViewById(R.id.contents_right), layoutBase);
            coordViews.titleY[0] = getRelativeTop(viewReference.findViewById(R.id.title_left), layoutBase);
            coordViews.titleY[1] = getRelativeTop(viewReference.findViewById(R.id.title_right), layoutBase);

            coordViewsList.add(coordViews);
        }
        super.createNewSchedule(clickCreateNew, clickEdit);
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
        textsFadeOutAnimationListener.setData(idxA, idxB, low, high);
        for (int idx = low; idx <= high; idx++){
            /*Start of drawing views*/
            CircleImageView circleCopy = new CircleImageView(appContext);
            isLeft = getLeftVisbility(idx);
            View referenceView = listSchedule.get(idx).view;

            //start of copying circle image//
            circleCopy.setX(coordViewsList.get(idx).circleX[isLeft ? 0 : 1]);
            circleCopy.setY(coordViewsList.get(idx).circleY[isLeft ? 0 : 1]);
            circleCopy.setBorderWidth(BORDER_WIDTH);
            circleCopy.setBorderColor(Color.BLACK);
            circleCopy.setImageResource(R.color.colorPrimaryDark);
            LinearLayout.LayoutParams circleSize = new LinearLayout.LayoutParams((int)toDp(IMAGE_SIZE), (int)toDp(IMAGE_SIZE));
            circleCopy.setLayoutParams(circleSize);
            circleCopy.setVisibility(View.VISIBLE);
            //end of copying circle image//
            layoutBase.addView(circleCopy);
            queueCircleAnimated.add(circleCopy);
            /*end of drawing views*/

            //start of setting destination coordinates//
            //setting text animations
            TextView textTitle, textContents;
            if (isLeft){
                textTitle = referenceView.findViewById(R.id.title_left);
                textContents = referenceView.findViewById(R.id.contents_left);
            }
            else{
                textTitle = referenceView.findViewById(R.id.title_right);
                textContents = referenceView.findViewById(R.id.contents_right);
            }
            if (idx == low)
                textTitle.animate().setListener(textsFadeOutAnimationListener);
            textTitle.animate().setDuration(DRAGDROP_ANIMATION_DURATION/2).alpha(0);
            textContents.animate().setDuration(DRAGDROP_ANIMATION_DURATION/2).alpha(0);

            //end of setting text animations

            //setting circle animations
            circleCopy.animate().setListener(circleAnimationListener).setDuration(DRAGDROP_ANIMATION_DURATION);
            if (isMoveDown){ //if moving down
                if (idx != high) {
                    circleCopy.animate().y(coordViewsList.get(idx + 1).circleY[isLeft ? 1 : 0]).x(coordViewsList.get(idx + 1).circleX[isLeft ? 1 : 0]);
                }
                else { //where the target is being created + texts to be added
                    isLeft = getLeftVisbility(low);
                    circleCopy.setY(coordViewsList.get(low).circleY[isLeft ? 0 : 1]);
                    circleCopy.setX(coordViewsList.get(low).circleX[isLeft ? 0 : 1]);
                    circleCopy.animate().alpha(0).setDuration(1);
                    circleCopy.animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION);
                }
            }
            else{
                if (idx != low) {
                    circleCopy.animate().y(coordViewsList.get(idx - 1).circleY[isLeft ? 1 : 0]).x(coordViewsList.get(idx - 1).circleX[isLeft ? 1 : 0]);
                }
                else { //where the target is being created
                    isLeft = getLeftVisbility(high);
                    circleCopy.setY(coordViewsList.get(high).circleY[isLeft ? 0 : 1]);
                    circleCopy.setX(coordViewsList.get(high).circleX[isLeft ? 0 : 1]);
                    circleCopy.animate().alpha(0).setDuration(1);
                    circleCopy.animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION);
                }
            }
            //end of setting destination coordinates//

        }
        runnableVisOff.setData(low, high);

        //super.moveSchedule(idxA, idxB);
    }
    private Runnable runnableTimeCounter = new Runnable() {
        @Override
        public void run() {
            if (startID == endID) //no necessary animation needs to be made.
                return;

            moveSchedule(startID, endID);
            isScheduleMoved = true;
            //Toast.makeText(appContext, "Start: " + (startID) + ", " + "End: " + (endID), Toast.LENGTH_SHORT).show();
            startID = endID;
        }
    };

    class RunnableVisOff implements Runnable{
        private int start,  end;
        public void setData(int start, int end){
            this.start = start;
            this.end = end;
        }
        @Override
        public void run() {
            for (int idx = start; idx <= end; idx++) {
                listSchedule.get(idx).view.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
                listSchedule.get(idx).view.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
            }
        }
    }

    class TextsFadeOutAnimationListener implements Animator.AnimatorListener{
        int idxStart, idxEnd, low, high;
        public void setData(int idxStart, int idxEnd, int low, int high){
            this.idxStart = idxStart;
            this.idxEnd = idxEnd;
            this.low = low;
            this.high = high;
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {

            TextsFadeInAnimationListener textsFadeInAnimationListener = new TextsFadeInAnimationListener();

            ScheduleServiceAnimated.super.moveSchedule(idxStart, idxEnd);

            for (int idx = low; idx <= high; idx++) {
                View referenceView = listSchedule.get(idx).view;
                if (getLeftVisbility(idx)) {
                    referenceView.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
                    referenceView.findViewById(R.id.title_left).setAlpha(0);
                    referenceView.findViewById(R.id.contents_left).setAlpha(0);
                    referenceView.findViewById(R.id.title_left).animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION / 2);
                    referenceView.findViewById(R.id.contents_left).animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION / 2);
                    if (idx == low)
                        referenceView.findViewById(R.id.title_left).animate().setListener(textsFadeInAnimationListener);
                } else {
                    referenceView.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
                    referenceView.findViewById(R.id.title_right).setAlpha(0);
                    referenceView.findViewById(R.id.contents_right).setAlpha(0);
                    referenceView.findViewById(R.id.title_right).animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION / 2);
                    referenceView.findViewById(R.id.contents_right).animate().alpha(1).setDuration(DRAGDROP_ANIMATION_DURATION / 2);
                    if (idx == low)
                        referenceView.findViewById(R.id.title_right).animate().setListener(textsFadeInAnimationListener);
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
        public void onAnimationEnd(Animator animator) {
            //remove animations from layout
            for (int idx = 0; idx < listSchedule.size() - 1; idx++) {
                if (getLeftVisbility(idx)) {
                    listSchedule.get(idx).view.findViewById(R.id.circleimageview_left).setVisibility(View.VISIBLE);
                    listSchedule.get(idx).view.findViewById(R.id.title_left).animate().setListener(null);
                    listSchedule.get(idx).view.findViewById(R.id.contents_left).animate().setListener(null);
                }
                else {
                    listSchedule.get(idx).view.findViewById(R.id.circleimageview_right).setVisibility(View.VISIBLE);
                    listSchedule.get(idx).view.findViewById(R.id.title_right).animate().setListener(null);
                    listSchedule.get(idx).view.findViewById(R.id.contents_right).animate().setListener(null);
                }
            }
            while(queueCircleAnimated.size() > 0){
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

    Animator.AnimatorListener circleAnimationListener =  new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animator) {
            for (View view : queueCircleAnimated){
                view.setVisibility(View.VISIBLE);
            }
            visHandler.post(runnableVisOff);
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
                case DragEvent.ACTION_DRAG_ENTERED:
                    if (startID == -1) {
                        startID = toListIdx((int)view.getTag());
                    }
                    endID = toListIdx((int)view.getTag());
                    queueIdxHistory.add(toListIdx(Integer.parseInt(view.getTag().toString())));
                    view.setBackgroundColor(Color.GRAY);
                    tHandler.postDelayed(runnableTimeCounter, DRAGDROP_WAITING_TIME); //animation takes a place after the waiting time.

                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    tHandler.removeCallbacksAndMessages(null); //cancel animation + messages when moved to the next

                    view.setBackgroundColor(Color.WHITE);
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
                    //if schedule is already moved, animation is not necessary.
                    if (!isScheduleMoved)
                        moveSchedule(toListIdx(Integer.parseInt(dragData)), toListIdx((int)view.getTag()));

                    view.setBackgroundColor(Color.WHITE);
                    Log.d("selected data index: ", dragData);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    //cancel animaton if necessary
                    endID = toListIdx((int)view.getTag());
                    queueIdxHistory.clear();
                    tHandler.removeCallbacksAndMessages(null);
                    isScheduleMoved = false;
                    return true;

                default:
                    break;
            }
            return false;
        }
    };
    View.OnLongClickListener longClickListener = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View view) {
            View parentView = (View) view.getParent();
            ClipData.Item item = new ClipData.Item((CharSequence) parentView.getTag().toString());
            String[] mimeType = {ClipDescription.MIMETYPE_TEXT_PLAIN};

            ClipData data = new ClipData(parentView.getTag().toString(), mimeType, item); //pass on the tag of the selected layout

            view.setBackgroundColor(Color.TRANSPARENT);
            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, dragShadowBuilder, view, 0);

/*
            Animation aniShake = AnimationUtils.loadAnimation(appContext, R.anim.shake);

            for (int idx = 0; idx < listSchedule.size() - 1; idx++){
                CircleImageView circleImageView;
                if (getLeftVisbility(idx))
                    circleImageView = listSchedule.get(idx).view.findViewById(R.id.circleimageview_left);
                else
                    circleImageView = listSchedule.get(idx).view.findViewById(R.id.circleimageview_right);
                circleImageView.startAnimation(aniShake);
            }*/

            return true;
        }
    };
}
