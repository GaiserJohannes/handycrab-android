package de.dhbw.handycrab.view;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.Barrier;

public class HandyCrabMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap map;

    private Marker marker;

    private MapMode mapMode = MapMode.GPS;

    public HandyCrabMapFragment(){
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setOnMapClickListener((latLong) -> {
            if(mapMode == MapMode.MAP)
                setLocation(latLong, getString(R.string.selected_location));
        });
        map.moveCamera(CameraUpdateFactory.zoomTo(12f));
    }

    public void setMapMode(MapMode mode){
        this.mapMode = mode;
    }

    public void showBarriers(List<Barrier> barriers){
        if(map != null){
            for (Barrier barrier : barriers) {
                map.addMarker(new MarkerOptions().position(new LatLng(barrier.getLatitude(), barrier.getLongitude())).title(barrier.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
            }
        }
    }

    public void setLocation(double latitude, double longitude, String title){
        setLocation(new LatLng(latitude, longitude), title);
    }

    public void setLocation(LatLng location, String title){
        if(mapMode == MapMode.GPS){
            map.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
        if(marker == null){
            marker = map.addMarker(new MarkerOptions().position(location).title(title));
            return;
        }
        marker.setPosition(location);
        marker.setTitle(title);
    }

    public LatLng getLocation(){
        if(marker == null){
            return null;
        }
        return marker.getPosition();
    }
}
