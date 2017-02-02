package pranav.apps.amazing.hppoliceassistant;


import android.app.ProgressDialog;
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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Search extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private RecyclerView recyclerview;
    private List<ChallanDetails> challanDetails = new ArrayList<>();
    private List<ChallanDetails> offlineList = new ArrayList<>();
    private RVAdapter adapter;
    private  ChallanDetails challan;
    private TextView search;
    private SessionManager sessionManager;
    private RVAdapter adapterOffline;
    private DBManagerChallanOnline dbManagerChallanOnline;
    private BroadcastReceiver logoutBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        FirebaseDatabase database =FirebaseDatabase.getInstance();              //it return root url
        DatabaseReference myRef = database.getReference("challan");              //migrate from tree in other branches

        sessionManager = new SessionManager(Search.this);
        setLogoutBroadcastReceiver();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Search Online Challan");
        setSupportActionBar(toolbar);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        search=(TextView)findViewById(R.id.loading);
        LinearLayoutManager layoutManager = new LinearLayoutManager(Search.this);
        recyclerview.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerview.getContext(),
                layoutManager.getOrientation());
        recyclerview.addItemDecoration(dividerItemDecoration);
        dbManagerChallanOnline = new DBManagerChallanOnline(Search.this,null,null,1);
        offlineList = dbManagerChallanOnline.showChallan();

        challanDetails = new ArrayList<>();
        adapter = new RVAdapter(Search.this,challanDetails);
        adapterOffline = new RVAdapter(Search.this,offlineList);
        recyclerview.setAdapter(adapterOffline);
        adapterOffline.notifyDataSetChanged();
        search.setText("");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                challan =  dataSnapshot.getValue(ChallanDetails.class);
                challanDetails.add(challan);
                if(!dbManagerChallanOnline.checkIfPresent(challan)){
                    dbManagerChallanOnline.addChallan(challan);
                    offlineList.add(0,challan);
                    adapterOffline.notifyDataSetChanged();
                    Toast.makeText(Search.this,"New Challans Added , List is Updated",Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                challan =  dataSnapshot.getValue(ChallanDetails.class);
                offlineList.add(0,challan);
                adapter.notifyDataSetChanged();
                search.setText("");
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                challan =  dataSnapshot.getValue(ChallanDetails.class);
                offlineList.add(0,challan);
                adapter.notifyDataSetChanged();
                search.setText("");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                challan =  dataSnapshot.getValue(ChallanDetails.class);
                offlineList.add(0,challan);
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
            case R.id.developers_activity:
                startActivity(new Intent(Search.this,DevelopersActivity.class));
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
        ProgressDialog pg = ProgressDialog.show(Search.this,"Searching Challan","Searching...");
        final List<ChallanDetails> filteredChallan = filter(offlineList, query);
        adapterOffline.setFilter(filteredChallan);
        adapterOffline.notifyDataSetChanged();
        pg.dismiss();
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
            if(model.getVehicle_number()!= null && model.getViolator_name()!= null&&model.getLicense_number()!=null
                    &&model.getViolator_number()!=null&&model.getPolice_officer_name()!=null) {
                final String text1 = model.getVehicle_number().toLowerCase();
                final String text2 = model.getViolator_name().toLowerCase();
                final String text3 = model.getLicense_number().toLowerCase();
                final String text4 = model.getViolator_number().toLowerCase();
                final String text5 = model.getPolice_officer_name().toLowerCase();
                final String text6 = model.getChallanID().toLowerCase();
                if (text1.contains(query) || text2.contains(query) || text3.contains(query) || text4.contains(query) || text5.contains(query)
                        ||text6.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
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


