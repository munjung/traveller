package gamsung.traveller.frag;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import gamsung.traveller.R;

/**
 * Created by jekan on 2018-01-30.
 */

public class CalendarFragment extends Fragment {
    private CalendarPickerView calendar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_calendar_picker, container, true);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        calendar = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);
        Date today = new Date();
        calendar.init(today, nextYear.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE);
        /*calendar.init(today, nextYear.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.RANGE);*/
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                //for(Date da: calendar.getSelectedDates()){
                //  Log.d("잘나오냐", da.toString());

                //이건 안될까?
                String month = (String)android.text.format.DateFormat.format("MM", date);
                String day = (String)android.text.format.DateFormat.format("dd", date);
                String year = (String)android.text.format.DateFormat.format("yyyy", date);

//                int month = date.getMonth()+1;
//                int day = date.getDay();
//                int year = date.getYear()+1900;
                Toast.makeText(getContext(), year+"년"+month+"월"+day+"일",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });
        return rootView;
    }

}
