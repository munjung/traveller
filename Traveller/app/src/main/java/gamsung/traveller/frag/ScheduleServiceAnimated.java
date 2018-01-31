package gamsung.traveller.frag;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import gamsung.traveller.R;

/**
 * Created by JKPark on 2018-01-31.
 */

public class ScheduleServiceAnimated extends ScheduleService {
    private final int coordCircleX[] = new int[2], coordTitleX[] = new int[2], coordContentsX[] = new int[2];


    public ScheduleServiceAnimated(ViewGroup rootView, int layoutSingle, NestedScrollView scrollView, LinearLayout layoutBase, Context appContext, boolean isDragDrop) {
        super(rootView, layoutSingle, scrollView, layoutBase, appContext, isDragDrop);
    }

    @Override
    public void createNewSchedule(@Nullable View.OnClickListener clickCreateNew, @Nullable View.OnClickListener clickEdit) {
        //animation before creating of new schedule
        View viewReference = listSchedule.get(0).view;
        if (viewReference == null)
            return; //throw exception
        coordCircleX[0] = getRelativeLeft(viewReference.findViewById(R.id.circleimageview_left), layoutBase);
        coordCircleX[1] = getRelativeLeft(viewReference.findViewById(R.id.circleimageview_right), layoutBase);
        coordContentsX[0] = getRelativeLeft(viewReference.findViewById(R.id.contents_left), layoutBase);
        coordContentsX[1] = getRelativeLeft(viewReference.findViewById(R.id.contents_right), layoutBase);
        coordTitleX[0] = getRelativeLeft(viewReference.findViewById(R.id.title_left), layoutBase);
        coordTitleX[1] = getRelativeLeft(viewReference.findViewById(R.id.title_right), layoutBase);

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
}
