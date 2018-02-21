package gamsung.traveller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

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
    private static final Integer[] XMEN = {};
    private ArrayList<String> selectImagePath = new ArrayList<>();
    private ArrayList<String> selectMemoPath = new ArrayList<>();
    private ImageButton btnHome;
    private TextView titleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);
        btnHome= (ImageButton)findViewById(R.id.btnHome);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        titleText = (TextView)findViewById(R.id.txt_image_slider_title);

        Intent intent = getIntent();
        String title = intent.getExtras().getString("title");
        ArrayList<String> imgPath = intent.getStringArrayListExtra(EditLocationActivity.KEY_SEND_ACTIVITY_IMAGE_LIST);
        ArrayList<String> memoPath = intent.getStringArrayListExtra(EditLocationActivity.KEY_SEND_ACTIVITY_MEMO_LIST);

        titleText.setText(title);
        for (int i=0; i<imgPath.size(); i++){
            selectImagePath.add(imgPath.get(i));
        }

        for (int i=0; i<memoPath.size(); i++){
            selectMemoPath.add(memoPath.get(i));
        }

        init();
    }
    private void init() {

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ImageSliderAdapter(getApplicationContext(),selectImagePath,selectMemoPath));
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
