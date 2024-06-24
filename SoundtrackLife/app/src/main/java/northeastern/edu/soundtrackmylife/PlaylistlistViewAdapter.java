package northeastern.edu.soundtrackmylife;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistlistViewAdapter extends RecyclerView.Adapter<PlaylistlistViewHolder> {
    List<Playlist> playlists;
    private final Context context;

    public PlaylistlistViewAdapter(List<Playlist> playlists, Context context) {
        this.playlists = playlists;
        this.context = context;
    }
    @NonNull
    @Override
    public PlaylistlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistlistViewHolder(LayoutInflater.from(context).inflate(R.layout.indiv_playlist_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistlistViewHolder holder, int position) {
        holder.playlistNameBox.setText(playlists.get(position).getPlaylistName());
        holder.itemView.setOnLongClickListener(view -> {
            //Open intent with playlist information bundled to load
            Intent intent = new Intent(context, PlaylistActivity.class);
            intent.putExtra("playlist", playlists.get(position));
            intent.putExtra("index", position);
            context.startActivity(intent);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }
}
