package northeastern.edu.soundtrackmylife;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity implements SongViewInterface, LocationInterface{

    public List<Song> songs;
    public List<SingleLocation> locations;
    boolean editing;
    int editIndex;
    Button addSongButton;
    Button cancelButton;
    Button doneButton;
    Button locationButton;
    EditText nameBox;
    Playlist playlist;
    RecyclerView playlistView;
    PlaylistViewAdapter playlistAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist_view);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            playlist = extras.getParcelable("playlist");
            editIndex = extras.getInt("index");
            editing = true;
        } else {
            playlist = new Playlist();
            editing = false;
        }
        addSongButton = findViewById(R.id.addSongButton);
        songs = new ArrayList<>();
        getSongs();
        playlistView = findViewById(R.id.playlistSongs);
        playlistView.setLayoutManager(new LinearLayoutManager(this));
        playlistAdapter = new PlaylistViewAdapter(playlist, this);
        playlistView.setAdapter(playlistAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(playlistView);
        addSongButton.setOnClickListener(view -> {
            showDialogue();
        });
        nameBox = findViewById(R.id.playlistNameBox);
        if(editing){
            nameBox.setText(playlist.getPlaylistName());
        }
        cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("frgToLoad", "MusicFragment");
            startActivity(i);
        });
        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(view -> {
            this.playlist.setPlaylistName(nameBox.getText().toString());
            SharedPreferences mPrefs;
            mPrefs = this.getSharedPreferences("TotalPlaylists", MODE_PRIVATE);
            Gson gson = new Gson();
            String json = mPrefs.getString("Playlists", "");
            Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
            List<Playlist> playlists;
            if(!(gson.fromJson(json, type) == null)){
                playlists = gson.fromJson(json, type);
            } else {
                playlists = new ArrayList<>();
            }
            if(editing){
                playlists.set(editIndex, this.playlist);
            } else {
                playlists.add(this.playlist);
            }
            json = gson.toJson(playlists);
            SharedPreferences.Editor mprefsEdit = mPrefs.edit();
            mprefsEdit.putString("Playlists", json);
            mprefsEdit.commit();
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Playlist", playlist);
            startActivity(i);
        });
        //Populate locations
        SharedPreferences mPrefs = this.getSharedPreferences("Locations", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("Locations", "");
        Type type = new TypeToken<ArrayList<SingleLocation>>() {}.getType();
        if(!(gson.fromJson(json, type) == null)){
            this.locations = gson.fromJson(json, type);
        } else {
            this.locations = new ArrayList<>();
        }
        locationButton = findViewById(R.id.locationButton);
        if(editing){
            if(playlist.getLocation() != null){
                locationButton.setText(playlist.getLocation().getName());
            }
        }
        locationButton.setOnClickListener(view -> {
            if(locations.size() > 0){
                showLocationDialogue();
            } else {
                Toast.makeText(this,"Sorry you have no locations saved.", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void showDialogue(){
        Dialog dialog = new Dialog(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.setContentView(R.layout.add_song_view);
        RecyclerView songsView = dialog.findViewById(R.id.songsView);
        songsView.setLayoutManager(new LinearLayoutManager(this));
        SongViewAdapter adapter = new SongViewAdapter(songs, this, dialog, this);
        songsView.setAdapter(adapter);
        songsView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    public void showLocationDialogue(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_song_view);
        RecyclerView locationView = dialog.findViewById(R.id.songsView);
        locationView.setLayoutManager(new LinearLayoutManager(this));
        LocationViewAdapter adapter = new LocationViewAdapter(locations,this,this, dialog);
        locationView.setAdapter(adapter);
        locationView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        dialog.show();
    }

    public void getSongs(){
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 ";
        Cursor cursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
        //Loops to get all data and put in array, testing for getting music
        while(cursor.moveToNext()){
            Song indivSong = new Song(cursor.getString(1), cursor.getString(0), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            //Checks that song still exists in phone
            if(new File(indivSong.getPath()).exists()){
                songs.add(indivSong);
            }
        }
    }

    @Override
    public void onSongClick(Song song) {
        Toast.makeText(this,song.getTitle() + " has been added!", Toast.LENGTH_SHORT).show();
        playlist.addSong(song);
        playlistAdapter.notifyItemInserted(playlist.getSongs().size());
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            playlist.removeSong(viewHolder.getAdapterPosition());
            playlistAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onLocationClick(SingleLocation location) {
        playlist.setLocation(location);
        locationButton.setText(location.getName());
    }
}
