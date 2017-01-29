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

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static pranav.apps.amazing.hppoliceassistant.R.id.recyclerview;
import static pranav.apps.amazing.hppoliceassistant.R.layout.search;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Search extends AppCompatActivity {

    private String TAG = "Search.java";

    BroadcastReceiver logoutBroadcastReceiver;

    private final String VEHICLE = "vehicle number";
    private final String PHONE = "phone number";
    private final String NAME = "violator name";
    private final String LICENSE = "license number";

    private String search_based_on = VEHICLE; //Search is based on vehicle number by default

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handleIntent(getIntent());

        /*Following helps to finish this activity when user logs out (so that they can't navigate back here)*/
        setLogoutBroadcastReceiver();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search_based_on_vehicle:
                searchView.setQueryHint("Enter Vehicle Number");
                search_based_on = VEHICLE;
                break;
            case R.id.search_based_on_phone:
                searchView.setQueryHint("Enter Phone Number");
                search_based_on = PHONE;
                break;
            case R.id.search_based_on_name:
                searchView.setQueryHint("Enter Violator Name");
                search_based_on = NAME;
                break;
            case R.id.search_based_on_license:
                searchView.setQueryHint("Enter License Number");
                search_based_on = LICENSE;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if(!validQuery(query, search_based_on)) {
                Toast.makeText(this, "Please enter valid " + search_based_on, Toast.LENGTH_SHORT).show();
                return;
            }

            /*Fetch the data from the server according to query and display the data*/

        }
    }

    /**
     * This method returns whether the string entered by user is a valid string
     * according to dataType e.g. if dataType is vehicle number then string should not
     * contain any special character and can be alphanumeric
     * @param data  The data whose type is to be checked
     * @param dataType data type of the data e.g. VEHICLE, LICENSE, PHONE etc.
     * @return true if data is according to data type else false
     */
    private boolean validQuery(String data, String dataType) {
        /*@TODO Implement this funciton*/
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
}


/*
Old Search activity code
public class Search extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerview;
    private List<ChallanDetails> challanDetails = new ArrayList<>();
    private List<ChallanDetails> offlineList = new ArrayList<>();
    private RVAdapter adapter;
    private  ChallanDetails challan;
    private TextView search;
    private SessionManager sessionManager;
    private RVAdapter adapterOffline;
    DBManagerChallanOnline dbManagerChallanOnline;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        FirebaseDatabase database =FirebaseDatabase.getInstance();              //it return root url
        DatabaseReference myRef = database.getReference("challan");              //migrate from tree in other branches

        sessionManager = new SessionManager(Search.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        search=(TextView)findViewById(R.id.loading);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Search.this);
        recyclerview.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerview.getContext(),
                layoutManager.getOrientation());
        recyclerview.addItemDecoration(dividerItemDecoration);


        challanDetails = new ArrayList<>();
        adapter = new RVAdapter(Search.this,challanDetails);
        adapterOffline = new RVAdapter(Search.this,offlineList);
        recyclerview.setAdapter(adapterOffline);
        adapter.notifyDataSetChanged();
        //Toast.makeText(Search.this,String.valueOf(offlineList.size()),Toast.LENGTH_SHORT).show();
        dbManagerChallanOnline = new DBManagerChallanOnline(Search.this,null,null,1);
        offlineList = dbManagerChallanOnline.showChallan();
        myRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                challan =  dataSnapshot.getValue(ChallanDetails.class);
                challanDetails.add(challan);
                if(!dbManagerChallanOnline.checkIfPresent(challan)){
                    dbManagerChallanOnline.addChallan(challan);
                    offlineList.add(challan);
                    adapterOffline.notifyDataSetChanged();
                }
                search.setText("");
            }
            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                challan =  dataSnapshot.getValue(ChallanDetails.class);
                challanDetails.add(challan);
                adapter.notifyDataSetChanged();
                search.setText("");
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                challan =  dataSnapshot.getValue(ChallanDetails.class);
                challanDetails.add(challan);
                adapter.notifyDataSetChanged();
                search.setText("");
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                challan =  dataSnapshot.getValue(ChallanDetails.class);
                challanDetails.add(challan);
                adapter.notifyDataSetChanged();
                search.setText("");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Search.this,"Something Went Wrong",Toast.LENGTH_SHORT).show();
            }
        });
        //challanDetails.clear();
    }

    *//*private void invalidate() {
        challanDetails=dbManagerChallanOnline.showChallan();
        adapter.notifyDataSetChanged();
    }*//*

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
                Intent i = new Intent(Search.this,OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(Search.this,OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                sessionManager.logoutUser();
                Intent intent1 = new Intent(Search.this,Login.class);
                intent1.putExtra("finish", true); // if you are checking for this in your other Activities
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        getMenuInflater().inflate(R.menu.popup_menu,menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(Search.this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapterOffline.setFilter(offlineList);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<ChallanDetails> filteredChallan = filter(offlineList, newText);
        adapterOffline.setFilter(filteredChallan);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        final List<ChallanDetails> filteredChallan = filter(offlineList, query);
        adapterOffline.setFilter(filteredChallan);
        return true;
    }


    private List<ChallanDetails> filter(List<ChallanDetails> models, String query) {
        if(query.contentEquals("")){
            final List<ChallanDetails> filteredChallan = new ArrayList<>();
            filteredChallan.addAll(models);
            return filteredChallan;
        }
        query = query.toLowerCase();

        final List<ChallanDetails> filteredModelList = new ArrayList<>();
        for (ChallanDetails model : models) {
            final String text1 = model.getVehicle_number().toLowerCase();
            final String text2 = model.getViolator_name().toLowerCase();
            final String text3 = model.getLicense_number().toLowerCase();
            final String text4 = model.getViolator_number().toLowerCase();
            final String text5 = model.getPolice_officer_name().toLowerCase();
            if (text1.contains(query)|| text2.contains(query)|| text3.contains(query)|| text4.contains(query)||text5.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
*/

