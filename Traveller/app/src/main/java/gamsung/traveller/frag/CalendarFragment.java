package gamsung.traveller.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        calendar = (CalendarPickerView) rootView.findViewById(R.id.calendar_view);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        calendar.init(new Date(), nextYear.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE);
        /*calendar.init(today, nextYear.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.RANGE);*/

        return rootView;
    }

    public void setCalendarSelectedListener(CalendarPickerView.OnDateSelectedListener listener){
        calendar.setOnDateSelectedListener(listener);
    }

    public List<Date> getSelectedDates(){
        return calendar.getSelectedDates();
    }

    public void setSelectedDates(Date goDate, Date backDate){
        calendar.selectDate(goDate);
        calendar.selectDate(backDate);
    }
}
