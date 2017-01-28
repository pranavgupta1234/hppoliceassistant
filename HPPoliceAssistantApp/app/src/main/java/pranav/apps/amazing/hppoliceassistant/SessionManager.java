package pranav.apps.amazing.hppoliceassistant;

/**
 * Created by Pranav Gupta on 1/14/2017.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {

    /*Used for logging purposes*/
    private String TAG = "SessionManager.java";

    /*District used to login in this session*/
    public static String district;

    /*Police Station used to login in this session*/
    public static String policeStation;

    /*Police Post in this session*/
    public static String policePost;

    /*IO Name in the session*/
    public static String iOName;


    /*Shared Preferences*/
    private SharedPreferences pref;

    /*Editor for Shared preferences*/
    private Editor editor;

    /*Context*/
    private Context _context;

    // Sharedpref file name
    private static final String PREF_NAME = "HPPolice";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    private static final String DISTRICT = "district";

    private static final String POLICE_STATION = "policeStation";

    private static final String POLICE_POST = "policePost";

    private static final String IO_NAME = "iOName";


    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); /*Opens te shared pref in private mode so that the shared pref file is accessible by only this app*/
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String district, String policeStation, String policePost, String iOName){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        this.district = district;
        this.policeStation = policeStation;
        this.policePost = policePost;
        this.iOName = iOName;

        editor.putString(DISTRICT, district);
        editor.putString(POLICE_STATION, policeStation);
        editor.putString(POLICE_POST, policePost);
        editor.putString(IO_NAME, iOName);

        // commit changes
        editor.commit();
    }


    /**
     * The method just initialises this class' public static variables
     */
    public void createLoginSession(){
        this.district = pref.getString(DISTRICT, "");
        this.policeStation = pref.getString(POLICE_STATION, "");
        this.policePost = pref.getString(POLICE_POST, "");
        this.iOName = pref.getString(IO_NAME, "");
    }


    public String getDistrict() {
        return pref.getString(DISTRICT, "");
    }

    public String getPoliceStation() {
        return pref.getString(POLICE_STATION, "");
    }

    public String getPolicePost() {
        return pref.getString(POLICE_POST, "");
    }

    public String getIOName() {
        return pref.getString(IO_NAME, "");
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.putBoolean(IS_LOGIN, false);
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, Login.class);

        /**  broadcast a logout message to all your Activities needing to stay under a logged-in status
         * http://stackoverflow.com/questions/3007998/on-logout-clear-activity-history-stack-preventing-back-button-from-opening-l**/
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.pranav.apps.amazing.ACTION_LOGOUT");
        _context.sendBroadcast(broadcastIntent);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * This method checks if user is already logged in to system
     * @return true if user logged in else false
     */
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false); //Return default value as 'false' if it is not sure if user logged in.
    }
}
