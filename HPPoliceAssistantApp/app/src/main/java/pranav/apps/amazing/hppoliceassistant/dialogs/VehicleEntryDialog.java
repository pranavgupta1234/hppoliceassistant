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

public class VehicleEntryDialog extends Dialog implements android.view.View.OnClickListener{

    private TextView vehicle_number,phone_number,date,time,place_name
            ,officer_name,description,date_and_time,EntryID,location;
    private Button edit,submit,offline;
    private Activity activity;
    private VehicleEntry vehicleEntry;

    public VehicleEntryDialog(Activity activity,VehicleEntry vehicleEntry) {
        super(activity);
        this.activity=activity;
        this.vehicleEntry=vehicleEntry;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.entry_dialog);
        setAllViews();
        edit.setOnClickListener(this);
        submit.setOnClickListener(this);
        setData();
    }

    private void setData() {
        EntryID.setText(vehicleEntry.getEntryID());
        location.setText("Latitude : "+vehicleEntry.getLatitude()+"\n Longitude : "+vehicleEntry.getLongitude());
        date_and_time.setText(vehicleEntry.getDate()+" at "+vehicleEntry.getTime());
        vehicle_number.setText(vehicleEntry.getVehicle_number());
        phone_number.setText(vehicleEntry.getPhone_number());
        date.setText(vehicleEntry.getDate());
        time.setText(vehicleEntry.getTime());
        place_name.setText(vehicleEntry.getName_of_place());
        officer_name.setText(vehicleEntry.getOfficer_name());
        description.setText(vehicleEntry.getDescription());

    }

    private void setAllViews() {
        vehicle_number=(TextView)findViewById(R.id.vehicle_num);
        phone_number=(TextView)findViewById(R.id.phone_no);
        date=(TextView)findViewById(R.id.date_picker);
        time=(TextView)findViewById(R.id.time);
        place_name=(TextView)findViewById(R.id.place_name);
        officer_name=(TextView)findViewById(R.id.officer_name);
        description=(TextView)findViewById(R.id.description);
        edit=(Button)findViewById(R.id.edit_challan);
        submit=(Button)findViewById(R.id.submit_challan);
        offline=(Button)findViewById(R.id.offline);
        EntryID=(TextView)findViewById(R.id.entry_id);
        date_and_time=(TextView)findViewById(R.id.date_and_time);
        location=(TextView)findViewById(R.id.location);
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.submit_challan:
               break;
           case  R.id.edit_challan:
               dismiss();
               break;
       }
    }
}
