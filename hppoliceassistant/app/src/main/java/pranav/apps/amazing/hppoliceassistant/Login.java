package pranav.apps.amazing.hppoliceassistant;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */

public class Login extends Activity{
    private FirebaseDatabase database;
    private Button login;
    private DatabaseReference rootRef,dRef,nRef;
    Spinner district,police_station,police_post;
    String selected_district = "none",selected_police_station = "none",selected_police_post = "none";
    EditText login_name, login_password;
    String name,password;
    ProgressDialog progressDialog;
    Firebase mRootRef;                                                   //deprecated now
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        login = (Button)findViewById(R.id.login);
        district = (Spinner) findViewById(R.id.district);
        police_station = (Spinner) findViewById(R.id.police_station);
        police_post = (Spinner) findViewById(R.id.police_post);
        login_name = (EditText)findViewById(R.id.name);
        login_password = (EditText)findViewById(R.id.password);
        //mRootRef = new Firebase("https://hppoliceassistant.firebaseio.com/Shimla");   //deprecated now
         database = FirebaseDatabase.getInstance();
        rootRef = database.getReference("login");
        nRef = database.getReference("authenticated_users");
        progressDialog = new ProgressDialog(Login.this);
        ArrayAdapter<CharSequence> adapter_district = ArrayAdapter.createFromResource(this,
                R.array.district,R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter_district.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        district.setAdapter(adapter_district);
        district.setSelection(0);
        final ArrayAdapter<CharSequence> adapter_optionShow = ArrayAdapter.createFromResource(this,
                R.array.option_station,R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter_district.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        police_station.setAdapter(adapter_optionShow);
        police_station.setSelection(0);
        final ArrayAdapter<CharSequence> adapter_optionShow_post = ArrayAdapter.createFromResource(this,
                R.array.option_station_post,R.layout.spinner_layout);
        // Specify the layout to use when the list of choices appears
        adapter_optionShow_post.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        police_post.setAdapter(adapter_optionShow_post);
        police_post.setSelection(0);
        district.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_district = (String) adapterView.getItemAtPosition(i);
                if(!selected_district.contentEquals("Select District")) {
                    Toast.makeText(getApplicationContext(), "Selected District :" + selected_district + "", Toast.LENGTH_SHORT).show();
                }
                if(selected_district.contentEquals("Kinnaur")) {
                    ArrayAdapter<CharSequence> adapter_police_station_kinnaur = ArrayAdapter.createFromResource(getBaseContext(),
                            R.array.police_station_kinnaur, R.layout.spinner_layout);
                    // Specify the layout to use when the list of choices appears
                    adapter_police_station_kinnaur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    police_station.setAdapter(adapter_police_station_kinnaur);
                    police_station.setSelection(0);
                }

                else if(selected_district.contentEquals("Shimla")) {
                    ArrayAdapter<CharSequence> adapter_police_station_shimla = ArrayAdapter.createFromResource(getBaseContext(),
                            R.array.police_station_shimla, R.layout.spinner_layout);
                    // Specify the layout to use when the list of choices appears
                    adapter_police_station_shimla.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    police_station.setAdapter(adapter_police_station_shimla);
                    police_station.setSelection(0);
                }

                else if(selected_district.contentEquals("Mandi")){
                    ArrayAdapter<CharSequence> adapter_police_station_mandi = ArrayAdapter.createFromResource(getBaseContext(),
                            R.array.police_station_mandi, R.layout.spinner_layout);
                    // Specify the layout to use when the list of choices appears
                    adapter_police_station_mandi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner
                    police_station.setAdapter(adapter_police_station_mandi);
                    police_station.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        police_station.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selected_police_station = (String) adapterView.getItemAtPosition(i);
                    if(!selected_police_station.contentEquals("Select PoliceStation")) {
                        Toast.makeText(getApplicationContext(), "Selected PoliceStation: :" + selected_police_station + "", Toast.LENGTH_SHORT).show();
                    }
                    if(selected_police_station.contentEquals("PS B/Nagar")){
                        ArrayAdapter<CharSequence> adapter_police_post_bnagar = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_BNagar_policepost, R.layout.spinner_layout);
                        // Specify the layout to use when the list of choices appears
                        adapter_police_post_bnagar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        police_post.setAdapter(adapter_police_post_bnagar);
                        police_post.setSelection(0);

                    }
                    if(selected_police_station.contentEquals("PS Pooh")){
                        ArrayAdapter<CharSequence> adapter_police_post_pooh = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Pooh_policepost, R.layout.spinner_layout);
                        // Specify the layout to use when the list of choices appears
                        adapter_police_post_pooh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        police_post.setAdapter(adapter_police_post_pooh);
                        police_post.setSelection(0);
                    }
                    if(selected_police_station.contentEquals("PS Sangla")){
                        ArrayAdapter<CharSequence> adapter_police_post_sangla = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Sangla_policepost, R.layout.spinner_layout);
                        // Specify the layout to use when the list of choices appears
                        adapter_police_post_sangla.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        police_post.setAdapter(adapter_police_post_sangla);
                        police_post.setSelection(0);
                    }
                    if(selected_police_station.contentEquals("PS R/Peo")){
                        ArrayAdapter<CharSequence> adapter_police_post_rpeo = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Rpeo_policepost, R.layout.spinner_layout);
                        // Specify the layout to use when the list of choices appears
                        adapter_police_post_rpeo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        police_post.setAdapter(adapter_police_post_rpeo);
                        police_post.setSelection(0);
                    }
                    if(selected_police_station.contentEquals("PS Sadar")){
                        ArrayAdapter<CharSequence> adapter_police_post_sadar = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Sadar_policepost, R.layout.spinner_layout);
                        // Specify the layout to use when the list of choices appears
                        adapter_police_post_sadar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        // Apply the adapter to the spinner
                        police_post.setAdapter(adapter_police_post_sadar);
                        police_post.setSelection(0);
                    }

                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
        });
        police_post.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_police_post = (String)adapterView.getItemAtPosition(i);
                if(!selected_police_post.contentEquals("Select PolicePost")) {
                    Toast.makeText(getApplicationContext(), "Selected PolicePost :" + selected_police_post + "", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Checking...");
                progressDialog.show();
                name = login_name.getText().toString().trim();
                password = login_password.getText().toString().trim();
                if(name.contentEquals("")|| password.contentEquals("")||selected_police_post.contentEquals("none")||selected_police_station.contentEquals("none")||selected_district.contentEquals("none")){
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(),"Please Enter all details",Toast.LENGTH_SHORT).show();
                }
                if(!name.contentEquals("") && !password.contentEquals("")&&!selected_police_post.contentEquals("none")&&!selected_police_station.contentEquals("none")&&!selected_district.contentEquals("none")){
                    dRef = rootRef.child(selected_district).child(selected_police_station.replace("/","")).child(selected_police_post.replace("/",""));
                    final DatabaseReference to_user = nRef.child(selected_district.toLowerCase()).child(name.toLowerCase());

                    dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //
                            String pass = dataSnapshot.getValue(String.class);
                            if(pass == null){
                                Toast.makeText(getBaseContext(),"Something Went Wrong",Toast.LENGTH_SHORT).show();
                            }
                            if(pass != null) {
                                if(!pass.contentEquals(password)) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getBaseContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                                }
                                if (pass.contentEquals(password)) {
                                    /*to add user authentication */

                                    to_user.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String auth = dataSnapshot.getValue(String.class);
                                            if (auth == null) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getBaseContext(), "User doesn't exist !", Toast.LENGTH_LONG).show();
                                            }
                                            if (auth != null) {
                                                if (auth.contentEquals("0")) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getBaseContext(), "You are not a authenticated User ! Sorry :( ", Toast.LENGTH_LONG).show();
                                                }
                                                if (auth.contentEquals("1")) {
                                                    progressDialog.dismiss();
                                                    Intent i = new Intent("pranav.apps.amazing.hppoliceassistant.HOME");
                                                    startActivity(i);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getBaseContext(),"Operation Cancelled",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
