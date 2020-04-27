package de.dhbw.handycrab;

import android.app.Application;
import android.content.Context;
import de.dhbw.handycrab.helper.ApplicationGraph;
import de.dhbw.handycrab.helper.DaggerApplicationGraph;

public class Program extends Application {
    private static Context context;
    private static ApplicationGraph graph;

    @Override
    public void onCreate() {
        super.onCreate();
        Program.context = getApplicationContext();

        graph = DaggerApplicationGraph.create();
    }

    public static Context getAppContext() {
        return Program.context;
    }

    public static ApplicationGraph getApplicationGraph() {
        return Program.graph;
    }
}
