package gamsung.traveller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import gamsung.traveller.R;

/**
 * Created by mj on 2018-01-24.
 */

public class AddLocationActivity extends AppCompatActivity {

    ToggleButton eatBtn, doBtn, takeBtn, someBtn;
    EditText memoEdit;
    ImageView memoImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        eatBtn= (ToggleButton)findViewById(R.id.eatToggleBtn);
        doBtn = (ToggleButton)findViewById(R.id.doToggleBtn);
        takeBtn = (ToggleButton)findViewById(R.id.takeToggleBtn);
        someBtn = (ToggleButton)findViewById(R.id.somethingToggleBtn);
        memoEdit = (EditText)findViewById(R.id.memoEdit);
        memoImage = (ImageView)findViewById(R.id.memoImage);

        eatBtn.setOnCheckedChangeListener(addMemoListener);
        doBtn.setOnCheckedChangeListener(addMemoListener);
        takeBtn.setOnCheckedChangeListener(addMemoListener);
        someBtn.setOnCheckedChangeListener(addMemoListener);
    }
    public void setVisibleMemo(){
        memoEdit.setText(null);
        memoEdit.setVisibility(View.VISIBLE);
        memoImage.setVisibility(View.VISIBLE);
    }
    ToggleButton.OnCheckedChangeListener addMemoListener  = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if(compoundButton.isChecked()){
                switch (compoundButton.getId()){
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


}
