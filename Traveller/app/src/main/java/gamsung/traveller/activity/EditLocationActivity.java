package gamsung.traveller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;

import gamsung.traveller.R;
import gamsung.traveller.adapter.CustomPagerAdapter;
import gamsung.traveller.dao.DataManager;


/**
 * Created by mj on 2018-01-24.
 * 문정이가 다 만들어줄 6,14,15화면
 */

public class EditLocationActivity extends AppCompatActivity {

    ImageButton eatBtn, buyBtn, takeBtn, visitBtn, anythingBtn, addImgBtn;
    EditText memoEdit,tvMission;
    TextView editLocation;
    ImageView memoImage,eat,buy,take,visit,anything;
    ViewPager pager;
    public static String imgPath;
    public static Bitmap imgBitmap;

    private DataManager _dataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);

        pager= (ViewPager)findViewById(R.id.pager);
        eatBtn= (ImageButton)findViewById(R.id.eatBtn);
        buyBtn = (ImageButton)findViewById(R.id.buyBtn);
        takeBtn = (ImageButton)findViewById(R.id.takeBtn);
        visitBtn = (ImageButton)findViewById(R.id.visitBtn);
        anythingBtn = (ImageButton)findViewById(R.id.anythingBtn);
        memoEdit = (EditText)findViewById(R.id.memoEdit);
//     memoImage = (ImageView)findViewById(R.id.memoImage);
        editLocation = (TextView)findViewById(R.id.editLocation);
        tvMission = (EditText)findViewById(R.id.tvMission);

        eat = (ImageView) findViewById(R.id.eat);
        buy = (ImageView)findViewById(R.id.buy);
        take = (ImageView)findViewById(R.id.photo);
        visit = (ImageView)findViewById(R.id.visit);
        anything = (ImageView)findViewById(R.id.anything);



        eatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eat.setVisibility(View.VISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.INVISIBLE);
            }
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eat.setVisibility(View.INVISIBLE);
                buy.setVisibility(View.VISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.INVISIBLE);
            }
        });

        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eat.setVisibility(View.INVISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.VISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.INVISIBLE);
            }
        });

        visitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eat.setVisibility(View.INVISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.VISIBLE);
                anything.setVisibility(View.INVISIBLE);
            }
        });

        anythingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eat.setVisibility(View.INVISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.VISIBLE);
            }
        });

        CustomPagerAdapter adapter= new CustomPagerAdapter(getLayoutInflater(), getApplicationContext());
        pager.setAdapter(adapter);

        memoEdit.clearFocus();
        tvMission.clearFocus();

        _dataManager = DataManager.getInstance(this);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String path = data.getExtras().getString("img");
        if (requestCode == 1 && resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(), "전달: "+path, Toast.LENGTH_SHORT).show();
            //imgPath = path;
            loadPicture(data);
           // Intent intent = new Intent(getApplicationContext(), CustomPagerAdapter.class);
            //intent.putExtra("path", imgPath);
           // startActivity(intent);
        }
    }

    public void loadPicture(Intent data){
        try {
            imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),  Uri.fromFile(new File(data.getExtras().getString("img"))));
            Log.d("1111", Uri.parse(data.getExtras().getString("img")).toString());
           // Uri.fromFile(new File(data.getExtras().getString("img")))
        } catch (IOException e) {
            e.printStackTrace();
        }
        // myBitmap = image_bitmap.copy(Bitmap.Config.ARGB_8888, true);
       // setImage.setAdjustViewBounds(true);
        //uploadImage.setImageBitmap(rotate(myBitmap, 0));
        // setImage.setImageBitmap(image_bitmap);
    }

}