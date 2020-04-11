package de.dhbw.handycrab;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.IDataHolder;
import de.dhbw.handycrab.model.Barrier;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchActivity extends AppCompatActivity {

    public static String BARRIER_KEY = "de.dhbw.handycrab.BARRIERS";

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    IDataHolder dataHolder;

    private FusedLocationProviderClient fusedLocationClient;

    private int radius = 10;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Program) getApplicationContext()).graph.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        UpdateCurrentLocation();

        if (currentLocation != null) {
            ((TextView) findViewById(R.id.search_lat)).setText(String.format("%s", currentLocation.getLatitude()));
            ((TextView) findViewById(R.id.search_lon)).setText(String.format("%s", currentLocation.getLongitude()));
        }
    }

    private void UpdateCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        currentLocation = location;
                    }
                });
    }

    public void switchRadius(View view) {
        switch (view.getId()) {
            case R.id.radius1:
                radius = 5;
                break;
            case R.id.radius3:
                radius = 30;
                break;
            default:
                radius = 10;
                break;
        }

        findViewById(R.id.radius1).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.radius2).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.radius3).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        view.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getTheme()));
    }

    public void switchLocation(View view) {
    }

    public void searchBarriers(View view) {
        findViewById(R.id.search_progressbar).setVisibility(View.VISIBLE);

        UpdateCurrentLocation();

        if (currentLocation == null) {
            return;
        }

        CompletableFuture<List<Barrier>> result = dataHandler.getBarriersAsync(currentLocation.getLongitude(), currentLocation.getLatitude(), radius);
        List<Barrier> list = result.join();
        dataHolder.store(BARRIER_KEY, list);

        Intent intent = new Intent(this, BarrierListActivity.class);
        startActivity(intent);
    }

}
