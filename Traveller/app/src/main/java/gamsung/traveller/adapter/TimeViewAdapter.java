package gamsung.traveller.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.activity.MainActivity;
import gamsung.traveller.frag.PhotoTouchHelperCallback;
import gamsung.traveller.frag.ScheduleService;
import gamsung.traveller.model.Spot;

/**
 * Created by JKPark on 2018-02-13.
 */

public class TimeViewAdapter extends RecyclerView.Adapter<TimeViewAdapter.TimeViewHolder> implements PhotoTouchHelperCallback.ActionCompletionContract{
    final static private int START_TIMELINE_COLOR_R = 0xd5;
    final static private int END_TIMELINE_COLOR_R = 0x79;
    final static private int START_TIMELINE_COLOR_G = 0x2c;
    final static private int END_TIMELINE_COLOR_G = 0xaf;
    final static private int START_TIMELINE_COLOR_B = 0x80;
    final static private int END_TIMELINE_COLOR_B = 0xff;
    final static private int BOOKMARK_LEFT_MARGIN = 12;

    private int GAB_COLOR_R;
    private int GAB_COLOR_G;
    private int GAB_COLOR_B;
    static final int MAX_NUM_IMAGES = 10;


    private ClickListener callback;
    private List<Spot> spotList;

    public TimeViewAdapter(List<Spot> spotList, ClickListener callback) {
        this.spotList = spotList;
        this.callback = callback;
        updateColorGab();
    }
    public void refreshSpotlist(List<Spot> spotList){
        this.spotList = spotList;
    }

    public void updateColorGab(){
        if (getItemCount() > 0) {
            GAB_COLOR_R = (END_TIMELINE_COLOR_R - START_TIMELINE_COLOR_R) / getItemCount();
            GAB_COLOR_G = (END_TIMELINE_COLOR_G - START_TIMELINE_COLOR_G) / getItemCount();
            GAB_COLOR_B = (END_TIMELINE_COLOR_B - START_TIMELINE_COLOR_B) / getItemCount();
        }
    }

    @Override
    public void onViewMoved(int oldPosition, int newPosition) {

        Collections.swap(spotList, oldPosition, newPosition);
        notifyItemMoved(oldPosition, newPosition);
        //notifyDataSetChanged();
        callback.notifyOrderChanged(oldPosition, newPosition);
    }

    @Override
    public void onInteractionEnded() {
        notifyItemRangeRemoved(0, spotList.size());
    }

    @Override
    public void onViewSwiped(int position) {
        callback.onClickDelete(position);
    }

    public interface ClickListener{
        void onClickDelete(int position);
        void onClickEdit(int position);
        void notifyOrderChanged(int oldPos, int newPos);
        void changeOrder(int oldPos, int newPos);
    }

    public void setCallback(ClickListener callback){
        this.callback = callback;
    }
    @Override
    public TimeViewAdapter.TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_view_by_photos_template, parent, false);
        return new TimeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TimeViewAdapter.TimeViewHolder holder, int position) {
        RelativeLayout.LayoutParams layoutParams;
        holder.txtMission.setText(spotList.get(position).getMission());
        int pos = spotList.get(position).get_id();
        holder.txtTitle.setText("Spot ID: " + pos);
        holder.callback = callback;
        layoutParams = (RelativeLayout.LayoutParams)holder.imageTimeLine.getLayoutParams();
        if (position == 0){
            layoutParams.setMargins(0, (int)ScheduleService.toDp(holder.imageTimeLine.getContext(), 35), 0, 0);
            layoutParams.height = (int)ScheduleService.toDp(holder.imageTimeLine.getContext(), 185);
            holder.imageTimeLine.setLayoutParams(layoutParams);
            holder.imageTimeLine.setBackground(setGradientTimeLine(position));
        }
        else{
            layoutParams.setMargins(0, (int)ScheduleService.toDp(holder.imageTimeLine.getContext(), 0), 0, 0);
            layoutParams.height = (int)ScheduleService.toDp(holder.imageTimeLine.getContext(), 210);
            holder.imageTimeLine.setLayoutParams(layoutParams);
            holder.imageTimeLine.setBackground(setGradientTimeLine(position));
        }
        adjustBookmark(holder, spotList.get(position).getPicture_id());
    }

    private void adjustBookmark (TimeViewAdapter.TimeViewHolder holder, int position){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.imageBookmark.getLayoutParams();
        if (position == 0){
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, position + 1);
            layoutParams.setMargins(0, 0, (int)(ScheduleService.dipToPixels(holder.imageBookmark.getContext(), BOOKMARK_LEFT_MARGIN)
                    + ScheduleService.dipToPixels(holder.imageBookmark.getContext(), 10)) , 0);        }
        else{
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_RIGHT, position + 1);
            layoutParams.setMargins(0, 0, (int)(ScheduleService.dipToPixels(holder.imageBookmark.getContext(), BOOKMARK_LEFT_MARGIN)
            + ScheduleService.dipToPixels(holder.imageBookmark.getContext(), 10)) , 0);
        }

        holder.imageBookmark.setLayoutParams(layoutParams);
    }

    private GradientDrawable setGradientTimeLine(int position){

        int start_color = Color.argb(255, (GAB_COLOR_R * position) + START_TIMELINE_COLOR_R, (GAB_COLOR_G * position) + START_TIMELINE_COLOR_G,
                (GAB_COLOR_B * position) + START_TIMELINE_COLOR_B);
        int end_color = Color.argb(255, (GAB_COLOR_R * (position + 1)) + START_TIMELINE_COLOR_R, (GAB_COLOR_G * (position + 1)) + START_TIMELINE_COLOR_G,
                (GAB_COLOR_B * (position + 1)) + START_TIMELINE_COLOR_B);
        GradientDrawable gradientTimeline = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {start_color, end_color});
        gradientTimeline.setCornerRadius(0f);
        return gradientTimeline;
    }

    @Override
    public int getItemCount() {
        return spotList.size();
    }

    public static class TimeViewHolder extends RecyclerView.ViewHolder{
        final static private int picture_size = 110;
        public TextView txtTitle, txtMission;
        public ImageButton btnEdit, btnDelete;
        public ImageView imageTimeLine, imageBookmark;
        public ImageView[] images = new ImageView[MAX_NUM_IMAGES];
        protected ClickListener callback;

        public TimeViewHolder(final View itemView) {
            super(itemView);
            imageTimeLine = itemView.findViewById(R.id.image_time_line);
            txtTitle = itemView.findViewById(R.id.txt_title_by_photos);
            txtMission = itemView.findViewById(R.id.txt_mission_by_photos);
            btnEdit = itemView.findViewById(R.id.btn_edit_view_by_photos);
            btnDelete = itemView.findViewById(R.id.btn_delete_view_by_photos);
            RelativeLayout layout = itemView.findViewById(R.id.time_view_photos_layout);


            for (int i = 0; i < MAX_NUM_IMAGES; i++){ //create image items + bookmark
                images[i] = new ImageView(itemView.getContext());
                images[i].setId(i + 1);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int)ScheduleService.dipToPixels(itemView.getContext(),picture_size),
                        (int)ScheduleService.dipToPixels(itemView.getContext(),picture_size));
                layoutParams.setMargins(0, 00, (int)ScheduleService.dipToPixels(itemView.getContext(), 10),0);
                if (i == 0) {
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                    imageBookmark = new ImageView(itemView.getContext());
                    RelativeLayout.LayoutParams bookmarkParams = new RelativeLayout.LayoutParams((int)ScheduleService.dipToPixels(itemView.getContext(), 14),
                            (int)ScheduleService.dipToPixels(itemView.getContext(), 23));
                    bookmarkParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE); //set the favorite image to be on the first index.
                    bookmarkParams.setMargins((int)ScheduleService.dipToPixels(itemView.getContext(), BOOKMARK_LEFT_MARGIN), 0,0,0);
                    imageBookmark.setImageResource(R.drawable.bookmark);
                    imageBookmark.setLayoutParams(bookmarkParams);
                    layout.addView(imageBookmark);
                }

                else layoutParams.addRule(RelativeLayout.RIGHT_OF, i);
                images[i].setLayoutParams(layoutParams);
                Glide.with(itemView.getContext()).load(R.drawable.grap_noimage).into(images[i]);
                images[i].setBackgroundResource(R.drawable.left_rounded_corners);
                //images[i].setImageResource(R.drawable.grap_noimage);
                layout.addView(images[i]);
                //load pictures
            }
            imageBookmark.bringToFront();
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null)
                        callback.onClickEdit(getLayoutPosition());
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null) callback.onClickDelete(getLayoutPosition());
                }
            });
        }

    }

}
