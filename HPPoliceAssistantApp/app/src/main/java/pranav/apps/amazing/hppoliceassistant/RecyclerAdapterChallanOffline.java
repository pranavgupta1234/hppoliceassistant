package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.Context;
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

public class RecyclerAdapterChallanOffline extends RecyclerView.Adapter<RecyclerAdapterChallanOffline.ViewHolder> {

    private List<ChallanDetails> challan;
    private List<ChallanDetails> challanDetails;
    private Activity activity;
    private Context context;

    public RecyclerAdapterChallanOffline(Activity activity,List<ChallanDetails> challanEntry) {
        this.activity=activity;
        this.challan=challanEntry;
        this.challanDetails=challanEntry;
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
        holder.vehicle_number.setText(challan.get(position).getVehicle_number());
        holder.violator_name.setText(challan.get(position).getViolator_name());
        holder.license_number.setText(challan.get(position).getLicense_number());
        holder.phone_number.setText(challan.get(position).getViolator_number());
        holder.time.setText(challan.get(position).getTime());
        holder.date.setText(challan.get(position).getDate());
        holder.challan_officer.setText(challan.get(position).getPolice_officer_name());
        if(challan.get(position).getStatus()==0){
            //Toast.makeText(activity,challan.get(position).getViolator_name()+" status "+String.valueOf(challan.get(position).getStatus()),Toast.LENGTH_SHORT).show();
            holder.details.setBackgroundResource(R.drawable.btn_back);
        }
        if(challan.get(position).getStatus()==1){
            holder.details.setBackgroundResource(R.drawable.btn_back_done);
        }
        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DialogChallanOffline dialogChallanOffline = new DialogChallanOffline(activity,challan.get(position));
                dialogChallanOffline.setTitle("Challan Details");
                dialogChallanOffline.setCancelable(true);
                dialogChallanOffline.show();
                final Button b =(Button)dialogChallanOffline.findViewById(R.id.send);
                if(challan.get(position).getStatus()==1){
                    b.setEnabled(false);
                    b.setText("SENT");
                    b.setBackgroundResource(R.drawable.btn_back_done);
                }
                dialogChallanOffline.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBManagerChallan dbManagerChallan = new DBManagerChallan(activity,null,null,1);
                        dbManagerChallan.deleteChallan(challan.get(position));
                        challan.remove(challan.get(position));
                        Toast.makeText(activity,"Deleted",Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();
                        dialogChallanOffline.dismiss();
                    }
                });
                dialogChallanOffline.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DBManagerChallan dbManagerChallan = new DBManagerChallan(activity,null,null,1);
                        dbManagerChallan.setStatus(challan.get(position),1);
                        Firebase mRootRef;
                        mRootRef = new Firebase("https://hppoliceassistant.firebaseio.com/challan");
                        Firebase idChild = mRootRef.push();
                        try {
                            idChild.setValue(challan.get(position));
                            Toast.makeText(activity,"Upload Done !",Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity,"Upload Failed !",Toast.LENGTH_SHORT).show();
                        }
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
        return challan.size();
    }

    public void setFilter(List<ChallanDetails> ch){
        challan = new ArrayList<>();
        int size = ch.size();
        for(int i=0;i<ch.size();i++){
            challan.add(0,ch.get(size));
            size--;
        }
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
            date=(TextView)itemView.findViewById(R.id.date_picker);
            time=(TextView)itemView.findViewById(R.id.time);
            challan_officer=(TextView)itemView.findViewById(R.id.officer_name);
            details=(Button)itemView.findViewById(R.id.details);
        }
    }
}
