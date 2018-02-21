package gamsung.traveller.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import gamsung.traveller.R;
import gamsung.traveller.activity.CustomGalleryActivity;
import gamsung.traveller.activity.ImageSliderActivity;
import gamsung.traveller.model.Photograph;

/**
 * Created by jekan on 2018-02-10.
 */

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> {

    private Context _context;
    private ArrayList<Photograph> _items;
    private View.OnClickListener _clickListener;
    private ViewHolderClickListenerArguments _args;

    public CustomRecyclerAdapter(Context context, ArrayList<Photograph> photoList, View.OnClickListener clickListener) {

        if (photoList == null) {
            throw new IllegalArgumentException("data must not be null");
        }

        this._context = context;
        this._items = photoList;
        this._clickListener = clickListener;
        this._args = new ViewHolderClickListenerArguments();
    }

    @Override
    public CustomRecyclerAdapter.CustomViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {

        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_edit_chlid_item, viewGroup, false);
        final CustomViewHolder customViewHolder = new CustomViewHolder(_context, itemView, new CustomTextChangeListener());
        customViewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = customViewHolder.getAdapterPosition();
                _args.setPosition(position);
                _args.setItem(_items.get(position));
                _args.setReturnType(ViewHolderClickListenerArguments.RETURN_TYPE_CLICK_IMAGE);
                _clickListener.onClick(view);
            }
        });

        customViewHolder.getBtnRepresent().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = customViewHolder.getAdapterPosition();
                _args.setPosition(position);
                _args.setItem(_items.get(position));
                _args.setReturnType(ViewHolderClickListenerArguments.RETURN_TYPE_CLICK_REPRESENT);
                _clickListener.onClick(view);
                view.setBackground(_context.getResources().getDrawable(R.drawable.btn_represent_photo_on));
            }
        });

        return customViewHolder;
    }


    @Override
    public void onBindViewHolder(CustomRecyclerAdapter.CustomViewHolder viewHolder, int position) {

        String imgPath = _items.get(position).getPath();
        if (!TextUtils.isEmpty(imgPath)) {
            Glide.with(_context).load(imgPath).into(viewHolder.imageView);
        }

        String memo = _items.get(position).getMemo();
        memo = memo == null ? "" : memo;
        viewHolder.getTxtMemo().setText(memo);
        viewHolder.getTextChangedListener().setUpdatePosition(viewHolder.getAdapterPosition(), viewHolder.getTxtMemo());
    }


    @Override
    public int getItemCount() {

        return _items == null ? 0 : _items.size();
    }

    public ViewHolderClickListenerArguments getViewHolderClickListenerArgs(){
        return _args;
    }

    public int addImagePath(String path){

        path = path.replace("[", "");
        path = path.replace("]", "");
        path = path.replace(" ", "");
        String[] pathArr = path.split(",");
        for(int i=0; i<pathArr.length; i++){

            Photograph photograph = new Photograph();
            photograph.setPath(pathArr[i]);

            _items.add(photograph);

            notifyItemChanged(_items.size()-1);
        }

        return _items.size();
    }

    public ArrayList<Photograph> getItems(){
        return _items;
    }

    public Photograph getItem(int position) {return _items.get(position);}


    public ArrayList<String> getImgPathList(){

        ArrayList<String> pathList = new ArrayList<>();
        for (int i=0; i<_items.size(); i++){

            pathList.add(_items.get(i).getPath());
        }

        return pathList;
    }


    public ArrayList<String> getMemoList(){

        ArrayList<String> memoList = new ArrayList<>();
        for (int i=0; i<_items.size(); i++){

            memoList.add(_items.get(i).getMemo());
        }

        return memoList;
    }


    //View Holder
    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        Context context;

        private ImageView imageView;
        private EditText txtMemo;
        private Button btnRepresent;
        private CustomTextChangeListener watcher;

        public CustomViewHolder(final Context context, View itemView, CustomTextChangeListener watcher) {
            super(itemView);
            this.context = context;
            this.watcher = watcher;

            imageView = (ImageView) itemView.findViewById(R.id.img_viewpager_childimage);
            btnRepresent = (Button) itemView.findViewById(R.id.btn_represent_edit_child_item);
            txtMemo = (EditText)itemView.findViewById(R.id.txt_memo_edit);
            txtMemo.addTextChangedListener(watcher);
        }

        public ImageView getImageView(){
            return imageView;
        }

        public EditText getTxtMemo(){
            return txtMemo;
        }

        public Button getBtnRepresent() {return btnRepresent;}

        public CustomTextChangeListener getTextChangedListener(){
            return watcher;
        }
    }

    class CustomTextChangeListener implements TextWatcher{

        int position;
        EditText editText;

        public void setUpdatePosition(int position, EditText editText){
            this.position = position;
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if(editText == null)
                return;

            String memo = editText.getText() == null ? "" : editText.getText().toString();
            _items.get(position).setMemo(memo);

            Log.d("watcher", position +  " : " + memo);
        }
    }


    public class ViewHolderClickListenerArguments{

        public static final int RETURN_TYPE_CLICK_IMAGE = 1;
        public static final int RETURN_TYPE_CLICK_REPRESENT = 2;

        private Photograph item;
        private int position;
        private int returnType;


        public Photograph getItem() {
            return item;
        }

        public void setItem(Photograph item) {
            this.item = item;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getReturnType() {
            return returnType;
        }

        public void setReturnType(int returnType) {
            this.returnType = returnType;
        }
    }
}



