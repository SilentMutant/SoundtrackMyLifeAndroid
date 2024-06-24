package northeastern.edu.soundtrackmylife;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Granularity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Playlist> playlists = new ArrayList<>();
    int currentIndex;
    Playlist currentPlaylist;
    SharedPreferences mPrefs;
    Song currentSong;
    TextView songTitle, currentArtist, currentLocation, currentTime, totalTime;
    SeekBar seekBar;
    ImageView pausePlay, skipSongBtn, prevSongBtn, rockIcon;
    MediaPlayer myMediaPlayer = MediaPlayerHelper.getInstance();
    private FusedLocationProviderClient fusedLocationClient;
    int pebbleRotation = 0;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPrefs = getActivity().getSharedPreferences("TotalPlaylists", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("Playlists", "");
        Type type = new TypeToken<ArrayList<Playlist>>() {
        }.getType();
        if (!(gson.fromJson(json, type) == null)) {
            this.playlists = gson.fromJson(json, type);
        } else {
            this.playlists = new ArrayList<>();
        }
        currentIndex = MediaPlayerHelper.index;
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        songTitle = root.findViewById(R.id.songTitle);
        currentArtist = root.findViewById(R.id.artistName);
        currentLocation = root.findViewById(R.id.locationName);
        currentTime = root.findViewById(R.id.current_time);
        totalTime = root.findViewById(R.id.total_time);
        seekBar = root.findViewById(R.id.seek_bar);
        pausePlay = root.findViewById(R.id.playPauseButton);
        pausePlay.setOnClickListener(view -> {
            try {
                playPause();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        skipSongBtn = root.findViewById(R.id.nextButton);
        skipSongBtn.setOnClickListener(view -> {
            try {
                nextSong();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        prevSongBtn = root.findViewById(R.id.previousButton);
        prevSongBtn.setOnClickListener(view -> {
            try {
                prevSong();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        rockIcon = root.findViewById(R.id.pebble);

        //Runs on UI thread to update the seek bar and ensure the right pause or play button is set.
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (myMediaPlayer != null) {
                    seekBar.setProgress(myMediaPlayer.getCurrentPosition());
                    currentTime.setText(convertTime(myMediaPlayer.getCurrentPosition() + ""));
                    if (myMediaPlayer.isPlaying()) {
                        pausePlay.setImageResource(R.drawable.outline_autopause_24);
                        rockIcon.setRotation(pebbleRotation++);
                    } else {
                        pausePlay.setImageResource(R.drawable.outline_autoplay_24);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //Moves song to spot seeked to
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (myMediaPlayer != null && b) {
                    myMediaPlayer.seekTo(i);
                }
            }
            //Response for interaction if user is scrubbing bar, will not be used
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            //Response for when user lets go of seek bar
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //Creates fused location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequests();

        //When song ends moves to next song
        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    if (currentPlaylist != null) {
                        nextSong();
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        //Binds on complete listener to media player
        myMediaPlayer.setOnCompletionListener(onCompletionListener);
        return root;
    }

    //Every 5 seconds checks location, only reports a change if the user moved a minimum of 40 meters.
    public void locationRequests() {
        LocationRequest locationRequest = new LocationRequest
                .Builder(5000)
                .setGranularity(Granularity.GRANULARITY_FINE)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setMinUpdateDistanceMeters(40)
                .build();

        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                if (task.isSuccessful()) {
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                } else {

                }
            }
        });
    }

    //Callback if user moved more than 40 meters
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            findPlaylist(locationResult.getLastLocation());
        }
    };

    //Searches to see if there is an available playlist in your area
    public void findPlaylist(Location lastLocation) {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

            LatLng current = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            for(Playlist p : playlists) {
                if (p.inLocation(current)) {
                    if (currentPlaylist != null) {
                        if (!Objects.equals(p.getPlaylistName(), currentPlaylist.getPlaylistName())) {
                            if (myMediaPlayer.isPlaying()) {
                                myMediaPlayer.reset();
                            }
                            currentPlaylist = p;
                            MediaPlayerHelper.currentPlaylist = currentPlaylist;
                        }
                    } else {
                        currentPlaylist = p;
                        MediaPlayerHelper.currentPlaylist = currentPlaylist;
                    }
                    try {
                        playMusic(0);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    if(myMediaPlayer.isPlaying()){
                        myMediaPlayer.stop();
                        Toast.makeText(getActivity(), "You are outside a pinned location, press play to continue your previous playlist.", Toast.LENGTH_LONG).show();
                    }
                }
            }
    }

    //Handles the playing of music and seekbar setup
    public void playMusic(int index) throws IOException {
        if (!myMediaPlayer.isPlaying()) {
            currentSong = currentPlaylist.getSongs().get(index);
            MediaPlayerHelper.index = index;
            MediaPlayerHelper.currentSong = currentSong;
            setSongDetails();
            myMediaPlayer.setDataSource(currentSong.getPath());
            myMediaPlayer.prepare();
            myMediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(myMediaPlayer.getDuration());
        }
    }

    //Interaction with play/pause button
    public void playPause() throws IOException {
        if(myMediaPlayer.isPlaying()){
            myMediaPlayer.pause();
        } else {
            myMediaPlayer.start();

        }
    }

    //Interaction with next song button
    public void nextSong() throws IOException {
        currentIndex += 1;
        myMediaPlayer.reset();
        if(currentIndex > currentPlaylist.getSongs().size() - 1){
            currentIndex = 0;
            playMusic(currentIndex);
        } else {
            playMusic(currentIndex);
        }
    }

    //Interaction with prev song button
    public void prevSong() throws IOException {
        currentIndex -= 1;
        myMediaPlayer.reset();
        if(currentIndex < 0){
            currentIndex = currentPlaylist.getSongs().size() - 1;
            playMusic(currentIndex);
        } else {
            playMusic(currentIndex);
        }
    }

    //Gets song details and sets UI
    public void setSongDetails(){
        currentArtist.setText(currentSong.getArtist());
        songTitle.setText(currentSong.getTitle());
        currentLocation.setText(currentPlaylist.getLocation().getName());
        totalTime.setText(convertTime(currentSong.getDuration()));
    }

    //Converts song duration to readable min and seconds
    public String convertTime(String duration){
        long mills = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mills) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(mills) % 60);
    }


    @Override
    public void onPause()   {
        super.onPause();
    }

    //On resume sets the seek bar and UI
    @Override
    public void onResume() {
        super.onResume();
        if(myMediaPlayer.isPlaying()){
            currentSong = MediaPlayerHelper.currentSong;
            currentPlaylist = MediaPlayerHelper.currentPlaylist;
            seekBar.setMax(myMediaPlayer.getDuration());
            seekBar.setProgress(myMediaPlayer.getCurrentPosition());
            setSongDetails();
        }

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
