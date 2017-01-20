package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Pranav Gupta on 12/18/2016.
 */


public class Home extends Activity{
    private ImageButton entry,challan,stolen_list,search_vehicle;
    boolean doubleBackToExitPressedOnce = false;
    private String name;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        entry =(ImageButton)findViewById(R.id.entry_iv);
        challan =(ImageButton)findViewById(R.id.challan_iv);
        stolen_list =(ImageButton)findViewById(R.id.stolen_vehicle_iv);
        search_vehicle =(ImageButton)findViewById(R.id.a_iv);
        sessionManager = new SessionManager(Home.this);
        name = sessionManager.getUserName();
        Toast.makeText(Home.this,"Welcome "+name,Toast.LENGTH_SHORT).show();
        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,MainActivity.class);
                i.putExtra("Tag", "0");
                i.putExtra("name",name);
                startActivity(i);
            }
        });
        challan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,MainActivity.class);
                i.putExtra("Tag","1");
                i.putExtra("name",name);
                startActivity(i);
            }
        });
        stolen_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,MainActivity.class);
                i.putExtra("Tag","2");
                i.putExtra("name",name);
                startActivity(i);
            }
        });
        search_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,MainActivity.class);
                i.putExtra("Tag","3");
                i.putExtra("name",name);
                startActivity(i);
            }
        });
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
