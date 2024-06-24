package northeastern.edu.soundtrackmylife;


import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleMap myMap;

    String markerName;
    List<SingleLocation> locations = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;


    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        SharedPreferences mPrefs = getActivity().getSharedPreferences("Locations", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("Locations", "");
        Type type = new TypeToken<ArrayList<SingleLocation>>() {}.getType();
        if(!(gson.fromJson(json, type) == null)){
            this.locations = gson.fromJson(json, type);
        } else {
            this.locations = new ArrayList<>();
        }
        return root;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;

        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        } else {
            myMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener( getActivity(), location -> {
                LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 16));
            });

        }

        myMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                showDialogue(latLng);
            }
        });


        if(!locations.isEmpty()){
            //Loop through list to set markers
            for(SingleLocation i : locations){
                createMarker(i.getLocation(), i.getName());
            }
        }



    }
    public void showDialogue(LatLng latLng){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.name_marker_dialogue);
        EditText newMarkerBox = dialog.findViewById(R.id.markerNameBox);
        Button markerButton = dialog.findViewById(R.id.markerDoneButton);
        markerButton.setOnClickListener(view -> {
            markerName = String.valueOf(newMarkerBox.getText());
            Toast.makeText(getActivity(), markerName + " has been added!", Toast.LENGTH_SHORT).show();
            createMarker(latLng, markerName);
            locations.add(new SingleLocation(latLng, markerName));
            dialog.dismiss();
        });
        dialog.show();
    }

    public void createMarker(LatLng latLng, String name){
        myMap.addMarker(new MarkerOptions().position(latLng).title(name));
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(40);
        circleOptions.strokeWidth(2);
        circleOptions.strokeColor(Color.RED);
        circleOptions.fillColor(0x220000FF);
        myMap.addCircle(circleOptions);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences mPrefs = getActivity().getSharedPreferences("Locations", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(locations);
        SharedPreferences.Editor mprefsEdit = mPrefs.edit();
        mprefsEdit.putString("Locations", json);
        mprefsEdit.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences mPrefs = getActivity().getSharedPreferences("Locations", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(locations);
        SharedPreferences.Editor mprefsEdit = mPrefs.edit();
        mprefsEdit.putString("Locations", json);
        mprefsEdit.commit();
    }
}