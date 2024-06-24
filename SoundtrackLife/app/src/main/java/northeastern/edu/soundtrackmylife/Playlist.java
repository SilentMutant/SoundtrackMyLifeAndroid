package northeastern.edu.soundtrackmylife;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Playlist implements Parcelable {
    private String playlistName;
    //Subject to change, this will potentially hold the location
    private SingleLocation location;

    private List<Song> songs;

    public Playlist() {
        songs = new ArrayList<Song>();
    }

    protected Playlist(Parcel in) {
        songs = new ArrayList<Song>();
        in.readTypedList(songs, Song.CREATOR);
        playlistName = in.readString();
        location = (SingleLocation) in.readValue(SingleLocation.class.getClassLoader());
    }

    public static final Creator<Playlist> CREATOR = new Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel in) {
            return new Playlist(in);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public SingleLocation getLocation() {
        return location;
    }

    public void setLocation(SingleLocation location) {
        this.location = location;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song){
        this.songs.add(song);
    }

    public void removeSong(int position){ this.songs.remove(position); }

    public boolean inLocation(LatLng currentLocation){
        if(location != null){
            float[] results = new float[3];
            Location.distanceBetween(location.getLocation().latitude, location.getLocation().longitude, currentLocation.latitude, currentLocation.longitude, results);
            return results[0] <= 40;
        } else {
            return false;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeTypedList(songs);
        parcel.writeString(playlistName);
        parcel.writeValue(location);
    }
}
