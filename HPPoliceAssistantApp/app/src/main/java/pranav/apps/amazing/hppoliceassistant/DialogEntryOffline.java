package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Pranav Gupta on 12/31/2016.
 */

public class DialogEntryOffline extends Dialog implements android.view.View.OnClickListener{

    private TextView vehicle_number,phone_number,date,time,place_name,officer_name,naka_name,description;
    private Button send,delete;
    private Activity activity;
    private VehicleEntry vehicleEntry;

    public DialogEntryOffline(Activity activity,VehicleEntry vehicleEntry) {
        super(activity);
        this.activity=activity;
        this.vehicleEntry=vehicleEntry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_entry_offline);
        setAllViews();
        setData();
       // send.setOnClickListener(this);
    }

    private void setData() {
        vehicle_number.setText(vehicleEntry.getVehicle_number());
        phone_number.setText(vehicleEntry.getPhone_number());
        date.setText(vehicleEntry.getDate());
        time.setText(vehicleEntry.getTime());
        place_name.setText(vehicleEntry.getName_of_place());
        officer_name.setText(vehicleEntry.getOfficer_name());
        naka_name.setText(vehicleEntry.getNaka_name());
        description.setText(vehicleEntry.getDescription());

    }

    private void setAllViews() {
        vehicle_number=(TextView)findViewById(R.id.vehicle_num);
        phone_number=(TextView)findViewById(R.id.phone_no);
        date=(TextView)findViewById(R.id.date_picker);
        time=(TextView)findViewById(R.id.time);
        place_name=(TextView)findViewById(R.id.place_name);
        officer_name=(TextView)findViewById(R.id.officer_name);
        naka_name=(TextView)findViewById(R.id.naka_name);
        description=(TextView)findViewById(R.id.description);
        send=(Button)findViewById(R.id.send);
        delete=(Button)findViewById(R.id.delete);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.send:
                break;

        }
    }
}
