package northeastern.edu.soundtrackmylife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PlaylistViewAdapter extends RecyclerView.Adapter<PlaylistViewHolder> {
    private final Playlist playlist;
    private final Context context;

    public PlaylistViewAdapter(Playlist playlist, Context context) {
        this.playlist = playlist;
        this.context = context;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlaylistViewHolder(LayoutInflater.from(context).inflate(R.layout.indiv_song_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.songNameBox.setText(playlist.getSongs().get(position).getTitle());
        holder.albumNameBox.setText(playlist.getSongs().get(position).getAlbum());
        holder.artistNameBox.setText(playlist.getSongs().get(position).getArtist());

    }

    @Override
    public int getItemCount() {
        return playlist.getSongs().size();
    }
}
