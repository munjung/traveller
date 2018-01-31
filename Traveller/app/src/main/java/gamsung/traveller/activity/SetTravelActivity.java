package gamsung.traveller.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import gamsung.traveller.R;
import gamsung.traveller.frag.CalendarFragment;

/**
 * Created by jekan on 2018-01-30.
 */

public class SetTravelActivity extends AppCompatActivity {

    CalendarFragment calendarFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_travel);
        calendarFragment = (CalendarFragment)getSupportFragmentManager().findFragmentById(R.id.dateFragment);
    }
}
