package pranav.apps.amazing.hppoliceassistant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
    private static final int REQUEST_ACCESS_FINE_LOCATION = 133;
    private Firebase mrootRef;
    private EditText veh, phone, description, place;
    private Button submit_det;
    private ImageButton upload;
    private String path;
    private StorageReference mStorage, filepath;
    private ProgressDialog progressDialog, progressDialog1;
    private Uri uri = null, downloadUrl = null;
    private String download_url_string = "";
    private VehicleEntry newEntry, newEntrywithoutImage;
    private VehicleEntryDialog vehicleEntryDialog;
    private File actualImage;
    private FirebaseDatabase database;
    private DatabaseReference mRootRef;
    private int PICK_IMAGE_REQUEST = 1;
    private String[] month = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private String ampm = "AM";
    private String off_name;
    private SessionManager sessionManager;
    private BroadcastReceiver logoutBroadcastReceiver;
    private String EntryID;
    private String date, time;
    private long epoch;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location currentBestLocation = null;
    private Location fixedLocationAfterButtonClick;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    //flag is 1 when our app has gps permission
    private int flag=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        sessionManager = new SessionManager(Entry.this);
        setLogoutBroadcastReceiver();

        mrootRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");
        database = FirebaseDatabase.getInstance();
        mRootRef = database.getReference("vehicle_entry");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Entry.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            flag=1;
        }

        if(currentBestLocation==null && flag==1){
            currentBestLocation = getLastBestLocation();
        }

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
                        builder.setTitle("Vehicle Entry")
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
                        //currentBestLocation = getLastBestLocation();
                        fixedLocationAfterButtonClick=currentBestLocation;



                        newEntrywithoutImage = new VehicleEntry(EntryID,veh.getText().toString(), phone.getText().toString(),
                                description.getText().toString(), place.getText().toString(),date,time,sessionManager.getIOName(), "null",sessionManager.getDistrict(),sessionManager.getPoliceStation(),
                                sessionManager.getPolicePost(),0);
                        newEntrywithoutImage.setEpoch(epoch);
                        if(fixedLocationAfterButtonClick!=null){
                            newEntrywithoutImage.setLatitude(fixedLocationAfterButtonClick.getLatitude());
                            newEntrywithoutImage.setLongitude(fixedLocationAfterButtonClick.getLongitude());
                        }
                        else {
                            Toast.makeText(Entry.this,"  Current Location is unknown\nPlease turn Data Connection or GPS ON",Toast.LENGTH_LONG).show();
                        }
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
                dispatchTakePictureIntent();
/*                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image*//*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST);*/
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
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
            if(fixedLocationAfterButtonClick!=null){
                newEntry.setLatitude(fixedLocationAfterButtonClick.getLatitude());
                newEntry.setLatitude(fixedLocationAfterButtonClick.getLongitude());
            }
            else {
                Toast.makeText(Entry.this,"Current Location is unknown\nPlease turn Data Connection or GPS ON",Toast.LENGTH_LONG).show();
            }

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
            progressDialog1.setCancelable(false);
            filepath = mStorage.child("PhotosVehicleEntry").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
                    if(fixedLocationAfterButtonClick!=null){
                        newEntry.setLatitude(fixedLocationAfterButtonClick.getLatitude());
                        newEntry.setLongitude(fixedLocationAfterButtonClick.getLongitude());
                    }
                    else {
                        Toast.makeText(Entry.this,"Current Location is unknown\nPlease turn Data Connection or GPS ON",Toast.LENGTH_LONG).show();
                    }

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

    /*@Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(Entry.this,"Error Loading File ",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                actualImage = FileUtil.from(Entry.this, data.getData());
                upload.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                if (actualImage == null) {
                    Toast.makeText(Entry.this,"Please Choose an image",Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setMessage("Compressing Image");
                    progressDialog.show();

                    // Compress image in main thread
                    actualImage = Compressor.getDefault(Entry.this).compressToFile(actualImage);
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
    }*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(Entry.this,"Error Loading File ",Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri tempUri = getImageUri(getApplicationContext(), photo);

                // CALL THIS METHOD TO GET THE ACTUAL PATH
                //File finalFile = new File(getRealPathFromURI(tempUri));
                actualImage = FileUtil.from(Entry.this, tempUri);
                //upload.setImageBitmap(BitmapFactory.decodeFile(actualImage.getAbsolutePath()));
                progressDialog.setMessage("Compressing Image");
                progressDialog.show();

                // Compress image in main thread
                //actualImage = Compressor.getDefault(Entry.this).compressToFile(actualImage);
                uri = Uri.fromFile(actualImage);

                upload.setImageURI(uri);
                progressDialog.dismiss();
            } catch (IOException e) {
                Toast.makeText(Entry.this,"Failed to read picture data",Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, veh.getText().toString(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
    private boolean validateFields() {
        return (DataTypeValidator.validatePhoneNumberFormat(phone.getText().toString())
                && DataTypeValidator.validateVehicleNumberFormat(veh.getText().toString()));
    }
    private String populateEntryID(){
        return "V"+sessionManager.getDistrict().substring(0,3).toUpperCase()+sessionManager.getPoliceStation().substring(3,7).toUpperCase().replace("/","")
                +String.valueOf(epoch).substring(4);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_ACCESS_FINE_LOCATION:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Entry.this,"GPS Access Permission Granted",Toast.LENGTH_SHORT).show();
                    flag=1;
                }
                else {
                    Toast.makeText(Entry.this,"GPS Access Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Entry.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            flag=1;
        }
        if(flag==1){
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationListener = new MyLocationListener(Entry.this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if ( 0 < GPSLocationTime - NetLocationTime ) {
                return locationGPS;
            }
            else {
                return locationNet;
            }
        }
        else {
            return null;
        }

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

    private class MyLocationListener implements LocationListener {

        private Context context;
        private static final int TWO_MINUTES = 1000 * 60 * 2;


        public MyLocationListener(Context context){
            this.context = context;
        }

        @Override
        public void onLocationChanged(Location location) {
            makeUseOfNewLocation(location);

            if(currentBestLocation == null){
                currentBestLocation = location;
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
            Toast.makeText(context,"GPS Enabled", Toast.LENGTH_SHORT ).show();
            Handler handler = new Handler();
            final ProgressDialog pg = ProgressDialog.show(Entry.this,"GPS Location","Updating location...");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pg.dismiss();
                }
            },5000);
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recreate();
                }
            },2000);

        }

        @Override
        public void onProviderDisabled(String s) {
            Toast.makeText(context,"GPS Disabled Please Turn It ON", Toast.LENGTH_SHORT ).show();

        }
        /**
         * This method modify the last know good location according to the arguments.
         *
         * @param location The possible new location.
         */
        void makeUseOfNewLocation(Location location) {
            if ( isBetterLocation(location, currentBestLocation) ) {
                currentBestLocation = location;
            }
            else {
                currentBestLocation = getLastBestLocation();
            }
        }

        /** Determines whether one location reading is better than the current location fix
         * @param location  The new location that you want to evaluate
         * @param currentBestLocation  The current location fix, to which you want to compare the new one.
         */
        protected boolean isBetterLocation(Location location, Location currentBestLocation) {
            if (currentBestLocation == null) {
                // A new location is always better than no location
                return true;
            }

            // Check whether the new location fix is newer or older
            long timeDelta = location.getTime() - currentBestLocation.getTime();
            boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
            boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
            boolean isNewer = timeDelta > 0;

            // If it's been more than two minutes since the current location, use the new location,
            // because the user has likely moved.
            if (isSignificantlyNewer) {
                return true;
                // If the new location is more than two minutes older, it must be worse.
            } else if (isSignificantlyOlder) {
                return false;
            }

            // Check whether the new location fix is more or less accurate
            int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
            boolean isLessAccurate = accuracyDelta > 0;
            boolean isMoreAccurate = accuracyDelta < 0;
            boolean isSignificantlyLessAccurate = accuracyDelta > 200;

            // Check if the old and new location are from the same provider
            boolean isFromSameProvider = isSameProvider(location.getProvider(),
                    currentBestLocation.getProvider());

            // Determine location quality using a combination of timeliness and accuracy
            if (isMoreAccurate) {
                return true;
            } else if (isNewer && !isLessAccurate) {
                return true;
            } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
                return true;
            }
            return false;
        }

        /** Checks whether two providers are the same */
        private boolean isSameProvider(String provider1, String provider2) {
            if (provider1 == null) {
                return provider2 == null;
            }
            return provider1.equals(provider2);
        }
    }
}
