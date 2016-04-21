package com.example.matthew.mapdirections;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

public class NewDayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_day);
    }

    public void onClickNewDayStartTime(View view) {
        DialogFragment dialogFragment = new OnTimeFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                ((TextView) getActivity().findViewById(R.id.clockNewDayStart)).setText(hour+":"+minute);
            }
        };
        dialogFragment.show(getSupportFragmentManager(), "time_new_day_start");
    }

    public void onClickNewDayEndTime(View view) {
        DialogFragment dialogFragment = new OnTimeFragment() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                ((TextView)getActivity().findViewById(R.id.clockNewDayEnd)).setText(hour+":"+minute);
            }
        };
        dialogFragment.show(getSupportFragmentManager(), "time_new_day_start");
    }
}
