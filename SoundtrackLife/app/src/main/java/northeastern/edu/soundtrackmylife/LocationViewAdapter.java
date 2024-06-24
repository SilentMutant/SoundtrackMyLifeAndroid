package northeastern.edu.soundtrackmylife;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewHolder> {

    List<SingleLocation> locations;
    private final Context context;
    private final Dialog dialog;
    private LocationInterface locationInterface;

    public LocationViewAdapter(List<SingleLocation> locations, Context context, LocationInterface locationInterface, Dialog dialog) {
        this.locations = locations;
        this.context = context;
        this.locationInterface = locationInterface;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationViewHolder(LayoutInflater.from(context).inflate(R.layout.indiv_location_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.locationNameView.setText(locations.get(position).getName());
        holder.itemView.setOnClickListener(view -> {
            locationInterface.onLocationClick(locations.get(position));
            dialog.dismiss();
        });
    }

    /*public SingleLocation onLocationClick(SingleLocation location){
        return location;
    }*/

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
