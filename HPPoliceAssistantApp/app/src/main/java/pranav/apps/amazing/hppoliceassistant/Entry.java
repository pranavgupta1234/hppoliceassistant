package pranav.apps.amazing.hppoliceassistant;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.Firebase;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Entry extends AppCompatActivity {
    private Firebase mrootRef;
    private EditText veh,phone,description,place;
    private Button submit_det;
    private ImageButton upload;
    private String path;
    private StorageReference mStorage,filepath;
    private ProgressDialog progressDialog,progressDialog1;
    private Uri uri=null,downloadUrl=null;
    private String download_url_string="";
    private VehicleEntry newEntry,newEntrywithoutImage;
    private VehicleEntryDialog vehicleEntryDialog;
    private File actualImage;
    private FirebaseDatabase database;
    private DatabaseReference mRootRef ;
    private int PICK_IMAGE_REQUEST=1;
    private String[] month = new String[]{"January","February","March","April","May","June","July","August","September","October","November","December"};
    private String ampm = "AM";
    private String off_name;
    private SessionManager sessionManager;
    private BroadcastReceiver logoutBroadcastReceiver;
    private String EntryID;
    private String date,time;
    private long epoch;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        sessionManager = new SessionManager(Entry.this);
        setLogoutBroadcastReceiver();

        mrootRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");
        database = FirebaseDatabase.getInstance();
        mRootRef = database.getReference("vehicle_entry");

        off_name = getIntent().getStringExtra("name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create a Vehicle Entry");
        setSupportActionBar(toolbar);



        progressDialog = new ProgressDialog(Entry.this);
        progressDialog1 = new ProgressDialog(Entry.this);

        veh=(EditText)findViewById(R.id.vehicle_num);
        phone=(EditText)findViewById(R.id.phone_num);
        description=(EditText)findViewById(R.id.description);
        place=(EditText)findViewById(R.id.place);


        upload=(ImageButton)findViewById(R.id.upload);
        submit_det=(Button)findViewById(R.id.make_entry);

        mStorage = FirebaseStorage.getInstance().getReference();
        submit_det.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (veh.getText().toString().trim().contentEquals("") || place.getText().toString().trim().contentEquals("")) {
                    Toast.makeText(Entry.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                    veh.setError("Field can not be empty");
                    place.setError("Field can not be empty");
                } else {
                    if (!DataTypeValidator.validatePhoneNumberFormat(phone.getText().toString())) {
                        phone.setError("Invalid Phone Number");
                        AlertDialog.Builder builder = new AlertDialog.Builder(Entry.this);
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
                    else if(!validateFields()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(Entry.this);
                        builder.setTitle("Challan Entry")
                                .setMessage("Invalid Input Captured")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                    else {
                        epoch=System.currentTimeMillis();
                        EntryID = populateEntryID();
                        date = generateDateFromSystem();
                        time = generateCurrentTime();

                        newEntrywithoutImage = new VehicleEntry(EntryID,veh.getText().toString(), phone.getText().toString(),
                                description.getText().toString(), place.getText().toString(),date,time,sessionManager.getIOName(), "null",sessionManager.getDistrict(),sessionManager.getPoliceStation(),
                                sessionManager.getPolicePost(),0);
                        newEntrywithoutImage.setEpoch(epoch);
                        vehicleEntryDialog = new VehicleEntryDialog(Entry.this, newEntrywithoutImage);
                        vehicleEntryDialog.setTitle("Entry Details");
                        vehicleEntryDialog.setCancelable(true);
                        vehicleEntryDialog.show();
                        vehicleEntryDialog.findViewById(R.id.offline).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newEntrywithoutImage.setStatus(0);
                                DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry.this, null, null, 1);
                                if (dbManagerEntry.addEntry(newEntrywithoutImage)) {
                                    Toast.makeText(Entry.this, "Entry Added Offline!", Toast.LENGTH_SHORT).show();
                                    vehicleEntryDialog.dismiss();
                                    resetAll();
                                }
                            }
                        });
                        vehicleEntryDialog.findViewById(R.id.submit_challan).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startPosting();
                                vehicleEntryDialog.dismiss();
                            }
                        });

                    }
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image*//**//*");
                startActivityForResult(intent,GALLERY_INTENT);*/
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });
    }

    private void startPosting() {
        final DatabaseReference idChild = mRootRef.push();

        if(uri == null){
            progressDialog1.setMessage("Uploading data....");
            progressDialog1.show();
            Toast.makeText(Entry.this,"No Photo Selected, uploading data...",Toast.LENGTH_SHORT).show();
            download_url_string ="Photo not available";

            newEntry = new VehicleEntry(EntryID,veh.getText().toString(),phone.getText().toString(),
                    description.getText().toString(),place.getText().toString(),date,time,
                    sessionManager.getIOName(),download_url_string,sessionManager.getDistrict(),sessionManager.getPoliceStation()
                    ,sessionManager.getPolicePost(),1);
            DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry.this,null,null,1);
            newEntry.setStatus(1);
            newEntry.setEpoch(epoch);
            dbManagerEntry.addEntry(newEntry);
            idChild.setValue(newEntry, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError== null){
                        progressDialog1.dismiss();
                        Toast.makeText(Entry.this,"Upload Done ",Toast.LENGTH_SHORT).show();
                        resetAll();
                    }
                    else {
                        progressDialog1.dismiss();
                        Toast.makeText(Entry.this,"Network Error! Data Not Saved,Sorry for inconvenience",Toast.LENGTH_LONG).show();
                        resetAll();
                    }
                }
            });
        }
        if(uri!=null) {
            progressDialog1.setMessage("Uploading Image and data....");
            progressDialog1.show();
            filepath = mStorage.child("PhotosVehicleEntry").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Entry.this, "Upload Done !", Toast.LENGTH_LONG).show();
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl!=null) {
                        download_url_string = downloadUrl.toString();
                    }

                    newEntry = new VehicleEntry(EntryID,veh.getText().toString(),phone.getText().toString(),
                            description.getText().toString(),place.getText().toString(),date,time,
                            sessionManager.getIOName(),download_url_string,sessionManager.getDistrict(),sessionManager.getPoliceStation(),
                            sessionManager.getPolicePost(),1);
                    DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry.this,null,null,1);
                    newEntry.setStatus(1);
                    newEntry.setEpoch(epoch);
                    dbManagerEntry.addEntry(newEntry);
                    idChild.setValue(newEntry, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError== null){
                                progressDialog1.dismiss();
                                Toast.makeText(Entry.this,"Upload Done ",Toast.LENGTH_SHORT).show();
                                resetAll();
                            }
                            else {
                                progressDialog1.dismiss();
                                Toast.makeText(Entry.this,"Network Error! Data Not Saved,Sorry for inconvenience",Toast.LENGTH_LONG).show();
                                resetAll();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Entry.this, "Upload Failed !", Toast.LENGTH_LONG).show();
                    progressDialog1.dismiss();
                    newEntry.setStatus(0);
                    DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry.this,null,null,1);
                    if(dbManagerEntry.addEntry(newEntry)){
                        Toast.makeText(Entry.this,"Entry Added Offline!",Toast.LENGTH_SHORT).show();
                        resetAll();
                    }
                }
            });
        }
        progressDialog.dismiss();
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
        return strDate.substring(0, 2) + ", " + month[Integer.valueOf(strDate.substring(3, 4))] + " " + strDate.substring(6, 10);
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
                Intent i = new Intent(Entry.this,OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(Entry.this,OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.developers_activity:
                startActivity(new Intent(Entry.this,DevelopersActivity.class));
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

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        /*if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            uri =data.getData();
            upload.setBackgroundColor(0);
            upload.setImageURI(uri);
        }*/
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(Entry.this,"Error Loading File ",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                actualImage = FileUtil.from(Entry.this, data.getData());
                upload.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                //actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));
                //clearImage();
                if (actualImage == null) {
                    Toast.makeText(Entry.this,"Please Choose an image",Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Compressing Image");
                    progressDialog.show();

                    // Compress image in main thread
                    actualImage = Compressor.getDefault(Entry.this).compressToFile(actualImage);
                    //setCompressedImage();

                    // Compress image to bitmap in main thread
            /*compressedImageView.setImageBitmap(Compressor.getDefault(this).compressToBitmap(actualImage));*/

                    // Compress image using RxJava in background thread using rx java
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
                    upload.setImageURI(uri);
                    progressDialog.dismiss();
                }
            } catch (IOException e) {
                Toast.makeText(Entry.this,"Failed to read picture data",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
    private boolean validateFields() {
        return (DataTypeValidator.validateVehicleNumberFormat(place.getText().toString())
                &&DataTypeValidator.validatePhoneNumberFormat(phone.getText().toString())
                && DataTypeValidator.validateVehicleNumberFormat(veh.getText().toString()));
    }
    private String populateEntryID(){
        return "V"+sessionManager.getDistrict().substring(0,3).toUpperCase()+sessionManager.getPoliceStation().substring(3,7).toUpperCase().replace("/","")
                +String.valueOf(epoch).substring(4);
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    private void resetAll() {
        veh.setText("");
        phone.setText("");
        description.setText("");
        place.setText("");
        upload.setImageResource(R.drawable.upload);
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
