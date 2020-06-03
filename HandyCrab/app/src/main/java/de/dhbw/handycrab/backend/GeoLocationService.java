package de.dhbw.handycrab.backend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import de.dhbw.handycrab.Program;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.BiConsumer;

@Singleton
public class GeoLocationService {

    private FusedLocationProviderClient fusedLocationClient;

    @Inject
    public GeoLocationService() {
        fusedLocationClient = new FusedLocationProviderClient(Program.getAppContext());
    }

    /**
     * gets the last known location and calls the callback function with the success state and the optional location
     * <p>
     * if success is true the location can still be null!
     *
     * @param function
     */
    public void getLastLocationCallback(BiConsumer<Boolean, Location> function) {
        if (!isLocationPermissionGranted()) {
            function.accept(false, null);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> function.accept(task.isSuccessful(), task.getResult()));
    }

    /**
     * checks if the app has the permission to retrieve the geo location
     *
     * @return whether the permission is granted or not
     */
    public boolean isLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(Program.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
