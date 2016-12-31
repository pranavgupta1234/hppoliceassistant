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

import java.util.List;

//Public class for Stolen Items


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<Friend> friends;
    private Activity activity;

    public RecyclerAdapter(Activity activity, List<Friend> friends) {
        this.friends = friends;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        //inflate your layout and pass it to view holder
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_recycler, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int position) {

        //setting data to view holder elements
        viewHolder.vehicle_no.setText(friends.get(position).getVehicle_num());
        viewHolder.phone_no.setText(friends.get(position).getPhone_num());
        viewHolder.place.setText(friends.get(position).getPlace_num());
        viewHolder.naka_name.setText(friends.get(position).getNaka_name());
        viewHolder.desc.setText(friends.get(position).getDescrip());
        viewHolder.setImage(viewHolder.imageView.getContext(),friends.get(position).getImage());

        //set on click listener for each element
        viewHolder.container.setOnClickListener(onClickListener(position));
    }

    private void setDataToView(TextView a, TextView b,TextView c,TextView d,TextView e, ImageView genderIcon, int position) {
        a.setText(friends.get(position).getVehicle_num());
        b.setText(friends.get(position).getPhone_num());
        c.setText(friends.get(position).getPlace_num());
        d.setText(friends.get(position).getNaka_name());
        e.setText(friends.get(position).getDescrip());
        genderIcon.setImageResource(R.drawable.ic_launcher);

    }

    @Override
    public int getItemCount() {
        return (null != friends ? friends.size() : 0);
    }

    private View.OnClickListener onClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(activity);
                dialog.setContentView(R.layout.item_recycler);
                dialog.setTitle("Entry No " + (position+1));
                dialog.setCancelable(true); // dismiss when touching outside Dialog

                // set the custom dialog components - texts and image
                TextView vh = (TextView) dialog.findViewById(R.id.vehno);
                TextView phn = (TextView) dialog.findViewById(R.id.phno);
                TextView pla = (TextView) dialog.findViewById(R.id.name_of_place);
                TextView nka = (TextView) dialog.findViewById(R.id.name_of_naka);
                TextView descrip = (TextView) dialog.findViewById(R.id.desc);
                ImageView icon = (ImageView) dialog.findViewById(R.id.image);

                setDataToView(vh,phn,pla,nka,descrip,icon, position);

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
        private TextView naka_name;
        private TextView desc;
        private View container;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
            vehicle_no = (TextView) view.findViewById(R.id.vehno);
            phone_no = (TextView) view.findViewById(R.id.phno);
            place = (TextView) view.findViewById(R.id.name_of_place);
            naka_name = (TextView) view.findViewById(R.id.name_of_naka);
            desc = (TextView) view.findViewById(R.id.desc);
            container = view.findViewById(R.id.card_view);
        }
        public void setImage(Context ctx,String image){
            Picasso.with(ctx).load(image).into(imageView);
        }
    }
}