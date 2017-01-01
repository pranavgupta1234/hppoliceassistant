package pranav.apps.amazing.hppoliceassistant;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by jahid on 12/10/15.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    String t;
    String m;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        TextView tv1=(TextView)getActivity().findViewById(R.id.time);
        TextView tv2=(TextView)getActivity().findViewById(R.id.time_entry);
        if(view.getCurrentHour()>=12){
             t = "PM";
        }
        else{
             t ="AM";
        }
        if(view.getCurrentMinute()<9){
            m="0"+view.getCurrentMinute();
        }
        else {
            m= String.valueOf(view.getCurrentMinute());
        }
        tv1.setText(view.getCurrentHour()+" : "+ m +" "+ t );
        tv2.setText(view.getCurrentHour()+" : "+ m +" "+ t );

    }
}
