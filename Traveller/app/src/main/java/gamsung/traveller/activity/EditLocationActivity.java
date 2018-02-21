package gamsung.traveller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.adapter.CustomRecyclerAdapter;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.Route;
import gamsung.traveller.model.SearchPlace;
import gamsung.traveller.model.Spot;

/**
 * Created by mj on 2018-01-24.
 * 문정이가 다 만들어줄 6,14,15화면
 */

public class EditLocationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String KEY_SEND_ACTIVITY_IMAGE_LIST = "img_list";
    public static final String KEY_SEND_ACTIVITY_MEMO_LIST = "memo_list";
    public static final String KEY_SEND_ACTIVITY_IMAGE_COUNT = "img_count";
    
    private final static int REQUEST_CODE_GO_ADD_PHOTO = 1;
    private final static int REQUEST_CODE_GO_MAP = 2;
    private final static int REQUEST_CODE_EMPTY = 3;

    private final static int CREATE_SPOT = 502;
    private final static int EDIT_SPOT = 503;
    private final static int MAP_SELECTED=10;
    
    private ImageView eatBtn, buyBtn, takeBtn, visitBtn, anythingBtn, btnHome,btnSave;
    private Button btnNextPlan;
    private EditText memoEdit,tvMission;
    private TextView editLocation, txtTitle;
    private View layoutAddPhoto;
    private Button btnAddPhoto;
    private ImageView eat,buy,take,visit,anything;
    
    private LinearLayout llGotoMap;
    
    private RecyclerView _recyclerView;
    private CustomRecyclerAdapter _adapter;
    private RelativeLayout photoRelative;

    private boolean isEdit = false;
    private int editRouteId = -1;
    private String editRotueTitle ="";
    private int editSpotId = -1;
    public int searchID=-1;
    private int CATEGORY_ID;
    private String picturePath;

    private List<Spot> spotList;
    private HashMap<Integer, Photograph> photoList;
    private DataManager _dataManager;

    public Bundle mbundle;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);


        _dataManager = DataManager.getInstance(this);
        mbundle = savedInstanceState;

        Intent intent = getIntent();
        this.editRouteId = intent.getIntExtra("route id", -1);
        this.editRotueTitle = intent.getStringExtra("route title");
        String whatActivity = intent.getStringExtra("TAG_ACTIVITY");
        if(whatActivity != null) {
            if (whatActivity.equals("create")) {

                //create spot
                this.isEdit = false;
            }
            else if (whatActivity.equals("empty")){

                //create spot
                this.isEdit = false;
                Intent i = new Intent(this, EmptyTravelActivity.class);
                i.putExtra("route id", this.editRouteId);
                i.putExtra("route title", this.editRotueTitle);
                startActivityForResult(i, REQUEST_CODE_EMPTY);
            }
            else {

                //edit spot
                this.isEdit = true;
                this.editSpotId = intent.getIntExtra("spot id", -1);
                if(this.editSpotId < 0){
                    //error
                    Log.e("edit spot id", "need edit spot id, not -1");
                    Toast.makeText(this, "error: need edit spot id, not -1", Toast.LENGTH_LONG).show();
                }
                else{
                    Spot spot = _dataManager.getSpotList().get(this.editSpotId);
                    this.searchID = spot.getSearch_id();
                    this.picturePath = spot.getPicture_path();
                }
            }
        }

        isEdit = true;

        this.registerListener();
        this.registerRecyclerView();
        this.visibleOperationForEditMode();
    }

    private void registerListener(){

        eatBtn= (ImageView)findViewById(R.id.eatBtn);
        buyBtn = (ImageView)findViewById(R.id.buyBtn);
        takeBtn = (ImageView)findViewById(R.id.takeBtn);
        visitBtn = (ImageView)findViewById(R.id.visitBtn);
        anythingBtn = (ImageView)findViewById(R.id.anythingBtn);
        memoEdit = (EditText)findViewById(R.id.memoEdit);
        editLocation = (TextView)findViewById(R.id.editLocation);
        photoRelative = (RelativeLayout) findViewById(R.id.photoRelative);
        txtTitle = (TextView) findViewById(R.id.txt_title_edit_location);
        if(this.editRouteId > 0) {
            Route route = _dataManager.getRouteWithID(this.editRouteId);
            txtTitle.setText(route.getTitle());
        }


        eat = (ImageView) findViewById(R.id.eat);
        buy = (ImageView)findViewById(R.id.buy);
        take = (ImageView)findViewById(R.id.photo);
        visit = (ImageView)findViewById(R.id.visit);
        anything = (ImageView)findViewById(R.id.anything);

        llGotoMap = (LinearLayout)findViewById(R.id.layoutLocation);

        llGotoMap.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditLocationActivity.this,MapActivity.class);
                startActivityForResult(intent,REQUEST_CODE_GO_MAP);
            }
        });


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


        layoutAddPhoto = (View)findViewById(R.id.layout_add_on_empty_edit_location);
        layoutAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EditLocationActivity.this, CustomGalleryActivity.class);
                startActivityForResult(i,REQUEST_CODE_GO_ADD_PHOTO);
            }
        });

        btnAddPhoto = (Button)findViewById(R.id.btn_add_photo_edit_location);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EditLocationActivity.this, CustomGalleryActivity.class);
                
                i.putExtra(KEY_SEND_ACTIVITY_IMAGE_COUNT, _adapter.getItemCount());
                startActivityForResult(i,REQUEST_CODE_GO_ADD_PHOTO);
            }
        });

        btnHome = (ImageButton)findViewById(R.id.btn_cancel_edit_location);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSave = (ImageButton)findViewById(R.id.btn_save_edit_location);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (memoEdit.getText().toString().equals("") || memoEdit.getText()==null) {
                    Toast.makeText(EditLocationActivity.this,"할일을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(editLocation.getText().toString().equals("") || editLocation.getText()==null) {
                    Toast.makeText(EditLocationActivity.this,"장소를 선택해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                else {

                    if (isEdit) {
                        updateSpot();

                    } else {
                        createSpot();
                    }

                    finish();
                }
            }
        });

        btnNextPlan = (Button)findViewById(R.id.btnNextPlan);
        btnNextPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memoEdit.getText().toString().equals("") || memoEdit.getText()==null) {
                    Toast.makeText(EditLocationActivity.this,"할일을 입력해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(editLocation.getText().toString().equals("") || editLocation.getText()==null) {
                    Toast.makeText(EditLocationActivity.this,"장소를 선택해주세요.",Toast.LENGTH_SHORT).show();
                    return;
                }

                else {
                    createSpot();
                    memoEdit.setText("");
                    editLocation.setText("");
                }
            }
        });
    }

    private void registerRecyclerView(){

        if(editSpotId > 0){

            ArrayList<Photograph> photoList = new ArrayList<>(_dataManager.getPhotoListWithSpot(editSpotId).values());
            _adapter = new CustomRecyclerAdapter(this, photoList, this);
        }
        else{
            _adapter = new CustomRecyclerAdapter(this, new ArrayList<Photograph>(), this);
        }

        _recyclerView = findViewById(R.id.recycler_edit_lcoation);
        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(_recyclerView);
    }

    private void visibleOperationForEditMode(){

        if(!isEdit){
            photoRelative.setVisibility(View.GONE);
            btnNextPlan.setVisibility(View.VISIBLE);
            memoEdit.requestFocus();
        }
        else{
            _recyclerView.setVisibility(View.VISIBLE);
            btnNextPlan.setVisibility(View.GONE);
        }

        if(memoEdit !=null)
            memoEdit.clearFocus();

        if(tvMission != null){
            tvMission.clearFocus();
            tvMission.requestFocus();
        }
    }

    private void updateSpot(){

//                        Bundle bundle = savedInstanceState;
//                        int route_id = bundle.getInt("route id");
//                        int spot_id = bundle.getInt("spot list");

        Spot editSpot = new Spot();
        editSpot.set_id(editSpotId);
        editSpot.setRoute_id(editRouteId);
        editSpot.setMission(memoEdit.getText().toString());
        editSpot.setSearch_id(searchID);
        editSpot.setCategory_id(CATEGORY_ID);
        editSpot.setPicture_path(picturePath);


        ArrayList<String> photolist = _adapter.getImgPathList();
        ArrayList<String> memolist = _adapter.getMemoList();
        for(int i=0;i<photolist.size();i++){
            Photograph photoforSet = new Photograph();
            photoforSet.setPath(photolist.get(i));
            photoforSet.setMemo(memolist.get(i));
            photoforSet.setRoute_id(editRouteId);
            photoforSet.setSpot_id(editSpotId);
            photoforSet.setSearch_id(searchID);
            photoforSet.setDate(new Date(System.currentTimeMillis()));
            _dataManager.insertPhoto(photoforSet);
        }


        //혹시나 싶어서 변수에 저장해보니 a엔 0이 뜬다
        int a = _dataManager.updateSpot(editSpot);


        //여기 if문으로 현재 들어갈 수가 없다 너무 슬퍼
        if(_dataManager.updateSpot(editSpot) > 0){

            Intent intent = new Intent();
            intent.putExtra("spot_id", editSpotId);
            setResult(EDIT_SPOT, intent);
            //finish();
            Toast.makeText(EditLocationActivity.this, "변경되었습니다", Toast.LENGTH_LONG);
        }
        else{

            Log.e("update spot", "error : not updated");
            Toast.makeText(EditLocationActivity.this, "error: not updated", Toast.LENGTH_LONG);
        }
    }

    private void createSpot() {

//                        Bundle bundle = savedInstanceState;
//                        int route_id = bundle.getInt("route id");

        Spot newSpot = new Spot();
        newSpot.setRoute_id(editRouteId);
        newSpot.setMission(memoEdit.getText().toString());
        newSpot.setSearch_id(searchID);
        newSpot.setCategory_id(CATEGORY_ID);
        newSpot.setPicture_path(picturePath);

        int spot_id = (int) _dataManager.insertSpot(newSpot);
        if (spot_id > 0) {
            Intent intent = new Intent(); //
            intent.putExtra("spot_id", spot_id);
            setResult(CREATE_SPOT, intent);
            //finish();//+
            Toast.makeText(EditLocationActivity.this, "추가되었습니다", Toast.LENGTH_LONG);
        } else {

            Log.e("insert spot", "error : not inserted");
            Toast.makeText(EditLocationActivity.this, "error: not updated", Toast.LENGTH_LONG);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_GO_MAP && resultCode == MAP_SELECTED){
            HashMap<Integer,SearchPlace> placelist = _dataManager.getSearchPlaceList();
            searchID =data.getIntExtra("placeID",0);
            SearchPlace searchPlace =placelist.get(searchID);
            editLocation.setText(searchPlace.getPlace_address());
        }

        if (requestCode == REQUEST_CODE_GO_ADD_PHOTO && resultCode == RESULT_OK){

            String imgPath = data.getExtras().getString("img");

             if(_adapter.addImagePath(imgPath) > 0){

                 Button btnAdd = findViewById(R.id.btn_add_photo_edit_location);
                 if(btnAdd.getVisibility() == View.INVISIBLE)
                    btnAdd.setVisibility(View.VISIBLE);

                 View layoutFrame = findViewById(R.id.layout_frame_edit_location);
                 if(layoutFrame.getVisibility() == View.VISIBLE)
                     layoutFrame.setVisibility(View.INVISIBLE);
             }
        }

        if(requestCode == REQUEST_CODE_EMPTY && resultCode == RESULT_CANCELED){
            //empty에서 취소
            finish();
        }
    }

    @Override
    public void onClick(View view) {

        CustomRecyclerAdapter.ViewHolderClickListenerArguments arguments = _adapter.getViewHolderClickListenerArgs();
        switch (arguments.getReturnType()){
            case CustomRecyclerAdapter.ViewHolderClickListenerArguments.RETURN_TYPE_CLICK_IMAGE:

                Intent intent = new Intent(EditLocationActivity.this, ImageSliderActivity.class);
                intent.putStringArrayListExtra(KEY_SEND_ACTIVITY_IMAGE_LIST, _adapter.getImgPathList());
                intent.putStringArrayListExtra(KEY_SEND_ACTIVITY_MEMO_LIST, _adapter.getMemoList());

                EditLocationActivity.this.startActivity(intent);
                break;

            case  CustomRecyclerAdapter.ViewHolderClickListenerArguments.RETURN_TYPE_CLICK_REPRESENT:
                picturePath = arguments.getItem().getPath();
                Log.d("test", picturePath);

               // _recyclerView.getview
                break;
        }
    }
}
