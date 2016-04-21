package com.example.matthew.mapdirections;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Calendar;

public class SelectTimeActivity extends FragmentActivity {

    int minute = 0;
    int hour = 19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
    }

    public void onClickStartTime(View view) {
        DialogFragment dialogFragment = new OnTimeFragment();
        dialogFragment.show(getSupportFragmentManager(), "show_time");
    }

    public void onClickEndTime(View view)
    {
        DialogFragment dialogFragment = new OnTimeFragment() {
          @Override
          public void onTimeSet(TimePicker view, int hour, int minute)
          {
              ((TextView)getActivity().findViewById(R.id.txtEnd)).setText(hour+":"+minute);
          }
        };

        dialogFragment.show(getSupportFragmentManager(), "show_end_time");
    }
}
