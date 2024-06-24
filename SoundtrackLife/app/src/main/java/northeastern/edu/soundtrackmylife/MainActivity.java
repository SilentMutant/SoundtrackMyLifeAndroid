package northeastern.edu.soundtrackmylife;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_MEDIA_AUDIO;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Objects;

import northeastern.edu.soundtrackmylife.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            replaceFragment(new MusicFragment());
        } else {
            replaceFragment(new HomeFragment());
        }


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();
            if(id == R.id.home){
                replaceFragment(new HomeFragment());
            } else if(id == R.id.music) {
                replaceFragment(new MusicFragment());
            } else if(id == R.id.maps) {
                replaceFragment((new MapFragment()));
            }

            return true;
        });
        if(checkPermissionsAudio() == false){
            requestPermissionsAudio();
            return;
        }
        if(checkPermissionsLocation() == false){
            requestPermissionsLocation();
            return;
        }

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    boolean checkPermissionsAudio(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, READ_MEDIA_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermissionsAudio(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, READ_MEDIA_AUDIO)){
            Toast.makeText(this, "Read Audio is required, please enable it from settings.", Toast.LENGTH_SHORT).show();
        }else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_MEDIA_AUDIO}, 42);
        }
    }

    boolean checkPermissionsLocation(){
        int result = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION);
        if(result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    void requestPermissionsLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_FINE_LOCATION}, 42);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ACCESS_COARSE_LOCATION}, 42);
            return;
        } else {
            Toast.makeText(this, "Location is required, please enable it from settings.", Toast.LENGTH_SHORT).show();
        }
    }
}