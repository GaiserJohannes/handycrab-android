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
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.SearchMode;

public class HandyCrabMapFragment extends SupportMapFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;

    private Marker marker;

    private SearchMode searchMode = SearchMode.GPS;

    private BiConsumer<Boolean, Location> function;

    private HashMap<Marker, Barrier> marked = new HashMap<>();

    private List<Barrier> markedBarriers;

    private LatLng startupLocation;

    private String startupLocationTitle;

    private Consumer<Barrier> barrierClicked;

    public HandyCrabMapFragment(){
        getMapAsync(this);
    }

    public HandyCrabMapFragment(Consumer<Barrier> barrierClicked){
        getMapAsync(this);
        this.barrierClicked = barrierClicked;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        map.setOnMapClickListener((latLong) -> {
            if(searchMode == SearchMode.MAP)
                setLocation(latLong, getString(R.string.selected_location));
        });
        map.setOnMarkerClickListener(this::onMarkerClick);
        updateMarker();
        if(startupLocation != null){
            setLocation(startupLocation, startupLocationTitle);
        }
    }

    public void setSearchMode(SearchMode mode){
        this.searchMode = mode;
    }

    public void setMarkerBarriers(List<Barrier> barriers){
        this.markedBarriers = barriers;
    }

    public void updateMarker(){
        marked.forEach((marker,barrier) -> marker.remove());
        marked.clear();
        if(map != null && markedBarriers != null){
            for (Barrier barrier : markedBarriers) {
                Marker m = map.addMarker(new MarkerOptions().position(new LatLng(barrier.getLatitude(), barrier.getLongitude())).title(barrier.getTitle()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round)));
                marked.put(m, barrier);
            }
        }
    }

    public void setLocation(double latitude, double longitude, String title){
        setLocation(new LatLng(latitude, longitude), title);
    }

    public void setLocation(Location location, String title){
        setLocation(new LatLng(location.getLatitude(), location.getLongitude()), title);
    }

    public void onLocationChangedCallback(BiConsumer<Boolean, Location> function){
        this.function = function;
    }

    public void setLocation(LatLng location, String title){
        if(map != null){
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
        else{
            startupLocation = location;
            startupLocationTitle = title;
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marked.containsKey(marker)){
            barrierClicked.accept(marked.get(marker));
        }
        return false;
    }
}
