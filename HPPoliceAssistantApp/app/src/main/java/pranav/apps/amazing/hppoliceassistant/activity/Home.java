package pranav.apps.amazing.hppoliceassistant;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Pranav Gupta on 12/18/2016.
 */
public class Home extends AppCompatActivity{
    boolean doubleBackToExitPressedOnce = false;

    private SessionManager sessionManager;

    BroadcastReceiver logoutBroadcastReceiver;

    /*Used for logging purposes*/
    private String TAG = "Home.java";

    private DBManagerChallan dbManagerChallan;
    private ArrayList<ChallanDetails> challanDetails;

    private FirebaseDatabase database;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.parseColor("#039be5"));
        myToolbar.inflateMenu(R.menu.menu_menu);

        sessionManager = new SessionManager(Home.this);

        //setting up firebase instances
        database = FirebaseDatabase.getInstance();
        rootRef = database.getReference("challan");


        /*Following helps to finish this activity when user logs out (so that they can't navigate back here)*/
        setLogoutBroadcastReceiver();

        sendAllChallans();



    }

    private void sendAllChallans() {
        if(isConnectedToInternet()){
            //setting up database manager to automatically send data to server each time user comes to home
            dbManagerChallan = new DBManagerChallan(Home.this,null,null,1);

            challanDetails = new ArrayList<>();
            challanDetails = dbManagerChallan.showChallan();
            for (int i=0;i<challanDetails.size();i++){
                if (challanDetails.get(i).getStatus()==0){
                    DatabaseReference child  = rootRef.push();
                    final int no = i;
                    challanDetails.get(i).setStatus(1);
                    child.setValue(challanDetails.get(i), new DatabaseReference.CompletionListener() {
                        final ProgressDialog pg = ProgressDialog.show(Home.this,"Automatic Data Update","Sending offline entries to server...");
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError== null){
                                pg.dismiss();
                                dbManagerChallan.setStatus(challanDetails.get(no),1);
                            }
                            else {
                                pg.dismiss();
                                Toast.makeText(Home.this,"Network Error! Data Not Saved,Sorry for inconvenience",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
            Toast.makeText(Home.this,"Data is in Sync with Server",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(Home.this,"You are not connected to Internet Unable to Sync Data ",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);//Menu Resource, Menu
        return true;
    }


    /**
     * Following function handles action to be carried out if some menu item is clicked from the app bar
     * @param item The menu item that is clicked by user
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.offline_challan:
                Intent i = new Intent(Home.this,OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(Home.this,OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.developers_activity:
                startActivity(new Intent(Home.this,DevelopersActivity.class));
                return true;
            case R.id.action_logout:

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

    public void openNakaEntryActivity(View v) {
        Intent i = new Intent(Home.this, Entry.class);
        startActivity(i);
    }

    public void openChallanActivity(View v) {
        Intent i = new Intent(Home.this, Challan.class);
        startActivity(i);
    }

    public void openStolenVehicleActivity(View v) {
        Intent i = new Intent(Home.this,Stolen.class);
        startActivity(i);
    }

    public void openSearchActivity(View v) {
        Intent i = new Intent(Home.this,Search.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        if(!doubleBackToExitPressedOnce) {
            Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
        }
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
    public boolean isConnectedToInternet() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&      //isConnected tells if Connected
                activeNetwork.isConnectedOrConnecting();
    }
}
