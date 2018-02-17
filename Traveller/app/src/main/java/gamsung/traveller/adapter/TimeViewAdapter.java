package gamsung.traveller.adapter;

import android.content.Context;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
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

import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.activity.MainActivity;
import gamsung.traveller.frag.ScheduleService;
import gamsung.traveller.model.Spot;

/**
 * Created by JKPark on 2018-02-13.
 */

public class TimeViewAdapter extends RecyclerView.Adapter<TimeViewAdapter.TimeViewHolder> {
    static final int MAX_NUM_IMAGES = 10;
    private ClickListener callback;
    private List<Spot> spotList;

    public TimeViewAdapter(List<Spot> spotList, ClickListener callback) {
        this.spotList = spotList;
        this.callback = callback;
    }

    public interface ClickListener{
        void onClickDelete(int position);
        void onClickEdit(int position);
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
        holder.txtTitle.setText(spotList.get(position).getMission());
        holder.callback = callback;
        layoutParams = (RelativeLayout.LayoutParams)holder.imageTimeLine.getLayoutParams();
        if (position == 0){
            layoutParams.setMargins(0, (int)ScheduleService.toDp(holder.imageTimeLine.getContext(), 25), 0, 0);
            layoutParams.height = (int)ScheduleService.toDp(holder.imageTimeLine.getContext(), 175);
            holder.imageTimeLine.setLayoutParams(layoutParams);
        }
        else{
            layoutParams.setMargins(0, (int)ScheduleService.toDp(holder.imageTimeLine.getContext(), 0), 0, 0);
            layoutParams.height = (int)ScheduleService.toDp(holder.imageTimeLine.getContext(), 200);
            holder.imageTimeLine.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return spotList.size();
    }

    public static class TimeViewHolder extends RecyclerView.ViewHolder{
        public TextView txtTitle, txtMission;
        public Button btnDelete;
        public ImageButton btnEdit;
        public ImageView imageTimeLine;
        public ImageView[] images = new ImageView[MAX_NUM_IMAGES];
        protected ClickListener callback;

        public TimeViewHolder(final View itemView) {
            super(itemView);
            imageTimeLine = itemView.findViewById(R.id.image_time_line);
            btnDelete = itemView.findViewById(R.id.btn_delete_view_by_photos);
            txtTitle = itemView.findViewById(R.id.txt_title_by_photos);
            txtMission = itemView.findViewById(R.id.txt_mission_by_photos);
            btnEdit = itemView.findViewById(R.id.btn_edit_view_by_photos);
            LinearLayout layout = itemView.findViewById(R.id.time_view_photos_layout);
            for (int i = 0; i < MAX_NUM_IMAGES; i++){
                images[i] = new ImageView(itemView.getContext());
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int)ScheduleService.dipToPixels(itemView.getContext(),110), (int)ScheduleService.dipToPixels(itemView.getContext(),110));
                layoutParams.setMargins(0, 0, (int)ScheduleService.dipToPixels(itemView.getContext(), 10),0);
                images[i].setLayoutParams(layoutParams);
                Glide.with(itemView.getContext()).load(R.drawable.grap_noimage).into(images[i]);
                images[i].setBackgroundResource(R.drawable.left_rounded_corners);
                //images[i].setImageResource(R.drawable.grap_noimage);
                layout.addView(images[i]);
                //load pictures
                //load pictures;
            }

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null)
                        callback.onClickDelete(getLayoutPosition());
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callback != null)
                        callback.onClickEdit(getLayoutPosition());
                }
            });
        }

    }
}
