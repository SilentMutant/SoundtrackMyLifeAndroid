package northeastern.edu.soundtrackmylife;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaylistlistViewHolder extends RecyclerView.ViewHolder {
    TextView playlistNameBox;
    public PlaylistlistViewHolder(@NonNull View itemView) {
        super(itemView);
        playlistNameBox = itemView.findViewById(R.id.playlistNameRecycleView);
    }
}
