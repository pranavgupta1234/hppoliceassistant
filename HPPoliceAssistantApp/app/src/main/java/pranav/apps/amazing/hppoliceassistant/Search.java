package pranav.apps.amazing.hppoliceassistant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Search extends FragmentActivity implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerview;
    private List<ChallanDetails> challanDetails = new ArrayList<>();
    private List<ChallanDetails> offlineList = new ArrayList<>();
    private RVAdapter adapter;
    private  ChallanDetails challan;
    private TextView search;
    private RVAdapter adapterOffline;
    DBManagerChallanOnline dbManagerChallanOnline;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        FirebaseDatabase database =FirebaseDatabase.getInstance();              //it return root url
        DatabaseReference myRef = database.getReference("challan");              //migrate from tree in other branches

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

    /*private void invalidate() {
        challanDetails=dbManagerChallanOnline.showChallan();
        adapter.notifyDataSetChanged();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

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
        return true;
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


