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

    public void onClickTime(View view) {
        DialogFragment dialogFragment = new OnTimeFragment();
        dialogFragment.show(getSupportFragmentManager(), "show_time");
    }

}
