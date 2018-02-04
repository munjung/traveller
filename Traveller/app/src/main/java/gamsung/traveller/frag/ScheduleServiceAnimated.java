package gamsung.traveller.frag;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Circle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.LogRecord;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;

/**
 * Created by JKPark on 2018-01-31.
 */
class ScheduleLayout{
    CircleImageView circleImageView;
    TextView textTitle, textContent;
}
class CoordViews{
    int circleX[] = new int[2];
    int circleY[] = new int[2];
    int titleX[] = new int[2];
    int titleY[] = new int[2];
    int contentsX[] = new int[2];
    int contentsY[] = new int[2];

    boolean isLeft; //optional
}

public class ScheduleServiceAnimated extends ScheduleService {
    long DRAGDROP_ANIMATION_DURATION;
    ArrayList<CoordViews> coordViewsList = new ArrayList<>(); //뷰의 위치 저장
    Queue<View> queueView= new LinkedList<>();
    Queue<Integer> queueIdxHistory = new LinkedList<>();

    public ScheduleServiceAnimated(ViewGroup rootView, int layoutSingle, NestedScrollView scrollView, LinearLayout layoutBase, Context appContext, boolean isDragDrop) {
        super(rootView, layoutSingle, scrollView, layoutBase, appContext, isDragDrop);
        super.longClickedCircle = this.longClickListener;
        super.dragListener = this.scheduleDragListener;
        DRAGDROP_ANIMATION_DURATION = 400;
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
    View.OnDragListener scheduleDragListener = new View.OnDragListener(){
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            NestedScrollView scrollView;
            int action = dragEvent.getAction();
            View localParent = (View)((View)dragEvent.getLocalState()).getParent();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("DragDrop", "Started entered");
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //where animation happens
                    //dragEntered(view, Integer.parseInt(item));

                    //queueIdxHistory.add(toListIdx(Integer.parseInt(view.getTag().toString())));
                    //Toast.makeText(appContext, toListIdx(Integer.parseInt(view.getTag().toString())) + ".", Toast.LENGTH_SHORT).show();
                    queueIdxHistory.add(toListIdx(Integer.parseInt(view.getTag().toString())));
                    view.setVisibility(View.INVISIBLE);
                    dragEntered(view);
                    Log.d("Entered", view.getTag().toString());
                    return true;
                case DragEvent.ACTION_DRAG_LOCATION:
                    /*
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
                        scrollView.smoothScrollBy(0, -30);*/
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    //cancel animation if necessary
                    view.setVisibility(View.VISIBLE);
                    Log.d("DragDrop", "Exited entered");
                    return true;
                case DragEvent.ACTION_DROP:
                    //switch of data happens
                    view.setVisibility(View.VISIBLE);
                    ClipData.Item item = dragEvent.getClipData().getItemAt(0);
                    String dragData =item.getText().toString();

                    //swapSchedules(Integer.parseInt(dragData) - 1, Integer.parseInt(view.getTag().toString()) - 1);
                    Log.d("DragDrop", "Ended entered");
                    for (int i = 0; i < listSchedule.size(); i++){
                        listSchedule.get(i).view.findViewById(R.id.circleimageview_left).clearAnimation();
                        listSchedule.get(i).view.findViewById(R.id.circleimageview_right).clearAnimation();
                    }
                    moveSchedule(Integer.parseInt(dragData), (int)view.getTag());
                    Log.d("selected data index: ", dragData);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    //cancel animaton if necessary
                    queueIdxHistory.clear();
                    return true;

                default:
                    break;
            }
            return false;
        }
    };

    private void dragEntered(View view){ //view is the drop zone, item holds the info of ID
        int idxView, idxDrag; //move idxView to idxDrag location
        //draw ==> drop (queue delete)
        //moving 0 to 1
        CoordViews coordItems[] = new CoordViews[2];

        idxView = toListIdx((int)view.getTag()); //index of drop zone view; idxView is being moved to idxDrag

        if (queueIdxHistory.peek() == idxView) {
            //no animation necessary to be made; or at the very beginning where the queue is empty.
            return;
        }
        idxDrag = queueIdxHistory.remove(); //the previous location where the cursor was

        if (idxView < idxDrag){
            coordItems[1] = coordViewsList.get(idxView + 1); //move down
        }
        else
            coordItems[1] = coordViewsList.get(idxView - 1); //move up
        coordItems[0] = coordViewsList.get(idxView);

        CircleImageView circleCopy = new CircleImageView(appContext);

        Boolean isLeft = getLeftVisbility(idxView);

        if (queueView.size() > 0){ //if theres an animation in motion, set delay
            circleCopy.animate().setStartDelay((DRAGDROP_ANIMATION_DURATION + 150) * (queueView.size() - 1));
        }
        circleCopy.setX(coordItems[0].circleX[isLeft ? 0 : 1]);
        circleCopy.setY(coordItems[0].circleY[isLeft ? 0 : 1]);
        circleCopy.setBorderWidth(BORDER_WIDTH);
        circleCopy.setBorderColor(Color.BLACK);
        circleCopy.setImageResource(R.color.cardview_light_background);
        LinearLayout.LayoutParams circleSize = new LinearLayout.LayoutParams((int)toDp(IMAGE_SIZE), (int)toDp(IMAGE_SIZE));
        circleCopy.setLayoutParams(circleSize);
        circleCopy.setVisibility(View.INVISIBLE);

        layoutBase.addView(circleCopy);
        queueView.add(circleCopy);

        circleCopy.animate().setListener(animatorListener);
        circleCopy.animate().setDuration(DRAGDROP_ANIMATION_DURATION);

        circleCopy.animate().x(coordItems[1].circleX[isLeft? 1 : 0]).y(coordItems[1].circleY[isLeft? 1 : 0]);
        circleCopy.setVisibility(View.VISIBLE);
        //Toast.makeText(appContext, "IdxView: " + idxView + ", idxDrag: " + idxDrag + ", idxDragonLayout: " + toListIdx(idxDrag), Toast.LENGTH_SHORT).show();
        //when idxDragonLayout is smaller move to idxView - 1, when idxView is greater move to idxView + 1
    }


    class AnimatedMovingTask extends AsyncTask<View, Void, Void>{
        CoordViews coordViews;
        public AnimatedMovingTask(CoordViews coordViews) {
            this.coordViews = coordViews;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(View... views) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Toast.makeText(appContext, "Animation fin.", Toast.LENGTH_SHORT).show();
            super.onProgressUpdate(values);

        }

    }
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

            Animation aniShake = AnimationUtils.loadAnimation(appContext, R.anim.shake);
            for (ListSchedule listViews: listSchedule){
                CircleImageView circleImageView;
                if (getLeftVisbility(toListIdx(listViews.view_ID)))
                    circleImageView = listViews.view.findViewById(R.id.circleimageview_left);
                else
                    circleImageView = listViews.view.findViewById(R.id.circleimageview_right);

                circleImageView.startAnimation(aniShake);
                //listViews.view.startAnimation(aniShake);
            }
            return true;
        }
    };

    Animator.AnimatorListener animatorListener =  new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animator) {
            //queueView.peek().setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            layoutBase.removeView(queueView.remove());
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }
        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };
}
