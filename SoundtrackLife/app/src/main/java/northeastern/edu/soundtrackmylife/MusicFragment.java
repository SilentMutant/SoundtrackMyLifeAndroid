package northeastern.edu.soundtrackmylife;

import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MusicFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SharedPreferences mPrefs;
    private FloatingActionButton addPlaylistButton;

    //Testing
    private List<Playlist> playlists = new ArrayList<>();
    RecyclerView playlistView;
    RecyclerView.Adapter playlistAdapter;

    public MusicFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MusicFragment newInstance(String param1, String param2) {
        MusicFragment fragment = new MusicFragment();
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

        if(!checkPermissions()){
            Toast.makeText(getActivity(), "Read Audio is required, please enable it from settings.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPrefs = getActivity().getSharedPreferences("TotalPlaylists", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("Playlists", "");
        Type type = new TypeToken<ArrayList<Playlist>>() {}.getType();
        if(!(gson.fromJson(json, type) == null)){
            this.playlists = gson.fromJson(json, type);
        } else {
            this.playlists = new ArrayList<>();
        }
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_music, container, false);
        addPlaylistButton = root.findViewById(R.id.addPlaylistButton);
        addPlaylistButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PlaylistActivity.class);
            startActivity(intent);
        });
        playlistView = root.findViewById(R.id.playlistRecylerView);
        playlistView.setLayoutManager(new LinearLayoutManager(getActivity()));
        playlistAdapter = new PlaylistlistViewAdapter(this.playlists, getActivity());
        playlistView.setAdapter(playlistAdapter);
        playlistView.addItemDecoration(new DividerItemDecoration(playlistView.getContext(), DividerItemDecoration.VERTICAL));
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(playlistView);
        return root;

    }

    //Saves the playlist data when page is moved

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences mPrefs = getActivity().getSharedPreferences("TotalPlaylists", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(playlists);
        SharedPreferences.Editor mprefsEdit = mPrefs.edit();
        mprefsEdit.putString("Playlists", json);
        mprefsEdit.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences mPrefs = getActivity().getSharedPreferences("TotalPlaylists", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(playlists);
        SharedPreferences.Editor mprefsEdit = mPrefs.edit();
        mprefsEdit.putString("Playlists", json);
        mprefsEdit.commit();
    }

    boolean checkPermissions(){
        int result = ContextCompat.checkSelfPermission(getActivity(), READ_MEDIA_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            playlists.remove(viewHolder.getAdapterPosition());
            playlistAdapter.notifyDataSetChanged();
        }
    };

}