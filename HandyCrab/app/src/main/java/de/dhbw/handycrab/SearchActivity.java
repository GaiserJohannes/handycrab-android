package de.dhbw.handycrab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.BackendConnector;
import de.dhbw.handycrab.backend.GeoLocationService;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.SearchMode;
import de.dhbw.handycrab.view.HandyCrabMapFragment;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback{
    private final static int REQUEST_ACCESS_FINE_LOCATION = 1;
    public final static String BARRIER_LIST = "de.dhbw.handycrab.BARRIERS";
    public final static String SEARCH_LOCATION = "de.dhbw.handycrab.SEARCH_LOCATION";
    public final static String USER_BARRIERS = "de.dhbw.handycrab.USER_BARRIERS";

    private Button[] radiusButtons;
    private Button searchGpsButton;
    private Button searchMapButton;
    private Button searchZipButton;
    private TextView zipText;
    private TextInputLayout zipLayout;
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

        radiusButtons = new Button[4];
        radiusButtons[0] = findViewById(R.id.search_radius1);
        radiusButtons[1] = findViewById(R.id.search_radius2);
        radiusButtons[2] = findViewById(R.id.search_radius3);
        radiusButtons[3] = findViewById(R.id.search_radius4);
        searchGpsButton = findViewById(R.id.search_gps);
        searchMapButton = findViewById(R.id.search_map);
        searchZipButton = findViewById(R.id.search_zip);
        zipText = findViewById(R.id.search_zip_text);
        zipLayout = findViewById(R.id.search_zip_text_layout);
        progressBar = findViewById(R.id.search_progressbar);

        if (checkPermission()) {
            locationService.getLastLocationCallback(this::UpdateLocationText);
        }

        mapFragment = (HandyCrabMapFragment) getSupportFragmentManager().findFragmentById(R.id.search_mapfragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_default, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_userBarriers:
                if (!dataCache.contains(USER_BARRIERS)) {
                    try {
                        dataCache.store(USER_BARRIERS, dataHandler.getBarriersAsync().get(BackendConnector.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
                    }
                    catch (ExecutionException | InterruptedException e) {
                        if (e.getCause() instanceof BackendConnectionException) {
                            BackendConnectionException ex = (BackendConnectionException) e.getCause();
                            Toast.makeText(SearchActivity.this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SearchActivity.this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
                        }
                    } catch (TimeoutException e) {
                        Toast.makeText(this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                    }
                }

                intent = new Intent(this, BarrierListActivity.class);
                intent.putExtra(USER_BARRIERS, true);
                startActivity(intent);

                return true;
            case R.id.action_logout:
                CompletableFuture<Void> finished = dataHandler.logoutAsync();
                Intent logout = new Intent(this, LoginActivity.class);
                logout.putExtra(LoginActivity.LOGOUT, true);
                startActivity(logout);
                try {
                    finished.get(BackendConnector.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                }
                catch (ExecutionException | InterruptedException e) {
                    if (e.getCause() instanceof BackendConnectionException) {
                        BackendConnectionException ex = (BackendConnectionException) e.getCause();
                        Toast.makeText(SearchActivity.this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(SearchActivity.this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                } catch (TimeoutException e) {
                    Toast.makeText(this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void UpdateLocationText(Boolean success, Location location) {
        if (success && location != null) {
            // found Solution
            mapFragment.setLocation(location.getLatitude(), location.getLongitude(), 12, getString(R.string.current_location));
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
                searchGpsButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
                Snackbar.make(findViewById(R.id.search_activity_layout), R.string.missingPermission, Snackbar.LENGTH_LONG).show();
                mode = SearchMode.NONE;
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
                    searchGpsButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
                    Snackbar.make(findViewById(R.id.search_activity_layout), R.string.missingPermission, Snackbar.LENGTH_LONG).show();
                    mode = SearchMode.NONE;
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
                zipLayout.setVisibility(View.GONE);
                if (checkPermission()) {
                    locationService.getLastLocationCallback(this::UpdateLocationText);
                    mode = SearchMode.GPS;
                }
                else {
                    searchGpsButton.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
                    Snackbar.make(findViewById(R.id.search_activity_layout), R.string.missingPermission, Snackbar.LENGTH_LONG).show();
                    mode = SearchMode.NONE;
                }
                break;
            case R.id.search_map:
                mode = SearchMode.MAP;
                zipLayout.setVisibility(View.GONE);
                break;
            case R.id.search_zip:
                mode = SearchMode.ZIP;
                zipLayout.setVisibility(View.VISIBLE);
                break;
        }
        mapFragment.setSearchMode(mode);
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
        switch (mode) {
            case GPS:
                if (checkPermission()) {
                    progressBar.setVisibility(View.VISIBLE);
                    locationService.getLastLocationCallback(this::findBarriersGps);
                }
                break;
            case MAP:
                progressBar.setVisibility(View.VISIBLE);
                mapFragment.getLocationCallback(this::findBarriersGps);
                break;
            case ZIP:
                String zip = zipText.getText() != null ? zipText.getText().toString() : "";
                if (!zip.equals("")) {
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        List<Barrier> list = dataHandler.getBarriersAsync(zip).get(BackendConnector.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
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
                    } catch (TimeoutException e) {
                        Toast.makeText(this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                    }
                    finally {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    Intent intent = new Intent(this, BarrierListActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(SearchActivity.this, getString(R.string.missingZip), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                Toast.makeText(SearchActivity.this, getString(R.string.missingSearchMode), Toast.LENGTH_SHORT).show();
        }
    }

    private void findBarriersGps(Boolean success, Location location) {
        if (success && location != null) {
            try {
                List<Barrier> list = dataHandler.getBarriersAsync(location.getLongitude(), location.getLatitude(), radius).get(BackendConnector.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
                list.forEach(b -> b.setDistanceTo(location));
                dataCache.store(BARRIER_LIST, list);
                dataCache.store(SEARCH_LOCATION, location);
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
            } catch (TimeoutException e) {
                Toast.makeText(this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
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
        locationService.getLastLocationCallback(this::UpdateLocationText);
    }
}
