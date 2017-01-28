package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Pranav Gupta on 12/18/2016.
 */


public class Home extends AppCompatActivity{
    boolean doubleBackToExitPressedOnce = false;
    String iOName;
    private SessionManager sessionManager;

    BroadcastReceiver logoutBroadcastReceiver;

    /*Used for logging purposes*/
    private String TAG = "Home.java";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.inflateMenu(R.menu.menu_menu);


        sessionManager = new SessionManager(Home.this);

        /*Following helps to finish this activity when user logs out (so that they can't navigate back here)*/
        setLogoutBroadcastReceiver();

        iOName = sessionManager.getIOName();
        Toast.makeText(Home.this,"Welcome "+ iOName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);//Menu Resource, Menu
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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
            case R.id.action_logout:
                sessionManager.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openNakaEntryActivity(View v) {
        Intent i = new Intent(Home.this, Entry_veh.class);
        i.putExtra("Tag", "0");
        i.putExtra("iOName",iOName);
        startActivity(i);
    }

    public void openChallanActivity(View v) {
        Intent i = new Intent(Home.this, Challan.class);
        i.putExtra("Tag","1");
        i.putExtra("iOName",iOName);
        startActivity(i);
    }

    public void openStolenVehicleActivity(View v) {
        Intent i = new Intent(Home.this,Stolen.class);
        i.putExtra("Tag","2");
        i.putExtra("iOName",iOName);
        startActivity(i);
    }

    public void openSearchActivity(View v) {
        Intent i = new Intent(Home.this,Search.class);
        i.putExtra("Tag","3");
        i.putExtra("iOName",iOName);
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

        this.doubleBackToExitPressedOnce = true;

        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();
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
}
