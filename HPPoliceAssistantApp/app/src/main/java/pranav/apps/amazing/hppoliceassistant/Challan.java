package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Challan extends Fragment {


    //variables to be used
    private Firebase mRootRef;
    private CheckBox helmet,rc,insurance,license,rash_drive,mobile,number_plate,horn,seat_belt,triple_riding,
            idle_parking,restricted_park;
    private EditText other,offence_section,veh_number,place_name,challan_amount,naka_name,owner_name,violator_name
            ,violator_address,license_number,policeofficer_name,violator_number;
    private TextView date,time;
    private Button submit,reset;
    private ImageButton upload_photo;
    private String details,section,no_veh,place_n,naka_n;
    private TextView nak;
    private Uri uri=null,downloadUrl=null;
    private String download_url_string="";
    private String crime="";
    private StorageReference mStorage,filepath;
    private static final int GALLERY_INTENT =2;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;
    private static final int CAMERA_REQUEST = 1888;



    private CustomDialog customDialog;

    //object to store challan details
    private ChallanDetails challanDetails, challanDetailswithoutImage;



    @Override


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {


       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mRootRef = new Firebase("https://hppoliceassistant.firebaseio.com/challan");

        View  view=  getActivity().getLayoutInflater().inflate(R.layout.challan,container,false);


        helmet=(CheckBox)view.findViewById(R.id.helmet);
        rc=(CheckBox)view.findViewById(R.id.rc);
        insurance=(CheckBox)view.findViewById(R.id.insurance);
        license=(CheckBox)view.findViewById(R.id.license);
        rash_drive=(CheckBox)view.findViewById(R.id.rash_drive);
        mobile=(CheckBox)view.findViewById(R.id.mobile);
        number_plate=(CheckBox)view.findViewById(R.id.number_plate);
        seat_belt=(CheckBox)view.findViewById(R.id.seat_belt);
        horn=(CheckBox)view.findViewById(R.id.pressure_horn);
        triple_riding=(CheckBox)view.findViewById(R.id.triple_riding);
        idle_parking=(CheckBox)view.findViewById(R.id.idle_parking);
        restricted_park=(CheckBox)view.findViewById(R.id.restricted);


        other=(EditText)view.findViewById(R.id.other_offence);
        offence_section=(EditText)view.findViewById(R.id.offence_section);
        veh_number=(EditText)view.findViewById(R.id.vehicle_number);
        place_name=(EditText)view.findViewById(R.id.place_name);
        naka_name=(EditText)view.findViewById(R.id.naka_name);
        policeofficer_name=(EditText)view.findViewById(R.id.officer_name);
        owner_name=(EditText)view.findViewById(R.id.vehicle_owner_name);
        violator_name=(EditText)view.findViewById(R.id.violator_name);
        violator_address=(EditText)view.findViewById(R.id.violator_address);
        license_number=(EditText)view.findViewById(R.id.license_number);
        challan_amount=(EditText)view.findViewById(R.id.challan_amount);
        violator_number=(EditText)view.findViewById(R.id.violator_number);
        date=(TextView)view.findViewById(R.id.date);
        time=(TextView)view.findViewById(R.id.time);

        upload_photo=(ImageButton) view.findViewById(R.id.upload_photo);
        reset=(Button)view.findViewById(R.id.reset);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog1 = new ProgressDialog(getActivity());
        nak=(TextView)view.findViewById(R.id.nak_name);
        mStorage = FirebaseStorage.getInstance().getReference();
        submit=(Button)view.findViewById(R.id.submit);


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(view);
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAll();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helmet.isChecked()){
                    crime  =  crime + "w/o Helmet," ;
                }
                if(insurance.isChecked()){
                    crime  = crime + "w/o Insurance,";
                }
                if(license.isChecked()){
                    crime  = crime + "w/o License,";
                }
                if(rc.isChecked()){
                    crime = crime+ "w/o RC,";
                }
                if(rash_drive.isChecked()){
                    crime  = crime + "Rash Ans Negligent Driving,";
                }
                if(mobile.isChecked()){
                    crime = crime + "Using Mobile during driving,";
                }
                if(number_plate.isChecked()){
                    crime = crime + "w/o NumberPlate,";
                }
                if(horn.isChecked()){
                    crime = crime + "Using Pressure Horn,";
                }
                if(seat_belt.isChecked()){
                    crime = crime + "w/o SeatBelt,";
                }
                if(triple_riding.isChecked()){
                    crime = crime + "Triple Riding,";
                }
                if(idle_parking.isChecked()){
                    crime = crime + "Idle Parking,";
                }
                if(restricted_park.isChecked()){
                    crime = crime+ "Restricted Area Parking,";
                }

                challanDetailswithoutImage = new ChallanDetails(violator_name.getText().toString(),
                        crime,owner_name.getText().toString(),violator_address.getText().toString(),
                        veh_number.getText().toString(),place_name.getText().toString(),
                        offence_section.getText().toString(),challan_amount.getText().toString(),
                        license_number.getText().toString(),policeofficer_name.getText().toString(),
                        "disrict","policeStation",other.getText().toString(),"null",
                        violator_number.getText().toString(),date.getText().toString(),time.getText().toString());

                //instantiate dialog box
                customDialog = new CustomDialog(getActivity(),challanDetailswithoutImage);
                //customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customDialog.setTitle("Challan Details");
                customDialog.setCancelable(true);
                customDialog.show();
                customDialog.findViewById(R.id.offline).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //local DB
                        final DBManagerChallan dbManagerChallan = new DBManagerChallan(getActivity(),null,null,1);
                        if(dbManagerChallan.addChallan(challanDetailswithoutImage)){
                            Toast.makeText(getActivity(),"Challan Added Offline!",Toast.LENGTH_SHORT).show();
                            customDialog.dismiss();
                        }
                    }
                });
                customDialog.findViewById(R.id.submit_challan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startPosting();
                        customDialog.dismiss();
                    }
                });


                //nak.setText("Done");
                /*
                UUID idOne = UUID.randomUUID();
                UUID idTwo = UUID.randomUUID();
                UUID idThree = UUID.randomUUID();
                UUID idFour = UUID.randomUUID();
                String time = idOne.toString().replace("-", "");
                String time2 = idTwo.toString().replace("-", "");
                String time3 = idThree.toString().replace("-", "");
                String time4 = idFour.toString().replace("-", "");
                StringBuffer data = new StringBuffer();
                data.append(time);
                data.append(time2);
                data.append(time3);
                data.append(time4);

                SecureRandom random = new SecureRandom();
                int beginIndex = random.nextInt(100);       //Begin index + length of your string < data length
                int endIndex = beginIndex + 10;            //Length of string which you want
                String ID = data.substring(beginIndex, endIndex);
                Firebase idChild = mRootRef.child(ID);
                */
            }
        });
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
                /*
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                */
            }
        });
        return  view;
    }
    public void startPosting(){
        final Firebase idChild = mRootRef.push();

        if(uri == null){

            Toast.makeText(getActivity(),"No Photo Selected, Uploading Data...",Toast.LENGTH_LONG).show();
            download_url_string ="Photo not available";
            challanDetails = new ChallanDetails(violator_name.getText().toString(),
                    crime,owner_name.getText().toString(),violator_address.getText().toString(),
                    veh_number.getText().toString(),place_name.getText().toString(),
                    offence_section.getText().toString(),challan_amount.getText().toString(),
                    license_number.getText().toString(),policeofficer_name.getText().toString(),
                    "disrict","policeStation",other.getText().toString(),download_url_string,violator_number.getText().toString(),date.getText().toString(),time.getText().toString());
                    //local DB
                    final DBManagerChallan dbManagerChallan = new DBManagerChallan(getActivity(),null,null,1);
                    if(dbManagerChallan.addChallan(challanDetails)){
                    Toast.makeText(getActivity(),"Challan Added !",Toast.LENGTH_SHORT).show();
                    }
                    idChild.setValue(challanDetails);

            Toast.makeText(getActivity(),"Upload Done ",Toast.LENGTH_SHORT).show();
        }
        if(uri!=null) {
            progressDialog1.setMessage("Uploading Image and Data....");
            progressDialog1.show();
            filepath = mStorage.child("PhotosChallan").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Upload Done !", Toast.LENGTH_LONG).show();
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl!=null) {
                        download_url_string = downloadUrl.toString();
                    }
                    challanDetails = new ChallanDetails(violator_name.getText().toString(),
                            crime,owner_name.getText().toString(),violator_address.getText().toString(),
                            veh_number.getText().toString(),place_name.getText().toString(),
                            offence_section.getText().toString(),challan_amount.getText().toString(),
                            license_number.getText().toString(),policeofficer_name.getText().toString(),
                            "disrict","policeStation",other.getText().toString(),download_url_string,violator_number.getText().toString(),date.getText().toString(),time.getText().toString());
                    //local DB
                    final DBManagerChallan dbManagerChallan = new DBManagerChallan(getActivity(),null,null,1);
                    if(dbManagerChallan.addChallan(challanDetails)){
                        Toast.makeText(getActivity(),"Challan Added Offline !",Toast.LENGTH_SHORT).show();
                    }
                    idChild.setValue(challanDetails);
                    progressDialog1.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Upload Failed !", Toast.LENGTH_LONG).show();
                    progressDialog1.dismiss();
                    download_url_string="null";
                    challanDetails = new ChallanDetails(violator_name.getText().toString(),
                            crime,owner_name.getText().toString(),violator_address.getText().toString(),
                            veh_number.getText().toString(),place_name.getText().toString(),
                            offence_section.getText().toString(),challan_amount.getText().toString(),
                            license_number.getText().toString(),policeofficer_name.getText().toString(),
                            "disrict","policeStation",other.getText().toString(),download_url_string,violator_number.getText().toString(),date.getText().toString(),time.getText().toString());
                    //local DB
                    final DBManagerChallan dbManagerChallan = new DBManagerChallan(getActivity(),null,null,1);
                    if(dbManagerChallan.addChallan(challanDetails)){
                        Toast.makeText(getActivity(),"Challan Added Offline!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
            progressDialog.dismiss();
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            uri =data.getData();
            upload_photo.setBackgroundColor(0);
            upload_photo.setImageURI(uri);
        }
    }
    private void resetAll()
    {
        other.setText("");
        offence_section.setText("");
        veh_number.setText("");
        place_name.setText("");
        challan_amount.setText("");
        naka_name.setText("");
        owner_name.setText("");
        violator_name.setText("");
        violator_address.setText("");
        license_number.setText("");
        policeofficer_name.setText("");
        violator_number.setText("");
        time.setText("fill time here");
        date.setText("fill date here");
        helmet.setChecked(false);
        rc.setChecked(false);
        insurance.setChecked(false);
        license.setChecked(false);
        rash_drive.setChecked(false);
        mobile.setChecked(false);
        number_plate.setChecked(false);
        horn.setChecked(false);
        seat_belt.setChecked(false);
        triple_riding.setChecked(false);
        idle_parking.setChecked(false);
        restricted_park.setChecked(false);

    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(),"datePicker");
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

}
