package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
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
public class Stolen extends Activity implements SearchView.OnQueryTextListener{
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stolen);

        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //mRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");        //this can be used but its deprecated now
        // DatabaseReference databaseReference = FirebaseDatabase.getInstance();//.getReferenceFromUrl("https://hppoliceassistant.firebaseio.com/vehicle");


        FirebaseDatabase database =FirebaseDatabase.getInstance();              //it return root url
        DatabaseReference myRef = database.getReference("vehicle_entry");              //migrate from tree in other branches
        mDatabase =FirebaseDatabase.getInstance().getReference("vehicle_entry");

        sessionManager = new SessionManager(Stolen.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Stolen Items");
        toolbar.inflateMenu(R.menu.menu_search);
        toolbar.inflateMenu(R.menu.popup_menu);
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


        myRef.orderByKey().addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                newEntry  = dataSnapshot.getValue(VehicleEntry.class);
                vehicleEntries.add(0,newEntry);
                adapter.notifyDataSetChanged();
                loading.setText("");
            }
            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                newEntry  = dataSnapshot.getValue(VehicleEntry.class);
                vehicleEntries.add(0,newEntry);
                adapter.notifyDataSetChanged();
                loading.setText("");
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                newEntry  = dataSnapshot.getValue(VehicleEntry.class);
                vehicleEntries.add(0,newEntry);
                adapter.notifyDataSetChanged();
                loading.setText("");
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

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
            case R.id.logout:
                sessionManager.logoutUser();
                Intent intent1 = new Intent(Stolen.this,Login.class);
                intent1.putExtra("finish", true); // if you are checking for this in your other Activities
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
}
