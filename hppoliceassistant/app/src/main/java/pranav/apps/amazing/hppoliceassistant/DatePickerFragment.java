package pranav.apps.amazing.hppoliceassistant;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;


/**
 * Created by Pranav
 */
public class DatePickerFragment extends DialogFragment
         implements DatePickerDialog.OnDateSetListener {

    private String[] months = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        TextView tv1= (TextView) getActivity().findViewById(R.id.date);
        tv1.setText(view.getDayOfMonth()+", "+months[view.getMonth()]+" "+view.getYear());

    }
}