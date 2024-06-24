package northeastern.edu.soundtrackmylife;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistViewHolder extends RecyclerView.ViewHolder{

    public TextView songNameBox;
    public TextView albumNameBox;
    public TextView artistNameBox;
    public PlaylistViewHolder(@NonNull View itemView) {
        super(itemView);
        songNameBox = itemView.findViewById(R.id.songNameBox);
        albumNameBox = itemView.findViewById(R.id.albumNameBox);
        artistNameBox = itemView.findViewById(R.id.artistNameBox);
    }
}
