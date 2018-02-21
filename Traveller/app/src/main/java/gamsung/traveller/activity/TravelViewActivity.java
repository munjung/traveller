package gamsung.traveller.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.dto.TableManager;
import gamsung.traveller.frag.ViewByPhotosFragment;
import gamsung.traveller.frag.ViewByScheduleFragment;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.Spot;

import static gamsung.traveller.activity.MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_ID;

public class TravelViewActivity extends AppCompatActivity {

    /**
     * 준규가 다 만들어줄 9,13화면
     */

    private static final int REQUEST_CODE_TO_EMPTY_ITEM = 101;

    private ImageButton btnAddLocation, btnHome, btnCamera;
    private ViewSwitcher viewSwitcher;
    private EditText editTitle;
    private TextView textTitle;
    private ViewByPhotosFragment viewByPhotosFragment;
    private ViewByScheduleFragment viewByScheduleFragment;
    private android.support.v4.app.Fragment selectedFrag;


    private DataManager dataManager;
    private List<Integer> deletedSpotID, editedSpotID;
    private boolean isOrderChanged = false, isChangeMade = false;
    private int route_id;
    private String route_title;
    private List<Spot> spotList;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_TO_EMPTY_ITEM:
                //isChangeMade = true;
                //viewByScheduleFragment.force_update();
                implementEvents();
                getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByScheduleFragment).commit();
                if(resultCode == RESULT_OK){
                    //준규가 마법을 부릴 edit Location 리턴 결과
        //            add item (refresh)
        //            viewByScheduleFragment
        //            viewByPhotosFragment
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_view);

        dataManager = DataManager.getInstance(this);

        Intent intent = getIntent();
        route_id = intent.getIntExtra(MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_ID, 0);
        route_title = intent.getStringExtra(MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_TITLE);
        spotList = new ArrayList<Spot>(dataManager.getSpotListWithRouteId(route_id).values());
/*
        spotList = new ArrayList<>();
        for (int i = 0; i < 15; i++){
            Spot spot = new Spot();
            spot.set_id(i);
            spot.setMission("Number: " + i);
            spot.setRoute_id(route_id);
            spotList.add(spot);
        }*/
        findViews();

        if(spotList.size() == 0){
            //등록된 일정이 없는 경우, edit location으로 직행
            Intent editLocationIntent = new Intent(this, EditLocationActivity.class);
            editLocationIntent.putExtra("route id", route_id);
            editLocationIntent.putExtra("route title", route_title);
            editLocationIntent.putExtra("TAG_ACTIVITY", "empty");
            startActivityForResult(editLocationIntent, REQUEST_CODE_TO_EMPTY_ITEM);
        }
        else{
            implementEvents();
            getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByScheduleFragment).commit();
        }

        deletedSpotID = new ArrayList<>();
        editedSpotID = new ArrayList<>();

        //findViews();
//        implementEvents();

//        getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByScheduleFragment).commit(); //set the schedule_fragment as the default
    }

    private void findViews(){ //find friends
        btnAddLocation = findViewById(R.id.btnAddLocation);
        btnHome = findViewById(R.id.btnHome);
        viewSwitcher = findViewById(R.id.viewSwitcher);
        editTitle = findViewById(R.id.editTitle);
        textTitle = findViewById(R.id.textTitle);

        viewByPhotosFragment = new ViewByPhotosFragment();
        viewByScheduleFragment = new ViewByScheduleFragment();
    }

    private void implementEvents(){
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TravelViewActivity.this, EditLocationActivity.class);
                startActivity(i);
            }
        });
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //Switches between edit and text
        textTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.showNext();
            }
        });
        editTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textTitle.setText(editTitle.getText().toString());
                viewSwitcher.showNext();
            }
        });
        //end of switches

        editTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //hide keyboard and switch views when done
                if (i == EditorInfo.IME_ACTION_DONE){
                    viewSwitcher.showNext();
                    textTitle.setText(editTitle.getText().toString());
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(editTitle.getWindowToken(), inputManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });
        editTitle.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                //when enter is detected while editing the title
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getAction() == keyEvent.KEYCODE_ENTER){
                    viewSwitcher.showNext();
                    textTitle.setText(editTitle.getText().toString());
                    editTitle.clearFocus();
                    return true;
                }
                return false;
            }
        });

        //draw and implement events for the tab
        TabLayout tabsTravel = findViewById(R.id.tabsTravelView);
        //tabsTravel.setSelectedTabIndicatorColor(R.drawable.common_google_signin_btn_icon_dark_normal_background);
        tabsTravel.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();

                if (pos == 0)
                    selectedFrag = viewByScheduleFragment;
                else if (pos == 1)
                    selectedFrag = viewByPhotosFragment;

                getSupportFragmentManager().beginTransaction().replace(R.id.containerTravelView, selectedFrag).commit();
                getSupportFragmentManager().beginTransaction().addToBackStack(null);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btnCamera = (ImageButton) findViewById(R.id.btnTakePic);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TravelViewActivity.this, CameraActivity.class);
                startActivity(i);
            }
        });

    }


    /*
     * Activity <-> Fragment
     */


    public List<Spot> getSpotList(){
        return spotList;
    }
    public List<Integer> getDeletedSpotID(){
        return deletedSpotID;
    }
    public List<Integer> getEditedSpotID(){
        return editedSpotID;
    }
    public boolean isOrderChanged() {
        return isOrderChanged;
    }
    public void setOrderChanged(boolean orderChanged){
        isOrderChanged = orderChanged;
    }
    public int getRoute_id(){
        return route_id;
    }
    public boolean getChangeMade(){
        return  isChangeMade;
    }
    public void setChangeMade(boolean isChangeMade){
        this.isChangeMade = isChangeMade;
    }
    public HashMap<Integer, Photograph> getImageListWithSpot(int spot_id){
        return dataManager.getPhotoListWithSpot(spot_id);
    }
    public List<Spot> refreshSpotList(){
        Toast.makeText(getApplicationContext(), "Spotlist updated.", Toast.LENGTH_SHORT).show();
        return new ArrayList<>(dataManager.getSpotListWithRouteId(route_id).values());
        //return spotList; //temporarily
    }
    public void updateSpotFromDB(Spot spot){
        dataManager.updateSpot(spot);
    }
    public void deleteSpotFromDB(int spot_id){
        dataManager.deleteSpot(spot_id);
    }

}
