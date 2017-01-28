package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.Intent;
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
    String name;
    private SessionManager sessionManager;
    /*Used for logging purposes*/
    private String TAG = "Home.java";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.inflateMenu(R.menu.popup_menu);

        sessionManager = new SessionManager(Home.this);


        name = sessionManager.getUserName();
        Toast.makeText(Home.this,"Welcome "+ name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.popup_menu,menu);
        return true;
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
                Intent i = new Intent(Home.this,OfflineChallan.class);
                startActivity(i);
                return true;
            case R.id.offline_entry:
                Intent intent = new Intent(Home.this,OfflineEntry.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                sessionManager.logoutUser();
                Intent intent1 = new Intent(Home.this,Login.class);
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

    public void openNakaEntryActivity(View v) {
        Intent i = new Intent(Home.this, Entry_veh.class);
        i.putExtra("Tag", "0");
        i.putExtra("name",name);
        startActivity(i);
    }

    public void openChallanActivity(View v) {
        Intent i = new Intent(Home.this, Challan.class);
        i.putExtra("Tag","1");
        i.putExtra("name",name);
        startActivity(i);
    }

    public void openStolenVehicleActivity(View v) {
        Intent i = new Intent(Home.this,Stolen.class);
        i.putExtra("Tag","2");
        i.putExtra("name",name);
        startActivity(i);
    }

    public void openSearchActivity(View v) {
        Intent i = new Intent(Home.this,Search.class);
        i.putExtra("Tag","3");
        i.putExtra("name",name);
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
}
