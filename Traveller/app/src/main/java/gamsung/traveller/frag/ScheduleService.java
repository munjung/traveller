package gamsung.traveller.frag;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;

class ViewIdGenerator {
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    @SuppressLint("NewApi")
    public static int generateViewId() {

        if (Build.VERSION.SDK_INT < 17) {
            for (;;) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF)
                    newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }
}

public class ScheduleService {
    /**
     * Created by JKPark on 2018-01-30.
     */
    class CoordinateInformation{
        int circleX[] = new int[2];
        //int circleY[] = new int[2]; same increasement as layout height
        int layout_height, first_margin, end_margin;
    }
    class ListSchedule{
        View view, circleImage;
        int view_ID;
        public ListSchedule(View view, View circleImage, int view_ID) {
            this.circleImage = circleImage;
            this.view = view;
            this.view_ID = view_ID;
        }
    }
    int BORDER_WIDTH, IMAGE_SIZE, FIRST_CIRCLE_BIGGER;
    CoordinateInformation coordinateInformation = new CoordinateInformation();
    final boolean isDragDrop;
    boolean isEditing;
    private int EMPTY_CIRCLE_BIGGER;
    ArrayList<ListSchedule> listSchedule = new ArrayList<>();

    ViewGroup rootView;
    NestedScrollView scrollView;
    Context appContext;
    RelativeLayout layoutBase;
    View.OnLongClickListener longClickedCircle;
    View.OnDragListener dragListener;
    View.OnClickListener clickRemoveSelectedSchedule;
    int layoutSingle;


    public ScheduleService(ViewGroup rootView, @LayoutRes int layoutSingle, NestedScrollView scrollView,
                           RelativeLayout layoutBase, Context appContext, boolean isDragDrop) {
        this.rootView = rootView;
        this.scrollView = scrollView;
        this.layoutSingle = layoutSingle;
        this.layoutBase = layoutBase;
        this.appContext = appContext;
        this.isDragDrop= isDragDrop;
        //this.longClickedCircle = this.longClickedListener;
        this.dragListener = this.scheduleDragListener;
        BORDER_WIDTH = (int)toDp(5);
        IMAGE_SIZE = 100;
        FIRST_CIRCLE_BIGGER = 40;
        EMPTY_CIRCLE_BIGGER = 24;
        coordinateInformation.layout_height = 0; //initalized to zero
        isEditing = false;
    }


    public boolean initCoordInformation(View referenceView){
        //draw an invisible schedule to calculate layout height and coordinates of circles.
        if (coordinateInformation.layout_height == 0) {
            coordinateInformation.circleX[0] = getRelativeLeft(referenceView.findViewById(R.id.circleimageview_left), layoutBase);
            coordinateInformation.circleX[1] = getRelativeLeft(referenceView.findViewById(R.id.circleimageview_right), layoutBase);
            coordinateInformation.layout_height = referenceView.getHeight();
            coordinateInformation.first_margin = getRelativeTop(referenceView.findViewById(R.id.circleimageview_right), layoutBase);
            coordinateInformation.end_margin = coordinateInformation.layout_height - coordinateInformation.first_margin - (int)dipToPixels(appContext, IMAGE_SIZE);
            return true;
        }
        else return false;
    }

    private View addEmptySchedule(View.OnClickListener onClickListener){
        LayoutInflater layoutInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutSchedule = layoutInflater.inflate(layoutSingle, null);
        layoutSchedule.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        layoutSchedule.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
        layoutSchedule.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
        layoutSchedule.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
        layoutSchedule.findViewById(R.id.contents_left).setVisibility(View.INVISIBLE);
        layoutSchedule.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);

        CircleImageView circleImageView = layoutSchedule.findViewById(R.id.circleimageview_left);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams)circleImageView.getLayoutParams();
        layoutParams.width = (int)toDp(IMAGE_SIZE + EMPTY_CIRCLE_BIGGER);
        layoutParams.height = (int)toDp(IMAGE_SIZE + EMPTY_CIRCLE_BIGGER);
        circleImageView.setLayoutParams(layoutParams);

        //circleImageView.setBackgroundResource(R.drawable.schedule_dotted_border);
        circleImageView.setImageResource(R.drawable.ghost_btn_add_new_travel);
        circleImageView.setBorderWidth(0);
        circleImageView.setOnClickListener(onClickListener);

        int viewID = ViewIdGenerator.generateViewId();
        layoutSchedule.setTag(viewID);
        layoutSchedule.setId(viewID);
        layoutSchedule.setY(allocateViewCoordinateY(1));
        layoutSchedule.setX(0);
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) layoutSchedule.getLayoutParams();
        relativeParams.height = coordinateInformation.layout_height + 50;
        layoutSchedule.setLayoutParams(relativeParams);

        scrollView.smoothScrollBy(0, 30);
        layoutSchedule.setBackgroundColor(Color.TRANSPARENT);
        return layoutSchedule;
    }

    private View addFilledSchedule(boolean isLeft, int idx){

        LayoutInflater layoutInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutSchedule = layoutInflater.inflate(layoutSingle, null);
        layoutSchedule.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        if (isLeft) {
            layoutSchedule.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
        }
        else {
            layoutSchedule.findViewById(R.id.contents_left).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
            layoutSchedule.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
        }

        int viewID = ViewIdGenerator.generateViewId();
        ((TextView)layoutSchedule.findViewById(R.id.title_right)).setText("View ID: " + viewID);
        ((TextView)layoutSchedule.findViewById(R.id.title_left)).setText("View ID: " + viewID);
        (layoutSchedule.findViewById(R.id.btn_delete_schedule_right)).setOnClickListener(clickRemoveSelectedSchedule);
        (layoutSchedule.findViewById(R.id.btn_delete_schedule_left)).setOnClickListener(clickRemoveSelectedSchedule);

        layoutSchedule.setTag(viewID);
        layoutSchedule.setId(viewID);

        scrollView.smoothScrollBy(0, 30);
        layoutSchedule.setBackgroundColor(Color.TRANSPARENT);
        layoutSchedule.setOnDragListener(dragListener);

        draw_lines(idx);
        return layoutSchedule;
    }

    private CircleImageView createCircleImage(int coord_x, int coord_y){
        //static_temp
        CircleImageView circleCopy = new CircleImageView(appContext);
        circleCopy.setX(coord_x);
        circleCopy.setY(coord_y);
        circleCopy.setBorderWidth(BORDER_WIDTH);
        circleCopy.setBorderColor(Color.BLACK);

        //circleCopy.setImageResource(R.color.calendar_highlighted_day_bg);
        int tempSelected;
        tempSelected = R.drawable.test_1;
        switch (listSchedule.size() % 10){
            case 1:
                tempSelected = R.drawable.test_1;
                break;
            case 2:
                tempSelected = R.drawable.test_2;
                break;
            case 3:
                tempSelected = R.drawable.test_3;
                break;
            case 4:
                tempSelected = R.drawable.test_4;
                break;
            case 5:
                tempSelected = R.drawable.test_5;
                break;
            case 6:
                tempSelected = R.drawable.test_6;
                break;
            case 7:
                tempSelected = R.drawable.test_7;
                break;
            case 8:
                tempSelected = R.drawable.test_8;
                break;
            case 9:
                tempSelected = R.drawable.test_9;
                break;

        }

        circleCopy.setImageResource(tempSelected);

        //circleCopy.setImageResource(R.color.colorPrimaryDark);
        RelativeLayout.LayoutParams circleSize = new RelativeLayout.LayoutParams((int)toDp(IMAGE_SIZE), (int)toDp(IMAGE_SIZE));
        circleCopy.setLayoutParams(circleSize);
        circleCopy.setOnLongClickListener(startEditing);

        return circleCopy;
    }

    public void addSchedule(@Nullable View.OnClickListener clickCreateNew, @Nullable View.OnClickListener clickEdit){
        //내용있는 일정 생성;
        Boolean isLeft = !getLeftVisbility(listSchedule.size() - 1);
        View createdView = addFilledSchedule(isLeft, listSchedule.size() - 1);
        CircleImageView circleImageView = createCircleImage(coordinateInformation.circleX[isLeft ? 0 : 1],
                allocateViewCoordinateY(listSchedule.size() - 1) + coordinateInformation.first_margin);
        circleImageView.setOnClickListener(clickEdit);

        layoutBase.addView(createdView);
        layoutBase.addView(circleImageView);
        circleImageView.setTag(createdView.getId());
        listSchedule.add(listSchedule.size() - 1, new ListSchedule(createdView, circleImageView, createdView.getId())); //last index always occupies add button
        heightUpdate(true);

        updateYCoordinateViews(listSchedule.size() - 2);

        //update view
        for (int i = 0; i < listSchedule.size() - 1; i++) {
            setScheduleVis(listSchedule.get(i).view, i);
        }
    }


    public void setScheduleVis(View view, int idx){ //updated method
        boolean isLeft = getLeftVisbility(idx);
        if (isLeft){ //left side on, right side off
            //view.findViewById(R.id.circleimageview_left).setVisibility(View.VISIBLE);
            listSchedule.get(idx).circleImage.setX(coordinateInformation.circleX[0]);
            //listSchedule.get(idx).circleImage.setTop(100);
            view.findViewById(R.id.title_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.contents_left).setVisibility(View.VISIBLE);
            //view.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
        }else{
            listSchedule.get(idx).circleImage.setX(coordinateInformation.circleX[1]);
            view.findViewById(R.id.title_right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.contents_right).setVisibility(View.VISIBLE);
            //view.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.contents_left).setVisibility(View.INVISIBLE);
        }

        if (isEditing == false){
            view.findViewById(R.id.btn_delete_schedule_left).setVisibility(View.GONE);
            view.findViewById(R.id.btn_delete_schedule_right).setVisibility(View.GONE);
        }
    }


    public float toDp(int dp){
        //픽셀을 dp로 변환
        DisplayMetrics dm = rootView.getContext().getResources().getDisplayMetrics();
        float dpIndx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        return dpIndx;
    }


    public boolean getLeftVisbility(int idxList){
        //현재 인덱스의 맞는 vis 구함
        int cur = listSchedule.size() - idxList - 1;
        boolean isLeft;
        if (cur % 2 == 1)
            isLeft = false;
        else
            isLeft = true;
        return isLeft;
    }



    public void moveSchedule(int idxA, int idxB){
        //A 레이아웃을 B로 이동. A + 1 ~ B는 위로 한칸 이동

        if (idxA == idxB) return; //같을경우 리턴
        //int y_coordinateA, y_coordinateB;
        //y_coordinateA = coordinateInformation.layout_height * idxB;
        //y_coordinateB = coordinateInformation.layout_height * idxA;
        //switching two views
        //listSchedule.get(idxA).view.setY(y_coordinateB); //하나씩 아래로
        //listSchedule.get(idxB).view.setY(y_coordinateA);
        //end of switching


        ListSchedule lsTempA = new ListSchedule(listSchedule.get(idxA).view, listSchedule.get(idxA).circleImage, listSchedule.get(idxA).view.getId());
        listSchedule.remove(idxA);
        listSchedule.add(idxB, lsTempA);

        if (idxA > idxB){
            int temp = idxA;
            idxA = idxB;
            idxB = temp;
        }
        for (int i = idxA; i <= idxB; i++){
            setScheduleVis(listSchedule.get(i).view, i);
        }

        updateYCoordinateViews(idxA);
    }

    public int toListIdx(int unique_ID){
        //ID 값으로 어레이 리스트의 순서를 찾아 리턴
        int total = listSchedule.size() - 1;
        for (int i = 0; i < total; i++){
            if (listSchedule.get(i).view.getId() == unique_ID)
                return i;
        }
        return 0; //if failed
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public void draw_lines(int idx){
        ImageView imageView = new ImageView(appContext);

        imageView.setX(coordinateInformation.circleX[0] +  dipToPixels(appContext,this.IMAGE_SIZE/2));
        if (idx > 0) {
            if (idx == 1) imageView.setY((coordinateInformation.first_margin + dipToPixels(appContext, this.IMAGE_SIZE)) * idx);
            //else imageView.setY(((coordinateInformation.first_margin + coordinateInformation.end_margin) + dipToPixels(appContext, this.IMAGE_SIZE)) * idx - coordinateInformation.first_margin);\
            else imageView.setY(listSchedule.get(idx).view.getY() - coordinateInformation.end_margin);
        }
        imageView.setBackgroundColor(appContext.getResources().getColor(R.color.cardview_dark_background));

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(coordinateInformation.circleX[1] - coordinateInformation.circleX[0], coordinateInformation.first_margin * 2);
        imageView.setLayoutParams(layoutParams);


        layoutBase.addView(imageView);
    }

    public void initSchedule(@Nullable View.OnClickListener clickCreateNew, @Nullable View.OnClickListener clickEdit){
        //첫 시작화면에서 동그라미 생성. 동그라미 하나는 처음 생성된거 + 빈 동그라미까지
        layoutBase.removeAllViews();

        CoordinatorLayout.LayoutParams coordParms = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        coordParms.gravity = Gravity.TOP;
        scrollView.setLayoutParams(coordParms);

        View createdView = addFilledSchedule(false, listSchedule.size());
        CircleImageView circleImageView = createCircleImage(coordinateInformation.circleX[1], (coordinateInformation.first_margin));
        circleImageView.setOnClickListener(clickEdit);
        listSchedule.add(new ListSchedule(createdView, circleImageView, createdView.getId()));
        layoutBase.addView(circleImageView);
        layoutBase.addView(createdView);
        circleImageView.setTag(createdView.getId());
        heightUpdate(true);

        createdView = addEmptySchedule(clickCreateNew); //not necessary to create a new circle image.
        listSchedule.add(new ListSchedule(createdView, null, createdView.getId()));
        layoutBase.addView(createdView);
        heightUpdate(true);
        layoutBase.getLayoutParams().height += FIRST_CIRCLE_BIGGER;

        updateYCoordinateViews(0);

        draw_lines(0);
    }

    public void updateYCoordinateViews(int startIdx){
        //assumed that there at least two views which are filled and empty.
        //and saved into the list schedule.
        for (int idx = startIdx; idx < listSchedule.size(); idx++){
            listSchedule.get(idx).view.setY(allocateViewCoordinateY(idx));
        }
    }
    public void drawFirstScreen_Coordinator(@Nullable View.OnClickListener onClickListener){
        //첫번째 화면을 그린다
        final CoordinatorLayout.LayoutParams coordParms = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        coordParms.gravity = Gravity.CENTER;
        scrollView.setLayoutParams(coordParms);

        CircleImageView emptyCircle = new CircleImageView(appContext);
        emptyCircle.setBorderWidth(this.BORDER_WIDTH);
        emptyCircle.setBorderColor(Color.BLACK);
        emptyCircle.setImageResource(R.color.cardview_light_background);
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams((int)toDp(this.IMAGE_SIZE) + this.FIRST_CIRCLE_BIGGER, (int)toDp(this.IMAGE_SIZE) + this.FIRST_CIRCLE_BIGGER);
        //linearParams.gravity = Gravity.CENTER;
        linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        emptyCircle.setId(ViewIdGenerator.generateViewId());
        emptyCircle.setLayoutParams(linearParams);


        layoutBase.addView(emptyCircle);
        //listSchedule.add((View)emptyCircle, unique_ID++); //add an emptycircle
        //listSchedule.add(new ListSchedule(emptyCircle, emptyCircle.getId()));
        emptyCircle.setOnClickListener(onClickListener);

        TextView textView = new TextView(appContext);
        textView.setText("첫 일정을\n등록해 주세요!");
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, emptyCircle.getId());
        textView.setLayoutParams(layoutParams);
        layoutBase.addView(textView, layoutParams);

        //draw an invisible schedule to calculate layout height and xy coordinates.

    }
    public int getRelativeLeft(View  view, View root){
        if (view.getParent() == root)
            return view.getLeft();
        else
            return view.getLeft() + getRelativeLeft((View)view.getParent(), root);
    }

    public int getRelativeTop(View view, View root){
        if (view.getParent() == root)
            return view.getTop();
        else
            return view.getTop() + getRelativeTop((View)view.getParent(), root);
    }
    public void heightUpdate(boolean isIncreased){
        layoutBase.getLayoutParams().height = listSchedule.size() * coordinateInformation.layout_height + this.EMPTY_CIRCLE_BIGGER;
    }

    private int allocateViewCoordinateY(int idx){ //allocate y coordinate for a view based on listschedue index.
        return (coordinateInformation.layout_height * idx);
    }
    public boolean removeSchedule(int view_id){
        if (listSchedule.size() > 2){
            int idx = toListIdx(view_id);
            layoutBase.removeView(listSchedule.get(idx).view);
            layoutBase.removeView(listSchedule.get(idx).circleImage);
            listSchedule.remove(idx);
            isEditing = false;

            //update views with new y coordinates
            for (int i = 0 ; i < listSchedule.size() - 1; i++) {
                setScheduleVis(listSchedule.get(i).view, i);
                if (i >= idx) {
                    listSchedule.get(i).circleImage.setY(listSchedule.get(i).circleImage.getY() - coordinateInformation.layout_height);
                }
            }
            updateYCoordinateViews(idx);
            heightUpdate(false);
            return true;
        }
        else{
            return false;
        }

    }

    View.OnLongClickListener startEditing = new View.OnLongClickListener(){
        @Override
        public boolean onLongClick(View view) {

            boolean isLeft;
            int idx, isEnabled;
            if (!isEditing) { //if editing has not started, enable delete buttons
                //start of shaking  animation

                //end of shaking animation
                isEnabled = View.VISIBLE;
                isEditing = true;
            }
            else{ //start drag drop
                ClipData.Item item = new ClipData.Item((CharSequence) view.getTag().toString());
                String[] mimeType = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData data = new ClipData(view.getTag().toString(), mimeType, item); //pass on the tag of the selected layout

                view.setBackgroundColor(Color.TRANSPARENT);
                View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);

                view.startDrag(data, dragShadowBuilder, view, 0);
                isEnabled = View.INVISIBLE;
            }

            //turn on/off delete buttons
            for (idx = 0; idx < listSchedule.size() - 1; idx++) {
                isLeft = getLeftVisbility(idx);
                if (isLeft) {
                    listSchedule.get(idx).view.findViewById(R.id.btn_delete_schedule_left).setVisibility(isEnabled);
                } else {
                    listSchedule.get(idx).view.findViewById(R.id.btn_delete_schedule_right).setVisibility(isEnabled);
                }
                listSchedule.get(idx).view.bringToFront(); //gurantee delete buttons to be in front. unnecessary to perform every time => will be moved later.
            }
            return true;
        }
    };



    View.OnDragListener scheduleDragListener = new View.OnDragListener(){
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {
            //드래그드롭 리스너; 동그라미 아래에 있는 리니어 레이어 아웃 친구들
            NestedScrollView scrollView;
            int action = dragEvent.getAction();
            View viewParent = (View)view.getParent();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.d("DragDrop", "Started entered");
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //where animation happens
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
                    return true;

                default:
                    break;
            }
            return false;
        }
    };
}

