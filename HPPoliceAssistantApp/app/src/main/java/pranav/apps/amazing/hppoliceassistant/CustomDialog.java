package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EdgeEffect;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by Pranav Gupta on 12/30/2016.
 */

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    private TextView violator,owner,address,violator_number,vehicle_number,offence_sec,lic_num,challan_am,name_of_place,off_name,offences;
    private Button edit,submit;
    private ChallanDetails challanDetails;
    private Activity activity;

    public CustomDialog(Activity activity, ChallanDetails challanDeatils) {
        super(activity);
        this.challanDetails=challanDeatils;
        this.activity= activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.challan_dialog);
        setAllTextViews();
        setButtons();
        edit.setOnClickListener(this);
        submit.setOnClickListener(this);
        setAllValues();

    }

    private void setAllValues() {
        violator.setText(challanDetails.getViolator_name());
        owner.setText(challanDetails.getOwner_name());
        address.setText(challanDetails.getViolator_address());
        violator_number.setText(challanDetails.getViolator_number());
        vehicle_number.setText(challanDetails.getVehicle_number());
        offence_sec.setText(challanDetails.getOffences_section());
        lic_num.setText(challanDetails.getLicense_number());
        challan_am.setText(challanDetails.getChallan_amount());
        name_of_place.setText(challanDetails.getName_of_place());
        off_name.setText(challanDetails.getPolice_officer_name());
        offences.setText(challanDetails.getOffences());
    }

    private void setButtons() {
        edit=(Button)findViewById(R.id.edit_challan);
        submit=(Button)findViewById(R.id.submit_challan);
    }

    private void setAllTextViews() {
        violator=(TextView)findViewById(R.id.violator_name);
        owner=(TextView)findViewById(R.id.owner_name);
        address=(TextView)findViewById(R.id.violator_address);
        violator_number=(TextView)findViewById(R.id.violator_number);
        vehicle_number=(TextView)findViewById(R.id.vehicle_number);
        offence_sec=(TextView)findViewById(R.id.offence_section);
        lic_num=(TextView)findViewById(R.id.license_number);
        challan_am=(TextView)findViewById(R.id.challan_amount);
        name_of_place=(TextView)findViewById(R.id.name_of_place);
        off_name=(TextView)findViewById(R.id.police_officer_name);
        offences=(TextView)findViewById(R.id.offences);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_challan:
                dismiss();
                break;
            case R.id.submit_challan:
                break;
        }

    }
}
