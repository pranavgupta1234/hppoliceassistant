package pranav.apps.amazing.hppoliceassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranav Gupta on 1/2/2017.
 */

public class OfflineEntry extends AppCompatActivity{
    private List<VehicleEntry> vehicleEntries;
    private RecyclerAdapterEntryOffline adapter;
    private  ChallanDetails challan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.offline_entry);
        RecyclerView recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        TextView search = (TextView) findViewById(R.id.loading);

        //setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.inflateMenu(R.menu.popup_menu);
        LinearLayoutManager layoutManager = new LinearLayoutManager(OfflineEntry.this);
        recyclerview.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerview.getContext(),
                layoutManager.getOrientation());
        recyclerview.addItemDecoration(dividerItemDecoration);
        vehicleEntries = new ArrayList<>();
        DBManagerEntry dbManagerChallan = new DBManagerEntry(OfflineEntry.this,null,null,1);
        vehicleEntries = dbManagerChallan.showEntries();
        adapter = new RecyclerAdapterEntryOffline(OfflineEntry.this,vehicleEntries);
        recyclerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        search.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //*** setOnQueryTextFocusChangeListener ***
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchQuery) {
                final List<VehicleEntry> filteredEntry = filter(vehicleEntries, searchQuery );
                adapter.setFilter(filteredEntry);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                adapter.setFilter(vehicleEntries);
                adapter.notifyDataSetChanged();
                return true; // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });
        return true;
    }


    private List<VehicleEntry> filter(List<VehicleEntry> entries, String query) {
        if(query.contentEquals("")){
            final List<VehicleEntry> filteredEntry = new ArrayList<>();
            filteredEntry.addAll(entries);
            return filteredEntry;
        }
        query = query.toLowerCase();

        final List<VehicleEntry> filteredEntry = new ArrayList<>();
        for (VehicleEntry entry : entries) {
            final String text1 = entry.getVehicle_number().toLowerCase();
            final String text2 = entry.getDescription().toLowerCase();
            final String text3 = entry.getPhone_number().toLowerCase();
            final String text4 = entry.getDate().toLowerCase();
            final String text5 = entry.getName_of_place().toLowerCase();
            if (text1.contains(query)|| text2.contains(query)|| text3.contains(query)|| text4.contains(query)||text5.contains(query)) {
                filteredEntry.add(entry);
            }
        }
        return filteredEntry;
    }
    public void doNothing(){

    }
}
