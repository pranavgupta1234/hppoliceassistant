package pranav.apps.amazing.hppoliceassistant;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pranav Gupta on 2/3/2017.
 */

public class MyLocationListener implements LocationListener {

    private Context context;
    Map<String,Double> coordinates = new HashMap<>();


    public MyLocationListener(Context context,Map<String,Double> coordinates){
        this.context = context;
        this.coordinates= coordinates;
    }

    @Override
    public void onLocationChanged(Location location) {
        coordinates.put("Latitude",location.getLatitude());
        coordinates.put("Longitude",location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(context,"GPS Enabled", Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(context,"GPS Disabled", Toast.LENGTH_SHORT ).show();
    }
}
