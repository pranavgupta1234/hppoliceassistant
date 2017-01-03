package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pranav Gupta on 1/2/2017.
 */

public class RecyclerAdapterEntryOffline extends RecyclerView.Adapter<RecyclerAdapterEntryOffline.ViewHolder> {

    private List<VehicleEntry> vehicleEntries;
    private List<VehicleEntry> vehicle_entry;
    private Activity activity;
    private Context context;

    public RecyclerAdapterEntryOffline(Activity activity,List<VehicleEntry> entry) {
        this.activity=activity;
        this.vehicle_entry=entry;
        this.vehicleEntries=entry;
        this.context=activity.getBaseContext();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.list_row,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.vehicle_number.setText(vehicleEntries.get(position).getVehicle_number());
        holder.violator_name.setText(vehicleEntries.get(position).getName_of_place());
        holder.license_number.setText(vehicleEntries.get(position).getDescription());
        holder.phone_number.setText(vehicleEntries.get(position).getPhone_number());
        holder.time.setText(vehicleEntries.get(position).getTime());
        holder.date.setText(vehicleEntries.get(position).getDate());
        holder.challan_officer.setText(vehicleEntries.get(position).getOfficer_name());
        if(vehicleEntries.get(position).getStatus()==0){
            holder.details.setBackgroundResource(R.drawable.btn_back);
        }
        if(vehicleEntries.get(position).getStatus()==1){
            holder.details.setBackgroundResource(R.drawable.btn_back_done);
        }
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogEntryOffline dialogEntryOffline = new DialogEntryOffline(activity,vehicleEntries.get(position));
                dialogEntryOffline.setTitle("Challan Details");
                dialogEntryOffline.setCancelable(true);
                dialogEntryOffline.show();
                final Button b =(Button)dialogEntryOffline.findViewById(R.id.send);
                if(vehicleEntries.get(position).getStatus()==1){
                    b.setEnabled(false);
                    b.setText("SENT");
                    b.setBackgroundResource(R.drawable.btn_back_done);
                }


                //to delete the entry from the SQLite Database
                dialogEntryOffline.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBManagerEntry dbManagerEntry = new DBManagerEntry(activity,null,null,1);
                        dbManagerEntry.deleteEntry(vehicleEntries.get(position));
                        notifyDataSetChanged();
                        dialogEntryOffline.dismiss();
                    }
                });

                //to send the data present in local database to server
                dialogEntryOffline.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBManagerEntry dbManagerEntry = new DBManagerEntry(activity,null,null,1);
                        dbManagerEntry.setStatus(vehicleEntries.get(position),1);
                        Firebase mRootRef;
                        mRootRef = new Firebase("https://hppoliceassistant.firebaseio.com/vehicle_entry");
                        Firebase idChild = mRootRef.push();
                        try {
                            idChild.setValue(vehicleEntries.get(position));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity,"Upload Failed !",Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(activity,"Upload Done !",Toast.LENGTH_SHORT).show();
                        holder.details.setBackgroundResource(R.drawable.btn_back_done);
                        b.setText("SENT");
                        b.setEnabled(false);
                        b.setBackgroundResource(R.drawable.btn_back_done);
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleEntries.size();
    }

    public void setFilter(List<VehicleEntry> vh){
        vehicleEntries = new ArrayList<>();
        vehicleEntries.addAll(vh);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView vehicle_number,violator_name,license_number,phone_number,date,time,challan_officer;
        private Button details;
        public ViewHolder(View itemView) {
            super(itemView);
            vehicle_number=(TextView)itemView.findViewById(R.id.vehicle_number);
            violator_name=(TextView)itemView.findViewById(R.id.name_of_person);
            license_number=(TextView)itemView.findViewById(R.id.license_number);
            phone_number=(TextView)itemView.findViewById(R.id.phone_number);
            date=(TextView)itemView.findViewById(R.id.date);
            time=(TextView)itemView.findViewById(R.id.time);
            challan_officer=(TextView)itemView.findViewById(R.id.officer_name);
            details=(Button)itemView.findViewById(R.id.details);
        }
    }
}
