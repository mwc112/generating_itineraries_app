package com.example.matthew.mapdirections;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class SelectDateActivity extends AppCompatActivity {

    //TODO: Run all UI on UI thread
    //TODO: Resolve all methods too new for target API

    private LinearLayout quitLayout;
    private ProgressBar busyBar;
    private Calendar calendar;
    private Button[] buttons;
    private RequestQueue queue;
    private int[] selectedDate;
    private boolean selected = false;
    private SparseBooleanArray disruptions;
    public static String SELECT_DATE_DATE = "com.example.matthew.mapdirection.SELECT_DATE_DATE";
    private static final String TAG = "SelectDateActivity";
    private boolean useTfl = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);
        disruptions = new SparseBooleanArray();
        selectedDate = new int[3];
        queue = Volley.newRequestQueue(this);
        buttons = new Button[35];
        LinearLayout calLayout = (LinearLayout) findViewById(R.id.layoutSelectDateRootCal);
        quitLayout = (LinearLayout) findViewById(R.id.layoutExitBtnsSelectDate);

        busyBar = new ProgressBar(this);
        busyBar.setIndeterminate(true);

        findViewById(R.id.btnSelectDateMonthBack).setEnabled(false);
        findViewById(R.id.btnSelectDateMonthForward).setEnabled(false);

        Button confirmButton = (Button) findViewById(R.id.btnSelectDateConfirm);
        LinearLayout.LayoutParams quitBtnLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,
                0.5f);
        confirmButton.setLayoutParams(quitBtnLayout);
        confirmButton.setText(R.string.confirm_btn);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected) {
                    Intent result = new Intent();
                    result.putExtra(SELECT_DATE_DATE, selectedDate);
                    setResult(RESULT_OK, result);
                    finish();
                    Log.i(TAG, "Date selected");
                }
                else {
                    Log.i(TAG, "No date selected");
                    Toast.makeText
                            (SelectDateActivity.this.getBaseContext(),
                                    R.string.select_date_none_selected,
                                    Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.btnSelectDateCancel);
        cancelButton.setLayoutParams(quitBtnLayout);
        cancelButton.setText(R.string.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                setResult(RESULT_CANCELED, result);
                finish();
                Log.i(TAG, "Date selected cancelled");
            }
        });


        int k = 0;
        for(int j = 0; j < 7; j++) {
            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,0);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setLayoutParams(layoutParams);
            for(int i = 0; i < 5; i++) {
                Button button  = new Button(this);
                buttons[k] = button;
                button.setId(Button.generateViewId());
                k++;
                button.setEnabled(false);
                LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 0.2f);
                button.setBackground(getResources().getDrawable(R.drawable.calendar_button, null));
                buttonLayoutParams.setMargins(0, 0, 0, 0);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedDate[0] = v.getId() + 1;
                        selected = true;
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
            calLayout.addView(linearLayout);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        calendar = Calendar.getInstance();
        selectedDate[0] = calendar.get(Calendar.DAY_OF_MONTH);
        selectedDate[1] = calendar.get(Calendar.MONTH);
        selectedDate[2] = calendar.get(Calendar.YEAR);

        String start_date = Integer.toString(selectedDate[2]) + "-" + Integer.toString(selectedDate[1]) +
                "-" + Integer.toString(selectedDate[0]);

        String end_date = Integer.toString(selectedDate[2] + 1) + "-"
                + Integer.toString(selectedDate[1]) +
                "-" + Integer.toString(selectedDate[0]);

        ////////////////////////////////////////////////////////////////////////////////
        //TODO: DON'T ALLOW BUTTON PRESS UNTIL HAVE RECEIVED DISRUPTIONS FROM SERVER!!//
        ////////////////////////////////////////////////////////////////////////////////

        //TODO: Why does this silently fail when 500 error returned
        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.layoutSelectDateRoot);
        rootLayout.removeView(quitLayout);
        rootLayout.addView(busyBar);
        StringRequest request = new StringRequest(Request.Method.GET, "http://178.62.46.132/disruption?" +
                "city=London&travel_mode=transit&start_date=" + start_date + "&end_date=" + end_date,
                new ListenerExtended<String>(this) {
                    @Override
                    public void onResponse(final String response) {
                                try {
                                    Log.i(TAG, "Received TfL disruption information");

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
                                            LinearLayout rootLayout = (LinearLayout) findViewById(R.id.layoutSelectDateRoot);
                                            rootLayout.removeView(busyBar);
                                            rootLayout.addView(quitLayout);
                                            buildCalendar();
                                            findViewById(R.id.btnSelectDateMonthForward).setEnabled(true);
                                            findViewById(R.id.btnSelectDateMonthBack).setEnabled(true);
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
                        Log.e(TAG, "Error: Received error response from server");
                        useTfl = false;
                        ((Activity)c).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), R.string.select_date_no_response,
                                        Toast.LENGTH_LONG).show();
                                LinearLayout rootLayout = (LinearLayout) findViewById(R.id.layoutSelectDateRoot);
                                rootLayout.removeView(busyBar);
                                rootLayout.addView(quitLayout);
                                buildCalendar();
                                findViewById(R.id.btnSelectDateMonthForward).setEnabled(true);
                                findViewById(R.id.btnSelectDateMonthBack).setEnabled(true);
                            }
                        });
                    }
                });

        queue.add(request);
    }

    public void onClickSelectDate(View view) {
        findViewById(R.id.btnSelectDateMonthForward).setEnabled(false);
        findViewById(R.id.btnSelectDateMonthBack).setEnabled(false);

        if(view.getId() == R.id.btnSelectDateMonthBack) {
            if(calendar.get(Calendar.MONTH) == 12) {
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
        selectedDate[1] = calendar.get(Calendar.MONTH);
        selectedDate[2] = calendar.get(Calendar.YEAR);

        Log.i(TAG, "Date selected");

        buildCalendar();
        findViewById(R.id.btnSelectDateMonthForward).setEnabled(true);
        findViewById(R.id.btnSelectDateMonthBack).setEnabled(true);
    }

    private void buildCalendar() {

        TextView monthYear = ((TextView) findViewById(R.id.txtSelectDateMonth));
        String displayMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                getResources().getConfiguration().locale);
        String displayYear = Integer.toString(calendar.get(Calendar.YEAR));
        monthYear.setText(String.format(getResources()
                .getString(R.string.select_date_show_format), displayMonth, displayYear));

        if(useTfl) {
            for (int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                buttons[i].setText(getResources().getString(R.string.number,
                        i + 1));
                buttons[i].setEnabled(true);

                if (disruptions.get(calendar.get(Calendar.YEAR) * 500 + calendar.get(Calendar.MONTH) * 35 + i + 1)) {
                    buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button_dis, null));
                } else {
                    buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button, null));
                }
            }
        }
        else {
            for(int i = 0; i < calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                buttons[i].setText(getResources().getString(R.string.number,
                        i + 1));
                buttons[i].setEnabled(true);
                buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button_off_border, null));
            }
        }

        for (int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i < 35; i++) {
            buttons[i].setText("");
            buttons[i].setEnabled(true);
            buttons[i].setBackground(getResources().getDrawable(R.drawable.calendar_button_off, null));
        }
        Log.i(TAG, "Calendar built");
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
