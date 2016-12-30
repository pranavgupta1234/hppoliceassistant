package pranav.apps.amazing.hppoliceassistant;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public TextView name_TextView;
    public TextView phone_TextView;
    public TextView place;
    public TextView naka;
    public TextView descrip;


    public ItemViewHolder(View itemView) {
        super(itemView);
        itemView.setClickable(true);
        name_TextView = (TextView) itemView.findViewById(R.id.country_name);
        phone_TextView= (TextView) itemView.findViewById(R.id.country_iso);
        place= (TextView) itemView.findViewById(R.id.phone_number);
        naka= (TextView) itemView.findViewById(R.id.place_name);
        descrip= (TextView) itemView.findViewById(R.id.naka_naam);

    }

    public void bind(CountryModel countryModel) {
        name_TextView.setText(countryModel.getName());
        phone_TextView.setText(countryModel.getisoCode());
        place.setText(countryModel.getPlace());
        naka.setText(countryModel.getNaka());
        descrip.setText(countryModel.getD());

    }


}
