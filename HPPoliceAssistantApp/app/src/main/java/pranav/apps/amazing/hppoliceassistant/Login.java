package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;

/**
 * Created by Pranav Gupta on 12/10/2016.
 * This Class is for Logging in into app
 * On Successfully logging in it launches ??? Activity
 * This Activity is first activity that is launched
 */

public class Login extends Activity {
    private FirebaseDatabase database;
    private Button login;
    private DatabaseReference rootRef, dRef, nRef;
    private SessionManager sessionManager;
    Spinner district, police_station, police_post;
    String selected_district = "none", selected_police_station = "none", selected_police_post = "none";
    EditText login_name, login_password;
    String name, password;
    ProgressDialog progressDialog;

    /*Used for logging purposes*/
    private String TAG = "Login.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
       /*
        If user did not logout from last session then directly take him to login screen instead of
        prompting for login details again.
         */
        sessionManager = new SessionManager(Login.this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, Home.class));
        }

        login = (Button) findViewById(R.id.login);
        district = (Spinner) findViewById(R.id.district);
        police_station = (Spinner) findViewById(R.id.police_station);
        police_post = (Spinner) findViewById(R.id.police_post);
        login_name = (EditText) findViewById(R.id.name);
        login_password = (EditText) findViewById(R.id.password);

        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference("login");
        nRef = database.getReference("authenticated_users");
        progressDialog = new ProgressDialog(Login.this);

        /*Populate Districts dropdown(spinner) with a list of districts in HP*/
        populateDistrictSpinner();

        /*Populates the police station dropdown(spinner) with a dummy option of "Select Police Station" */
        populatePoliceStationSpinner();

        /*Populates the police post dropdown(spinner) with a dummy option of "Select Police Post" */
        populatePolicePostDropdown();

        /*When a district is selected this method updates the police station spinner according to the selected district*/
        setDistrictChangeListener();

        /*When a police station is selected this method updates the police post spinner according to selected police post*/
        setPoliceStationChangeListener();

        setLoginButton();



    }

    /**
     * This method populates Districts dropdown(spinner) with a list of districts in HP
     */
    private void populateDistrictSpinner() {

        /*Select the district spinner*/
        Spinner districtSpinner = (Spinner) findViewById(R.id.district);

        /*Create an ArrayAdapter for all the districts for the districts dropdown*/
        ArrayAdapter<CharSequence> districtArrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.district, R.layout.spinner_layout);

        /*Specify the layout to use when the list of choices appears*/
        districtArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /*Apply the adapter to the spinner*/
        districtSpinner.setAdapter(districtArrayAdapter);

    }


    /**
     * This method populates the police station dropdown(spinner) with a dummy option of "Select Police Station"
     */
    private void populatePoliceStationSpinner() {

        /*Create reference to the police station spinner*/
        Spinner policeStationSpinner = (Spinner) findViewById(R.id.police_station);

        /*Until a real district is selected show "Select Police Station" in the dropdown*/
        /*Create an array adapter for "Select Police Station"*/
        final ArrayAdapter<CharSequence> adapter_optionShow = ArrayAdapter.createFromResource(this,
                R.array.option_station, R.layout.spinner_layout);

        /*Apply the adapter to the spinner*/
        policeStationSpinner.setAdapter(adapter_optionShow);


    }


    /**
     * This method populates the police post dropdown(spinner) with a dummy option of "Select Police Post"
     */
    private void populatePolicePostDropdown() {

        /*Create reference to the police station spinner*/
        Spinner policePostSpinner = (Spinner) findViewById(R.id.police_post);

        /*Until a real police Station is selected show "Select Police Post" in the dropdown*/
        /*Create an array adapter for "Select Police Post"*/
        final ArrayAdapter<CharSequence> adapter_optionShow_post = ArrayAdapter.createFromResource(this,
                R.array.option_station_post, R.layout.spinner_layout);

        /*Specify the layout to use when the list of choices appears*/
        adapter_optionShow_post.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /*Apply the adapter to the spinner*/
        policePostSpinner.setAdapter(adapter_optionShow_post);
    }


    /**
     * When a district is selected this method updates the police station spinner according to the selected district
     */
    private void setDistrictChangeListener() {
        /*Select the district spinner*/
        Spinner districtSpinner = (Spinner) findViewById(R.id.district);
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_district = (String) adapterView.getItemAtPosition(i);

                ArrayAdapter<CharSequence> adapter_police_station;
                switch (selected_district) {
                    case "Kinnaur":
                        adapter_police_station = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.police_station_kinnaur, R.layout.spinner_layout);
                        break;
                    case "Shimla":
                        adapter_police_station = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.police_station_shimla, R.layout.spinner_layout);
                        break;
                    case "Mandi":
                        adapter_police_station = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.police_station_mandi, R.layout.spinner_layout);
                        break;
                    default:
                        adapter_police_station = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.option_station, R.layout.spinner_layout);
                        break;
                }
                // Specify the layout to use when the list of choices appears
                adapter_police_station.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                police_station.setAdapter(adapter_police_station);
                police_station.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


    }


    /**
     * When a police station is selected this method updates the police post spinner according to selected police post
     */
    private void setPoliceStationChangeListener() {
        Spinner policeStationSpinner = (Spinner) findViewById(R.id.police_station);
        policeStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedPoliceStation = getSelectedPoliceStation();

                ArrayAdapter<CharSequence> policePostAdapter;
                switch (selectedPoliceStation) {
                    case "PS B/Nagar":
                        policePostAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_BNagar_policepost, R.layout.spinner_layout);
                        break;
                    case "PS Pooh":
                        policePostAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Pooh_policepost, R.layout.spinner_layout);
                        break;
                    case "PS Sangla":
                        policePostAdapter =  ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Sangla_policepost, R.layout.spinner_layout);
                        break;
                    case "PS R/Peo":
                        policePostAdapter =  ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Rpeo_policepost, R.layout.spinner_layout);
                        break;
                    case "PS Sadar":
                        policePostAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Sadar_policepost, R.layout.spinner_layout);
                        break;
                    default:
                        policePostAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.option_station_post, R.layout.spinner_layout);
                        break;

                }
                /*Set the layout for the dropdown*/
                policePostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                /*Apply the adapter to the spinner*/
                police_post.setAdapter(policePostAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



    private void setLoginButton() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String selectedDistrict = getSelectedDistrict();
                final String selectedPoliceStation = getSelectedPoliceStation();
                final String selectedPolicePost = getSelectedPolicePost();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                progressDialog.setMessage("Checking...");
                progressDialog.show();
                name = login_name.getText().toString().trim();
                password = login_password.getText().toString().trim();
                Log.v(TAG, "Selected Police Post is: " + selectedPolicePost);
                Log.v(TAG, "Selected District is: " + selectedDistrict);
                Log.v(TAG, "Selected Police Station is: " + selectedPoliceStation);
                if (name.contentEquals("") || password.contentEquals("") || selectedPolicePost.contentEquals("none") || selectedPoliceStation.contentEquals("none") || selectedDistrict.contentEquals("none")) {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Please Enter all details", Toast.LENGTH_SHORT).show();
                }
                if (!name.contentEquals("") && !password.contentEquals("") && !selectedPolicePost.contentEquals("none") && !selectedPoliceStation.contentEquals("none") && !selectedDistrict.contentEquals("none")) {
                    dRef = rootRef.child(selectedDistrict).child(selectedPoliceStation.replace("/", "")).child(selected_police_post.replace("/", ""));
                    final DatabaseReference to_user = nRef.child(selectedDistrict.toLowerCase()).child(name.toLowerCase());

                    dRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //
                            String pass = dataSnapshot.getValue(String.class);
                            if (pass == null) {
                                Toast.makeText(getBaseContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            }
                            if (pass != null) {
                                if (!pass.contentEquals(password)) {
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
                                                    goToHomeScreen();
                                                    sessionManager.createLoginSession(login_name.getText().toString(), "null");
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
                            Toast.makeText(getBaseContext(), "Operation Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    /**
     * This method takes user to home screen and finishes this activity
     */
    private void goToHomeScreen() {
        Intent i = new Intent(this, Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //i.putExtra("name",login_name.getText().toString());
        startActivity(i);
        finish(); //Finish this activity so that user cannot come back to this activity
    }


    /**
     * This method returns the selected district in the dropdown
     * @return Selected district's string value
     */
    private String getSelectedDistrict() {
        Spinner districtSpinner = (Spinner) findViewById(R.id.district);
        return districtSpinner.getSelectedItem().toString();
    }

    /**
     * This method returns the selected police station in the dropdown
     * @return Selected Police Station's string value
     */
    private String getSelectedPoliceStation() {
        Spinner policeStationSpinner = (Spinner) findViewById(R.id.police_station);
        return policeStationSpinner.getSelectedItem().toString();
    }

    /**
     * THis method returns the selected police post in the dropdown
     * @return Selected Police Post's dropdown
     */
    private String getSelectedPolicePost() {
        Spinner policePostSpinner = (Spinner) findViewById(R.id.police_post);
        return policePostSpinner.getSelectedItem().toString();
    }
}
