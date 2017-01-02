package pranav.apps.amazing.hppoliceassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Entry_veh extends Fragment {
    private Firebase mRootRef;
    private EditText veh,phone,description,place,naka,officer;
    private TextView date,time;
    private Button submit_det,reset;
    private ImageButton upload;
    private String path;
    private StorageReference mStorage,filepath;
    private static final int GALLERY_INTENT =2;
    private ProgressDialog progressDialog,progressDialog1;
    private Uri uri=null,downloadUrl=null;
    private String download_url_string="";
    private int ACTION_IMAGE_CAPTURE_ACTIVITY =1888;
    private VehicleEntry newEntry,newEntrywithoutImage;
    private VehicleEntryDialog vehicleEntryDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mRootRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");


        View  view=  getActivity().getLayoutInflater().inflate(R.layout.entry,container,false);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog1 = new ProgressDialog(getActivity());

        veh=(EditText)view.findViewById(R.id.vehicle_num);
        phone=(EditText)view.findViewById(R.id.phone_num);
        description=(EditText)view.findViewById(R.id.description);
        place=(EditText)view.findViewById(R.id.place);
        naka=(EditText)view.findViewById(R.id.naka);
        time=(TextView)view.findViewById(R.id.time1);
        date=(TextView)view.findViewById(R.id.date1);
        officer=(EditText)view.findViewById(R.id.police_officer_name);


        upload=(ImageButton)view.findViewById(R.id.upload);
        submit_det=(Button)view.findViewById(R.id.make_entry);
        reset=(Button)view.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAll();
            }
        });
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
        mStorage = FirebaseStorage.getInstance().getReference();
        submit_det.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                newEntrywithoutImage = new VehicleEntry(veh.getText().toString(),phone.getText().toString(),description.getText().toString(),place.getText().toString(),
                        naka.getText().toString(),date.getText().toString(),time.getText().toString(),officer.getText().toString(),"null");
                vehicleEntryDialog= new VehicleEntryDialog(getActivity(),newEntrywithoutImage);
                vehicleEntryDialog.setTitle("Entry Details");
                vehicleEntryDialog.setCancelable(true);
                vehicleEntryDialog.show();
                vehicleEntryDialog.findViewById(R.id.offline).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBManagerEntry dbManagerEntry = new DBManagerEntry(getActivity(),null,null,1);
                        if(dbManagerEntry.addEntry(newEntrywithoutImage)){
                            Toast.makeText(getActivity(),"Entry Added Offline!",Toast.LENGTH_SHORT).show();
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

                //nak.setText("Done");                                 //function to generate UUID
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
                */
                //Firebase idChild = mRootRef.push();           //unique ID generated by server firebase
            }
        });
        final Fragment fragment = this;
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               // intent.setType("image/*");
                startActivityForResult(intent,ACTION_IMAGE_CAPTURE_ACTIVITY);
            }
        });
        return  view;
    }

    private void startPosting() {
        final Firebase idChild = mRootRef.push();

        if(uri == null){
            Toast.makeText(getActivity(),"No Photo Selected, uploading data...",Toast.LENGTH_LONG).show();
            download_url_string ="Photo not available";

            newEntry = new VehicleEntry(veh.getText().toString(),phone.getText().toString(),
                    description.getText().toString(),place.getText().toString(),
                    naka.getText().toString(),date.getText().toString(),time.getText().toString(),
                    officer.getText().toString(),download_url_string);
            DBManagerEntry dbManagerEntry = new DBManagerEntry(getActivity(),null,null,1);
            if(dbManagerEntry.addEntry(newEntry)){
                Toast.makeText(getActivity(),"Entry Added Offline!",Toast.LENGTH_SHORT).show();
            }
            idChild.setValue(newEntry);
            Toast.makeText(getActivity(),"Upload Done ",Toast.LENGTH_SHORT).show();
        }
        if(uri!=null) {
            progressDialog1.setMessage("Uploading Image and data....");
            progressDialog1.show();
            filepath = mStorage.child("PhotosVehicleEntry").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(), "Upload Done !", Toast.LENGTH_LONG).show();
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    if(downloadUrl!=null) {
                        download_url_string = downloadUrl.toString();
                    }

                    newEntry = new VehicleEntry(veh.getText().toString(),phone.getText().toString(),
                            description.getText().toString(),place.getText().toString(),
                            naka.getText().toString(),date.getText().toString(),time.getText().toString(),
                            officer.getText().toString(),download_url_string);
                    DBManagerEntry dbManagerEntry = new DBManagerEntry(getActivity(),null,null,1);
                    if(dbManagerEntry.addEntry(newEntry)){
                        Toast.makeText(getActivity(),"Entry Added Offline!",Toast.LENGTH_SHORT).show();
                    }
                    idChild.setValue(newEntry);

                    progressDialog1.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(), "Upload Failed !", Toast.LENGTH_LONG).show();
                    progressDialog1.dismiss();
                    DBManagerEntry dbManagerEntry = new DBManagerEntry(getActivity(),null,null,1);
                    if(dbManagerEntry.addEntry(newEntry)){
                        Toast.makeText(getActivity(),"Entry Added Offline!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        progressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==ACTION_IMAGE_CAPTURE_ACTIVITY && resultCode==RESULT_OK){
            Bitmap bmp = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // convert byte array to Bitmap

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0,
                    byteArray.length);

            upload.setImageBitmap(bitmap);
            /*
            uri = getImageUri(getActivity(),bitmap);
            upload.setImageURI(uri);
            */
            /*
           uri =data.getData();
            upload.setBackgroundColor(0);
            upload.setImageURI(uri);
            */

        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragmentEntry();
        newFragment.show(getFragmentManager(),"datePicker");
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragmentEntry();
        newFragment.show(getFragmentManager(), "timePicker");
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
        officer.setText("");
        date.setText("fill date here");
        time.setText("fill time here");
    }
}
