package de.dhbw.handycrab;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import de.dhbw.handycrab.backend.GeoLocationService;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.model.Barrier;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchActivity extends AppCompatActivity {

    public static String BARRIER_KEY = "de.dhbw.handycrab.BARRIERS";

    private TextView latitude;
    private TextView longitude;
    private Button search;

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    IDataCache dataCache;

    @Inject
    GeoLocationService locationService;

    private int radius = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        latitude = findViewById(R.id.search_lat);
        longitude = findViewById(R.id.search_lon);
        search = findViewById(R.id.search);

        locationService.getLastLocationCallback(this::UpdateLocationText);
    }

    private void UpdateLocationText(Boolean success, Location location) {
        if (success && location != null) {
            latitude.setText(String.format("%s", location.getLatitude()));
            longitude.setText(String.format("%s", location.getLongitude()));
            search.setEnabled(true);
        }
        else {
            locationService.getLastLocationCallback(this::UpdateLocationText);
        }
    }

    public void switchRadius(View view) {
        switch (view.getId()) {
            case R.id.search_radius1:
                radius = 5;
                break;
            case R.id.search_radius3:
                radius = 30;
                break;
            default:
                radius = 10;
                break;
        }

        findViewById(R.id.search_radius1).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.search_radius2).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        findViewById(R.id.search_radius3).setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryLight, getTheme()));
        view.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimary, getTheme()));
    }

    public void switchLocation(View view) {
    }

    public void searchBarriers(View view) {
        findViewById(R.id.search_progressbar).setVisibility(View.VISIBLE);

        locationService.getLastLocationCallback(this::findBarriers);
    }

    private void findBarriers(Boolean success, Location location) {
        if (success && location != null) {
            CompletableFuture<List<Barrier>> result = dataHandler.getBarriersAsync(location.getLongitude(), location.getLatitude(), radius);
            List<Barrier> list = result.join();
            dataCache.store(BARRIER_KEY, list);

            Intent intent = new Intent(this, BarrierListActivity.class);
            startActivity(intent);
        }
    }
}
