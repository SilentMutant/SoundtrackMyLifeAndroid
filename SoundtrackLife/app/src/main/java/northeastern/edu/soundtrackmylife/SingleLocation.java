package northeastern.edu.soundtrackmylife;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class SingleLocation implements Parcelable {
    LatLng location;
    String name;

    public SingleLocation(LatLng location, String name) {
        this.location = location;
        this.name = name;
    }

    protected SingleLocation(Parcel in) {
        double lat = in.readDouble();
        double lon = in.readDouble();
        location = new LatLng(lat, lon);
        name = in.readString();
    }

    public static final Creator<SingleLocation> CREATOR = new Creator<SingleLocation>() {
        @Override
        public SingleLocation createFromParcel(Parcel in) {
            return new SingleLocation(in);
        }

        @Override
        public SingleLocation[] newArray(int size) {
            return new SingleLocation[size];
        }
    };

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        double lat = location.latitude;
        double lon = location.longitude;
        parcel.writeDouble(lat);
        parcel.writeDouble(lon);
        parcel.writeString(name);

    }
}
