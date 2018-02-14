package gamsung.traveller.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.adapter.CustomPagerAdapter;
import gamsung.traveller.adapter.CustomRecyclerAdapter;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.dao.PhotographManager;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.Route;
import gamsung.traveller.model.Spot;
import gamsung.traveller.util.DebugToast;

/**
 * Created by mj on 2018-01-24.
 * 문정이가 다 만들어줄 6,14,15화면
 */

public class EditLocationActivity extends AppCompatActivity {

    ArrayList<String> pathhhhhh; // = new ArrayList<>();
    private ImageButton eatBtn, buyBtn, takeBtn, visitBtn, anythingBtn, btnHome,btnSave;
    private Button btnNextPlan;
    private EditText memoEdit,tvMission;
    private TextView editLocation;
    private View layoutAddPhoto;
    private Button btnAddPhoto;
    private Button btnRepresent;
    private ImageView memoImage,eat,buy,take,visit,anything;
//    private ViewPager pager;
    private String imgPath;
    //private CustomPagerAdapter adapter;
    private CustomRecyclerAdapter _adapter;
    private boolean isEdit = false;
    public static Bitmap imgBitmap;
    private int CATEGORY_ID;
    private RecyclerView recyclerView;

    private HashMap<Integer, Photograph> photoList;
    private DataManager _dataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_location);

        _dataManager = DataManager.getInstance(this);

//        pager= (ViewPager)findViewById(R.id.pager);
        eatBtn= (ImageButton)findViewById(R.id.eatBtn);
        buyBtn = (ImageButton)findViewById(R.id.buyBtn);
        takeBtn = (ImageButton)findViewById(R.id.takeBtn);
        visitBtn = (ImageButton)findViewById(R.id.visitBtn);
        anythingBtn = (ImageButton)findViewById(R.id.anythingBtn);
        memoEdit = (EditText)findViewById(R.id.memoEdit);
        editLocation = (TextView)findViewById(R.id.editLocation);
        //tvMission = (EditText)findViewById(R.id.tvMission);
        btnHome = (ImageButton)findViewById(R.id.btn_cancel_set_travel);
        btnSave = (ImageButton)findViewById(R.id.btn_save_edit_location);
        btnNextPlan = (Button)findViewById(R.id.btnNextPlan);

        eat = (ImageView) findViewById(R.id.eat);
        buy = (ImageView)findViewById(R.id.buy);
        take = (ImageView)findViewById(R.id.photo);
        visit = (ImageView)findViewById(R.id.visit);
        anything = (ImageView)findViewById(R.id.anything);



//        Intent intent = getIntent();
//        String whatActivity = intent.getStringExtra("TAG_ACTIVITY");
//
//        if(whatActivity.equals("create")) {
//            isEdit = false;
//            recyclerView.setVisibility(View.GONE);
//            btnNextPlan.setVisibility(View.VISIBLE);
//        }
//
//        else if(whatActivity.equals("edit")) {
//            isEdit = true;
//            recyclerView.setVisibility(View.VISIBLE);
//            btnNextPlan.setVisibility(View.GONE);
//        }

        eatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eat.setVisibility(View.VISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.INVISIBLE);
                CATEGORY_ID = 0;
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
                CATEGORY_ID = 1;
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
                CATEGORY_ID = 2;
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
                CATEGORY_ID = 3;
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
                CATEGORY_ID = 4;
            }
        });

        List<String> temp = new ArrayList<>();
        _adapter = new CustomRecyclerAdapter(this, temp);

        RecyclerView recyclerView = findViewById(R.id.recycler_edit_lcoation);
        recyclerView.setAdapter(_adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        layoutAddPhoto = (View)findViewById(R.id.layout_add_on_empty_edit_location);
        layoutAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EditLocationActivity.this, CustomGalleryActivity.class);
                startActivityForResult(i,1);
            }
        });

        btnRepresent = (Button)findViewById(R.id.btn_represent_edit_location);
        btnRepresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnAddPhoto = (Button)findViewById(R.id.btn_add_photo_edit_location);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EditLocationActivity.this, CustomGalleryActivity.class);
                startActivityForResult(i,1);
            }
        });



        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEdit) {

                }

                else {
                    Spot newSpot = new Spot();
//                    newSpot.setCategory_id(CATEGORY_ID);
                    _dataManager.insertSpot(newSpot);
                }

                finish();
            }
        });

        btnNextPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if(memoEdit !=null)
            memoEdit.clearFocus();

        if(tvMission != null){
            tvMission.clearFocus();
            tvMission.requestFocus();
        }

        _dataManager = DataManager.getInstance(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK){

            imgPath = data.getExtras().getString("img");

             if(_adapter.addImagePath(imgPath) > 0){

                 Button btnRepresent = findViewById(R.id.btn_represent_edit_location);
                 if(btnRepresent.getVisibility() == View.INVISIBLE)
                    btnRepresent.setVisibility(View.VISIBLE);

                 Button btnAdd = findViewById(R.id.btn_add_photo_edit_location);
                 if(btnAdd.getVisibility() == View.INVISIBLE)
                    btnAdd.setVisibility(View.VISIBLE);

                 View layoutFrame = findViewById(R.id.layout_frame_edit_location);
                 if(layoutFrame.getVisibility() == View.VISIBLE)
                     layoutFrame.setVisibility(View.INVISIBLE);
             }
        }
    }

    public String getImgPath(){
        return imgPath;
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