package gamsung.traveller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import gamsung.traveller.R;
import gamsung.traveller.adapter.ImageSliderAdapter;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by jekan on 2018-01-31.
 */

public class ImageSliderActivity extends AppCompatActivity {

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN = {}; //= { R.drawable.cheeze,R.drawable.cheeze2,R.drawable.cheeze3};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();
    private ArrayList<String> selectImagePath = new ArrayList<>();
    private String[] arrr = new String[]{"","","","",""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        Intent intent = getIntent();
        selectImagePath.add(intent.getExtras().getString("ImgPath"));
        Log.d("사이즈", selectImagePath.size()+""); //왜 1개만나오지ㅣ 들어온게 3갠데
          /*for(int i=0; i<selectImagePath.size(); i++){
          //  selectImagePath.add(i,intent.getExtras().getString("ImgPath") );
            Log.d("ㅠㅠ", selectImagePath.get(i).toString());
        }*/

        init();
    }
    private void init() {

        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ImageSliderAdapter(getApplicationContext(),XMENArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
    }
}
