package pranav.apps.amazing.hppoliceassistant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
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
public class Entry extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final String TAG = "Entry.java";
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
    private Location currentBestLocation;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CHECK_SETTINGS = 223;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 133;
    private static final int REQUEST_ACCESS_CAMERA = 100;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 90;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    //flag is 1 when our app has gps permission
    private int flag = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //setting up google api client to request for location updates
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //request permission
        boolean hasPermissionPhoneState = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionPhoneState) {
            ActivityCompat.requestPermissions(Entry.this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_ACCESS_CAMERA);
        }
        boolean hasPermissionPhoneStorage = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermissionPhoneStorage) {
            ActivityCompat.requestPermissions(Entry.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        //create a location request
        createLocationRequest();

        sessionManager = new SessionManager(Entry.this);
        setLogoutBroadcastReceiver();

        mrootRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");
        database = FirebaseDatabase.getInstance();
        mRootRef = database.getReference("vehicle_entry");


        /**The strategies described in this guide apply to the platform location API in android.location. The Google Location
         * Services API, part of Google Play Services, provides a more powerful, high-level framework that automatically handles
         * location providers, user movement, and location accuracy. It also handles location update scheduling based on power
         * consumption parameters you provide. In most cases, you'll get better battery performance, as well as more appropriate
         * accuracy, by using the Location Services API.
         * Here below is an example how you can use ****Android Framework location API (android.location)***** which is built in
         * android but it is strongly recommended to any developer that he/she should switch to google play service API
         *
         *  some ref links to study :-
         * google android developer guide : https://developer.android.com/guide/topics/location/strategies.html#BestPerformance
         * google android developer guide for google play api : https://developer.android.com/training/location/index.html
         * * */

        /** One of the unique features of mobile applications is location awareness. Mobile users take their devices with them
         *  everywhere, and adding location awareness to your app offers users a more contextual experience. The location APIs
         *  available in Google Play services facilitate adding location awareness to your app with automated location tracking,
         *  geofencing, and activity recognition.
         *****The Google Play services location APIs are preferred over the Android framework location APIs (android.location) ******
         * as a way of adding location awareness to your app. If you are currently using the Android framework location APIs,
         * you are strongly encouraged to switch to the Google Play services location APIs as soon as possible.
         * */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Entry.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA}, REQUEST_ACCESS_FINE_LOCATION);
        } else {
            flag = 1;
        }

        if (currentBestLocation == null && flag == 1) {
            mGoogleApiClient.connect();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create a Vehicle Entry");
        setSupportActionBar(toolbar);


        progressDialog = new ProgressDialog(Entry.this);
        progressDialog1 = new ProgressDialog(Entry.this);

        veh = (EditText) findViewById(R.id.vehicle_num);
        phone = (EditText) findViewById(R.id.phone_num_entry);
        description = (EditText) findViewById(R.id.description);
        place = (EditText) findViewById(R.id.place);


        upload = (ImageButton) findViewById(R.id.upload);
        submit_det = (Button) findViewById(R.id.make_entry);

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
                    } else if (!validateFields()) {
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
                    } else {
                        epoch = System.currentTimeMillis();
                        EntryID = populateEntryID();
                        date = generateDateFromSystem();
                        time = generateCurrentTime();

                        newEntrywithoutImage = new VehicleEntry(EntryID, veh.getText().toString(), phone.getText().toString(),
                                description.getText().toString(), place.getText().toString(), date, time, sessionManager.getIOName(), "null", sessionManager.getDistrict(), sessionManager.getPoliceStation(),
                                sessionManager.getPolicePost(), 0);
                        newEntrywithoutImage.setEpoch(epoch);
                        if (currentBestLocation != null) {
                            newEntrywithoutImage.setLatitude(currentBestLocation.getLatitude());
                            newEntrywithoutImage.setLongitude(currentBestLocation.getLongitude());
                        } else {
                            Toast.makeText(Entry.this, "  Current Location is unknown\nPlease turn Data Connection or GPS ON", Toast.LENGTH_LONG).show();
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

        if (uri == null) {
            progressDialog1.setMessage("Uploading data....");
            progressDialog1.show();
            Toast.makeText(Entry.this, "No Photo Selected, uploading data...", Toast.LENGTH_SHORT).show();
            download_url_string = "Photo not available";

            newEntry = new VehicleEntry(EntryID, veh.getText().toString(), phone.getText().toString(),
                    description.getText().toString(), place.getText().toString(), date, time,
                    sessionManager.getIOName(), download_url_string, sessionManager.getDistrict(), sessionManager.getPoliceStation()
                    , sessionManager.getPolicePost(), 1);
            DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry.this, null, null, 1);
            newEntry.setStatus(1);
            newEntry.setEpoch(epoch);
            if (currentBestLocation != null) {
                newEntry.setLatitude(currentBestLocation.getLatitude());
                newEntry.setLatitude(currentBestLocation.getLongitude());
            } else {
                Toast.makeText(Entry.this, "Current Location is unknown\nPlease turn Data Connection or GPS ON", Toast.LENGTH_LONG).show();
            }

            dbManagerEntry.addEntry(newEntry);
            idChild.setValue(newEntry, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        progressDialog1.dismiss();
                        Toast.makeText(Entry.this, "Upload Done ", Toast.LENGTH_SHORT).show();
                        resetAll();
                    } else {
                        progressDialog1.dismiss();
                        Toast.makeText(Entry.this, "Network Error! Data Not Saved,Sorry for inconvenience", Toast.LENGTH_LONG).show();
                        resetAll();
                    }
                }
            });
        }
        if (uri != null) {
            progressDialog1.setMessage("Uploading Image and data....");
            progressDialog1.show();
            progressDialog1.setCancelable(false);
            filepath = mStorage.child("PhotosVehicleEntry").child(idChild.getKey());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if (downloadUrl != null) {
                        download_url_string = downloadUrl.toString();
                    }

                    newEntry = new VehicleEntry(EntryID, veh.getText().toString(), phone.getText().toString(),
                            description.getText().toString(), place.getText().toString(), date, time,
                            sessionManager.getIOName(), download_url_string, sessionManager.getDistrict(), sessionManager.getPoliceStation(),
                            sessionManager.getPolicePost(), 1);
                    DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry.this, null, null, 1);
                    newEntry.setStatus(1);
                    newEntry.setEpoch(epoch);
                    if (currentBestLocation != null) {
                        newEntry.setLatitude(currentBestLocation.getLatitude());
                        newEntry.setLongitude(currentBestLocation.getLongitude());
                    } else {
                        Toast.makeText(Entry.this, "Current Location is unknown\nPlease turn Data Connection or GPS ON", Toast.LENGTH_LONG).show();
                    }

                    dbManagerEntry.addEntry(newEntry);
                    idChild.setValue(newEntry, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                progressDialog1.dismiss();
                                Toast.makeText(Entry.this, "Upload Done ", Toast.LENGTH_SHORT).show();
                                resetAll();
                            } else {
                                progressDialog1.dismiss();
                                Toast.makeText(Entry.this, "Network Error! Data Not Saved,Sorry for inconvenience", Toast.LENGTH_LONG).show();
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
                    DBManagerEntry dbManagerEntry = new DBManagerEntry(Entry.this, null, null, 1);
                    if (dbManagerEntry.addEntry(newEntry)) {
                        Toast.makeText(Entry.this, "Entry Added Offline!", Toast.LENGTH_SHORT).show();
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

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(6000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates a = result.getLocationSettingsStates();
                switch (status.getStatusCode()){
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(Entry.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu, menu);
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
                Intent i = new Intent(Entry.this, OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(Entry.this, OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.developers_activity:
                startActivity(new Intent(Entry.this, DevelopersActivity.class));
                return true;
            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle("LogOut")
                        .setMessage("Do you really want to logout?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                sessionManager.logoutUser();
                            }
                        })
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
                Toast.makeText(Entry.this, "Error Loading File ", Toast.LENGTH_SHORT).show();
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
                actualImage = Compressor.getDefault(Entry.this).compressToFile(actualImage);
                uri = Uri.fromFile(actualImage);

                upload.setImageURI(uri);
                progressDialog.dismiss();
            } catch (IOException e) {
                Toast.makeText(Entry.this, "Failed to read picture data", Toast.LENGTH_SHORT).show();
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

    private String populateEntryID() {
        return "V" + sessionManager.getDistrict().substring(0, 3).toUpperCase() + sessionManager.getPoliceStation().substring(3, 7).toUpperCase().replace("/", "")
                + String.valueOf(epoch).substring(4);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Entry.this, "GPS Permission Granted", Toast.LENGTH_SHORT).show();
                    flag = 1;
                } else {
                    Toast.makeText(Entry.this, "Permissions denied ! App will npt function properly!" +
                            "Please Go to settings and manually provide permissions to this App", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case REQUEST_ACCESS_CAMERA:
                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Entry.this, "Camera Permission Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Entry.this,"Camera Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(Entry.this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Entry.this,"Write Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
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
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(logoutBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Entry.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        } else {
            flag = 1;
            if (currentBestLocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }else {
                currentBestLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(Entry.this,"Connection To Fetch Location Was Suspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Entry.this,"Unable to Fetch the location",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        currentBestLocation = location;
    }
}
