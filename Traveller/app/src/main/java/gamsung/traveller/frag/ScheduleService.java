package gamsung.traveller.frag;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;

public class ScheduleService {
    /**
     * Created by JKPark on 2018-01-30.
     */
    class ListSchedule{
        View view;
        int view_ID;
        public ListSchedule(View view, int view_ID) {
            this.view = view;
            this.view_ID = view_ID;
        }
    }
    private int unique_ID;
    int BORDER_WIDTH, IMAGE_SIZE, FIRST_CIRCLE_BIGGER;
    final boolean isDragDrop;
    ArrayList<ListSchedule> listSchedule = new ArrayList<>();
    ViewGroup rootView;
    NestedScrollView scrollView;
    Context appContext;
    LinearLayout layoutBase;
    View.OnLongClickListener longClickedCircle;
    View.OnDragListener dragListener;
    int layoutSingle;

    public ScheduleService(ViewGroup rootView, @LayoutRes int layoutSingle, NestedScrollView scrollView,
                           LinearLayout layoutBase, Context appContext, boolean isDragDrop) {
        this.rootView = rootView;
        this.scrollView = scrollView;
        this.layoutSingle = layoutSingle;
        this.layoutBase = layoutBase;
        this.appContext = appContext;
        this.isDragDrop= isDragDrop;
        this.longClickedCircle = this.longClickedListener;
        this.dragListener = this.scheduleDragListener;
        unique_ID = 0;
        BORDER_WIDTH = 5;
        IMAGE_SIZE = 80;
        FIRST_CIRCLE_BIGGER = 40;
    }

    private View createScheduleView(@Nullable View.OnClickListener onClickListener){
        //점선으로 되있는 빈 동그라미 생성
        LayoutInflater layoutInflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layoutSchedule = layoutInflater.inflate(layoutSingle, null);

        layoutSchedule.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
        layoutSchedule.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
        layoutSchedule.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
        layoutSchedule.findViewById(R.id.contents_left).setVisibility(View.INVISIBLE);
        layoutSchedule.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);

        CircleImageView circleImageView = layoutSchedule.findViewById(R.id.circleimageview_left);
        circleImageView.setBackgroundResource(R.drawable.schedule_dotted_border);
        circleImageView.setImageResource(0);
        circleImageView.setBorderWidth(0);
        circleImageView.setOnClickListener(onClickListener);

        layoutSchedule.setTag(unique_ID);

        scrollView.smoothScrollBy(0, 30);

        return layoutSchedule;
    }

    public void setScheduleView(@Nullable View.OnClickListener clickEdit, int idx){
        //점선으로 되있는 동그라미에 생기를 넣어줌
        //make an empty dotted circle to an actual schedule view.
        TextView textTitle, textContents;
        CircleImageView circleImageView;
        View view = listSchedule.get(idx).view;

        for (int i = 0; i < 2; i++){
            if (i == 0){
                textTitle = view.findViewById(R.id.title_right);
                textContents = view.findViewById(R.id.contents_right);
                circleImageView = view.findViewById(R.id.circleimageview_right);
            }
            else{
                textTitle = view.findViewById(R.id.title_left);
                textContents = view.findViewById(R.id.contents_left);
                circleImageView = view.findViewById(R.id.circleimageview_left);
            }
            textTitle.setText("View ID: " + view.getTag().toString());
            textContents.setText("Circle x: " + getRelativeLeft(circleImageView, layoutBase) + "Circle y: " + getRelativeTop(circleImageView, layoutBase));
            circleImageView.setImageResource(R.color.colorPrimaryDark);
            circleImageView.setBorderWidth(this.BORDER_WIDTH);
            circleImageView.setOnLongClickListener(longClickedCircle);
            if (clickEdit != null)
                circleImageView.setOnClickListener(clickEdit);

        }
        if (isDragDrop)
            view.setOnDragListener(this.dragListener);
        setVisbility(view, getLeftVisbility(idx));
    }


    public void setVisbility(View view, boolean isLeft){
        //왼쪽보이기 오른쪽보이기
        if (isLeft){ //left side on, right side off
            view.findViewById(R.id.circleimageview_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.title_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.contents_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.circleimageview_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.contents_right).setVisibility(View.INVISIBLE);
        }else{
            view.findViewById(R.id.circleimageview_right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.title_right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.contents_right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.circleimageview_left).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.contents_left).setVisibility(View.INVISIBLE);
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
    View.OnLongClickListener longClickedListener = new View.OnLongClickListener(){
        //동그라미 롱클릭하면 쉐도우빌더 생성
        @Override
        public boolean onLongClick(View view) {
            //동그라미 롱 클릭시 쉐도우 빌더 만들기
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
    public void moveSchedule(int idxA, int idxB){
        //A 레이아웃을 B로 이동. A + 1 ~ B는 위로 한칸 이동
        //move idxA to idxB; idxA + 1 ~ idxB layouts move one layout up
        //given unique IDs of the views
        if (idxA == idxB) return; //같을경우 리턴
        idxA = toListIdx(idxA);
        idxB = toListIdx(idxB);
        ListSchedule lsTemp = new ListSchedule(listSchedule.get(idxA).view, listSchedule.get(idxA).view_ID);

        layoutBase.removeView(listSchedule.get(idxA).view);
        layoutBase.addView(listSchedule.get(idxA).view, idxB);

        listSchedule.remove(idxA);
        listSchedule.add(idxB, lsTemp);
        //refresh vis.
        if (idxA > idxB){ //swap
            int temp = idxA;
            idxA = idxB;
            idxB = temp;
        }
        for (int i = idxA; i <= idxB; i++)
            setVisbility(listSchedule.get(i).view, getLeftVisbility(i));


    }
    public int toListIdx(int unique_ID){
        //ID 값으로 어레이 리스트의 순서를 찾아 리턴
        int total = listSchedule.size() - 1;
        for (int i = 0; i < total; i++){
            if (listSchedule.get(i).view_ID == unique_ID)
                return i;
        }
        return 0; //if failed
    }
    public int getLastIdx(){
        return listSchedule.size() - 1;
    }
    public int getListSize(){
        return listSchedule.size();
    }
    public void startSchedule(@Nullable View.OnClickListener clickCreateNew, @Nullable View.OnClickListener clickEdit){
        //첫 시작화면에서 동그라미 생성. 동그라미 하나는 처음 생성된거 + 빈 동그라미까지
        layoutBase.removeAllViews();
        listSchedule.remove(0);

        CoordinatorLayout.LayoutParams coordParms = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        coordParms.gravity = Gravity.TOP;
        scrollView.setLayoutParams(coordParms);

        for (int i = 0; i < 2; i++) {
            listSchedule.add(new ListSchedule(createScheduleView(clickCreateNew), unique_ID++));
            layoutBase.addView((View)listSchedule.get(i).view, listSchedule.size() - 1);
        }

        setScheduleView(clickEdit, 0);
    }

    public void createNewSchedule(@Nullable View.OnClickListener clickCreateNew, @Nullable View.OnClickListener clickEdit){
        //점선 동그라미에 내용을 넣고, 점선 동그라미를 하나 더 만듬
        listSchedule.add(new ListSchedule(createScheduleView(clickCreateNew), unique_ID++));
        layoutBase.addView(listSchedule.get(getLastIdx()).view, listSchedule.size() - 1);
        setScheduleView(clickEdit, listSchedule.size() - 2);
        for (int i = 0; i < listSchedule.size() - 1; i++)
            setVisbility(listSchedule.get(i).view, getLeftVisbility(i));
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
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams((int)toDp(this.IMAGE_SIZE) + this.FIRST_CIRCLE_BIGGER, (int)toDp(this.IMAGE_SIZE) + this.FIRST_CIRCLE_BIGGER);
        linearParams.gravity = Gravity.CENTER;

        emptyCircle.setLayoutParams(linearParams);
        layoutBase.addView(emptyCircle);
        //listSchedule.add((View)emptyCircle, unique_ID++); //add an emptycircle
        listSchedule.add(new ListSchedule(emptyCircle, unique_ID));
        emptyCircle.setOnClickListener(onClickListener);

        TextView textView = new TextView(appContext);
        textView.setText("첫 일정을\n등록해 주세요!");
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        layoutBase.addView(textView);
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
}
