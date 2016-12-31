package pranav.apps.amazing.hppoliceassistant;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */

public class HPPoliceAssistant extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
