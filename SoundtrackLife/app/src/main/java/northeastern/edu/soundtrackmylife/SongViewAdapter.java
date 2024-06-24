package northeastern.edu.soundtrackmylife;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongViewAdapter extends RecyclerView.Adapter<SongViewHolder> {
    List<Song> songs;
    private final Context context;
    private final Dialog dialog;
    private SongViewInterface songsInterface;

    public SongViewAdapter(List<Song> songs, Context context, Dialog dialog, SongViewInterface songsInterface){
        this.songs = songs;
        this.context = context;
        this.dialog = dialog;
        this.songsInterface = songsInterface;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongViewHolder(LayoutInflater.from(context).inflate(R.layout.indiv_song_layout, null));
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.songNameBox.setText(songs.get(position).getTitle());
        holder.albumNameBox.setText(songs.get(position).getAlbum());
        holder.artistNameBox.setText(songs.get(position).getArtist());
        holder.itemView.setOnClickListener(view -> {
            songsInterface.onSongClick(songs.get(position));
            dialog.dismiss();
        });
    }

    public Song returnClickedSong(Song song){
        return song;
    }


    @Override
    public int getItemCount() {
        return songs.size();
    }
}
