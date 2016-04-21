package com.example.matthew.mapdirections;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

/**
 * Created by matthew on 21/04/16.
 */
public class OnTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new TimePickerDialog(getActivity(), this, 19, 0,true);
    }

    public void onTimeSet(TimePicker view, int hour, int minute) {
    }

}
