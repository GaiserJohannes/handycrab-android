package de.dhbw.handycrab.view;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import javax.inject.Inject;

import de.dhbw.handycrab.R;
import de.dhbw.handycrab.backend.GeoLocationService;
import de.dhbw.handycrab.model.Barrier;

public class HandyCrabMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap map;

    private Marker selectedPosition;

    public HandyCrabMapFragment(){
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setOnMapClickListener((latLong) -> setLocation(latLong, getString(R.string.selected_location)));
    }

    public void showBarriers(List<Barrier> barriers){
        if(map != null){
            for (Barrier barrier : barriers) {
                map.addMarker(new MarkerOptions().position(new LatLng(barrier.getLatitude(), barrier.getLongitude())).title(barrier.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
            }
        }
    }

    public void setLocation(LatLng location, String title){
        if(selectedPosition == null){
            selectedPosition = map.addMarker(new MarkerOptions().position(location).title(title));
            return;
        }
        selectedPosition.setPosition(location);
        selectedPosition.setTitle(title);
    }

    public LatLng getSelectedLocation(){
        if(selectedPosition == null){
            return null;
        }
        return selectedPosition.getPosition();
    }


}
