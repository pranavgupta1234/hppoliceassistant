package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static pranav.apps.amazing.hppoliceassistant.R.id.police_post;
import static pranav.apps.amazing.hppoliceassistant.R.id.police_station;

/**
 * Created by Pranav Gupta on 12/10/2016.
 * Modified by: Gopal Krishan Aggarwal on 29/01/2017
 * This Class is for Logging in (Signing in) into app
 * On Successfully logging in it launches Home Activity
 * This Activity is first activity that is launched on opening the app
 */
public class Login extends Activity {

    private SessionManager sessionManager;

    ProgressDialog progressDialog;

    /*Used for logging purposes*/
    private String TAG = "Login.java";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        /*Create a new session (a session may not yet be a logged in session)*/
        sessionManager = new SessionManager(Login.this);

        /*
        If user did not logout from last session then directly take him to login screen instead of
        prompting for login details again.
         */
        if (sessionManager.isLoggedIn()) {
            goToHomeScreen();
        }


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

        /*Set behaviour for when login button is clicked*/
        setLoginButton();

        /* This function populates district and IO Name from last successful login if any
        * As a result of selecting the district, last selected police station and in turn
        * last selected police post is also automatically selected*/
        populateDataFromLastLogin();


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
        Spinner policeStationSpinner = (Spinner) findViewById(police_station);

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
        Spinner policePostSpinner = (Spinner) findViewById(police_post);

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
                String selectedDistrict = (String) adapterView.getItemAtPosition(i);

                ArrayAdapter<CharSequence> adapter_police_station;
                switch (selectedDistrict) {
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

                /*Get reference to police station dropdown*/
                Spinner policeStationSpinner = (Spinner) findViewById(R.id.police_station);

                // Apply the adapter to the spinner
                policeStationSpinner.setAdapter(adapter_police_station);

                /*Select police station that was selected in last successful login, if any*/
                selectPoliceStationFromLastLogin();
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
        Spinner policeStationSpinner = (Spinner) findViewById(police_station);
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
                        policePostAdapter = ArrayAdapter.createFromResource(getBaseContext(),
                                R.array.PS_Sangla_policepost, R.layout.spinner_layout);
                        break;
                    case "PS R/Peo":
                        policePostAdapter = ArrayAdapter.createFromResource(getBaseContext(),
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

                /*Get reference to police post spinner*/
                Spinner policePostSpinner = (Spinner) findViewById(R.id.police_post);

                /*Apply the adapter to the spinner*/
                policePostSpinner.setAdapter(policePostAdapter);

                /*Select police post that was selected in last successful login, if any*/
                selectPolicePostFromLastLogin();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    /**
     * This function determines what happens when login button is clicked
     */
    private void setLoginButton() {
        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard();

                /*If user did not enter any detail, then exit this function after displaying appropriate message*/
                if (!validInput()) {
                    return;
                }

                /*Get the data entered by user*/
                final String selectedDistrict = getSelectedDistrict();
                final String selectedPoliceStation = getSelectedPoliceStation();
                final String selectedPolicePost = getSelectedPolicePost();
                final String iOName = getEnteredIOName().trim(); //Trim spaces at the end and beginning of string
                final String enteredPassword = getEnteredPassword();

                /*Check if password exists in local store
                * if exists loginIfTrueCredentials
                * else check internet
                * if no internet ask for internet connection
                * else login through internet(and save password in local store)*/

                SharedPreferences sharedPrefPassword = getSharedPreferences("passwords", Context.MODE_PRIVATE);
                String realPasswordLocal = sharedPrefPassword.getString(selectedDistrict + selectedPoliceStation, "");

                if (!realPasswordLocal.equals("")) {
                    /*Indicating the password is stored in local database*/
                    loginIfTrueCredentials(enteredPassword, realPasswordLocal, selectedDistrict, selectedPoliceStation, selectedPolicePost, iOName);
                }
                else {
                    if (!isConnectedToInternet()) {
                        //Throw toast asking user to connect
                        Toast.makeText(Login.this, "Please connect to Internet to be able to Login first time with this police station", Toast.LENGTH_LONG).show();
                        return;
                    }

                    displayProgressDialog();

                    /*Indicating the password is NOT stored in local database*/
                    String passwordLocationInFirebase =
                    "login/" + selectedDistrict + "/" + selectedPoliceStation.replace("/", "") + "/password";

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(passwordLocationInFirebase);

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                        /*Dismiss progress dialog after fetching the password from server*/
                            dismissProgressDialog();
                            String realPassword = dataSnapshot.getValue(String.class);

                            /*Store password in local database so that user do not have to be connected to internet to login next time through this PS*/
                            storePasswordInSharedPref(realPassword, selectedDistrict, selectedPoliceStation);

                            if (realPassword == null) {
                                Toast.makeText(getBaseContext(), "Problem verifying password", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                loginIfTrueCredentials(enteredPassword, realPassword, selectedDistrict, selectedPoliceStation, selectedPolicePost, iOName);
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
     * This function populates district and IO Name from last successful login if any
     * As a result of selecting the district, last selected police station and in turn
     * last selected police post is also automatically selected
     */
    private void populateDataFromLastLogin() {
        Spinner districtSpinner = (Spinner) findViewById(R.id.district);
        int position = ((ArrayAdapter)districtSpinner.getAdapter()).getPosition(sessionManager.getDistrict());
        if(position >= 0) {
            districtSpinner.setSelection(position);
        }

        EditText iOName = (EditText) findViewById(R.id.io_name);
        if(!sessionManager.getIOName().equals("")) {
            iOName.setText(sessionManager.getIOName());
        }
    }

    /**
     * This method selects the police station from last successful login, if any,
     * and if police station available in current list of police station spinner
     */
    private void selectPoliceStationFromLastLogin() {
        Spinner policeStationSpinner = (Spinner) findViewById(R.id.police_station);
        int position = ((ArrayAdapter)policeStationSpinner.getAdapter()).getPosition(sessionManager.getPoliceStation());
        if(position >= 0) {
            policeStationSpinner.setSelection(position);
        }
    }

    /**
     * This method selects the police post from last successful login, if any,
     * and if police post available in current list of police post spinner
     */
    private void selectPolicePostFromLastLogin() {
        Spinner policePostSpinner = (Spinner) findViewById(R.id.police_post);
        int position = ((ArrayAdapter)policePostSpinner.getAdapter()).getPosition(sessionManager.getPolicePost());
        if(position >= 0) {
            policePostSpinner.setSelection(position);
        }
    }

    /**
     * This method takes user to home screen and finishes this activity
     */
    private void goToHomeScreen() {
        Intent i = new Intent(this, Home.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish(); //Finish this activity so that user cannot come back to this activity
    }


    /**
     * This method returns the selected district in the dropdown
     *
     * @return Selected district's string value
     */
    private String getSelectedDistrict() {
        Spinner districtSpinner = (Spinner) findViewById(R.id.district);
        return districtSpinner.getSelectedItem().toString();
    }

    /**
     * This method returns the selected police station in the dropdown
     *
     * @return Selected Police Station's string value
     */
    private String getSelectedPoliceStation() {
        Spinner policeStationSpinner = (Spinner) findViewById(police_station);
        return policeStationSpinner.getSelectedItem().toString();
    }

    /**
     * This method returns the selected police post in the dropdown
     *
     * @return Selected Police Post's dropdown
     */
    private String getSelectedPolicePost() {
        Spinner policePostSpinner = (Spinner) findViewById(police_post);
        return policePostSpinner.getSelectedItem().toString();
    }

    /**
     * This method returns the IO Name entered by user
     *
     * @return IO Name String
     */
    private String getEnteredIOName() {
        EditText iONameEditText = (EditText) findViewById(R.id.io_name);
        return iONameEditText.getText().toString().trim();
    }

    /**
     * This method returns the password entered by user
     *
     * @return Password String entered by user
     */
    private String getEnteredPassword() {
        EditText enteredPasswordEditText = (EditText) findViewById(R.id.password);
        return enteredPasswordEditText.getText().toString().trim();
    }

    private void displayProgressDialog() {
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Checking...");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }


    /**
     * This method is used to programmatically hide soft keyboard
     */
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /**
     * The following function checks whether the user entered all the fields.
     * It displays a toast if some field is not entered requesting the user to enter it.
     *
     * @return true if all fields are entered/selected else false
     */
    private boolean validInput() {
        /*Get the data entered by user*/
        String selectedDistrict = getSelectedDistrict();
        String selectedPoliceStation = getSelectedPoliceStation();
        String selectedPolicePost = getSelectedPolicePost();
        String iOName = getEnteredIOName();
        String enteredPassword = getEnteredPassword();
        if (selectedDistrict.equals(getResources().getString(R.string.select_district))) {
            Toast.makeText(this, R.string.select_district_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedPoliceStation.equals(getResources().getString(R.string.select_police_station))) {
            Toast.makeText(this, R.string.select_police_station_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedPolicePost.equals(getResources().getString(R.string.select_police_post))) {
            Toast.makeText(this, R.string.select_police_post_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (iOName.equals("")) {
            Toast.makeText(this, R.string.enter_io_name_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (enteredPassword.equals("")) {
            Toast.makeText(this, R.string.enter_password_toast, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * This method checks if user entered correct password and if yes creates a login session and takes the user to home screen.
     * If user entered wrong password it just displays a wrong password message to user.
     * @param enteredPassword Password entered by user
     * @param realPassword The real correct password
     * @param selectedDistrict District selected by user
     * @param selectedPoliceStation The police station selected by user
     * @param selectedPolicePost The police post selected by user
     * @param iOName IO Name entered by user
     */
    private void loginIfTrueCredentials(String enteredPassword, String realPassword, String selectedDistrict, String selectedPoliceStation, String selectedPolicePost, String iOName) {
        if (realPassword.contentEquals(enteredPassword)) {
            sessionManager.createLoginSession(selectedDistrict, selectedPoliceStation, selectedPolicePost, iOName);
            goToHomeScreen();
        }
        else {
            Toast.makeText(getBaseContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
        }
    }


    /*Store password in local database so that user do not have to be connected to internet to login next time through this PS*/
    public void storePasswordInSharedPref(String realPassword, String selectedDistrict, String selectedPoliceStation) {
        SharedPreferences sharedPrefPassword = getSharedPreferences("passwords", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefPassword.edit();
        editor.putString(selectedDistrict + selectedPoliceStation, realPassword); //Now next time user won't need internet to login for this police station!
        editor.commit();

    }

    /**
     * Check if device connected to Internet
     * @return true if connected else false
     */
    public boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&      //isConnected tells if Connected
                activeNetwork.isConnectedOrConnecting();
    }
}
