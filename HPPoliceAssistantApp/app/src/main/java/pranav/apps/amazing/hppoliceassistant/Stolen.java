package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Stolen extends AppCompatActivity implements SearchView.OnQueryTextListener{
    Firebase mRef;
    Firebase childRef;
    private SessionManager sessionManager;

    private DatabaseReference mDatabase;               //its like an address pointer to your database location
    private ListView lv;
    //private ArrayList<String> veh_entry = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    //private ArrayList<Friend> veh_entry = new ArrayList<>();
    private TextView loading;

    private List<VehicleEntry> vehicleEntries = new ArrayList<>();

    private VehicleEntry newEntry;
    private BroadcastReceiver logoutBroadcastReceiver;
    private ChildEventListener childEventListener;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stolen);

        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //mRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");        //this can be used but its deprecated now
        // DatabaseReference databaseReference = FirebaseDatabase.getInstance();//.getReferenceFromUrl("https://hppoliceassistant.firebaseio.com/vehicle");


        FirebaseDatabase database =FirebaseDatabase.getInstance();              //it return root url
        myRef = database.getReference("vehicle_entry");              //migrate from tree in other branches
        mDatabase =FirebaseDatabase.getInstance().getReference("vehicle_entry");

        sessionManager = new SessionManager(Stolen.this);
        setLogoutBroadcastReceiver();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.recyle_view);
        recyclerView.setHasFixedSize(true);

        loading=(TextView)findViewById(R.id.loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(Stolen.this);
        recyclerView.setLayoutManager(layoutManager);


        vehicleEntries = new ArrayList<>();
        adapter = new RecyclerAdapter(Stolen.this,vehicleEntries);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        /*adapter = new RecyclerAdapter(Stolen.this,vehicleEntries);
        recyclerView.setAdapter(adapter);*/


        myRef.orderByKey().addChildEventListener(childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                newEntry  = dataSnapshot.getValue(VehicleEntry.class);
                vehicleEntries.add(0,newEntry);
                adapter.notifyDataSetChanged();
                loading.setText("");
            }
            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                reloadActivity();
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                reloadActivity();
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                reloadActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        vehicleEntries.clear();
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
                Intent i = new Intent(Stolen.this,OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(Stolen.this,OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.developers_activity:
                startActivity(new Intent(Stolen.this,DevelopersActivity.class));
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        getMenuInflater().inflate(R.menu.popup_menu, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(Stolen.this);

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                        adapter.setFilter(vehicleEntries);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        final List<VehicleEntry> filteredEntry = filter(vehicleEntries, query);
        adapter.setFilter(filteredEntry);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        final List<VehicleEntry> filteredEntry = filter(vehicleEntries, query);
        adapter.setFilter(filteredEntry);
        return true;
    }
    private List<VehicleEntry> filter(List<VehicleEntry> models, String query) {
        if(query.contentEquals("")){
            final List<VehicleEntry> filteredEntry = new ArrayList<>();
            filteredEntry.addAll(models);
            return filteredEntry;
        }
        query = query.toLowerCase();

        final List<VehicleEntry> filteredModelList = new ArrayList<>();
        for (VehicleEntry model : models) {
            final String text1 = model.getVehicle_number().toLowerCase();
            final String text2 = model.getPhone_number().toLowerCase();
            final String text3 = model.getDescription().toLowerCase();
            final String text4 = model.getName_of_place().toLowerCase();
            final String text5 = model.getDate().toLowerCase();
            if (text1.contains(query)|| text2.contains(query)|| text3.contains(query)|| text4.contains(query)||text5.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }


    private void reloadActivity() {

        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);

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
        super.onDestroy();
        myRef.removeEventListener(childEventListener);
        if(logoutBroadcastReceiver!=null) {
            unregisterReceiver(logoutBroadcastReceiver);
        }
    }

    // this is added
}
