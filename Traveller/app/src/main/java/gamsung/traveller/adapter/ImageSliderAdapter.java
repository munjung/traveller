package gamsung.traveller.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import gamsung.traveller.R;

/**
 * Created by jekan on 2018-01-31.
 */

public class ImageSliderAdapter extends PagerAdapter {

    private ArrayList<String> images;
    private LayoutInflater inflater;
    private Context context;

    public ImageSliderAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images=images;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slider, view, false);
        ImageView myImage = (ImageView) myImageLayout.findViewById(R.id.slideimage);

        String item = images.get(position);
        if (!TextUtils.isEmpty(item)) {
            Glide.with(context).load(item).into(myImage);
        }

        //myImage.setImageResource(images.get(position));

        // myImage.setImageBitmap(images.get(position));
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
