package pranav.apps.amazing.hppoliceassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Pranav Gupta on 1/5/2017.
 */

public class DBManagerChallanOnline  extends SQLiteOpenHelper{

    private Context context;


    public DBManagerChallanOnline(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "hp_police_online_challan.db", factory, 6);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS challanOnline (id INTEGER PRIMARY KEY AUTOINCREMENT, offences TEXT,violator_name TEXT," +
                "vehicle_owner_name TEXT,violator_address TEXT,violator_number TEXT,license_number TEXT,challan_amount TEXT," +
                "offences_section TEXT,vehicle_number TEXT,date TEXT,time TEXT,name_of_place TEXT,officer_name TEXT,other_remarks TEXT," +
                "place TEXT,image TEXT,status INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS challanOnline");
        onCreate(db);
    }
    public boolean checkIfPresent(ChallanDetails details){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM challanOnline WHERE time = \""+details.getTime()+"\";", null);
        /*Cursor cursor = db.rawQuery("SELECT * FROM challanOnline WHERE offences = \""+details.getOffences()+"\" AND violator_name = \""+details.getViolator_name()+
                "\" AND vehicle_number=\""+details.getVehicle_number()+"\";",null);*/
        if(cursor.getCount()==0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean addChallan(ChallanDetails details){
        //DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM challanOnline;",null);

        Cursor c =  db.rawQuery("SELECT * FROM challanOnline WHERE time = \""+details.getTime()+"\";", null);
        c.moveToFirst();
        int count = c.getCount();
        if (count>0) {
            Toast.makeText(context,"Already Present",Toast.LENGTH_SHORT).show();
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("offences",details.getOffences());
        contentValues.put("violator_name",details.getViolator_name());
        contentValues.put("vehicle_owner_name",details.getOwner_name());
        contentValues.put("violator_address",details.getViolator_address());
        contentValues.put("violator_number",details.getViolator_number());
        contentValues.put("license_number",details.getLicense_number());
        contentValues.put("challan_amount",details.getChallan_amount());
        contentValues.put("offences_section",details.getOffences_section());
        contentValues.put("vehicle_number",details.getVehicle_number());
        contentValues.put("date",details.getDate());
        contentValues.put("time",details.getTime());
        contentValues.put("name_of_place",details.getName_of_place());
        contentValues.put("officer_name",details.getPolice_officer_name());
        contentValues.put("other_remarks",details.getOther_remarks());
        contentValues.put("place",details.getDistrict());
        contentValues.put("image","image");
        contentValues.put("status", 0);
        db.insert("challanOnline", null, contentValues);
        // c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        //c.moveToFirst();
        //int id = c.getInt(c.getColumnIndex("id"));

        //db.execSQL("CREATE TABLE IF NOT EXISTS todo_lists_"+id+" (id INTEGER PRIMARY KEY AUTOINCREMENT, item TEXT, checkbox TEXT);");
        c.close();
        cursor.close();
        return true;
    }

    public ArrayList<ChallanDetails> showChallan(){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT * FROM challanOnline;", null);
        cursor.moveToFirst();

        ArrayList<ChallanDetails> information = new ArrayList<>();
        //String[] strings = new String[cursor.getCount()];
        if(cursor.getCount()!=0) {
            while (!cursor.isAfterLast()) {
                information.add(new ChallanDetails(cursor.getString(cursor.getColumnIndex("violator_name")), cursor.getString(cursor.getColumnIndex("offences")),
                        cursor.getString(cursor.getColumnIndex("vehicle_owner_name")), cursor.getString(cursor.getColumnIndex("violator_address")),
                        cursor.getString(cursor.getColumnIndex("vehicle_number")), cursor.getString(cursor.getColumnIndex("name_of_place")),
                        cursor.getString(cursor.getColumnIndex("offences_section")), cursor.getString(cursor.getColumnIndex("challan_amount")),
                        cursor.getString(cursor.getColumnIndex("license_number")), cursor.getString(cursor.getColumnIndex("officer_name")),
                        "district","policeStation", cursor.getString(cursor.getColumnIndex("other_remarks")),
                        cursor.getString(cursor.getColumnIndex("image")), cursor.getString(cursor.getColumnIndex("violator_number")),
                        cursor.getString(cursor.getColumnIndex("date")), cursor.getString(cursor.getColumnIndex("time")),cursor.getInt(cursor.getColumnIndex("status"))));
                // information[i] = cursor.getString();
                cursor.moveToNext();
            }
        }
        cursor.close();
        return information;
    }

    public void deleteChallan(ChallanDetails details){
        //DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM challanOnline WHERE time = \""+details.getTime()+"\";", null);
        c.moveToFirst();
        //int id = c.getInt(c.getColumnIndex("id"));
        db.execSQL("DELETE FROM challanOnline WHERE time = \""+details.getTime()+"\";");
        //db.execSQL("DROP TABLE IF EXISTS  todo_lists_"+id+";");
    }

    public int getStatus(ChallanDetails details){
        int value;
        //DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM challanOnline WHERE time = \""+details.getTime()+"\";", null);
        c.moveToFirst();
        value = c.getInt(c.getColumnIndex("status"));
        c.close();
        return value;
    }

    public boolean setStatus(ChallanDetails details, int s){
        //DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM challanOnline WHERE time = \""+details.getTime()+"\";", null);
        c.moveToFirst();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("list", list);
        contentValues.put("status", s);
        db.update("challanOnline",contentValues,"time = \"" + details.getTime() + "\"",null);
        c.close();
        return true;
    }

}