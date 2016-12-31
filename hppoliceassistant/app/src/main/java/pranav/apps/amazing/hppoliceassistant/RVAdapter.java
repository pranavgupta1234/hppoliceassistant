package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ViewHolder> {

    private List<ChallanDetails> challan;
    private List<ChallanDetails> challanDetails;
    private Activity activity;

    public RVAdapter(Activity activity,List<ChallanDetails> challanEntry) {
        this.activity=activity;
        this.challan=challanEntry;
        this.challanDetails=challanEntry;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater =activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.list_row, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.vehicle_number.setText(challan.get(position).getVehicle_number());
        holder.violator_name.setText(challan.get(position).getViolator_name());
        holder.license_number.setText(challan.get(position).getLicense_number());
        holder.phone_number.setText(challan.get(position).getViolator_number());
        holder.time.setText(challan.get(position).getTime());
        holder.date.setText(challan.get(position).getDate());
        holder.challan_officer.setText(challan.get(position).getPolice_officer_name());

    }

    @Override
    public int getItemCount() {
        return challan.size();
    }

    public void setFilter(List<ChallanDetails> ch){
        challan = new ArrayList<>();
        challan.addAll(ch);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView vehicle_number,violator_name,license_number,phone_number,date,time,challan_officer;

        public ViewHolder(View itemView) {
            super(itemView);
            vehicle_number=(TextView)itemView.findViewById(R.id.vehicle_number);
            violator_name=(TextView)itemView.findViewById(R.id.name_of_person);
            license_number=(TextView)itemView.findViewById(R.id.license_number);
            phone_number=(TextView)itemView.findViewById(R.id.phone_number);
            date=(TextView)itemView.findViewById(R.id.date);
            time=(TextView)itemView.findViewById(R.id.time);
            challan_officer=(TextView)itemView.findViewById(R.id.officer_name);

        }
    }
}