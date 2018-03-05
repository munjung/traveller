package gamsung.traveller.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

public class TravelViewActivity extends AppCompatActivity {


    /**
     * 준규가 다 만들어줄 9,13화면
     */

    private static final int REQUEST_CODE_TO_EMPTY_ITEM = 101;
    private static final int REQUEST_CODE_TO_CAMERA = 102;

    private View btnGoToPicture, btnHome;
    private ImageButton btnCamera;
    private TextView textTitle;
    private ViewByPhotosFragment viewByPhotosFragment;
    private ViewByScheduleFragment viewByScheduleFragment;
    private android.support.v4.app.Fragment selectedFrag;


    private DataManager dataManager;
    private List<Integer> deletedSpotID, editedSpotID;
    private boolean isOrderChanged = false, isChangeMade = false;
    private int route_id, last_tab_pos = 0;
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
                if (last_tab_pos == 0) getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByScheduleFragment).commit();
                else getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByPhotosFragment).commit();
                if(resultCode == RESULT_OK){
                    //준규가 마법을 부릴 edit Location 리턴 결과
        //            add item (refresh)
        //            viewByScheduleFragment
        //            viewByPhotosFragment
                }
                else
                break;
            case REQUEST_CODE_TO_CAMERA:
                viewByPhotosFragment.forceUpdate();
                break;
        }
    }

    public void destroyScheduleFragment(){
        getSupportFragmentManager().beginTransaction().remove(viewByScheduleFragment).commit();
    }
    public void destroyPhotosFragment(){
        getSupportFragmentManager().beginTransaction().remove(viewByPhotosFragment).commit();
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

        findViews();

        if(spotList.size() == 0){
            //등록된 일정이 없는 경우, edit location으로 직행
            showEmptyTravelActivity();
        }
        else{
            implementEvents();
            getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByScheduleFragment).commit();
        }

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

    /*
     * Activity <-> Fragment
     */
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
    public void removeRootviewFromScheduleFragment(){
        viewByScheduleFragment = null;
        viewByScheduleFragment = new ViewByScheduleFragment();
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