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
import android.widget.TextView;
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
    //private ArrayList<Friend> veh_entry = new ArrayList<>();
    private TextView loading;

    private List<VehicleEntry> vehicleEntries = new ArrayList<>();

    private VehicleEntry newEntry;

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

        loading=(TextView)view.findViewById(R.id.loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(getActivity(),vehicleEntries);
        recyclerView.setAdapter(adapter);


        myRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {
                newEntry  = dataSnapshot.getValue(VehicleEntry.class);
                vehicleEntries.add(newEntry);
                adapter.notifyDataSetChanged();
                loading.setText("");
            }
            @Override
            public void onChildChanged(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.google.firebase.database.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.google.firebase.database.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        vehicleEntries.clear();
        return  view;
    }
}
