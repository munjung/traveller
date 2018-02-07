package gamsung.traveller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import gamsung.traveller.R;
import gamsung.traveller.adapter.CustomPagerAdapter;
import gamsung.traveller.dao.DataManager;

/**
 * Created by mj on 2018-01-24.
 * 문정이가 다 만들어줄 6,14,15화면
 */

public class EditLocationActivity extends AppCompatActivity {

    ImageButton eatBtn, buyBtn, takeBtn, visitBtn, anythingBtn;
    EditText memoEdit,tvMission;
    TextView editLocation;
    ImageView memoImage,eat,buy,take,visit,anything;
    ViewPager pager;

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
                anything.setVisibility(View.INVISIBLE);
            }
        });

        CustomPagerAdapter adapter= new CustomPagerAdapter(getLayoutInflater());
        pager.setAdapter(adapter);

        memoEdit.clearFocus();
        tvMission.clearFocus();

        _dataManager = DataManager.getInstance(this);

    }


    public void setVisibleMemo(){
        memoEdit.setText(null);
        memoEdit.setVisibility(View.VISIBLE);
        //memoImage.setVisibility(View.VISIBLE);
    }

    /*

    ToggleButton.OnCheckedChangeListener addMemoListener  = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(compoundButton.isChecked()){
                switch (compoundButton.getId()) {
                    case R.id.eatToggleBtn:
                        doBtn.setChecked(false);
                        takeBtn.setChecked(false);
                        someBtn.setChecked(false);
                        setVisibleMemo();
                        break;

                    case R.id.doToggleBtn:
                        eatBtn.setChecked(false);
                        takeBtn.setChecked(false);
                        someBtn.setChecked(false);
                        setVisibleMemo();
                        break;

                    case R.id.takeToggleBtn:
                        eatBtn.setChecked(false);
                        someBtn.setChecked(false);
                        doBtn.setChecked(false);
                        setVisibleMemo();
                        break;

                    case R.id.somethingToggleBtn:
                        eatBtn.setChecked(false);
                        takeBtn.setChecked(false);
                        doBtn.setChecked(false);
                        setVisibleMemo();
                        break;
                }
            }
        }
    };
    */
}