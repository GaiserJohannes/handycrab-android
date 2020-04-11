package de.dhbw.handycrab;

import android.app.Application;
import de.dhbw.handycrab.helper.ApplicationGraph;
import de.dhbw.handycrab.helper.DaggerApplicationGraph;

public class Program extends Application {
    ApplicationGraph graph = DaggerApplicationGraph.create();

    @Override public void onCreate() {
        super.onCreate();
    }
}
