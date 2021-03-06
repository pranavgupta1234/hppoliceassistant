package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

//Public class for Stolen Items


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<VehicleEntry> vehicleEntry;
    private Activity activity;

    public RecyclerAdapter(Activity activity, List<VehicleEntry> vehicleEntry) {
        this.vehicleEntry = vehicleEntry;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //inflate your layout and pass it to view holder
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_recycler, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {
        //setting data to view holder elements
        viewHolder.id.setText(vehicleEntry.get(position).getEntryID());
        viewHolder.vehicle_no.setText(vehicleEntry.get(position).getVehicle_number());
        viewHolder.phone_no.setText(vehicleEntry.get(position).getPhone_number());
        viewHolder.place.setText(vehicleEntry.get(position).getName_of_place());
        viewHolder.desc.setText(vehicleEntry.get(position).getDescription());
        viewHolder.date.setText(vehicleEntry.get(position).getDate());
        viewHolder.time.setText(vehicleEntry.get(position).getTime());

        if(vehicleEntry.get(position).getImage().contentEquals("Photo not available")){
         viewHolder.imageView.setBackgroundResource(R.drawable.notavailable);
        }else {
            //viewHolder.imageView.setPadding(10,10,10,10);
            viewHolder.imageView.setBackgroundResource(R.drawable.loading);
            viewHolder.imageView.setPadding(0,0,0,0);
            viewHolder.setImage(activity, vehicleEntry.get(position).getImage());
        }
        //set on click listener for each element
        viewHolder.container.setOnClickListener(onClickListener(position));
        viewHolder.imageView.setOnClickListener(onClickListenerImage(position));
    }
    public void setFilter(List<VehicleEntry> vh){
        vehicleEntry = new ArrayList<>();
        vehicleEntry.addAll(vh);
        notifyDataSetChanged();
    }

    private void setDataToView(TextView id,TextView a, TextView b, TextView c, TextView e, ImageView genderIcon, TextView date,
                               TextView time, int position) {
        id.setText(vehicleEntry.get(position).getEntryID());
        a.setText(vehicleEntry.get(position).getVehicle_number());
        b.setText(vehicleEntry.get(position).getPhone_number());
        c.setText(vehicleEntry.get(position).getName_of_place());
        e.setText(vehicleEntry.get(position).getDescription());
        date.setText(vehicleEntry.get(position).getDate());
        time.setText(vehicleEntry.get(position).getTime());
        genderIcon.setBackgroundResource(R.drawable.loading);
    }

    @Override
    public int getItemCount() {
        return (null != vehicleEntry ? vehicleEntry.size() : 0);
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerAdapter.ViewHolder viewHolder = new RecyclerAdapter.ViewHolder(v);
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.stolen_entry_dialog);
                dialog.setTitle("Entry No " + (position+1));
                dialog.setCancelable(true); // dismiss when touching outside Dialog

                // set the custom dialog components - texts and image

                TextView vh = (TextView) dialog.findViewById(R.id.vehno);
                TextView phn = (TextView) dialog.findViewById(R.id.phno);
                TextView pla = (TextView) dialog.findViewById(R.id.name_of_place);
                TextView descrip = (TextView) dialog.findViewById(R.id.desc);
                TextView date=(TextView)dialog.findViewById(R.id.date_picker);
                TextView time =(TextView)dialog.findViewById(R.id.time);
                ImageView icon = (ImageView) dialog.findViewById(R.id.image);
                TextView id = (TextView)dialog.findViewById(R.id.entry_id);
                setDataToView(id,vh,phn,pla,descrip,icon,date,time,position);
                viewHolder.setImageDialog(dialog.getContext(),vehicleEntry.get(position).getImage(),icon);
                dialog.show();
            }
        };
    }
    private View.OnClickListener onClickListenerImage(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerAdapter.ViewHolder viewHolder = new RecyclerAdapter.ViewHolder(v);
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.stolen_entry_image);
                dialog.setTitle("Entry No " + (position + 1));
                dialog.setCancelable(true); // dismiss when touching outside Dialog

                // set the custom dialog components - texts and image
                ImageView icon = (ImageView) dialog.findViewById(R.id.stolen_image);
                viewHolder.setImageDialogAlone(dialog.getContext(), vehicleEntry.get(position).getImage(), icon,position);
                dialog.show();
            }
        };
    }
    /**
     * View holder to display each RecylerView item
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView vehicle_no;
        private TextView phone_no;
        private TextView place;
        private TextView desc;
        private View container;
        private TextView date;
        private TextView time;
        private TextView id;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
            vehicle_no = (TextView) view.findViewById(R.id.vehno);
            phone_no = (TextView) view.findViewById(R.id.phno);
            place = (TextView) view.findViewById(R.id.name_of_place);
            desc = (TextView) view.findViewById(R.id.desc);
            container = view.findViewById(R.id.card_view);
            date=(TextView)view.findViewById(R.id.date_picker);
            time=(TextView)view.findViewById(R.id.time);
            id = (TextView)view.findViewById(R.id.entry_id);
        }
        public void setImage(Context ctx,String image){
            Picasso.with(ctx).load(image).resize(120,180).centerCrop().into(imageView);
        }
        public void setImageDialog(Context ctx,String image,ImageView icon){
            Picasso.with(ctx).load(image).fit().into(icon);
        }

        public void setImageDialogAlone(Context context, String image, ImageView icon,int position) {
            if(vehicleEntry.get(position).getImage().contentEquals("Photo not available")||vehicleEntry.get(position).getImage().contentEquals("null")){
                icon.setBackgroundResource(R.drawable.notavailable);
            }else{
                icon.setBackgroundResource(R.drawable.loading);
                Picasso.with(context).load(image).fit().into(icon);
            }
        }
    }
}