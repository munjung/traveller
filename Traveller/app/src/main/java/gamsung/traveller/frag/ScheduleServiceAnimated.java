package gamsung.traveller.frag;

import android.animation.Animator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
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
}

public class ScheduleServiceAnimated extends ScheduleService {
    ArrayList<CoordViews> coordViewsList = new ArrayList<>(); //뷰의 위치 저장
    Queue<View> queueView= new LinkedList<>();
    Queue<Integer> queueIdxHistory = new LinkedList<>();

    public ScheduleServiceAnimated(ViewGroup rootView, int layoutSingle, NestedScrollView scrollView, LinearLayout layoutBase, Context appContext, boolean isDragDrop) {
        super(rootView, layoutSingle, scrollView, layoutBase, appContext, isDragDrop);
        super.longClickedCircle = this.longClickListener;
        super.dragListener = this.scheduleDragListener;
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

        /*
        View circleImage, textContents, textTitle, layout;
        for (int i = 0; i < listSchedule.size(); i++){
            boolean isLeft = getLeftVisbility(i);
            layout = listSchedule.get(i).view;
            if (isLeft){
                circleImage = layout.findViewById(R.id.circleimageview_left);
                textTitle = layout.findViewById(R.id.title_left);
                textContents = layout.findViewById(R.id.contents_left);
            }else{
                circleImage = layout.findViewById(R.id.circleimageview_right);
                textTitle = layout.findViewById(R.id.title_right);
                textContents = layout.findViewById(R.id.contents_right);
            }
            circleImage.animate().x(coordCircleX[isLeft? 1 : 0]);
            textTitle.animate().x(coordTitleX[isLeft? 1 : 0]);
            textContents.animate().x(coordContentsX[isLeft? 1 : 0]);
        }*/
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
                    dragEntered(view, Integer.parseInt(localParent.getTag().toString()));
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
                    Log.d("DragDrop", "Exited entered");
                    return true;
                case DragEvent.ACTION_DROP:
                    //switch of data happens
                    ClipData.Item item = dragEvent.getClipData().getItemAt(0);
                    String dragData =item.getText().toString();
                    //swapSchedules(Integer.parseInt(dragData) - 1, Integer.parseInt(view.getTag().toString()) - 1);
                    moveSchedule(Integer.parseInt(dragData), (int)view.getTag());
                    Log.d("selected data index: ", dragData);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    //cancel animaton if necessary
                    Log.d("DragDrop", "Ended entered");
                    queueIdxHistory.clear();
                    return true;

                default:
                    break;
            }
            return false;
        }
    };

    private void dragEntered(View view, int itemIdx){ //view is the drop zone, item holds the info of ID
        int idxView, idxDrag; //move idxView to idxDrag location
        //draw ==> drop (queue delete)
        boolean isLeft;
        CoordViews coordDrag, coordView;
        idxView = toListIdx((int)view.getTag()); //index of drop zone view; idxView is being moved to idxDrag

        if (queueIdxHistory.peek() == idxView) {
            //no animation necessary to be made; or at the very beginning where the queue is empty.
            return;
        }
        idxDrag = queueIdxHistory.remove(); //the previous location where the cursor was

        if (idxView < idxDrag){
            coordDrag = coordViewsList.get(idxView + 1); //move down
        }
        else
            coordDrag = coordViewsList.get(idxView - 1); //move up

        coordView = coordViewsList.get(idxView);

        //draw on idxView and move to idxDrag
        isLeft = getLeftVisbility(idxView);

        CircleImageView copyCircleView = new CircleImageView(appContext);

        copyCircleView.setX(coordView.circleX[isLeft ? 0:1]);
        copyCircleView.setY(coordView.circleY[isLeft ? 0:1]);
        copyCircleView.setBorderWidth(super.BORDER_WIDTH);
        copyCircleView.setBorderColor(Color.BLACK);
        copyCircleView.setImageResource(R.color.cardview_light_background);
        LinearLayout.LayoutParams circleSize = new LinearLayout.LayoutParams((int)toDp(this.IMAGE_SIZE), (int)toDp(this.IMAGE_SIZE));
        copyCircleView.setLayoutParams(circleSize);

        layoutBase.addView(copyCircleView);
        queueView.add(copyCircleView);

        copyCircleView.animate().setListener(animatorListener);
        copyCircleView.animate().setDuration(500);


        copyCircleView.animate().x(coordDrag.circleX[isLeft ? 1 : 0]);
        copyCircleView.animate().y(coordDrag.circleY[isLeft ? 1 : 0]);

        Toast.makeText(appContext, "IdxView: " + idxView + ", idxDrag: " + idxDrag + ", idxDragonLayout: " + toListIdx(idxDrag), Toast.LENGTH_SHORT).show();
        //when idxDragonLayout is smaller move to idxView - 1, when idxView is greater move to idxView + 1
    }


    Animator.AnimatorListener animatorListener =  new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

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
            return true;
        }
    };
}
