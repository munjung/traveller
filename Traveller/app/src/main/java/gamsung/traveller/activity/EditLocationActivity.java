package gamsung.traveller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
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
    private final static int MAP_SELECTED = 10;

    private ImageView eatBtn, buyBtn, takeBtn, visitBtn, anythingBtn, btnHome, btnSave;
    private Button btnNextPlan;
    private EditText memoEdit;
    private TextView editLocation, txtTitle;
    private View layoutAddPhoto;
    private Button btnAddPhoto;
    private ImageView btnEditMission;
    private ImageView eat, buy, take, visit, anything;

    private LinearLayout llGotoMap;

    private RecyclerView _recyclerView;
    private CustomRecyclerAdapter _adapter;
    private RelativeLayout photoRelative;

    private boolean isEdit = false;
    private int editRouteId = -1;
    private String editRotueTitle = "";
    private int editSpotId = -1;
    private int editSpotIndex = 0;
    public int searchID = -1;
    private int CATEGORY_ID;
    public int photographId;
    private String picturePath;

    private List<Spot> spotList;
    private HashMap<String, Photograph> photoList;
    private DataManager _dataManager;

    public Bundle mbundle;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location);


        _dataManager = DataManager.getInstance(this);
        _dataManager.beginTrans();
        mbundle = savedInstanceState;

        this.registerListener();

        Intent intent = getIntent();
        this.editRouteId = intent.getIntExtra("route id", -1);
        this.editRotueTitle = intent.getStringExtra("route title");
        String whatActivity = intent.getStringExtra("TAG_ACTIVITY");
        if (whatActivity != null) {
            if (whatActivity.equals("create")) {
                //create spot
                this.editSpotIndex = intent.getIntExtra("spot index", -1);
                this.isEdit = false;
            } else if (whatActivity.equals("empty")) {

                //create spot
                this.isEdit = false;
                Intent i = new Intent(this, EmptyTravelActivity.class);
                i.putExtra("route id", this.editRouteId);
                i.putExtra("route title", this.editRotueTitle);
                startActivityForResult(i, REQUEST_CODE_EMPTY);
            } else {
                //edit spot
                this.isEdit = true;
                this.editSpotId = intent.getIntExtra("spot id", -1);
                this.editSpotIndex = intent.getIntExtra("spot index", -1);
                if (this.editSpotId < 0) {
                    //error
                    Log.e("edit spot id", "need edit spot id, not -1");
                    Toast.makeText(this, "error: need edit spot id, not -1", Toast.LENGTH_LONG).show();
                } else {
                    Spot spot = _dataManager.getSpotList().get(this.editSpotId);
                    this.searchID = spot.getSearch_id();

                    HashMap<Integer, SearchPlace> placelist = _dataManager.getSearchPlaceList();
                    SearchPlace searchPlace = placelist.get(searchID);
                    editLocation.setText(searchPlace.getPlace_address());
                    memoEdit.setText(spot.getMission());
                    setCategory(spot.getCategory_id());

                    this.picturePath = spot.getPicture_path();
                    this.photographId = spot.getPicture_id();
                }
            }
        }

        this.registerRecyclerView();
        this.visibleOperationForEditMode();

    }

    private void registerListener() {

        eatBtn = (ImageView) findViewById(R.id.eatBtn);
        buyBtn = (ImageView) findViewById(R.id.buyBtn);
        takeBtn = (ImageView) findViewById(R.id.takeBtn);
        visitBtn = (ImageView) findViewById(R.id.visitBtn);
        anythingBtn = (ImageView) findViewById(R.id.anythingBtn);
        memoEdit = (EditText) findViewById(R.id.memoEdit);
        editLocation = (TextView) findViewById(R.id.editLocation);
        photoRelative = (RelativeLayout) findViewById(R.id.photoRelative);
        txtTitle = (TextView) findViewById(R.id.txt_title_edit_location);
        if (this.editRouteId > 0) {
            Route route = _dataManager.getRouteWithID(this.editRouteId);
            txtTitle.setText(route.getTitle());
        }


        eat = (ImageView) findViewById(R.id.eat);
        buy = (ImageView) findViewById(R.id.buy);
        take = (ImageView) findViewById(R.id.photo);
        visit = (ImageView) findViewById(R.id.visit);
        anything = (ImageView) findViewById(R.id.anything);

        llGotoMap = (LinearLayout) findViewById(R.id.layoutLocation);

        llGotoMap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditLocationActivity.this, MapActivity.class);
                startActivityForResult(intent, REQUEST_CODE_GO_MAP);
            }
        });


        eatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CATEGORY_ID = 0;
                setCategory(CATEGORY_ID);
            }
        });

        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CATEGORY_ID = 1;
                setCategory(CATEGORY_ID);
            }
        });

        takeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CATEGORY_ID = 2;
                setCategory(CATEGORY_ID);
            }
        });

        visitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CATEGORY_ID = 3;
                setCategory(CATEGORY_ID);
            }
        });

        anythingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CATEGORY_ID = 4;
                setCategory(CATEGORY_ID);
            }
        });

        btnEditMission = (ImageView) findViewById(R.id.btn_edit_to_do);
        btnEditMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memoEdit.requestFocus();
            }
        });


        layoutAddPhoto = (View) findViewById(R.id.layout_add_on_empty_edit_location);
        layoutAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EditLocationActivity.this, CustomGalleryActivity.class);
                startActivityForResult(i, REQUEST_CODE_GO_ADD_PHOTO);
            }
        });

        btnAddPhoto = (Button) findViewById(R.id.btn_add_photo_edit_location);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EditLocationActivity.this, CustomGalleryActivity.class);
                i.putExtra(KEY_SEND_ACTIVITY_IMAGE_COUNT, _adapter.getItemCount());
                startActivityForResult(i, REQUEST_CODE_GO_ADD_PHOTO);
            }
        });

        btnHome = (ImageButton) findViewById(R.id.btn_cancel_edit_location);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                onBackPressed();
             }
        });

        btnSave = (ImageButton) findViewById(R.id.btn_save_edit_location);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (memoEdit.getText().toString().equals("") || memoEdit.getText() == null) {
                    Toast.makeText(EditLocationActivity.this, "할일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editLocation.getText().toString().equals("") || editLocation.getText() == null) {
                    Toast.makeText(EditLocationActivity.this, "장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {

                    if (isEdit) {
                        updateSpot();

                    } else {
                        createSpot();
                    }
                    _dataManager.commit();
                    finish();
                }
            }
        });

        btnNextPlan = (Button) findViewById(R.id.btnNextPlan);
        btnNextPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (memoEdit.getText().toString().equals("") || memoEdit.getText() == null) {
                    Toast.makeText(EditLocationActivity.this, "할일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (editLocation.getText().toString().equals("") || editLocation.getText() == null) {
                    Toast.makeText(EditLocationActivity.this, "장소를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    createSpot();
                    memoEdit.setText("");
                    editLocation.setText("");
                }
            }
        });
    }


    private void registerRecyclerView() {

        if (editSpotId > 0) {

            photoList = _dataManager.getPhotoListToStringWithSpot(editSpotId);
            ArrayList<Photograph> photographs = new ArrayList<Photograph>(photoList.values());
            _adapter = new CustomRecyclerAdapter(this, photographs, this);
            for(int i=0; i<photographs.size(); i++){

                if(this.picturePath!=null) {
                    if(this.picturePath.equals(photographs.get(i).getPath()))
                        _adapter.set_representedImagePosition(i);
                }
            }

        } else {
            _adapter = new CustomRecyclerAdapter(this, new ArrayList<Photograph>(), this);
        }

        _recyclerView = findViewById(R.id.recycler_edit_lcoation);
        _recyclerView.setAdapter(_adapter);
        _recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(_recyclerView);
    }

    private void visibleOperationForEditMode() {

        if (!isEdit) {
            photoRelative.setVisibility(View.GONE);
            btnNextPlan.setVisibility(View.VISIBLE);
            memoEdit.requestFocus();
        } else {

           if (_adapter.getImgPathList().size() > 0) {

                Button btnAdd = findViewById(R.id.btn_add_photo_edit_location);
                if (btnAdd.getVisibility() == View.INVISIBLE)
                    btnAdd.setVisibility(View.VISIBLE);

                View layoutFrame = findViewById(R.id.layout_frame_edit_location);
                if (layoutFrame.getVisibility() == View.VISIBLE)
                    layoutFrame.setVisibility(View.INVISIBLE);
            }

            btnNextPlan.setVisibility(View.GONE);
        }

        if (memoEdit != null)
            memoEdit.clearFocus();
    }

    private void updateSpot() {

        Spot editSpot = new Spot();
        editSpot.set_id(editSpotId);
        editSpot.setRoute_id(editRouteId);
        editSpot.setMission(memoEdit.getText().toString());
        editSpot.setSearch_id(searchID);
        editSpot.setCategory_id(CATEGORY_ID);
        editSpot.setIndex_id(editSpotIndex);
            ArrayList<Photograph> itemList = _adapter.getItems();

            for (int i = 0; i < itemList.size(); i++) {
                Photograph photo = itemList.get(i);
                photo.setRoute_id(editRouteId);
                photo.setSpot_id(editSpotId);
                photo.setSearch_id(searchID);

                if (photoList != null) {
                    if (photoList.containsKey(photo.getPath()))
                        _dataManager.updatePhoto(photo);
                    else {

                        _dataManager.insertPhoto(photo);
                    }

                    if (photographId == 0) {
                        picturePath = itemList.get(0).getPath();
                        photographId = itemList.get(0).get_id();
                    }
                }

                editSpot.setPicture_id(photographId);
                editSpot.setPicture_path(picturePath);


                if (_dataManager.updateSpot(editSpot) > 0) {

                    Intent intent = new Intent();
                    intent.putExtra("spot_id", editSpotId);
                    setResult(EDIT_SPOT, intent);
                    //finish();
                    Toast.makeText(EditLocationActivity.this, "변경되었습니다.", Toast.LENGTH_LONG).show();
                } else {

                    Log.e("update spot", "error : not updated");
                    Toast.makeText(EditLocationActivity.this, "error: not updated", Toast.LENGTH_LONG).show();
                }
            }
    }

    private void createSpot() {

        Spot newSpot = new Spot();
        newSpot.setRoute_id(editRouteId);
        newSpot.setMission(memoEdit.getText().toString());
        newSpot.setSearch_id(searchID);
        newSpot.setCategory_id(CATEGORY_ID);
        newSpot.setPicture_id(photographId);
        newSpot.setPicture_path(picturePath);
        
        int spot_id = (int) _dataManager.insertSpot(newSpot, ++editSpotIndex);
        if (spot_id > 0) {
            Intent intent = new Intent(); //
            intent.putExtra("spot_id", spot_id);
            setResult(CREATE_SPOT, intent);
            Toast.makeText(EditLocationActivity.this, "추가되었습니다.", Toast.LENGTH_LONG).show();
        } else {

            Log.e("insert spot", "error : not inserted");
            Toast.makeText(EditLocationActivity.this, "error: not updated", Toast.LENGTH_LONG).show();
        }
    }

    private void setCategory(int CATEGORY_ID) {

        switch (CATEGORY_ID) {
            case 0:
                eat.setVisibility(View.VISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.INVISIBLE);
                break;
            case 1:
                eat.setVisibility(View.INVISIBLE);
                buy.setVisibility(View.VISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.INVISIBLE);
                break;
            case 2:
                eat.setVisibility(View.INVISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.VISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.INVISIBLE);
                break;
            case 3:
                eat.setVisibility(View.INVISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.VISIBLE);
                anything.setVisibility(View.INVISIBLE);
                break;
            case 4:
                eat.setVisibility(View.INVISIBLE);
                buy.setVisibility(View.INVISIBLE);
                take.setVisibility(View.INVISIBLE);
                visit.setVisibility(View.INVISIBLE);
                anything.setVisibility(View.VISIBLE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GO_MAP && resultCode == MAP_SELECTED) {
            HashMap<Integer, SearchPlace> placelist = _dataManager.getSearchPlaceList();
            searchID = data.getIntExtra("placeID", 0);
            SearchPlace searchPlace = placelist.get(searchID);
            editLocation.setText(searchPlace.getPlace_address());
        }

        if (requestCode == REQUEST_CODE_GO_ADD_PHOTO && resultCode == RESULT_OK) {

            String imgPath = data.getExtras().getString("img");

            if (_adapter.addImagePath(imgPath) > 0) {

                Button btnAdd = findViewById(R.id.btn_add_photo_edit_location);
                if (btnAdd.getVisibility() == View.INVISIBLE)
                    btnAdd.setVisibility(View.VISIBLE);

                View layoutFrame = findViewById(R.id.layout_frame_edit_location);
                if (layoutFrame.getVisibility() == View.VISIBLE)
                    layoutFrame.setVisibility(View.INVISIBLE);
            }
        }

        if (requestCode == REQUEST_CODE_EMPTY && resultCode == RESULT_CANCELED) {
            //empty에서 취소
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
    }

    public void onBackPressed() {
        _dataManager.rollback();
        finish();
    }

    @Override
    public void onClick(View view) {

        CustomRecyclerAdapter.ViewHolderClickListenerArguments arguments = _adapter.getViewHolderClickListenerArgs();
        switch (arguments.getReturnType()) {
            case CustomRecyclerAdapter.ViewHolderClickListenerArguments.RETURN_TYPE_CLICK_IMAGE:

                Intent intent = new Intent(EditLocationActivity.this, ImageSliderActivity.class);
                intent.putExtra("title", txtTitle.getText().toString());
                intent.putStringArrayListExtra(KEY_SEND_ACTIVITY_IMAGE_LIST, _adapter.getImgPathList());
                intent.putStringArrayListExtra(KEY_SEND_ACTIVITY_MEMO_LIST, _adapter.getMemoList());

                EditLocationActivity.this.startActivity(intent);
                break;

            case CustomRecyclerAdapter.ViewHolderClickListenerArguments.RETURN_TYPE_CLICK_REPRESENT:
                picturePath = arguments.getItem().getPath();
                photographId = arguments.getItem().get_id();
                _adapter.notifyDataSetChanged();
                break;

            case CustomRecyclerAdapter.ViewHolderClickListenerArguments.RETURN_TYPE_CLICK_REMOVE:
                Log.d("present position: ", arguments.getPosition() + "");

                if(_adapter.getItemCount()==0){
                    View layoutFrame = findViewById(R.id.layout_frame_edit_location);
                    if (layoutFrame.getVisibility() == View.INVISIBLE)
                        layoutFrame.setVisibility(View.VISIBLE);
                   //_adapter.notifyDataSetChanged();
                }

                if (_adapter.get_representedImagePosition() == arguments.getPosition()) {
                    _adapter.set_representedImagePosition(-1);
                    picturePath = "";
                }
                int photoId = arguments.getItem().get_id();
                _dataManager.deletePhoto(photoId);
                _adapter.notifyDataSetChanged();

                break;
        }
    }
}
