package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;
/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Challan extends AppCompatActivity {

    /** in order to get external library working through jar file
     * 1) add jar file in the libs directory of root
     * 2) choose project view and then add as library will include
     * that in your app
     * */


    //variables to be used
    private Firebase mrootRef;
    private CheckBox helmet,rc,insurance,license,rash_drive,mobile,number_plate,horn,seat_belt,triple_riding,
            idle_parking,restricted_park;
    private EditText other,offence_section,veh_number,place_name,challan_amount,owner_name,violator_name
            ,violator_address,license_number,violator_number;
    private Button submit,reset;
    private ImageView upload_photo;
    private Uri uri=null,downloadUrl=null;
    private String download_url_string="";
    private String crime="";
    private StorageReference mStorage,filepath;
    private static final int GALLERY_INTENT =2;
    private ProgressDialog progressDialog;
    private ProgressDialog progressDialog1;
    private static final int CAMERA_REQUEST = 1888;
    private File actualImage;
    private FirebaseDatabase database;
    private DatabaseReference mRootRef;
    private int PICK_IMAGE_REQUEST=1;
    private SessionManager sessionManager;
    private String date,time;
    private long epoch;
    private static final int REQUEST_IMAGE_CAPTURE = 1;


    private CustomDialog customDialog;

    //object to store challan details
    private ChallanDetails challanDetails, challanDetailswithoutImage;
    private String[] month = new String[]{"","January","February","March","April","May","June","July","August","September","October","November","December"};
    private String ampm = "AM";
    private BroadcastReceiver logoutBroadcastReceiver;
    private String challanID;

    @Override


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challan);
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mrootRef = new Firebase("https://hppoliceassistant.firebaseio.com/challan");

        sessionManager = new SessionManager(Challan.this);
        setLogoutBroadcastReceiver();

        /** creating firebase instances
         * */
        database =FirebaseDatabase.getInstance();
        mRootRef= database.getReference("challan");
        mStorage = FirebaseStorage.getInstance().getReference();

        /*Initiating toolbar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Create a Challan");

        /* initialising all views*/
        initialiseCheckBoxes();
        initialiseEditText();
        initialiseOtherViews();

        //Initialise dialogs
        progressDialog = new ProgressDialog(Challan.this);
        progressDialog1 = new ProgressDialog(Challan.this);


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAll();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //populate string data from checkboxes
                populateDataFromCheckBoxes();
                epoch=System.currentTimeMillis();
                challanID = populateChallanID();
                date = generateDateFromSystem();
                time = generateCurrentTime();


                //check that fields should not be empty
                if(veh_number.getText().toString().trim().contentEquals("")||place_name.getText().toString().trim().contentEquals("")){
                    Toast.makeText(Challan.this,"Fields are empty",Toast.LENGTH_SHORT).show();
                    veh_number.setError("Field can not be empty");
                    place_name.setError("Field can not be empty");
                }
                else {
                    if(!DataTypeValidator.validatePhoneNumberFormat(violator_number.getText().toString())){
                        violator_number.setError("Invalid Phone Number");
                        AlertDialog.Builder builder = new AlertDialog.Builder(Challan.this);
                        builder.setTitle("Mobile Number")
                                .setMessage("Invalid Mobile Number Captured")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else if (!validateFields()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Challan.this);
                        builder.setTitle("Challan Entry")
                                .setMessage("Invalid Input Captured")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        /** creating a challan without image section population intended for offline storage
                         */
                        challanDetailswithoutImage  = createChallanWithNoImage();
                        challanDetailswithoutImage.setEpoch(epoch);

                        /** CustomDialog is a dialog intended to be used for pre verification before submission either to online
                         * or offline storage and it contains all details previously filled by user an shows 3 buttons including
                         * edit , online and offline
                         * */
                        customDialog = new CustomDialog(Challan.this, challanDetailswithoutImage);
                        customDialog.setTitle("Confirm Challan Details");
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

                dispatchTakePictureIntent();
                //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                //intent.setType("image/*");
                //startActivityForResult(intent, PICK_IMAGE_REQUEST);

            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private String generateCurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        if (Integer.valueOf(strDate.substring(11, 13)) > 12) {
            ampm = "PM";
        }
        return strDate.substring(10, strDate.length()) + " " + ampm;
    }

    private String generateDateFromSystem() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String strDate = sdf.format(c.getTime());
        return strDate.substring(0, 2) + ", " + month[Integer.valueOf(strDate.substring(3, 5))] + " " + strDate.substring(6, 10);
    }

    private ChallanDetails createChallanWithNoImage() {
        return new ChallanDetails(challanID,violator_name.getText().toString(),
                crime, owner_name.getText().toString(), violator_address.getText().toString(),
                veh_number.getText().toString(), place_name.getText().toString(),
                offence_section.getText().toString(), challan_amount.getText().toString(),
                license_number.getText().toString(), sessionManager.getIOName(),
                sessionManager.getDistrict(),sessionManager.getPoliceStation(), other.getText().toString(), "null",
                violator_number.getText().toString(), date, time);
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
            challanDetails = new ChallanDetails(challanID,violator_name.getText().toString(),
                    crime,owner_name.getText().toString(),violator_address.getText().toString(),
                    veh_number.getText().toString(),place_name.getText().toString(),
                    offence_section.getText().toString(),challan_amount.getText().toString(),
                    license_number.getText().toString(),sessionManager.getIOName(),
                    sessionManager.getDistrict(),sessionManager.getPoliceStation(),other.getText().toString(),download_url_string,
                    violator_number.getText().toString(),date,time);
            //local DB
            challanDetails.setStatus(1);
            challanDetails.setEpoch(epoch);
            /** even if the officer is sending challan to the server we are keeping a copy of it locally so that officer can go to
             * his offline section and see the challan he has made and he can easily differentiate from other offline challan as
             * this will be shown in green color details box indicating that it is already sent
             * */

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
                        resetAll();
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
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl!=null) {
                        download_url_string = downloadUrl.toString();
                    }
                    /** download url is the url of the firebase location at which image is stored
                     * we will store this url in our database so as to fetch the image later
                     * */

                    challanDetails = new ChallanDetails(challanID,violator_name.getText().toString(),
                            crime,owner_name.getText().toString(),violator_address.getText().toString(),
                            veh_number.getText().toString(),place_name.getText().toString(),
                            offence_section.getText().toString(),challan_amount.getText().toString(),
                            license_number.getText().toString(),sessionManager.getIOName(),
                            sessionManager.getDistrict(),sessionManager.getPoliceStation(),other.getText().toString(),
                            download_url_string,violator_number.getText().toString(),date,time,1);

                    /** even if challan is stored online we will keep a local copy of it with green marker
                     * */
                    final DBManagerChallan dbManagerChallan = new DBManagerChallan(Challan.this,null,null,1);
                    challanDetails.setStatus(1);
                    challanDetails.setEpoch(epoch);
                    dbManagerChallan.addChallan(challanDetails);
                    idChild.setValue(challanDetails, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError== null){
                                progressDialog1.dismiss();
                                Toast.makeText(Challan.this,"Upload Done ",Toast.LENGTH_SHORT).show();
                                resetAll();
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
                    challanDetails = new ChallanDetails(challanID,violator_name.getText().toString(),
                            crime,owner_name.getText().toString(),violator_address.getText().toString(),
                            veh_number.getText().toString(),place_name.getText().toString(),
                            offence_section.getText().toString(),challan_amount.getText().toString(),
                            license_number.getText().toString(),sessionManager.getIOName(),
                            sessionManager.getDistrict(),sessionManager.getPoliceStation(),other.getText().toString(),
                            download_url_string,violator_number.getText().toString(),date,time,0);
                    challanDetails.setEpoch(epoch);
                    final DBManagerChallan dbManagerChallan = new DBManagerChallan(Challan.this,null,null,1);
                    if(dbManagerChallan.addChallan(challanDetails)){
                        Toast.makeText(Challan.this,"Challan Added Offline!",Toast.LENGTH_SHORT).show();
                        resetAll();
                    }
                }
            });
        }
        progressDialog.dismiss();
    }
/*    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                showError("Failed to open picture!");
                return;
            }
            try {
                actualImage = FileUtil.from(Challan.this, data.getData());
                upload_photo.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                if (actualImage == null) {
                    showError("Please choose an image!");
                } else {

                    // Compress image in main thread
                    actualImage = Compressor.getDefault(Challan.this).compressToFile(actualImage);

                    uri = Uri.fromFile(actualImage);
                    upload_photo.setImageURI(uri);
                }
            } catch (IOException e) {
                showError("Failed to read picture data!");
                e.printStackTrace();
            }
        }
    }*/
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
        if (data == null) {
            Toast.makeText(Challan.this,"Error Loading File ",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
            Uri tempUri = getImageUri(getApplicationContext(), photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            //File finalFile = new File(getRealPathFromURI(tempUri));
            actualImage = FileUtil.from(Challan.this, tempUri);
            //upload.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
            progressDialog.setMessage("Compressing Image");
            progressDialog.show();

            // Compress image in main thread
            actualImage = Compressor.getDefault(Challan.this).compressToFile(actualImage);
            uri = Uri.fromFile(actualImage);

            upload_photo.setImageURI(uri);
            progressDialog.dismiss();
        } catch (IOException e) {
            Toast.makeText(Challan.this,"Failed to read picture data",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, veh_number.getText().toString(), null);
        return Uri.parse(path);
    }

    private String populateChallanID(){
        return "C"+sessionManager.getDistrict().substring(0,3).toUpperCase()+sessionManager.getPoliceStation().substring(3,7).toUpperCase().replace("/","")
                +String.valueOf(epoch).substring(4);
    }

    private void populateDataFromCheckBoxes() {
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
    }

    private void initialiseOtherViews() {
        upload_photo=(ImageView)findViewById(R.id.upload_photo);
        reset=(Button)findViewById(R.id.reset);
        submit=(Button)findViewById(R.id.submit);
    }

    private void initialiseEditText() {
        other=(EditText)findViewById(R.id.other_offence);
        offence_section=(EditText)findViewById(R.id.offence_section);
        veh_number=(EditText)findViewById(R.id.vehicle_number);
        place_name=(EditText)findViewById(R.id.place_name);
        owner_name=(EditText)findViewById(R.id.vehicle_owner_name);
        violator_name=(EditText)findViewById(R.id.violator_name);
        violator_address=(EditText)findViewById(R.id.violator_address);
        license_number=(EditText)findViewById(R.id.license_number);
        challan_amount=(EditText)findViewById(R.id.challan_amount);
        violator_number=(EditText)findViewById(R.id.violator_number);
    }

    private void initialiseCheckBoxes() {
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
    }

    private boolean validateFields() {
        return (DataTypeValidator.validateNameOfPersonFormat(violator_name.getText().toString())
                &&DataTypeValidator.validatePhoneNumberFormat(violator_number.getText().toString()));
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
                new AlertDialog.Builder(this)
                        .setTitle("LogOut")
                        .setMessage("Do you really want to logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sessionManager.logoutUser();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
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
        owner_name.setText("");
        violator_name.setText("");
        violator_address.setText("");
        license_number.setText("");
        violator_number.setText("");
        upload_photo.setImageResource(R.drawable.upload);
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

    /*Following helps to finish this activity when user logs out (so that they can't navigate back here)*/
    private void setLogoutBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.pranav.apps.amazing.ACTION_LOGOUT");
        logoutBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(logoutBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(logoutBroadcastReceiver);
        super.onDestroy();
    }
}
