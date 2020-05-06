package de.dhbw.handycrab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import de.dhbw.handycrab.model.SearchMode;
import de.dhbw.handycrab.view.HandyCrabMapFragment;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static int REQUEST_ACCESS_FINE_LOCATION = 1;
    public final static String BARRIER_LIST = "de.dhbw.handycrab.BARRIERS";

    private Button search;
    private Button[] radiusButtons;
    private Button searchGpsButton;
    private Button searchMapButton;
    private Button searchZipButton;
    private TextView zipText;
    private ProgressBar progressBar;

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    IDataCache dataCache;

    @Inject
    GeoLocationService locationService;

    private int radius = 25;
    private SearchMode mode = SearchMode.GPS;

    private HandyCrabMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = findViewById(R.id.search);
        radiusButtons = new Button[4];
        radiusButtons[0] = findViewById(R.id.search_radius1);
        radiusButtons[1] = findViewById(R.id.search_radius2);
        radiusButtons[2] = findViewById(R.id.search_radius3);
        radiusButtons[3] = findViewById(R.id.search_radius4);
        searchGpsButton = findViewById(R.id.search_gps);
        searchMapButton = findViewById(R.id.search_map);
        searchZipButton = findViewById(R.id.search_zip);
        zipText = findViewById(R.id.search_zip_text);
        progressBar = findViewById(R.id.search_progressbar);

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
            // found Solution
            search.setEnabled(true);
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
                searchGpsButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
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

    public void changeSearchMode(View view) {
        searchGpsButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        searchMapButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        searchZipButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        view.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getTheme()));
        switch (view.getId()) {
            case R.id.search_gps:
                zipText.setVisibility(View.GONE);
                if (checkPermission()) {
                    locationService.getLastLocationCallback(this::UpdateLocationText);
                    mode = SearchMode.GPS;
                }
                else {
                    searchGpsButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
                    searchMapButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getTheme()));
                    Snackbar.make(findViewById(R.id.search_activity_layout), R.string.missingPermission, Snackbar.LENGTH_LONG).show();
                    mode = SearchMode.MAP;
                }
                break;
            case R.id.search_map:
                mode = SearchMode.MAP;
                zipText.setVisibility(View.GONE);
                break;
            case R.id.search_zip:
                mode = SearchMode.ZIP;
                zipText.setVisibility(View.VISIBLE);
                break;
        }
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

        for (Button b : radiusButtons) {
            b.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        }
        view.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getTheme()));
    }

    public void searchBarriers(View view) {
        progressBar.setVisibility(View.VISIBLE);

        switch (mode) {
            case GPS:
                locationService.getLastLocationCallback(this::findBarriersGps);
                break;
            case ZIP:
                String zip = zipText.getText() != null ? zipText.getText().toString() : "";
                if (!zip.equals("")) {
                    try {
                        List<Barrier> list = dataHandler.getBarriersAsync(zip).get();
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
                        break;
                    }
                    finally {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    Toast.makeText(SearchActivity.this, getString(R.string.missingZip), Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }

    private void findBarriersGps(Boolean success, Location location) {
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
                progressBar.setVisibility(View.INVISIBLE);
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
            List<Barrier> barriers = dataHandler.getBarriersAsync(0, 0, 0).get();
            mapFragment.showBarriers(barriers);
        }
        catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
