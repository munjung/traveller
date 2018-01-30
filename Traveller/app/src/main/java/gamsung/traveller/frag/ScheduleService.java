package gamsung.traveller.frag;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import gamsung.traveller.R;

/**
 * Created by JKPark on 2018-01-30.
 */
class list{
    View view;
    int view_ID;
    public list(View view, int view_ID) {
        this.view = view;
        this.view_ID = view_ID;
    }
}
public class ScheduleService {
    int WIDTH, IMAGE_SIZE;
    boolean IS_DRAGDROP;
    ArrayList<View> listSchedule = new ArrayList<>();
    ViewGroup rootView;
    NestedScrollView scrollView;
    Context appContext;
    LinearLayout layoutBase;
    @LayoutRes int layoutSingle;

    public ScheduleService(ViewGroup rootView, @LayoutRes int layoutSingle, NestedScrollView scrollView,
                           LinearLayout layoutBase, Context appContext) {
        this.rootView = rootView;
        this.scrollView = scrollView;
        this.layoutSingle = layoutSingle;
        this.layoutBase = layoutBase;
        this.appContext = appContext;
        WIDTH = 5;
        IMAGE_SIZE = 80;
        IS_DRAGDROP = true;
    }

    public View createScheduleView(int tempNum){
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
        circleImageView.setOnClickListener(createNewSchedule);

        layoutSchedule.setTag(tempNum); //temporary
        //layoutSchedule.setOnDragListener(scheduleDragListener);

        scrollView.smoothScrollBy(0, 30);

        return layoutSchedule;
    }

    public void setScheduleView(int idx){
        TextView textTitle, textContents;
        CircleImageView circleImageView;
        View view = listSchedule.get(idx);

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
            textTitle.setText("Title" + view.getTag().toString());
            textContents.setText("Hungry sae byuk day" + view.getTag().toString());
            circleImageView.setImageResource(R.color.colorPrimaryDark);
            circleImageView.setBorderWidth(this.WIDTH);
            //circleImageView.setOnLongClickListener(longClickedCircle);
        }
        //view.setOnDragListener();
        setVisbility(view, !getLeftVisbility(idx));
    }

    CircleImageView.OnClickListener createNewSchedule = new CircleImageView.OnClickListener(){
        @Override
        public void onClick(View view) {
            listSchedule.add(createScheduleView(listSchedule.size()));
            layoutBase.addView(listSchedule.get(listSchedule.size() - 1));
            setScheduleView(listSchedule.size() - 2);
            for (int i = 0; i < listSchedule.size() - 1; i++){
                setVisbility(listSchedule.get(i), !getLeftVisbility(i));
            }
        }
    };

    public void drawFirstScreen_Coordinator(){
        final CoordinatorLayout.LayoutParams coordParms = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);

        coordParms.gravity = Gravity.CENTER;
        scrollView.setLayoutParams(coordParms);

        CircleImageView emptyCircle = new CircleImageView(appContext);
        emptyCircle.setBorderWidth(this.WIDTH);
        emptyCircle.setBorderColor(Color.BLACK);
        emptyCircle.setImageResource(R.color.cardview_light_background);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams((int)toDp(this.IMAGE_SIZE), (int)toDp(this.IMAGE_SIZE));
        linearParams.gravity = Gravity.CENTER;

        emptyCircle.setLayoutParams(linearParams);
        layoutBase.addView(emptyCircle);
        listSchedule.add(emptyCircle); //add an emptycircle

        TextView textView = new TextView(appContext);
        textView.setText("첫 일정을\n등록해 주세요!");
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        layoutBase.addView(textView);


    }

    public void setVisbility(View view, boolean isLeft){
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
        DisplayMetrics dm = rootView.getContext().getResources().getDisplayMetrics();
        float dpIndx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        return dpIndx;
    }

    private int getRelativeLeft(View  view, View root){
        if (view.getParent() == root)
            return view.getLeft();
        else
            return view.getLeft() + getRelativeLeft((View)view.getParent(), root);
    }
    private int getRelativeTop(View view, View root){
        if (view.getParent() == root)
            return view.getTop();
        else
            return view.getTop() + getRelativeTop((View)view.getParent(), root);
    }


    private boolean getLeftVisbility(int idx){
        int cur = listSchedule.size() - idx;
        boolean isLeft;
        if (cur % 2 == 1)
            isLeft = false;
        else
            isLeft = true;
        return isLeft;
    }

}
