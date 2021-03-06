
package pranav.apps.amazing.hppoliceassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class DBManagerEntry extends SQLiteOpenHelper {
    private Context context;

    public DBManagerEntry(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "hp_police_vehicle_entry.db", factory, 7);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS entry (id INTEGER PRIMARY KEY AUTOINCREMENT, EntryID TEXT,vehicle_number TEXT,phone_number TEXT," +
                "description TEXT,date TEXT,time TEXT,name_of_place TEXT,officer_name TEXT,image TEXT,status INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS entry");
        onCreate(db);
    }

    public boolean addEntry(VehicleEntry newEntry){
        //DatabaseUtils.sqlEscapeString(newEntry);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM entry WHERE EntryID = \""+newEntry.getEntryID()+"\";", null);
        c.moveToFirst();
        int count = c.getCount();
        if (count>0) {
            Toast.makeText(context,"Already Present",Toast.LENGTH_SHORT).show();
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("EntryID",newEntry.getEntryID());
        contentValues.put("vehicle_number",newEntry.getVehicle_number());
        contentValues.put("phone_number",newEntry.getPhone_number());
        contentValues.put("description",newEntry.getDescription());
        contentValues.put("date",newEntry.getDate());
        contentValues.put("time",newEntry.getTime());
        contentValues.put("name_of_place",newEntry.getName_of_place());
        contentValues.put("officer_name",newEntry.getOfficer_name());
        contentValues.put("image",newEntry.getImage());
        contentValues.put("status", newEntry.getStatus());
        db.insert("entry", null, contentValues);
        //c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        //c.moveToFirst();
        //int id = c.getInt(c.getColumnIndex("id"));

        //db.execSQL("CREATE TABLE IF NOT EXISTS todo_lists_"+id+" (id INTEGER PRIMARY KEY AUTOINCREMENT, item TEXT, checkbox TEXT);");
        c.close();
        return true;
    }

    public ArrayList<VehicleEntry> showEntries(){
        //int i = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "SELECT * FROM entry;", null);
        cursor.moveToFirst();
        ArrayList<VehicleEntry> entries = new ArrayList<>();
        //String[] strings = new String[cursor.getCount()];
        while(!cursor.isAfterLast()){
            //strings[i] = cursor.getString(cursor.getColumnIndex("list"));
            VehicleEntry vehicleEntry = new VehicleEntry(cursor.getString(cursor.getColumnIndex("EntryID")),cursor.getString(cursor.getColumnIndex("vehicle_number")),cursor.getString(cursor.getColumnIndex("phone_number")),
                    cursor.getString(cursor.getColumnIndex("description")),cursor.getString(cursor.getColumnIndex("name_of_place"))
                    ,cursor.getString(cursor.getColumnIndex("date")),
                    cursor.getString(cursor.getColumnIndex("time")),cursor.getString(cursor.getColumnIndex("officer_name")),
                    cursor.getString(cursor.getColumnIndex("image")),cursor.getInt(cursor.getColumnIndex("status")));
            entries.add(0,vehicleEntry);
            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    public void deleteEntry(VehicleEntry entry){
        //DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM entry WHERE EntryID = \""+entry.getEntryID()+"\";", null);
        c.moveToFirst();
        //int id = c.getInt(c.getColumnIndex("id"));
        db.execSQL("DELETE FROM entry WHERE EntryID = \""+entry.getEntryID()+"\";");
        //db.execSQL("DROP TABLE IF EXISTS  todo_lists_"+id+";");
    }

    public int getStatus(VehicleEntry vehicleEntry){
        int value;
        //DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM entry WHERE EntryID = \""+vehicleEntry.getEntryID()+"\";", null);
        c.moveToFirst();
        value = c.getInt(c.getColumnIndex("status"));
        c.close();
        return value;
    }

    public boolean setStatus(VehicleEntry list, int p){
        //DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM entry WHERE EntryID = \""+list.getEntryID()+"\";", null);
        c.moveToFirst();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("list", list);
        contentValues.put("status", p);
        db.update("entry",contentValues,"EntryID = \"" + list.getEntryID() + "\"",null);
        c.close();
        return true;
    }

    public String[] showItems(String list){
        DatabaseUtils.sqlEscapeString(list);
        int i = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        c.moveToFirst();
        int id = c.getInt(c.getColumnIndex("id"));
        Cursor cursor =  db.rawQuery( "SELECT * FROM todo_lists_"+id+" ORDER BY id DESC;", null);
        cursor.moveToFirst();
        String[] strings = new String[cursor.getCount()];
        while(cursor.isAfterLast() == false){
            strings[i] = cursor.getString(cursor.getColumnIndex("item"));// + cursor.getString(cursor.getColumnIndex("checkbox"));
            i++;
            cursor.moveToNext();
        }
        return strings;
    }

    public boolean addItem(String list, String item){
        DatabaseUtils.sqlEscapeString(list);
        DatabaseUtils.sqlEscapeString(item);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        c.moveToFirst();
        int id = c.getInt(c.getColumnIndex("id"));
        c =  db.rawQuery( "SELECT * FROM todo_lists_"+id+" WHERE item = \""+item+"\";", null);
        c.moveToFirst();
        int count = c.getCount();
        if (count>0)
            return false;

        ContentValues contentValues = new ContentValues();
        contentValues.put("item", item);
        contentValues.put("checkbox", "0");
        db.insert("todo_lists_"+id, null, contentValues);
        return true;
    }

    public void deleteItem(String list, String item){
        DatabaseUtils.sqlEscapeString(list);
        DatabaseUtils.sqlEscapeString(item);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        c.moveToFirst();
        int id = c.getInt(c.getColumnIndex("id"));
        db.execSQL("DELETE FROM todo_lists_"+id+" WHERE item = \""+item+"\";");
    }

    public int toggleCheck(String list, String item){
        int value;
        DatabaseUtils.sqlEscapeString(list);
        DatabaseUtils.sqlEscapeString(item);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        c.moveToFirst();
        int id = c.getInt(c.getColumnIndex("id"));
        c =  db.rawQuery("SELECT * FROM todo_lists_"+id+" WHERE item = \""+item+"\";",null);
        c.moveToFirst();
        value = c.getInt(c.getColumnIndex("checkbox"));
        if (value == 0){
            ContentValues contentValues = new ContentValues();
            contentValues.put("item", item);
            contentValues.put("checkbox", "1");
            db.update("todo_lists_"+id,contentValues,"item = \"" + item + "\"",null);
            c =  db.rawQuery("SELECT * FROM todo_lists_"+id+" WHERE item = \""+item+"\";",null);
            c.moveToFirst();
            value = c.getInt(c.getColumnIndex("checkbox"));
            c.close();
        }
        else{
            ContentValues contentValues = new ContentValues();
            contentValues.put("item", item);
            contentValues.put("checkbox", "0");
            db.update("todo_lists_"+id,contentValues,"item = \"" + item + "\"",null);
            c =  db.rawQuery("SELECT * FROM todo_lists_"+id+" WHERE item = \""+item+"\";",null);
            c.moveToFirst();
            value = c.getInt(c.getColumnIndex("checkbox"));
            c.close();
        }
        c.close();
        return value;
    }

    public int checkItem(String list, String item){
        int value;
        DatabaseUtils.sqlEscapeString(list);
        DatabaseUtils.sqlEscapeString(item);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        c.moveToFirst();
        int id = c.getInt(c.getColumnIndex("id"));
        c =  db.rawQuery("SELECT * FROM todo_lists_"+id+" WHERE item = \""+item+"\";",null);
        c.moveToFirst();
        value = c.getInt(c.getColumnIndex("checkbox"));
        c.close();
        return value;
    }
}