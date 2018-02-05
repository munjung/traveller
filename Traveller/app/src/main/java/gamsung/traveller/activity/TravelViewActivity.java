package gamsung.traveller.activity;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.dto.TableManager;
import gamsung.traveller.frag.ViewByPhotosFragment;
import gamsung.traveller.frag.ViewByScheduleFragment;
import gamsung.traveller.model.Spot;

public class TravelViewActivity extends AppCompatActivity {

    /**
     * 준규가 다 만들어줄 9,13화면
     */

    private Button btnAddLocation, btnHome;
    private ViewSwitcher viewSwitcher;
    private EditText editTitle;
    private TextView textTitle;
    private ViewByPhotosFragment viewByPhotosFragment;
    private ViewByScheduleFragment viewByScheduleFragment;
    private android.support.v4.app.Fragment selectedFrag;


    //temp code activity<->fragment
    private List<Spot> tempSpotList;
    private DataManager dataManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_view);

        dataManager = DataManager.getInstance(this);

        //*temp code
        Spot spot = new Spot();
        spot.setMission("gamsung misson");
        Spot spot1 = new Spot();
        spot1.setMission("jk gamsung misson");

        tempSpotList = new ArrayList<>();
        tempSpotList.add(spot);
        tempSpotList.add(spot1);



        findViews();
        implementEvents();

        getSupportFragmentManager().beginTransaction().add(R.id.containerTravelView, viewByScheduleFragment).commit(); //set the schedule_fragment as the default
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
        tabsTravel.addTab(tabsTravel.newTab().setText("일정별"));
        tabsTravel.addTab(tabsTravel.newTab().setText("사진별"));

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

        FloatingActionButton fabTakePic = findViewById(R.id.fabTakePic);
        fabTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "floating clicked", Toast.LENGTH_LONG).show();
            }
        });
    }


    /*
     * Activity <-> Fragment
     */
    public List<Spot> getSpotList(){
        return tempSpotList;
    }

    public List<String> getImageListWithSpot(int spot_id){
        return dataManager.getPhotoListWithSpot(spot_id);
    }
}
