package de.dhbw.handycrab.backend;

import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import de.dhbw.handycrab.Program;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.function.BiConsumer;

@Singleton
public class GeoLocationService {

    private FusedLocationProviderClient fusedLocationClient;

    @Inject
    public  GeoLocationService() {
        fusedLocationClient = new FusedLocationProviderClient(Program.getAppContext());
    }

    /**
     * gets the last known location and calls the callback function with the success state and the optional location
     * @param function
     */
    public void getLastLocationCallback(BiConsumer<Boolean, Location> function) {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(task -> function.accept(task.isSuccessful(), task.getResult()));
    }


}
