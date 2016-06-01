package com.example.matthew.mapdirections;

import android.app.ActionBar;
import android.app.Activity;
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

import java.util.Calendar;

public class SelectDateActivity extends AppCompatActivity {

    private LinearLayout[] linearLayouts;
    private LinearLayout rootLayout;
    private Calendar calendar;
    private Button[] buttons;
    private RequestQueue queue;
    int[] selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);
        selectedDate = new int[3];
        queue = Volley.newRequestQueue(this);
        buttons = new Button[35];
        rootLayout = (LinearLayout) findViewById(R.id.layoutSelectDateRootCal);
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
        selectedDate[0] = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate[1] = calendar.get(Calendar.MONTH);
        selectedDate[2] = calendar.get(Calendar.YEAR);
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

    private void buildCalendar() {
        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.layoutSelectDateRoot);
        rootLayout.removeView(findViewById(R.id.btnSelectDateConfirm));
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setId(R.id.progressBarDate);
        rootLayout.addView(progressBar, 2);

        String start_date = new String(Integer.toString(selectedDate[2]) + "-" + Integer.toString(selectedDate[1]) +
                "-" + Integer.toString(selectedDate[0]));

        Calendar incCalender = increaseByDay(calendar);
        String end_date = new String(Integer.toString(incCalender.get(Calendar.YEAR)) + "-"
                + Integer.toString(incCalender.get(Calendar.MONTH)) +
                "-" + Integer.toString(incCalender.get(Calendar.DAY_OF_MONTH)));


        //TODO: Why does this silently fail when 500 error returned
        StringRequest request = new StringRequest(Request.Method.GET, "http://178.62.116.27/disruption?" +
                "city=London&travel_mode=transit&start_date=" + start_date + "&end_date=" + end_date,
                new ListenerExtended<String>(this) {
                    @Override
                    public void onResponse(final String response) {
                        ((Activity) c).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LinearLayout rootLayout = (LinearLayout) findViewById(R.id.layoutSelectDateRoot);
                                rootLayout.removeView(findViewById(R.id.progressBarDate));
                                Button button = new Button(c);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                button.setId(R.id.btnSelectDateConfirm);
                                button.setLayoutParams(layoutParams);
                                button.setText("Confirm Date");
                                rootLayout.addView(button);

                                TextView monthYear = ((TextView) findViewById(R.id.txtSelectDateMonth));
                                monthYear.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, getResources().getConfiguration().locale) +
                                        calendar.get(Calendar.YEAR));

                                for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                                    buttons[i].setText(Integer.toString(i + 1));
                                    buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button, null));
                                }

                                for (int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i < 35; i++) {
                                    buttons[i].setText("");
                                    buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button_off, null));
                                }
                                try {
                                    JSONArray disruptions = new JSONArray(response);

                                    for(int i = 0; i < disruptions.length(); i++) {
                                        JSONArray statuses = disruptions.getJSONObject(i).getJSONArray("statuses");
                                        for(int j = 0; j < statuses.length(); j++) {
                                            if(statuses.getJSONObject(j).getInt("severity") == 5) {
                                                JSONObject severity = statuses.getJSONObject(j);
                                                markCalendar(severity.getJSONArray("validity_periods"));
                                            }
                                        }
                                    }

                                }
                                catch (Exception e) {}

                            }
                        });
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

    private void markCalendar(JSONArray dates) {
        //TODO: Sort out which calendar should be used for destination

        //TODO: Change so that disruption gets checked upon activity load and stored in table and checked on month change
        for(int i = 0; i < dates.length(); i++) {
            try {
                String start_date = dates.getJSONObject(i).getString("fromDate");
                String end_date = dates.getJSONObject(i).getString("toDate");

                Calendar start_calendar = Calendar.getInstance();
                start_calendar.set(Calendar.YEAR, Integer.parseInt(start_date.substring(0,2)));
                start_calendar.set(Calendar.MONTH, Integer.parseInt(start_date.substring(3,5)));
                start_calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(start_date.substring(6)));

                Calendar end_calendar = Calendar.getInstance();
                end_calendar.set(Calendar.YEAR, Integer.parseInt(start_date.substring(0,2)));
                end_calendar.set(Calendar.MONTH, Integer.parseInt(start_date.substring(3,5)));
                end_calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(start_date.substring(6)));


            }
            catch (Exception e) {}
        }
    }

}
