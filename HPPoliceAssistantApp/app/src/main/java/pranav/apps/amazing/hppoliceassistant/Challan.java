package pranav.apps.amazing.hppoliceassistant;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Challan extends AppCompatActivity {


    //variables to be used
    private Firebase mrootRef;
    private CheckBox helmet,rc,insurance,license,rash_drive,mobile,number_plate,horn,seat_belt,triple_riding,
            idle_parking,restricted_park;
    private EditText other,offence_section,veh_number,place_name,challan_amount,naka_name,owner_name,violator_name
            ,violator_address,license_number,violator_number;
    String date_auto,time_auto;
    private Button submit,reset;
    private ImageView upload_photo;
    private TextView nak;
    private Uri uri=null,downloadUrl=null;
    private String download_url_string="";
    private String crime="";
    private StorageReference mStorage,filepath;
    private static final int GALLERY_INTENT =2;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;
    private static final int CAMERA_REQUEST = 1888;
    private Uri imageUri;
    int FILECHOOSER_RESULTCODE = 1888;
    private File actualImage;
    private Uri fileuri;
    private FirebaseDatabase database;
    private DatabaseReference mRootRef;
    private int PICK_IMAGE_REQUEST=1;
    private SessionManager sessionManager;


    private CustomDialog customDialog;

    //object to store challan details
    private ChallanDetails challanDetails, challanDetailswithoutImage;
    private String[] month = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
    private String ampm = "AM";
    String off_name;

    @Override


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challan);
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mrootRef = new Firebase("https://hppoliceassistant.firebaseio.com/challan");

        sessionManager = new SessionManager(Challan.this);

        database =FirebaseDatabase.getInstance();
        mRootRef= database.getReference("challan");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create a Challan");
        //setSupportActionBar(toolbar);



        off_name = Challan.this.getIntent().getStringExtra("name");
        helmet=(CheckBox)findViewById(R.id.helmet);
        rc=(CheckBox)findViewById(R.id.rc);
        insurance=(CheckBox)findViewById(R.id.insurance);
        license=(CheckBox)findViewById(R.id.license);
        rash_drive=(CheckBox)findViewById(R.id.rash_drive);
        mobile=(CheckBox)findViewById(R.id.mobile);
        number_plate=(CheckBox)findViewById(R.id.number_plate);
        seat_belt=(CheckBox)findViewById(R.id.seat_belt);
        horn=(CheckBox)findViewById(R.id.pressure_horn);
        triple_riding=(CheckBox)findViewById(R.id.triple_riding);
        idle_parking=(CheckBox)findViewById(R.id.idle_parking);
        restricted_park=(CheckBox)findViewById(R.id.restricted);


        other=(EditText)findViewById(R.id.other_offence);
        offence_section=(EditText)findViewById(R.id.offence_section);
        veh_number=(EditText)findViewById(R.id.vehicle_number);
        place_name=(EditText)findViewById(R.id.place_name);
        naka_name=(EditText)findViewById(R.id.naka_name);
        owner_name=(EditText)findViewById(R.id.vehicle_owner_name);
        violator_name=(EditText)findViewById(R.id.violator_name);
        violator_address=(EditText)findViewById(R.id.violator_address);
        license_number=(EditText)findViewById(R.id.license_number);
        challan_amount=(EditText)findViewById(R.id.challan_amount);
        violator_number=(EditText)findViewById(R.id.violator_number);

        upload_photo=(ImageView)findViewById(R.id.upload_photo);
        reset=(Button)findViewById(R.id.reset);
        progressDialog = new ProgressDialog(Challan.this);
        progressDialog1 = new ProgressDialog(Challan.this);
        nak=(TextView)findViewById(R.id.nak_name);
        mStorage = FirebaseStorage.getInstance().getReference();
        submit=(Button)findViewById(R.id.submit);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAll();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crime="";
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
                if(veh_number.getText().toString().trim().contentEquals("")||place_name.getText().toString().trim().contentEquals("")){
                    Toast.makeText(Challan.this,"Fields are empty",Toast.LENGTH_SHORT).show();
                    veh_number.setError("Field can not be empty");
                    place_name.setError("Field can not be empty");
                }
                else {
                    if (violator_number.getText().toString().length() >= 1 && violator_number.getText().toString().length() < 10) {
                        violator_number.setError("Invalid Phone Number");
                    } else {
                        /** creating a challan without image section population intended for offline storage
                         */
                        challanDetailswithoutImage = new ChallanDetails(violator_name.getText().toString(),
                                crime, owner_name.getText().toString(), violator_address.getText().toString(),
                                veh_number.getText().toString(), place_name.getText().toString(),
                                offence_section.getText().toString(), challan_amount.getText().toString(),
                                license_number.getText().toString(), off_name,
                                "disrict", "policeStation", other.getText().toString(), "null",
                                violator_number.getText().toString(), "will be filled", "will be filled");

                        /** CustomDialog is a dialog intended to be used for pre verification before submission either to online
                         * or offline storage and it contains all details previously filled by user an shows 3 buttons including
                         * edit , online and offline
                         * */
                        customDialog = new CustomDialog(Challan.this, challanDetailswithoutImage);
                        customDialog.setTitle("Challan Details");
                        customDialog.setCancelable(true);
                        customDialog.show();
                        customDialog.findViewById(R.id.offline).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                /** DBManager Challan is intended for offline storage of challans and status field determines whether it
                                 * is posted online or not (if posted then 1 else 0) and determine the color of details button
                                 * */
                                final DBManagerChallan dbManagerChallan = new DBManagerChallan(Challan.this, null, null, 1);
                                challanDetailswithoutImage.setStatus(0);
                                /** Auto population of date and time from android system
                                 * */
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String strDate = sdf.format(c.getTime());
                                challanDetailswithoutImage.setDate(strDate.substring(0, 2) + ", " + month[Integer.valueOf(strDate.substring(3, 4))] + " " + strDate.substring(6, 10));
                                if (Integer.valueOf(strDate.substring(11, 13)) > 12) {
                                    ampm = "PM";
                                }
                                challanDetailswithoutImage.setTime(strDate.substring(10, strDate.length()) + " " + ampm);
                                if (dbManagerChallan.addChallan(challanDetailswithoutImage)) {
                                    Toast.makeText(Challan.this, "Challan Added Offline! Make sure to add it online!", Toast.LENGTH_SHORT).show();
                                    customDialog.dismiss();
                                }
                            }
                        });
                        /** for online submission of challan into firebase database
                         * */
                        customDialog.findViewById(R.id.submit_challan).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startPosting();
                                customDialog.dismiss();
                            }
                        });
                    }
                }
            }
        });
        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);

            }
        });
    }

    public void startPosting(){
        final DatabaseReference idChild = mRootRef.push();
        /** in case user does not select an image(as uri is initially kept null) then create a challan with Photo not available and then send data
         * to server
         * */
        if(uri == null){
            progressDialog1.setMessage("No Photo Selected, Uploading Data...");
            progressDialog1.show();
            download_url_string ="Photo not available";
            challanDetails = new ChallanDetails(violator_name.getText().toString(),
                    crime,owner_name.getText().toString(),violator_address.getText().toString(),
                    veh_number.getText().toString(),place_name.getText().toString(),
                    offence_section.getText().toString(),challan_amount.getText().toString(),
                    license_number.getText().toString(),off_name,
                    "disrict","policeStation",other.getText().toString(),download_url_string,violator_number.getText().toString(),"willbefilled","willbefilled");
                    //local DB
            challanDetails.setStatus(1);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String strDate = sdf.format(c.getTime());
            challanDetails.setDate(strDate.substring(0,2)+", "+ month[Integer.valueOf(strDate.substring(3,4))]+" "+strDate.substring(6,10));
            if(Integer.valueOf(strDate.substring(11,13))>12){
                ampm = "PM";
            }
            /** even if the officer is sending challan to the server we are keeping a copy of it locally so that officer can go to
             * his offline section and see the challan he has made and he can easily differentiate from other offline challan as
             * this will be shown in green color details box indicating that it is already sent
             * */
            challanDetails.setTime(strDate.substring(10,strDate.length())+" "+ampm);
                    final DBManagerChallan dbManagerChallan = new DBManagerChallan(Challan.this,null,null,1);
                    dbManagerChallan.addChallan(challanDetails);
            /**If you are using completion callback listener of Database reference then the instance for Firebase should also be
             * of DatabaseReference and not of Firebase(the one which is initialised through url)
             */
            idChild.setValue(challanDetails, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError== null){
                        progressDialog1.dismiss();
                        Toast.makeText(Challan.this,"Upload Done ",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog1.dismiss();
                        Toast.makeText(Challan.this,"Network Error! Data Not Saved,Sorry for inconvenience",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        /** if the user has already selected an image then this will be executed
         * */
        if(uri!=null) {
            progressDialog1.setMessage("Uploading Image and Data....");
            progressDialog1.show();
            filepath = mStorage.child("PhotosChallan").child(idChild.getKey());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Challan.this, "Upload Done !", Toast.LENGTH_LONG).show();
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl!=null) {
                        download_url_string = downloadUrl.toString();
                    }
                    /** download url is the url of the firebase location at which image is stored
                     * we will store this url in our database so as to fetch the image later
                     * */

                    challanDetails = new ChallanDetails(violator_name.getText().toString(),
                            crime,owner_name.getText().toString(),violator_address.getText().toString(),
                            veh_number.getText().toString(),place_name.getText().toString(),
                            offence_section.getText().toString(),challan_amount.getText().toString(),
                            license_number.getText().toString(),off_name,
                            "disrict","policeStation",other.getText().toString(),download_url_string,violator_number.getText().toString(),"","",1);
                    //local DB
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String strDate = sdf.format(c.getTime());
                    challanDetails.setDate(strDate.substring(0,2)+", "+ month[Integer.valueOf(strDate.substring(3,4))]+" "+strDate.substring(6,10));
                    if(Integer.valueOf(strDate.substring(11,13))>12){
                        ampm = "PM";
                    }
                    challanDetails.setTime(strDate.substring(10,strDate.length())+" "+ampm);

                    /** even if challan is stored online we will keep a local copy of it with green marker
                     * */
                    final DBManagerChallan dbManagerChallan = new DBManagerChallan(Challan.this,null,null,1);
                    challanDetails.setStatus(1);
                    dbManagerChallan.addChallan(challanDetails);
                    idChild.setValue(challanDetails, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError== null){
                                progressDialog1.dismiss();
                                Toast.makeText(Challan.this,"Upload Done ",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progressDialog1.dismiss();
                                Toast.makeText(Challan.this,"Network Error! Data Not Saved,Sorry for inconvenience",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {

                /** this failure listener is for problem that occur during image upload so that in such case we will store
                 * the rest data into database online and we will also add a challan online
                 * */
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Challan.this, "Upload Failed !", Toast.LENGTH_LONG).show();
                    progressDialog1.dismiss();
                    download_url_string="null";
                    challanDetails = new ChallanDetails(violator_name.getText().toString(),
                            crime,owner_name.getText().toString(),violator_address.getText().toString(),
                            veh_number.getText().toString(),place_name.getText().toString(),
                            offence_section.getText().toString(),challan_amount.getText().toString(),
                            license_number.getText().toString(),off_name,
                            "district","policeStation",other.getText().toString(),download_url_string,violator_number.getText().toString(),"","",0);
                    //local DB
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String strDate = sdf.format(c.getTime());
                    challanDetails.setDate(strDate.substring(0,2)+", "+ month[Integer.valueOf(strDate.substring(3,4))]+" "+strDate.substring(6,10));
                    if(Integer.valueOf(strDate.substring(11,13))>12){
                        ampm = "PM";
                    }
                    challanDetails.setTime(strDate.substring(10,strDate.length())+" "+ampm);
                    final DBManagerChallan dbManagerChallan = new DBManagerChallan(Challan.this,null,null,1);
                    if(dbManagerChallan.addChallan(challanDetails)){
                        Toast.makeText(Challan.this,"Challan Added Offline!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
            progressDialog.dismiss();
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        /*if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            uri =data.getData();
            upload_photo.setBackgroundColor(0);
            upload_photo.setImageURI(uri);
        }*/
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(Challan.this, data.getData());
                upload_photo.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                //actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));
                //clearImage();
                if (actualImage == null) {
                    showError("Please choose an image!");
                } else {

                    // Compress image in main thread
                    actualImage = Compressor.getDefault(Challan.this).compressToFile(actualImage);
                    //setCompressedImage();

                    // Compress image to bitmap in main thread
            /*compressedImageView.setImageBitmap(Compressor.getDefault(this).compressToBitmap(actualImage));*/

                    // Compress image using RxJava in background thread
                    /*Compressor.getDefault(getActivity())
                            .compressToFileAsObservable(actualImage)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<File>() {
                                @Override
                                public void call(File file) {
                                    actualImage = file;
                                   // setCompressedImage();
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    showError(throwable.getMessage());
                                }
                            });*/
                    uri = Uri.fromFile(actualImage);
                    //uri=Uri.parse(actualImage.getAbsolutePath());
                    upload_photo.setImageURI(uri);
                }
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }
    public void showError(String errorMessage) {
        Toast.makeText(Challan.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
    /*private void clearImage() {
        actualImageView.setBackgroundColor(getRandomColor());
        compressedImageView.setImageDrawable(null);
        compressedImageView.setBackgroundColor(getRandomColor());
        compressedSizeTextView.setText("Size : -");
    }*/
    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                //mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.offline_challan:
                Intent i = new Intent(Challan.this,OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(Challan.this,OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                sessionManager.logoutUser();
                Intent intent1 = new Intent(Challan.this,Login.class);
                intent1.putExtra("finish", true); // if you are checking for this in your other Activities
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void resetAll()
    {
        crime="";
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
        violator_number.setText("");
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
}
