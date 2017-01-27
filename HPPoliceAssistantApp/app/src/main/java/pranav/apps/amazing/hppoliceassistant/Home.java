package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        sessionManager = new SessionManager(Home.this);
        name = sessionManager.getUserName();
        Toast.makeText(Home.this,"Welcome "+ name, Toast.LENGTH_SHORT).show();
    }

    public void openNakaEntryActivity(View v) {
        Intent i = new Intent(Home.this, MainActivity.class);
        i.putExtra("Tag", "0");
        i.putExtra("name",name);
        startActivity(i);
    }

    public void openChallanActivity(View v) {
        Intent i = new Intent(Home.this, MainActivity.class);
        i.putExtra("Tag","1");
        i.putExtra("name",name);
        startActivity(i);
    }

    public void openStolenVehicleActivity(View v) {
        Intent i = new Intent(Home.this,MainActivity.class);
        i.putExtra("Tag","2");
        i.putExtra("name",name);
        startActivity(i);
    }

    public void openSearchActivity(View v) {
        Intent i = new Intent(Home.this,MainActivity.class);
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
