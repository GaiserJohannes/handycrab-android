package de.dhbw.handycrab;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.GeoLocationService;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.DataHelper;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.model.Barrier;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EditorActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private TextView solution;

    @Inject
    DataHelper dataHelper;

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    IDataCache dataCache;

    @Inject
    GeoLocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        title = findViewById(R.id.editor_title);
        description = findViewById(R.id.editor_description);
        solution = findViewById(R.id.editor_solution);
    }

    public void sendBarrier(View view) {
        Thread t = new Thread(() -> {
            Location loc = locationService.getLastLocation(5000);
            if (loc == null) {
                Toast.makeText(this, getString(R.string.locationError), Toast.LENGTH_LONG).show();
                return;
            }

            try {
                Barrier barrier = dataHandler.addBarrierAsync(title.getText().toString(), loc.getLongitude(), loc.getLatitude(), "", description.getText().toString(), "", null).get();
                List<Barrier> barriers = (List<Barrier>) dataCache.retrieve(SearchActivity.BARRIER_KEY);
                barriers.add(barrier);
                dataCache.store(SearchActivity.BARRIER_KEY, barriers);

                finish();
            }
            catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof BackendConnectionException) {
                    BackendConnectionException ex = (BackendConnectionException) e.getCause();
                    dataHelper.showError(this, ex);
                }
                else {
                    Toast.makeText(this, getString(R.string.defaultError), Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
    }

    public void cancel(View view) {
        finish();
    }
}
