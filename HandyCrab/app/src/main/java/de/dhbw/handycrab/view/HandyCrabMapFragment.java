package de.dhbw.handycrab.view;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.SearchMode;

public class HandyCrabMapFragment extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap map;

    private Marker marker;

    private SearchMode searchMode = SearchMode.GPS;

    private BiConsumer<Boolean, Location> function;

    private List<Marker> markers = new ArrayList<>();

    public HandyCrabMapFragment(){
        getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setOnMapClickListener((latLong) -> {
            if(searchMode == SearchMode.MAP)
                setLocation(latLong, getString(R.string.selected_location));
        });
        map.moveCamera(CameraUpdateFactory.zoomTo(12f));
    }

    public void setSearchMode(SearchMode mode){
        this.searchMode = mode;
    }

    public void showBarriers(List<Barrier> barriers){
        markers.forEach(m -> m.remove());
        markers.clear();
        if(map != null){
            for (Barrier barrier : barriers) {
                Marker m = map.addMarker(new MarkerOptions().position(new LatLng(barrier.getLatitude(), barrier.getLongitude())).title(barrier.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
                markers.add(m);
            }
        }
    }

    public void setLocation(double latitude, double longitude, String title){
        setLocation(new LatLng(latitude, longitude), title);
    }

    public void onLocationChangedCallback(BiConsumer<Boolean, Location> function){
        this.function = function;
    }

    public void setLocation(LatLng location, String title){
        if(searchMode == SearchMode.GPS){
            map.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
        if(marker == null){
            marker = map.addMarker(new MarkerOptions().position(location).title(title));
            return;
        }
        marker.setPosition(location);
        marker.setTitle(title);
        if(function != null){
            getLocationCallback(function);
        }
    }

    public Location getLocation(){
        if(marker == null){
            return null;
        }
        Location l = new Location("map");
        l.setLongitude(marker.getPosition().longitude);
        l.setLatitude(marker.getPosition().latitude);
        return l;
    }

    public void getLocationCallback(BiConsumer<Boolean, Location> function) {
        function.accept(getLocation() != null, getLocation());
    }
}
