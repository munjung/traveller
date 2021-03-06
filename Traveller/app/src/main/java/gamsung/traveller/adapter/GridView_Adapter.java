package gamsung.traveller.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

import gamsung.traveller.R;
import gamsung.traveller.activity.CustomGalleryActivity;

/**
 * Created by jekan on 2018-02-01.
 */

public class GridView_Adapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imageUrls;
    private SparseBooleanArray mSparseBooleanArray;//Variable to store selected Images
    private DisplayImageOptions options;
    private boolean isCustomGalleryActivity;//Variable to check if gridview is to setup for Custom Gallery or not
    private CheckBox mCheckBox;
    private int addedImageCount;

    public GridView_Adapter(Context context, ArrayList<String> imageUrls, boolean isCustomGalleryActivity) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.isCustomGalleryActivity = isCustomGalleryActivity;
        mSparseBooleanArray = new SparseBooleanArray();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }


    public GridView_Adapter(Context context, ArrayList<String> imageUrls, boolean isCustomGalleryActivity, int addedImageCount) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.isCustomGalleryActivity = isCustomGalleryActivity;
        mSparseBooleanArray = new SparseBooleanArray();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .resetViewBeforeLoading(true).cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        this.addedImageCount = addedImageCount;
    }

    //Method to return selected Images
    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for (int i = 0; i < imageUrls.size(); i++) {
            if (mSparseBooleanArray.get(i)) {
                mTempArry.add(imageUrls.get(i));
            }
        }
        return mTempArry;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return imageUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.custom_gridview_item, viewGroup, false);//Inflate layout

        mCheckBox = (CheckBox) view.findViewById(R.id.selectCheckBox);

        final ImageView imageView = (ImageView) view.findViewById(R.id.galleryImageView);

        //If Context is MainActivity then hide checkbox
        if (!isCustomGalleryActivity)
            mCheckBox.setVisibility(View.GONE);

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this.context)); //추가
        ImageLoader.getInstance().displayImage("file://" + imageUrls.get(position), imageView, options);//Load Images over ImageView
        mCheckBox.setTag(position);//Set Tag for CheckBox
        mCheckBox.setChecked(mSparseBooleanArray.get(position));
        mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);

        return view;
    }

    CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            //사진갯수 제한은 삭제가 가능할때 반영합시다!
//            if(getCheckedItems().size() + addedImageCount > 5){
//                Toast.makeText(context, "선택된 사진은 5장을 넘길 수 없습니다", Toast.LENGTH_LONG).show();
//                return;
//            }
            mSparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);//Insert selected checkbox value inside boolean array
            ((CustomGalleryActivity) context).showSelectButton();//call custom gallery activity method
        }

    };

}
