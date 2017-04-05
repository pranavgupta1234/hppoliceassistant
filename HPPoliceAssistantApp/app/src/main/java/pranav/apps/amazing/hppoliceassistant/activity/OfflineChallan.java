package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranav Gupta on 1/2/2017.
 */

public class OfflineChallan extends AppCompatActivity {
    private RecyclerView recyclerview;
    private List<ChallanDetails> challanDetails;
    private RecyclerAdapterChallanOffline adapter;
    private  ChallanDetails challan;
    private TextView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.offline_challan);
        recyclerview = (RecyclerView)findViewById(R.id.recyclerview);
        search=(TextView)findViewById(R.id.loading);

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
        //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        //actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.inflateMenu(R.menu.popup_menu);

        LinearLayoutManager layoutManager = new LinearLayoutManager(OfflineChallan.this);
        recyclerview.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerview.getContext(),
                layoutManager.getOrientation());
        recyclerview.addItemDecoration(dividerItemDecoration);
        challanDetails = new ArrayList<>();

        DBManagerChallan dbManagerChallan = new DBManagerChallan(OfflineChallan.this,null,null,1);
        challanDetails = dbManagerChallan.showChallan();
        adapter = new RecyclerAdapterChallanOffline(OfflineChallan.this,challanDetails);
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
                final List<ChallanDetails> filteredChallan = filter(challanDetails, searchQuery );
                adapter.setFilter(filteredChallan);
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                adapter.setFilter(challanDetails);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
