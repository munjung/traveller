package gamsung.traveller.activity;


import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.dto.TableManager;
import gamsung.traveller.frag.ViewByPhotosFragment;
import gamsung.traveller.frag.ViewByScheduleFragment;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.SearchPlace;
import gamsung.traveller.model.Spot;

import static gamsung.traveller.activity.MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_ID;
import static gamsung.traveller.activity.MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_TITLE;
import static gamsung.traveller.activity.MainActivity.KEY_SEND_TO_TRAVEL_STARTED_FROM_EMPTY;

public class TravelViewActivity extends AppCompatActivity {


    /**
     * 준규가 다 만들어줄 9,13화면
     */

    private static final int REQUEST_CODE_TO_EMPTY_ITEM = 101;
    private static final int REQUEST_CODE_TO_CAMERA = 102;
    private static final int EDIT_CANCELED = 501;

    private View btnGoToPicture, btnHome;
    private ImageButton btnCamera;
    private TextView textTitle;
    private ViewByPhotosFragment viewByPhotosFragment;
    private ViewByScheduleFragment viewByScheduleFragment;
    private android.support.v4.app.Fragment selectedFrag;
    public View layoutFrame;

    private DataManager dataManager;
    private List<Integer> deletedSpotID, editedSpotID;
    private boolean isOrderChanged = false, isChangeMade = false;
    private int route_id, last_tab_pos = 0;
    private String route_title;
    private List<Spot> spotList;
    private int frameHeight;
    private boolean isStartedFromEmpty;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_TO_EMPTY_ITEM:
                implementEvents();

                if(resultCode != RESULT_CANCELED){
//                    if (last_tab_pos == 0) getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByScheduleFragment).commit();
//                    else getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByPhotosFragment).commit();
                    restartActivity();
                }
                else finish();
                break;
            case REQUEST_CODE_TO_CAMERA:
                viewByPhotosFragment.forceUpdate();
                break;
        }
    }


    public void restartActivity(){
//        this.finish();
//        Intent intent = new Intent(this, TravelViewActivity.class);
//        intent.putExtra(KEY_SEND_TO_ACTIVITY_ROUTE_ID, route_id);
//        intent.putExtra(KEY_SEND_TO_ACTIVITY_ROUTE_TITLE, route_title);
//        startActivity(intent);
        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setResult(RESULT_CANCELED);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_view);

        dataManager = DataManager.getInstance(this);

        Intent intent = getIntent();
        route_id = intent.getIntExtra(MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_ID, 0);
        route_title = intent.getStringExtra(KEY_SEND_TO_ACTIVITY_ROUTE_TITLE);
        isStartedFromEmpty = intent.getBooleanExtra(KEY_SEND_TO_TRAVEL_STARTED_FROM_EMPTY, false);
        spotList = new ArrayList<Spot>(dataManager.getSpotListWithRouteId(route_id).values());

        findViews();
        frameHeight = 0;
        if(spotList.size() == 0){
            //등록된 일정이 없는 경우, edit location으로 직행
            showEmptyTravelActivity();
        }
        else{
            implementEvents();
            getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByScheduleFragment).commit();
        }


        if (isStartedFromEmpty) layoutFrame.getViewTreeObserver().addOnGlobalLayoutListener(frameLayoutListenerStartedFromEmpty);
        else layoutFrame.getViewTreeObserver().addOnGlobalLayoutListener(frameLayoutListener);


        deletedSpotID = new ArrayList<>();
        editedSpotID = new ArrayList<>();

    }


    public void showEmptyTravelActivity(){
        Intent editLocationIntent = new Intent(this, EditLocationActivity.class);
        editLocationIntent.putExtra("route id", route_id);
        editLocationIntent.putExtra("route title", route_title);
        editLocationIntent.putExtra("TAG_ACTIVITY", "empty");
        startActivityForResult(editLocationIntent, REQUEST_CODE_TO_EMPTY_ITEM);
    }


    private void findViews(){ //find friends
        btnGoToPicture = findViewById(R.id.btn_goto_picture_travle_view);
        btnHome = findViewById(R.id.btn_home_travle_view);
        textTitle = findViewById(R.id.txt_title_travel_view);

        layoutFrame = findViewById(R.id.containerTravelView);
        viewByPhotosFragment = new ViewByPhotosFragment();
        viewByScheduleFragment = new ViewByScheduleFragment();


    }

    private void implementEvents(){
        btnGoToPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TravelViewActivity.this, MapClusterActivity.class);
                startActivity(i);
            }
        });

        textTitle.setText(route_title);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //draw and implement events for the tab
        TabLayout tabsTravel = findViewById(R.id.tabsTravelView);

        tabsTravel.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                last_tab_pos = tab.getPosition();

                if (last_tab_pos  == 0)
                    selectedFrag = viewByScheduleFragment;
                else if (last_tab_pos  == 1)
                    selectedFrag = viewByPhotosFragment;

                if (isOrderChanged) updateOrdersToDB(last_tab_pos);
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
                startActivityForResult(i, REQUEST_CODE_TO_CAMERA);
            }
        });

    }

    private void updateOrdersToDB(int tabSelected){
        List<Spot> updatedSpotlist;
        if (tabSelected == 0) {
            updatedSpotlist = viewByPhotosFragment.getSpotList();
        }else{
            updatedSpotlist = viewByScheduleFragment.getSpotListFromSchedule();
        }
//        dataManager.updateSpotList((ArrayList<Spot>) updatedSpotlist);
        updateSpotlistToDB((ArrayList<Spot>) updatedSpotlist);

    }

    private ViewTreeObserver.OnGlobalLayoutListener frameLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            frameHeight = layoutFrame.getHeight();
            layoutFrame.getViewTreeObserver().removeOnGlobalLayoutListener(frameLayoutListener);
        }
    };

    private ViewTreeObserver.OnGlobalLayoutListener frameLayoutListenerStartedFromEmpty = new ViewTreeObserver.OnGlobalLayoutListener() {
        int cnt = 0;
        @Override
        public void onGlobalLayout() {//being called three times. toolbar, tab, frame
            if (cnt++ >= 4) layoutFrame.getViewTreeObserver().removeOnGlobalLayoutListener(frameLayoutListenerStartedFromEmpty);

            if (frameHeight == 0) frameHeight = layoutFrame.getHeight();
            else{
                if (frameHeight < layoutFrame.getHeight()){
                    frameHeight = layoutFrame.getHeight();
                    viewByScheduleFragment.notifyFrameHeightChanged();
                }
            }
//            layoutFrame.getViewTreeObserver().removeOnGlobalLayoutListener(frameLayoutListener);
        }
    };

    /*
     * Activity <-> Fragment
     */
    public int getFrameHeight(){
        return frameHeight;
    }
    public void updateSpotlistToDB(ArrayList<Spot> spotList){
        dataManager.updateSpotList(spotList);
    }
    public DataManager getDataManager(){
        return dataManager;
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
        spotList = new ArrayList<>(dataManager.getSpotListWithRouteId(route_id).values());
        Collections.sort(spotList, new CustomComparator());
        return spotList;
    }

    public void updateSpotFromDB(Spot spot){
        dataManager.updateSpot(spot);
    }
    public void deleteSpotFromDB(int spot_id){
        dataManager.deleteSpot(spot_id);
    }
    public String getSearchPlaceFromDB(int placeID){
        HashMap<Integer, SearchPlace> placeHashMap = dataManager.getSearchPlaceList();
        SearchPlace searchPlace = placeHashMap.get(placeID);
        return searchPlace.getPlace_name();
    }
}
class CustomComparator implements Comparator<Spot>{
    @Override
    public int compare(Spot spot, Spot t1) {
        return spot.getIndex_id() - t1.getIndex_id();
    }
}