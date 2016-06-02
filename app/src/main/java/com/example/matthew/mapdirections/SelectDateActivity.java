package com.example.matthew.mapdirections;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import net.sf.json.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class SelectDateActivity extends AppCompatActivity {

    private LinearLayout[] linearLayouts;
    private LinearLayout rootLayout;
    private Calendar calendar;
    private Button[] buttons;
    private int[] buttonIds;
    private RequestQueue queue;
    int[] selectedDate;
    private HashMap<Integer, Boolean> disruptions;
    public static String SELECT_DATE_DATE = "com.example.matthew.mapdirection.SELECT_DATE_DATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);
        disruptions = new HashMap<>();
        selectedDate = new int[3];
        queue = Volley.newRequestQueue(this);
        buttons = new Button[35];
        rootLayout = (LinearLayout) findViewById(R.id.layoutSelectDateRootCal);
        linearLayouts = new LinearLayout[7];
        buttonIds = new int[35];
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
                buttonIds[k] = button.generateViewId();
                button.setId(buttonIds[k]);
                k++;
                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f);
                button.setBackground(getResources().getDrawable(R.drawable.calendar_button, null));
                buttonLayoutParams.setMargins(0, 0, 0, 0);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedDate[0] = v.getId() + 1;
                        buildCalendar();
                        v.setBackground(getResources().getDrawable(R.drawable.calendar_button_selected, null));
                    }
                });
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
        selectedDate[0] = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate[1] = calendar.get(Calendar.MONTH);
        selectedDate[2] = calendar.get(Calendar.YEAR);

        String start_date = new String(Integer.toString(selectedDate[2]) + "-" + Integer.toString(selectedDate[1]) +
                "-" + Integer.toString(selectedDate[0]));

        String end_date = new String(Integer.toString(selectedDate[2] + 1) + "-"
                + Integer.toString(selectedDate[1]) +
                "-" + Integer.toString(selectedDate[0]));

        ////////////////////////////////////////////////////////////////////////////////
        //TODO: DON'T ALLOW BUTTON PRESS UNTIL HAVE RECEIVED DISRUPTIONS FROM SERVER!!//
        ////////////////////////////////////////////////////////////////////////////////

        //TODO: Why does this silently fail when 500 error returned
        LinearLayout actualrootLayout = (LinearLayout)findViewById(R.id.layoutSelectDateRoot);
        actualrootLayout.removeView(findViewById(R.id.btnSelectDateConfirm));
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setId(R.id.progressBarDate);
        actualrootLayout.addView(progressBar, 2);
        StringRequest request = new StringRequest(Request.Method.GET, "http://178.62.116.27/disruption?" +
                "city=London&travel_mode=transit&start_date=" + start_date + "&end_date=" + end_date,
                new ListenerExtended<String>(this) {
                    @Override
                    public void onResponse(final String response) {
                                try {
                                    JSONArray disruptions_res = new JSONArray(response);

                                    for (int i = 0; i < disruptions_res.length(); i++) {
                                        JSONArray statuses = disruptions_res.getJSONObject(i).getJSONArray("statuses");
                                        for (int j = 0; j < statuses.length(); j++) {
                                            if (statuses.getJSONObject(j).getInt("severity") == 5) {
                                                JSONObject status = statuses.getJSONObject(j);
                                                for (int k = 0; k < status.getJSONArray("validityPeriods").length(); k++) {
                                                    JSONObject validity = status.getJSONArray("validityPeriods").getJSONObject(k);
                                                    JSONObject fromDate = validity.getJSONObject("fromDate");
                                                    JSONObject toDate = validity.getJSONObject("toDate");

                                                    int conv_from_date = fromDate.getInt("year") * 500
                                                            + fromDate.getInt("month") * 35
                                                            + fromDate.getInt("day");
                                                    int conv_to_date = toDate.getInt("year") * 500
                                                            + toDate.getInt("month") * 35
                                                            + toDate.getInt("day");

                                                    for(int l = conv_from_date; l <= conv_to_date; l++) {
                                                        disruptions.put(l, true);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    ((Activity)c).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            LinearLayout actualrootLayout = (LinearLayout) findViewById(R.id.layoutSelectDateRoot);
                                            actualrootLayout.removeView(findViewById(R.id.progressBarDate));
                                            Button button = new Button(SelectDateActivity.this);
                                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                            button.setId(R.id.btnSelectDateConfirm);
                                            button.setLayoutParams(layoutParams);
                                            button.setText("Confirm Date");
                                            button.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent result = new Intent();
                                                    result.putExtra(SELECT_DATE_DATE, selectedDate);
                                                    setResult(RESULT_OK, result);
                                                    finish();
                                                }
                                            });
                                            actualrootLayout.addView(button);
                                            buildCalendar();
                                        }
                                    });
                                }
                                catch (Exception e)
                                {int x =2;}
                    }
                },
                new ErrorListenerExtended(this) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        int x = 2;
                    }
                });

        queue.add(request);
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

        TextView monthYear = ((TextView) findViewById(R.id.txtSelectDateMonth));
        monthYear.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, getResources().getConfiguration().locale) +
                calendar.get(Calendar.YEAR));

        for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            buttons[i].setText(Integer.toString(i + 1));

            if(disruptions.get(calendar.get(Calendar.YEAR) * 500 + calendar.get(Calendar.MONTH) * 35 + i + 1) != null
                    && disruptions.get(calendar.get(Calendar.YEAR) * 500 + calendar.get(Calendar.MONTH) * 35 + i + 1) != false) {
                buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button_dis, null));
            }
            else {
                buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button, null));
            }
        }

        for (int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i < 35; i++) {
            buttons[i].setText("");
            buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button_off, null));
        }

    }

    private Calendar increaseByDay(Calendar calendar) {
        Calendar incCalender = Calendar.getInstance();
        if(incCalender.get(Calendar.DAY_OF_MONTH) == incCalender.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            incCalender.set(Calendar.DAY_OF_MONTH, 1);
            if(incCalender.get(Calendar.MONTH) == 12) {
                incCalender.set(Calendar.MONTH, 1);
                incCalender.set(Calendar.YEAR, incCalender.get(Calendar.YEAR) + 1);
            }
            else {
                incCalender.set(Calendar.MONTH, incCalender.get(Calendar.MONTH) + 1);
            }
        }
        else {
            incCalender.set(Calendar.DAY_OF_MONTH, incCalender.get(Calendar.DAY_OF_MONTH) + 1);
        }
        return incCalender;
    }


}
