package northeastern.edu.soundtrackmylife;

import android.media.MediaPlayer;

public class MediaPlayerHelper {
    static MediaPlayer instance;
    public static Song currentSong;
    public static Playlist currentPlaylist;
    public static int index = 0;
    public static MediaPlayer getInstance(){
        if(instance == null){
            instance = new MediaPlayer();
        }
        return instance;
    }
}
