package gamsung.traveller.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import gamsung.traveller.R;

/**
 * Created by Jiwon on 2018. 1. 24
 * 1번 화면
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.splashRelative);

        layout.setBackgroundDrawable(new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.bg)));

        Handler handler = new Handler();
        handler.postDelayed(new SplashHandler(), 1000);
    }


    private class SplashHandler implements Runnable {
        public void run() {
            Intent i = new Intent(SplashActivity.this,MapClusterActivity.class);
            startActivity(i);
            SplashActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recycleView(findViewById(R.id.splashRelative));
        System.gc();
    }

    private void recycleView(View view) {
        if (view != null) {
            Drawable bg = view.getBackground();
            if (bg != null) {
                bg.setCallback(null);
                ((BitmapDrawable) bg).getBitmap().recycle();
                view.setBackgroundDrawable(null);
            }
        }
    }
}
