package northeastern.edu.soundtrackmylife;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationViewHolder extends RecyclerView.ViewHolder {
    TextView locationNameView;
    public LocationViewHolder(@NonNull View itemView) {
        super(itemView);
        locationNameView = itemView.findViewById(R.id.locationNameRecyclerView);
    }
}
