package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * Created by Pranav Gupta on 12/18/2016.
 */


public class Home extends Activity{
    private ImageButton entry,challan,stolen_list,search_vehicle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        entry =(ImageButton)findViewById(R.id.entry_iv);
        challan =(ImageButton)findViewById(R.id.challan_iv);
        stolen_list =(ImageButton)findViewById(R.id.stolen_vehicle_iv);
        search_vehicle =(ImageButton)findViewById(R.id.a_iv);
        entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,MainActivity.class);
                i.putExtra("Tag", "0");
                startActivity(i);
            }
        });
        challan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,MainActivity.class);
                i.putExtra("Tag","1");
                startActivity(i);
            }
        });
        stolen_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,MainActivity.class);
                i.putExtra("Tag","2");
                startActivity(i);
            }
        });
        search_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Home.this,MainActivity.class);
                i.putExtra("Tag","3");
                startActivity(i);
            }
        });
    }
}
