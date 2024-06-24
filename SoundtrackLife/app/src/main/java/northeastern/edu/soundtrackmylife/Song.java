package northeastern.edu.soundtrackmylife;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Song implements Parcelable {
    String path;
    String title;
    String duration;
    String album;
    String artist;

    public Song(String path, String title, String duration, String album, String artist) {
        this.path = path;
        this.title = title;
        this.duration = duration;
        this.album = album;
        this.artist = artist;
    }

    protected Song(Parcel in) {
        path = in.readString();
        title = in.readString();
        duration = in.readString();
        album = in.readString();
        artist = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(path);
        parcel.writeString(title);
        parcel.writeString(duration);
        parcel.writeString(album);
        parcel.writeString(artist);
    }
}
