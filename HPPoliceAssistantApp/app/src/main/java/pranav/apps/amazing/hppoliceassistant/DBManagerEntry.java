
package pranav.apps.amazing.hppoliceassistant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBManagerEntry extends SQLiteOpenHelper {


    public DBManagerEntry(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "hp_police_vehicle_entry.db", factory, 6);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS entry (id INTEGER PRIMARY KEY AUTOINCREMENT, vehicle_number TEXT,phone_number TEXT," +
                "description TEXT,date TEXT,time TEXT,name_of_place TEXT,officer_name TEXT,naka_name TEXT,image TEXT,priority INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS entry");
        onCreate(db);
    }

    public boolean addEntry(VehicleEntry newEntry){
        //DatabaseUtils.sqlEscapeString(newEntry);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM entry WHERE time = \""+newEntry.getTime()+"\";", null);
        c.moveToFirst();
        int count = c.getCount();
        if (count>0)
            return false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("vehicle_number",newEntry.getVehicle_number());
        contentValues.put("phone_number",newEntry.getPhone_number());
        contentValues.put("description",newEntry.getDescription());
        contentValues.put("date",newEntry.getDate());
        contentValues.put("time",newEntry.getTime());
        contentValues.put("name_of_place",newEntry.getName_of_place());
        contentValues.put("officer_name",newEntry.getOfficer_name());
        contentValues.put("naka_name",newEntry.getNaka_name());
        contentValues.put("image",newEntry.getImage());
        contentValues.put("priority", 2);
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
        String[] strings = new String[cursor.getCount()];
        while(!cursor.isAfterLast()){
            //strings[i] = cursor.getString(cursor.getColumnIndex("list"));
            entries.add(new VehicleEntry(cursor.getString(cursor.getColumnIndex("vehicle_number")),cursor.getString(cursor.getColumnIndex("phone_number")),
                    cursor.getString(cursor.getColumnIndex("description")),cursor.getString(cursor.getColumnIndex("name_of_place")),
                    cursor.getString(cursor.getColumnIndex("naka_name")),cursor.getString(cursor.getColumnIndex("date")),
                    cursor.getString(cursor.getColumnIndex("time")),cursor.getString(cursor.getColumnIndex("officer_name")),
                    cursor.getString(cursor.getColumnIndex("image"))));

            cursor.moveToNext();
        }
        cursor.close();
        return entries;
    }

    public void deleteList(String list){
        DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        c.moveToFirst();
        int id = c.getInt(c.getColumnIndex("id"));
        db.execSQL("DELETE FROM todo_lists WHERE list = \""+list+"\";");
        db.execSQL("DROP TABLE IF EXISTS  todo_lists_"+id+";");
    }

    public int getPriority(String list){
        int value;
        DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        c.moveToFirst();
        value = c.getInt(c.getColumnIndex("priority"));
        c.close();
        return value;
    }

    public boolean setPriority(String list, int p){
        DatabaseUtils.sqlEscapeString(list);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c =  db.rawQuery( "SELECT * FROM todo_lists WHERE list = \""+list+"\";", null);
        c.moveToFirst();
        ContentValues contentValues = new ContentValues();
        contentValues.put("list", list);
        contentValues.put("priority", p);
        db.update("todo_lists",contentValues,"list = \"" + list + "\"",null);
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