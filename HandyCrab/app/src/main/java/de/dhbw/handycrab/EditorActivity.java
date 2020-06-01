package de.dhbw.handycrab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.BackendConnector;
import de.dhbw.handycrab.backend.GeoLocationService;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.DataHelper;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.User;
import org.apache.commons.codec.binary.Base64;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EditorActivity extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 2;
    private static final int REQUEST_ACCESS_CAMERA = 3;
    public static final String NEW_BARRIER = "de.dhbw.handycrab.NEW_BARRIER";

    private ImageView imageView;

    private TextView title;
    private TextView description;
    private TextView zip;
    private TextView solution;

    private boolean new_barrier;
    private Barrier barrier;

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    IDataCache dataCache;

    @Inject
    DataHelper dataHelper;

    @Inject
    GeoLocationService locationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Bundle extras = getIntent().getExtras();
        new_barrier = extras == null || extras.getBoolean(NEW_BARRIER);

        title = findViewById(R.id.editor_title);
        description = findViewById(R.id.editor_description);
        zip = findViewById(R.id.editor_zip);
        solution = findViewById(R.id.editor_solution);
        imageView = findViewById(R.id.editor_image);

        if (!new_barrier) {
            barrier = (Barrier) dataCache.retrieve(BarrierListActivity.ACTIVE_BARRIER);
            User user = (User) dataCache.retrieve(LoginActivity.USER);
            if (!barrier.getUserId().equals(user.getId())) {
                finish();
                return;
            }
            fillContent();
            solution.setVisibility(View.GONE);
//            findViewById(R.id.editor_solution_label).setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            setTitle(R.string.title_barrier_edit);
        }
        else {
            imageView.setVisibility(View.GONE);
            setTitle(R.string.title_barrier_new);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fillContent() {
        title.setText(barrier.getTitle());
        description.setText(barrier.getDescription());
        imageView.setImageBitmap(barrier.getImageBitmap());
    }

    public void choosePicture(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission();
        }
        choosePicture();
    }

    private void choosePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_PIC_REQUEST);
        }
    }

    public void sendBarrier(View view) {
        Thread t = new Thread(() -> {
            try {
                if (!new_barrier) {
                    updateBarrier();
                }
                else {
                    addBarrier();
                }
            }
            catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof BackendConnectionException) {
                    BackendConnectionException ex = (BackendConnectionException) e.getCause();
                    Toast.makeText(this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
                }
            } catch (TimeoutException e) {
                Toast.makeText(this, getString(R.string.timeout), Toast.LENGTH_SHORT).show();
            }
        });
        t.start();
    }

    private void addBarrier() throws ExecutionException, InterruptedException, TimeoutException {
        if (title.getText() == null || title.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.missingTitle), Toast.LENGTH_LONG).show();
            return;
        }
        if (zip.getText() == null || zip.getText().toString().equals("")) {
            Toast.makeText(this, getString(R.string.missingZip), Toast.LENGTH_LONG).show();
            return;
        }
        if (imageView.getDrawable() == null && (description.getText() == null || description.getText().toString().equals(""))) {
            Toast.makeText(this, getString(R.string.missingDescription), Toast.LENGTH_LONG).show();
            return;
        }
        Location loc = locationService.getLastLocation(5000);
        if (loc == null) {
            Toast.makeText(this, getString(R.string.locationError), Toast.LENGTH_LONG).show();
            return;
        }

        String descText = description.getText() != null ? description.getText().toString() : "";
        String solutionText = solution.getText() != null ? solution.getText().toString() : "";
        Barrier barrier = dataHandler.addBarrierAsync(title.getText().toString(), loc.getLongitude(), loc.getLatitude(), getImageAsBase64(), descText, zip.getText().toString(), solutionText).get(BackendConnector.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        List<Barrier> barriers = (List<Barrier>) dataCache.retrieve(SearchActivity.BARRIER_LIST);
        barriers.add(barrier);

        finish();
    }

    private void updateBarrier() throws ExecutionException, InterruptedException, TimeoutException {
        barrier = dataHandler.modifyBarrierAsync(barrier.getId(), title.getText().toString(), getImageAsBase64(), description.getText().toString()).get(BackendConnector.TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        dataCache.store(BarrierListActivity.ACTIVE_BARRIER, barrier);
        dataHelper.replaceBarrierInList(barrier);

        finish();
    }

    public void cancel(View view) {
        finish();
    }

    private String getImageAsBase64() {
        if (imageView.getDrawable() != null) {
            BitmapDrawable bmapDraw = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = bmapDraw.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return new String(Base64.encodeBase64(stream.toByteArray()));
        }
        return "";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_PIC_REQUEST) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnail);
        }
    }

    public void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_ACCESS_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            choosePicture();
        }
    }
}
