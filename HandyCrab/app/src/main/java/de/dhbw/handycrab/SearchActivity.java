package de.dhbw.handycrab;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import de.dhbw.handycrab.backend.BackendConnector;

import javax.inject.Inject;

public class SearchActivity extends AppCompatActivity {

    @Inject
    BackendConnector dataHandler;

    private FusedLocationProviderClient fusedLocationClient;

    private int radius = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    System.out.println("Found Location......................................");
                    if (location != null) {
                        ((TextView) findViewById(R.id.search_lat)).setText(String.format("%s", location.getLatitude()));
                        ((TextView) findViewById(R.id.search_lon)).setText(String.format("%s", location.getLongitude()));
                    }
                })
                .addOnCompleteListener(this, location -> {
                    System.out.println("End Location...................................");
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

        findViewById(R.id.radius1).setBackgroundTintList(getResources().getColorStateList(R.color.btn_default, getTheme()));
        findViewById(R.id.radius2).setBackgroundTintList(getResources().getColorStateList(R.color.btn_default, getTheme()));
        findViewById(R.id.radius3).setBackgroundTintList(getResources().getColorStateList(R.color.btn_default, getTheme()));
        view.setBackgroundTintList(ColorStateList.valueOf(0xFFFF503C));
    }

    public void switchLocation(View view) {
    }

    public void searchBarriers(View view) {
        //dataHandler.getBarriersAsync(0, 0, radius);
        findViewById(R.id.search_progressbar).setVisibility(View.VISIBLE);
    }

}
