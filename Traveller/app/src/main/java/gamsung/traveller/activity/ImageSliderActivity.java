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
    private String[] pathArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        Intent intent = getIntent();

        String pathBulk = intent.getStringExtra("ImgPath");
        pathBulk = pathBulk.replace("[","");
        pathBulk = pathBulk.replace("]","");
        pathBulk = pathBulk.replace(" ","");
        //selectImagePath= new ArrayList<>();
        pathArr = pathBulk.split(",");
        for(int i=0; i<pathArr.length; i++) {
            selectImagePath.add(pathArr[i]);
            Log.d("pagh", pathArr[i]); //넌뭐야
        }


        init();
    }
    private void init() {

       /* for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);*/

        mPager = (ViewPager) findViewById(R.id.pager);
      //  mPager.setAdapter(new ImageSliderAdapter(getApplicationContext(),XMENArray));

        mPager.setAdapter(new ImageSliderAdapter(getApplicationContext(),selectImagePath));
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
