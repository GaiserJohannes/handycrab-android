package de.dhbw.handycrab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.material.snackbar.Snackbar;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.GeoLocationService;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.view.HandyCrabMapFragment;
import de.dhbw.handycrab.view.MapMode;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static int REQUEST_ACCESS_FINE_LOCATION = 1;
    public final static String BARRIER_LIST = "de.dhbw.handycrab.BARRIERS";

    private TextView latitude;
    private TextView longitude;
    private Button search;

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    IDataCache dataCache;

    @Inject
    GeoLocationService locationService;

    private int radius = 25;

    private HandyCrabMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        latitude = findViewById(R.id.search_lat);
        longitude = findViewById(R.id.search_lon);
        search = findViewById(R.id.search);

        if (checkPermission()) {
            locationService.getLastLocationCallback(this::UpdateLocationText);
        }
        mapFragment = (HandyCrabMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    private void UpdateLocationText(Boolean success, Location location) {
        if (success && location != null) {
            latitude.setText(String.format("%s", location.getLatitude()));
            longitude.setText(String.format("%s", location.getLongitude()));
            search.setEnabled(true);
            mapFragment.setLocation(location.getLatitude(), location.getLongitude(), getString(R.string.current_location));
        }
        else {
            locationService.getLastLocationCallback(this::UpdateLocationText);
        }
    }

    private boolean checkPermission() {
        if (!locationService.isLocationPermissionGranted()) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                findViewById(R.id.search_gps).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
                Snackbar.make(findViewById(R.id.search_activity_layout), R.string.missingPermission, Snackbar.LENGTH_LONG).show();
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationService.getLastLocationCallback(this::UpdateLocationText);
                }
                else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }
        }
    }

    public void searchWithGPS(View view) {
        findViewById(R.id.search_gps).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getTheme()));
        findViewById(R.id.search_map).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.search_zip).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        mapFragment.setMapMode(MapMode.GPS);
        if (checkPermission()) {
            locationService.getLastLocationCallback(this::UpdateLocationText);
        }
    }

    public void searchWithMap(View view) {
        findViewById(R.id.search_gps).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.search_map).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getTheme()));
        findViewById(R.id.search_zip).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        mapFragment.setMapMode(MapMode.MAP);
    }

    public void switchRadius(View view) {
        switch (view.getId()) {
            case R.id.search_radius1:
                radius = 10;
                break;
            case R.id.search_radius3:
                radius = 50;
                break;
            case R.id.search_radius4:
                radius = 100;
                break;
            default:
                radius = 25;
                break;
        }

        findViewById(R.id.search_radius1).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.search_radius2).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.search_radius3).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.search_radius4).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        view.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getTheme()));
    }

    public void searchBarriers(View view) {
        findViewById(R.id.search_progressbar).setVisibility(View.VISIBLE);

        locationService.getLastLocationCallback(this::findBarriers);
    }

    private void findBarriers(Boolean success, Location location) {
        if (success && location != null) {
            try {
                List<Barrier> list = dataHandler.getBarriersAsync(location.getLongitude(), location.getLatitude(), radius).get();
                dataCache.store(BARRIER_LIST, list);
            }
            catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof BackendConnectionException) {
                    BackendConnectionException ex = (BackendConnectionException) e.getCause();
                    Toast.makeText(SearchActivity.this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SearchActivity.this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
                }
                return;
            }
            finally {
                findViewById(R.id.search_progressbar).setVisibility(View.INVISIBLE);
            }

            Intent intent = new Intent(this, BarrierListActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(SearchActivity.this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            List<Barrier> barriers = dataHandler.getBarriersAsync(0,0, 0).get();
            mapFragment.showBarriers(barriers);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
