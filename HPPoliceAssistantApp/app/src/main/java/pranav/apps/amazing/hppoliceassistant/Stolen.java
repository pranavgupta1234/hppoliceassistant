package pranav.apps.amazing.hppoliceassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */
public class Stolen extends Fragment {
    Firebase mRef;
    Firebase childRef;

    private DatabaseReference mDatabase;               //its like an address pointer to your database location
    private ListView lv;
    //private ArrayList<String> veh_entry = new ArrayList<>();
    private RecyclerView recyclerView;
    private RecyclerAdapter adapter;
    private ArrayList<Friend> veh_entry = new ArrayList<>();
    private boolean gender;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //mRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");        //this can be used but its deprecated now
       // DatabaseReference databaseReference = FirebaseDatabase.getInstance();//.getReferenceFromUrl("https://hppoliceassistant.firebaseio.com/vehicle");
        FirebaseDatabase database =FirebaseDatabase.getInstance();              //it return root url
        DatabaseReference myRef = database.getReference("vehicle_entry");              //migrate from tree in other branches
        mDatabase =FirebaseDatabase.getInstance().getReference("vehicle_entry");
        View  view=  getActivity().getLayoutInflater().inflate(R.layout.stolen,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyle_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //setRecyclerViewData(); //adding data to array list
        adapter = new RecyclerAdapter(getActivity(),veh_entry);
        recyclerView.setAdapter(adapter);
        //lv =(ListView)view.findViewById(R.id.list_view);
        //final ArrayAdapter<String> arrayAdapter =new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,veh_entry);
        //lv.setAdapter(arrayAdapter);


        myRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                 Map<String,String> entry = (Map)dataSnapshot.getValue();
                String v = entry.get("VehicleNumber");
                String p = entry.get("PhoneNumber");
                String pl = entry.get("Place");
                String nk = entry.get("Naka");
                String d = entry.get("Description");
                String i = entry.get("Image");
                veh_entry.add(new Friend(v,p,pl,nk,d,i));
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                /*
                Map<String,String> entry = (Map)dataSnapshot.getValue();
                String v = entry.get("VehicleNumber");
                String p = entry.get("PhoneNumber");
                String pl = entry.get("Place");
                String nk = entry.get("Naka");
                String d = entry.get("Description");
                String i = entry.get("Image");
                veh_entry.add(new Friend(v,p,pl,nk,d,i));
                adapter.notifyDataSetChanged();
                */
            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {
                /*
                Map<String,String> entry = (Map)dataSnapshot.getValue();
                String v = entry.get("VehicleNumber");
                String p = entry.get("PhoneNumber");
                String pl = entry.get("Place");
                String nk = entry.get("Naka");
                String d = entry.get("Description");
                String i = entry.get("Image");
                veh_entry.add(new Friend(v,p,pl,nk,d,i));
                adapter.notifyDataSetChanged();
                */
            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*Deprecated Now*/
        /*
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                veh_entry.add(new Friend(entry,entry,entry,entry,entry));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
         */
        veh_entry.clear();
        return  view;
    }
}
