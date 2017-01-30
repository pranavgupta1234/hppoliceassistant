package pranav.apps.amazing.hppoliceassistant;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.data;
import static pranav.apps.amazing.hppoliceassistant.R.id.recyclerview;
import static pranav.apps.amazing.hppoliceassistant.R.layout.search;

/**
 * This activity allows user to fetch Challan data from online database filtered according to
 * search queries.
 * Search queries can be made on one of following fields:
 * o Vehicle Number
 * o Phone number of violator
 * o Name of violator
 * o License number of violator
 * User selects one of the above 4 from the app bar and then the search is carried out on that
 * field only.
 * Then user enters the search query in the app bar and hit search button on the soft-keyboard
 * A list of Challans is fetched from server based on that query and is displayed in the same
 * activity in a recycler view
 */
public class Search extends AppCompatActivity {

    private String TAG = "Search.java";

    /*Used to listen to logout broadcast so that this activity finishes when user logs out*/
    private BroadcastReceiver logoutBroadcastReceiver;

    /*Search on the Challan Database is based on one of the following types*/
    private final String VEHICLE = "vehicle number";
    private final String PHONE = "phone number";
    private final String NAME = "violator name";
    private final String LICENSE = "license number";

    /*By default search is based on VEHICLE*/
    private String searchBasedOn = VEHICLE;

    /*Used to point to reference to tThe search view used in the app bar to input query*/
    SearchView searchView;

    private RVAdapter searchResultsRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        /*Set up app bar*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*This method handles the intents this activity receives
        * It is particularly used to handle the input query entered by user*/
        handleIntent(getIntent());

        /*Following helps to finish this activity when user logs out (so that they can't navigate back here)*/
        setLogoutBroadcastReceiver();
    }

    /**
     * This method creates the menu when this activity is created
     * It sets up the search view.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        setUpSearchView(menu);
        return true;
    }

    /**
     * This method handles what happens when user clicks on various menu items in the app bar
     * @param item menu item which the user clicked
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            /*When user chose to (clicked) search based on vehicle number then,
            * set query hint of search view to "Enter Vehicle Number"
            * It also stores what user chose in searchBasedOn field
            * Similar to this case below 3 cases are handles similarly*/
            case R.id.search_based_on_vehicle:
                searchView.setQueryHint(getResources().getString(R.string.search_hint_vehicle));
                searchBasedOn = VEHICLE;
                break;
            case R.id.search_based_on_phone:
                searchView.setQueryHint(getResources().getString(R.string.search_hint_phone));
                searchBasedOn = PHONE;
                break;
            case R.id.search_based_on_name:
                searchView.setQueryHint(getResources().getString(R.string.search_hint_name));
                searchBasedOn = NAME;
                break;
            case R.id.search_based_on_license:
                searchView.setQueryHint(getResources().getString(R.string.search_hint_license));
                searchBasedOn = LICENSE;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /*This method is used as this activity's launchmode is "singletop"
    * Refer: https://developer.android.com/training/search/setup.html*/
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    /**
     * This method receives intent that this activity receives
     * It is mainly used to handle ACTION_SEARCH intent which is received by this activity from
     * itself when user submits the search query. This intent contains the search query which
     * can then be used to fetch data from server.
     * @param intent Intent recieved by this activity
     */
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!validQuery(query, searchBasedOn)) {
                return;
            }

            /*Format the string according to developers' letter casing convention*/
            query = modifyLetterCasing(query, searchBasedOn);


            /*Create a list which will store data fetched from server*/
            List<ChallanDetails> challanList = new ArrayList<>();

            /*Fetch the data from the server according to query into the challanList*/
            fetchDataFromSever(challanList, query, searchBasedOn);

            /*display the data*/
            setUpRecyclerView(challanList);

        }
    }

    /**
     * This method returns whether the string entered by user is a valid string
     * according to dataType e.g. if dataType is vehicle number then string should not
     * contain any special character and can be alphanumeric
     *
     * @param data     The data whose type is to be checked
     * @param dataType data type of the data e.g. VEHICLE, LICENSE, PHONE etc.
     * @return true if data is according to data type else false
     */
    private boolean validQuery(String data, String dataType) {
        switch (dataType) {
            case VEHICLE: if(!DataTypeValidator.validateVehicleNumberFormat(data)) {
                Toast.makeText(this, "Vehicle number can contain only letters and numbers", Toast.LENGTH_SHORT).show();
                return false;
            }
                break;
            case PHONE: if(!DataTypeValidator.validatePhoneNumberFormat(data)) {
                Toast.makeText(this, "Please enter valid 10 digit phone number", Toast.LENGTH_SHORT).show();
                return false;
            }
                break;
            case NAME: if(!DataTypeValidator.validateNameOfPersonFormat(data)) {
                Toast.makeText(this, "Please enter valid name of person", Toast.LENGTH_SHORT).show();
                return false;
            }
                break;
            case LICENSE: if(!DataTypeValidator.validateLicenseNumberFormat(data)) {
                Toast.makeText(this, "License number can contain only letters and numbers", Toast.LENGTH_SHORT).show();
                return false;
            }
                break;
        }
        return true;
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

    /* Sets up the search view.
     * Specifically, it associates searchable configuration with the SearchView*/
    private void setUpSearchView(Menu menu) {
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
    }

    /*Format the string according to developers' letter casing convention
    * See: https://docs.google.com/document/d/1jtdNmAWRHgOnX6sBj08DK8MhlnbqV_1o7HL_dji5NIo/edit*/
    private String modifyLetterCasing(String data, String dataType) {
        if(dataType == VEHICLE || dataType == NAME || dataType == LICENSE) {
            return data.toLowerCase();
        }
        else return data;
    }

    private void fetchDataFromSever(final List<ChallanDetails> challanList, String query, String searchBasedOn) {
        DatabaseReference challanDatabaseRef = FirebaseDatabase.getInstance().getReference("challan/");
       Query queryChallanDatabase;
        switch (searchBasedOn) {
            case PHONE:
                queryChallanDatabase = challanDatabaseRef.orderByChild("violator_number").equalTo(query);
                break;
            case NAME:
                queryChallanDatabase = challanDatabaseRef.orderByChild("violator_name").equalTo(query);
                break;
            case LICENSE:
                queryChallanDatabase = challanDatabaseRef.orderByChild("license_number").equalTo(query);
                break;
            default:
                queryChallanDatabase = challanDatabaseRef.orderByChild("vehicle_number").equalTo(query);
                break;
        }
        queryChallanDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChallanDetails challan = dataSnapshot.getValue(ChallanDetails.class);
                challanList.add(challan);
                searchResultsRecyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
            }
            // TODO: implement the ChildEventListener methods as documented above
            // ...
        });

    }

    /**
     * Refer: https://developer.android.com/training/material/lists-cards.html
     * @param challanList
     */
    private void setUpRecyclerView(List<ChallanDetails> challanList) {
        RecyclerView searchResultsRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        searchResultsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(Search.this);
        searchResultsRecyclerView.setLayoutManager(layoutManager);

        /*Display border between two challan items*/
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(searchResultsRecyclerView.getContext(),
                layoutManager.getOrientation());
        searchResultsRecyclerView.addItemDecoration(dividerItemDecoration);

        // specify an adapter
        searchResultsRecyclerViewAdapter = new RVAdapter(Search.this, challanList);
        searchResultsRecyclerView.setAdapter(searchResultsRecyclerViewAdapter);
    }
}