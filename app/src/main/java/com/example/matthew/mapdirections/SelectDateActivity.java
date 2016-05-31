package com.example.matthew.mapdirections;

import android.app.ActionBar;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

public class SelectDateActivity extends AppCompatActivity {

    private LinearLayout[] linearLayouts;
    private LinearLayout rootLayout;
    private Calendar calendar;
    private Button[] buttons;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);
        buttons = new Button[35];
        rootLayout = (LinearLayout) findViewById(R.id.layoutSelectDateRoot);
        linearLayouts = new LinearLayout[7];
        int k = 0;
        for(int j = 0; j < 7; j++) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayouts[j] = linearLayout;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,0);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(layoutParams);
            for(int i = 0; i < 5; i++) {
                Button button  = new Button(this);
                buttons[k] = button;
                k++;
                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f);
                button.setBackground(getResources().getDrawable(R.drawable.calendar_button, null));
                buttonLayoutParams.setMargins(0, 0, 0, 0);

                button.setMinimumHeight(0);
                button.setMaxHeight(10);
                button.setLayoutParams(buttonLayoutParams);
                button.setMinimumWidth(0);
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                button.setMaxWidth(displayMetrics.widthPixels / 4);
                linearLayout.addView(button);
            }
            rootLayout.addView(linearLayout);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        calendar = Calendar.getInstance();
        buildCalendar();
    }

    public void onClickSelectDate(View view) {
        if(view.getId() == R.id.btnSelectDateMonthBack) {
            if(calendar.get(Calendar.MONTH) == 1) {
                calendar.set(Calendar.MONTH, 12);
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
            }
            else {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            }
        }
        else {
            if(calendar.get(Calendar.MONTH) == 12) {
                calendar.set(Calendar.MONTH, 1);
                calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1);
            }
            else {
                calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            }
        }
        buildCalendar();
    }

    private void buildCalendar() {
        TextView monthYear = ((TextView)findViewById(R.id.txtSelectDateMonth));
        monthYear.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, getResources().getConfiguration().locale) +
                calendar.get(Calendar.YEAR));

        for(int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            buttons[i].setText(Integer.toString(i + 1));
            buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button, null));
        }

        for(int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i < 35; i++) {
            buttons[i].setText("");
            buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button_off, null));
        }
    }

}
