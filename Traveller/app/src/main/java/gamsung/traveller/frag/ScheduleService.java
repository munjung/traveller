package gamsung.traveller.frag;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Spot;

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
        View lines[] = new View[2];
        int view_ID;
        public ListSchedule(View view, View circleImage, View lines[], int view_ID) {
            this.circleImage = circleImage;
            this.view = view;
            this.view_ID = view_ID;
            this.lines = lines;
        }
    }

    class DrawnLine extends AppCompatImageView{
        int startX, startY, endX, endY;
        public DrawnLine(Context context, int startX, int startY, int endX, int endY) {
            super(context);
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
            line.setStrokeWidth(toDp(5));
            line.setColor(Color.WHITE);
            canvas.drawLine(startX ,startY , endX, endY , line);
        }
    }



    int BORDER_WIDTH, IMAGE_SIZE, FIRST_CIRCLE_BIGGER;
    CoordinateInformation coordinateInformation = new CoordinateInformation();
    final boolean isDragDrop;
    boolean isEditing;
    private int EMPTY_CIRCLE_BIGGER;
    private List<Spot> spotList;
    ArrayList<ListSchedule> listSchedule = new ArrayList<>();

    ViewGroup rootView;
    NestedScrollView scrollView;
    Context appContext;
    RelativeLayout layoutBase;
    View.OnDragListener dragListener;
    View.OnClickListener clickRemoveSelectedSchedule, startScheduling, createNewSchedule, editSchedule;
    int layoutSingle;


    public ScheduleService(ViewGroup rootView, @LayoutRes int layoutSingle, NestedScrollView scrollView,
                           RelativeLayout layoutBase, Context appContext, List<Spot> spotList, boolean isDragDrop) {
        this.rootView = rootView;
        this.scrollView = scrollView;
        this.layoutSingle = layoutSingle;
        this.layoutBase = layoutBase;
        this.appContext = appContext;
        this.isDragDrop= isDragDrop;
        this.spotList = spotList;
        //this.longClickedCircle = this.longClickedListener;
        this.dragListener = this.scheduleDragListener;
        BORDER_WIDTH = (int)toDp(5);
        IMAGE_SIZE = 100;
        FIRST_CIRCLE_BIGGER = 40;
        EMPTY_CIRCLE_BIGGER = 24;
        coordinateInformation.layout_height = 0; //initalized to zero
        isEditing = false;

    }

    public void load_Spots(){
        if (spotList.size() == 0){
            drawFirstScreen_Coordinator();
        }
        else{
            initSchedule(spotList.get(0));
            for (int idx = 1; idx < spotList.size(); idx++){
                addSchedule(null);
            }
        }
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

    private View addEmptySchedule(){
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
        circleImageView.setOnClickListener(createNewSchedule);

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
    public void editSchedule(int view_id, Spot editedSpot){
        int idx = toListIdx(view_id);
        View editView = listSchedule.get(idx).view;
        Spot spot = spotList.get(idx);
        spot = editedSpot;

        ((TextView)editView.findViewById(R.id.contents_left)).setText(spot.getMission());
        ((TextView)editView.findViewById(R.id.contents_right)).setText(spot.getMission());
        //change image

    }
    private View addFilledSchedule(boolean isLeft, Spot newSpot){
        //add a new spot
        LayoutInflater layoutInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutSchedule = layoutInflater.inflate(layoutSingle, null);
        layoutSchedule.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        if (newSpot == null) newSpot = spotList.get(listSchedule.size() - 1);
        else if (newSpot == spotList.get(0)) newSpot = spotList.get(0);
        else spotList.add(newSpot);

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

        ((TextView)layoutSchedule.findViewById(R.id.title_right)).setText("Spot ID: " + newSpot.get_id());
        ((TextView)layoutSchedule.findViewById(R.id.title_right)).setTextColor(Color.WHITE);
        ((TextView)layoutSchedule.findViewById(R.id.title_left)).setText("Spot ID: " + newSpot.get_id());
        ((TextView)layoutSchedule.findViewById(R.id.title_left)).setTextColor(Color.WHITE);
        ((TextView)layoutSchedule.findViewById(R.id.contents_right)).setText(newSpot.getMission());
        ((TextView)layoutSchedule.findViewById(R.id.contents_left)).setText(newSpot.getMission());
        ((TextView)layoutSchedule.findViewById(R.id.contents_right)).setTextColor(Color.WHITE);
        ((TextView)layoutSchedule.findViewById(R.id.contents_left)).setTextColor(Color.WHITE);

        (layoutSchedule.findViewById(R.id.btn_delete_schedule_right)).setOnClickListener(clickRemoveSelectedSchedule);
        (layoutSchedule.findViewById(R.id.btn_delete_schedule_left)).setOnClickListener(clickRemoveSelectedSchedule);

        layoutSchedule.setTag(viewID);
        layoutSchedule.setId(viewID);

        scrollView.smoothScrollBy(0, 30);
        layoutSchedule.setBackgroundColor(Color.TRANSPARENT);
        layoutSchedule.setOnDragListener(dragListener);

        return layoutSchedule;
    }

    private CircleImageView createCircleImage(int coord_x, int coord_y, int spot_idx){
        //static_temp
        CircleImageView circleCopy = new CircleImageView(appContext);
        circleCopy.setX(coord_x);
        circleCopy.setY(coord_y);
        circleCopy.setBorderWidth(BORDER_WIDTH);
        circleCopy.setBorderColor(Color.WHITE);

        //spotList.get(photo_id).getPicture_path();
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

        int photo_id = spotList.get(spot_idx).getPicture_id();
        if (photo_id == 0)
            Glide.with(appContext).load(R.drawable.grap_noimage).into(circleCopy);
        else
            circleCopy.setImageResource(tempSelected);

        RelativeLayout.LayoutParams circleSize = new RelativeLayout.LayoutParams((int)toDp(IMAGE_SIZE), (int)toDp(IMAGE_SIZE));
        circleCopy.setLayoutParams(circleSize);
        circleCopy.setOnLongClickListener(startEditing);

        return circleCopy;
    }

    public void addSchedule(Spot newSpot){
        //내용있는 일정 생성;
        //리스트 스케줄과 함께 증가하는 스팟
        int idx = listSchedule.size() - 1;
        Boolean isLeft = !getLeftVisbility(idx);

        View createdView = addFilledSchedule(isLeft, newSpot);
        CircleImageView circleImageView = createCircleImage(coordinateInformation.circleX[isLeft ? 0 : 1],
                allocateViewCoordinateY(listSchedule.size() - 1) + coordinateInformation.first_margin, idx);
        circleImageView.setOnClickListener(editSchedule);

        layoutBase.addView(createdView);
        layoutBase.addView(circleImageView);
        circleImageView.setTag(createdView.getId());
        DrawnLine[] lineView = draw_lines(listSchedule.size() - 1);
        layoutBase.addView(lineView[0]);
        layoutBase.addView(lineView[1]);
        lineView[isLeft ? 1 : 0].setVisibility(View.INVISIBLE);
        listSchedule.add(listSchedule.size() - 1, new ListSchedule(createdView, circleImageView, lineView, createdView.getId())); //last index always occupies add button
        heightUpdate();

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
            //listSchedule.get(idx).lines[0].setVisibility(View.INVISIBLE);
            listSchedule.get(idx).lines[0].setVisibility(View.VISIBLE);
            listSchedule.get(idx).lines[1].setVisibility(View.INVISIBLE);
            //listSchedule.get(idx).circleImage.setTop(100);
            view.findViewById(R.id.title_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.contents_left).setVisibility(View.VISIBLE);
            //view.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
        }else{
            listSchedule.get(idx).circleImage.setX(coordinateInformation.circleX[1]);
            //listSchedule.get(idx).lines[0].setVisibility(View.INVISIBLE);
            listSchedule.get(idx).lines[1].setVisibility(View.VISIBLE);
            listSchedule.get(idx).lines[0].setVisibility(View.INVISIBLE);
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

        ListSchedule lsTempA = new ListSchedule(listSchedule.get(idxA).view, listSchedule.get(idxA).circleImage,
                listSchedule.get(idxA).lines, listSchedule.get(idxA).view.getId());
        listSchedule.remove(idxA);
        listSchedule.add(idxB, lsTempA);
        if (idxA > idxB){
            int temp = idxA;
            idxA = idxB;
            idxB = temp;
        }

        Collections.swap(spotList, idxA, idxB);
        for (int idx = idxB; idx > idxA + 1; idx--){//bubble swap
            Collections.swap(spotList, idx, idx - 1);
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

    public DrawnLine[] draw_lines(int idx){

        DrawnLine rtrLine[] = new DrawnLine[2];
        int padding = 400;
        for (int i = 0; i < 2; i++){
            if (i == 0) {
                rtrLine[0] =  new DrawnLine(appContext, 0, 0, coordinateInformation.circleX[1] - coordinateInformation.circleX[0], coordinateInformation.first_margin * 2);
            }
            else{
                rtrLine[1] = new DrawnLine(appContext, coordinateInformation.circleX[1] - coordinateInformation.circleX[0], 0, 0, coordinateInformation.first_margin * 2);
            }

            rtrLine[i].setX(coordinateInformation.circleX[0] +  dipToPixels(appContext,this.IMAGE_SIZE/2));
            if (idx > 0) {
                if (idx == 1) rtrLine[i].setY((coordinateInformation.first_margin + dipToPixels(appContext, this.IMAGE_SIZE)) * idx - padding);
                    //else imageView.setY(((coordinateInformation.first_margin + coordinateInformation.end_margin) + dipToPixels(appContext, this.IMAGE_SIZE)) * idx - coordinateInformation.first_margin);\
                else rtrLine[i].setY(listSchedule.get(idx).view.getY() - coordinateInformation.end_margin - padding);
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(coordinateInformation.circleX[1] - coordinateInformation.circleX[0],
                    coordinateInformation.first_margin * 2 + padding);
            rtrLine[i].setLayoutParams(layoutParams);
        }

        return rtrLine;
    }

    public void initSchedule(Spot firstSpot){
        //첫 시작화면에서 동그라미 생성. 동그라미 하나는 처음 생성된거 + 빈 동그라미까지
        layoutBase.removeAllViews();

        CoordinatorLayout.LayoutParams coordParms = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        coordParms.gravity = Gravity.TOP;
        scrollView.setLayoutParams(coordParms);

        View createdView = addFilledSchedule(false, firstSpot);
        CircleImageView circleImageView = createCircleImage(coordinateInformation.circleX[1], (coordinateInformation.first_margin), 0);

        circleImageView.setOnClickListener(editSchedule);
        DrawnLine lineView[] = draw_lines(listSchedule.size());
        listSchedule.add(new ListSchedule(createdView, circleImageView, lineView, createdView.getId()));
        layoutBase.addView(lineView[0]);
        layoutBase.addView(lineView[1]);
        lineView[1].setVisibility(View.INVISIBLE);
        layoutBase.addView(circleImageView);
        layoutBase.addView(createdView);
        circleImageView.setTag(createdView.getId());
        //heightUpdate();

        createdView = addEmptySchedule(); //not necessary to create a new circle image.
        listSchedule.add(new ListSchedule(createdView, null, null, createdView.getId()));
        layoutBase.addView(createdView);
        heightUpdate();
        layoutBase.getLayoutParams().height += FIRST_CIRCLE_BIGGER;

        updateYCoordinateViews(0);

    }

    public void updateYCoordinateViews(int startIdx){
        //assumed that there at least two views which are filled and empty.
        //and saved into the list schedule.
        for (int idx = startIdx; idx < listSchedule.size(); idx++){
            listSchedule.get(idx).view.setY(allocateViewCoordinateY(idx));
            if (listSchedule.get(idx).lines != null) {
                listSchedule.get(idx).lines[0].setY(allocateViewCoordinateY(idx) + coordinateInformation.layout_height - coordinateInformation.end_margin);
                listSchedule.get(idx).lines[1].setY(allocateViewCoordinateY(idx) + coordinateInformation.layout_height - coordinateInformation.end_margin);
            }
        }
    }
    public void drawFirstScreen_Coordinator(){
        //첫번째 화면을 그린다
        final CoordinatorLayout.LayoutParams coordParms = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        coordParms.gravity = Gravity.CENTER;
        scrollView.setLayoutParams(coordParms);

        CircleImageView emptyCircle = new CircleImageView(appContext);
        emptyCircle.setBorderWidth(this.BORDER_WIDTH);
        emptyCircle.setBorderColor(Color.BLACK);
        emptyCircle.setImageResource(R.color.cardview_light_background);
        RelativeLayout.LayoutParams linearParams = new RelativeLayout.LayoutParams((int)toDp(this.IMAGE_SIZE) + this.FIRST_CIRCLE_BIGGER,
                (int)toDp(this.IMAGE_SIZE) + this.FIRST_CIRCLE_BIGGER);
        //linearParams.gravity = Gravity.CENTER;
        linearParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        emptyCircle.setId(ViewIdGenerator.generateViewId());
        emptyCircle.setLayoutParams(linearParams);


        layoutBase.addView(emptyCircle);
        emptyCircle.setOnClickListener(startScheduling);

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

    public static int getRelativeLeft(View  view, View root){
        if (view.getParent() == root)
            return view.getLeft();
        else
            return view.getLeft() + getRelativeLeft((View)view.getParent(), root);
    }

    public static int getRelativeTop(View view, View root){
        if (view.getParent() == root)
            return view.getTop();
        else
            return view.getTop() + getRelativeTop((View)view.getParent(), root);
    }
    public void heightUpdate(){
        layoutBase.getLayoutParams().height = listSchedule.size() * coordinateInformation.layout_height + this.EMPTY_CIRCLE_BIGGER;
    }

    private int allocateViewCoordinateY(int idx){ //allocate y coordinate for a view based on listschedue index.
        return (coordinateInformation.layout_height * idx);
    }
    public boolean removeSchedule(int view_id){
        int idxDelete = toListIdx(view_id);
        if (listSchedule.size() > 2){

            layoutBase.removeView(listSchedule.get(idxDelete).view);
            layoutBase.removeView(listSchedule.get(idxDelete).circleImage);
            layoutBase.removeView(listSchedule.get(idxDelete).lines[0]);
            layoutBase.removeView(listSchedule.get(idxDelete).lines[1]);
            listSchedule.remove(idxDelete);
            spotList.remove(idxDelete);
            isEditing = false;

            updateYCoordinateViews(idxDelete);
            for (int idx = 0; idx < idxDelete; idx++)
                setScheduleVis(listSchedule.get(idx).view, idx);
            heightUpdate();
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

