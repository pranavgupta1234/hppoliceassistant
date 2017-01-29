package pranav.apps.amazing.hppoliceassistant;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import id.zelory.compressor.Compressor;
import id.zelory.compressor.FileUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Entry_veh extends AppCompatActivity {
    private Firebase mrootRef;
    private EditText veh,phone,description,place,naka;
    private Button submit_det,reset;
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
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        sessionManager = new SessionManager(Entry_veh.this);
        setLogoutBroadcastReceiver();

        mrootRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");
        database = FirebaseDatabase.getInstance();
        mRootRef = database.getReference("vehicle_entry");

        off_name = getIntent().getStringExtra("name");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create a Vehicle Entry");
        setSupportActionBar(toolbar);



        progressDialog = new ProgressDialog(Entry_veh.this);
        progressDialog1 = new ProgressDialog(Entry_veh.this);

        veh=(EditText)findViewById(R.id.vehicle_num);
        phone=(EditText)findViewById(R.id.phone_num);
        description=(EditText)findViewById(R.id.description);
        place=(EditText)findViewById(R.id.place);
        naka=(EditText)findViewById(R.id.naka);


        upload=(ImageButton)findViewById(R.id.upload);
        submit_det=(Button)findViewById(R.id.make_entry);
        reset=(Button)findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAll();
            }
        });

        mStorage = FirebaseStorage.getInstance().getReference();
        submit_det.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (veh.getText().toString().trim().contentEquals("") || place.getText().toString().trim().contentEquals("")) {
                    Toast.makeText(Entry_veh.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                    veh.setError("Field can not be empty");
                    place.setError("Field can not be empty");
                } else {
                    if (phone.getText().toString().length() >= 1 && phone.getText().toString().length() < 10) {
                        phone.setError("Invalid Phone Number");
                    } else {

                        newEntrywithoutImage = new VehicleEntry(veh.getText().toString(), phone.getText().toString(),
                                description.getText().toString(), place.getText().toString(),
                                naka.getText().toString(), "", "",sessionManager.getIOName(), "null",sessionManager.getDistrict(),sessionManager.getPoliceStation(),
                                sessionManager.getPolicePost(),0);
                        vehicleEntryDialog = new VehicleEntryDialog(Entry_veh.this, newEntrywithoutImage);
                        vehicleEntryDialog.setTitle("Entry Details");
                        vehicleEntryDialog.setCancelable(true);
                        vehicleEntryDialog.show();
                        vehicleEntryDialog.findViewById(R.id.offline).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newEntrywithoutImage.setStatus(0);
                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                String strDate = sdf.format(c.getTime());
                                newEntrywithoutImage.setDate(strDate.substring(0, 2) + ", " + month[Integer.valueOf(strDate.substring(3, 4))] + " " + strDate.substring(6, 10));
                                if (Integer.valueOf(strDate.substring(11, 13)) > 12) {
                                    ampm = "PM";
                                }
                                newEntrywithoutImage.setTime(strDate.substring(10, strDate.length()) + " " + ampm);
                                DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry_veh.this, null, null, 1);
                                if (dbManagerEntry.addEntry(newEntrywithoutImage)) {
                                    Toast.makeText(Entry_veh.this, "Entry Added Offline!", Toast.LENGTH_SHORT).show();
                                    vehicleEntryDialog.dismiss();
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
            Toast.makeText(Entry_veh.this,"No Photo Selected, uploading data...",Toast.LENGTH_LONG).show();
            download_url_string ="Photo not available";

            newEntry = new VehicleEntry(veh.getText().toString(),phone.getText().toString(),
                    description.getText().toString(),place.getText().toString(),
                    naka.getText().toString(),"","",
                    sessionManager.getIOName(),download_url_string,sessionManager.getDistrict(),sessionManager.getPoliceStation()
                    ,sessionManager.getPolicePost(),1);
            DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry_veh.this,null,null,1);
            newEntry.setStatus(1);
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String strDate = sdf.format(c.getTime());
            newEntry.setDate(strDate.substring(0,2)+", "+ month[Integer.valueOf(strDate.substring(3,4))]+" "+strDate.substring(6,10));
            if(Integer.valueOf(strDate.substring(11,13))>12){
                ampm = "PM";
            }
            newEntry.setTime(strDate.substring(10,strDate.length())+" "+ampm);
            dbManagerEntry.addEntry(newEntry);
            idChild.setValue(newEntry, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if(databaseError== null){
                        progressDialog1.dismiss();
                        Toast.makeText(Entry_veh.this,"Upload Done ",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        progressDialog1.dismiss();
                        Toast.makeText(Entry_veh.this,"Network Error! Data Not Saved,Sorry for inconvenience",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Entry_veh.this, "Upload Done !", Toast.LENGTH_LONG).show();
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl!=null) {
                        download_url_string = downloadUrl.toString();
                    }

                    newEntry = new VehicleEntry(veh.getText().toString(),phone.getText().toString(),
                            description.getText().toString(),place.getText().toString(),
                            naka.getText().toString(),"","",
                            sessionManager.getIOName(),download_url_string,sessionManager.getDistrict(),sessionManager.getPoliceStation(),
                            sessionManager.getPolicePost(),1);
                    DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry_veh.this,null,null,1);
                    newEntry.setStatus(1);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String strDate = sdf.format(c.getTime());
                    newEntry.setDate(strDate.substring(0,2)+", "+ month[Integer.valueOf(strDate.substring(3,4))]+" "+strDate.substring(6,10));
                    if(Integer.valueOf(strDate.substring(11,13))>12){
                        ampm = "PM";
                    }
                    newEntry.setTime(strDate.substring(10,strDate.length())+" "+ampm);
                    dbManagerEntry.addEntry(newEntry);
                    idChild.setValue(newEntry, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError== null){
                                progressDialog1.dismiss();
                                Toast.makeText(Entry_veh.this,"Upload Done ",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progressDialog1.dismiss();
                                Toast.makeText(Entry_veh.this,"Network Error! Data Not Saved,Sorry for inconvenience",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Entry_veh.this, "Upload Failed !", Toast.LENGTH_LONG).show();
                    progressDialog1.dismiss();
                    newEntry.setStatus(0);
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String strDate = sdf.format(c.getTime());
                    newEntry.setDate(strDate.substring(0,2)+", "+ month[Integer.valueOf(strDate.substring(3,4))]+" "+strDate.substring(6,10));
                    if(Integer.valueOf(strDate.substring(11,13))>12){
                        ampm = "PM";
                    }
                    newEntry.setTime(strDate.substring(10,strDate.length())+" "+ampm);
                    DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry_veh.this,null,null,1);
                    if(dbManagerEntry.addEntry(newEntry)){
                        Toast.makeText(Entry_veh.this,"Entry Added Offline!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        progressDialog.dismiss();
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
                Intent i = new Intent(Entry_veh.this,OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(Entry_veh.this,OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                sessionManager.logoutUser();
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
                Toast.makeText(Entry_veh.this,"Error Loading File ",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                actualImage = FileUtil.from(Entry_veh.this, data.getData());
                upload.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                //actualSizeTextView.setText(String.format("Size : %s", getReadableFileSize(actualImage.length())));
                //clearImage();
                if (actualImage == null) {
                    Toast.makeText(Entry_veh.this,"Please Choose an image",Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Compressing Image");
                    progressDialog.show();

                    // Compress image in main thread
                    actualImage = Compressor.getDefault(Entry_veh.this).compressToFile(actualImage);
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
                Toast.makeText(Entry_veh.this,"Failed to read picture data",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
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
        naka.setText("");
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
