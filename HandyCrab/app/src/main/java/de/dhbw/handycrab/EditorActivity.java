package de.dhbw.handycrab;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.GeoLocationService;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.model.Barrier;
import org.apache.commons.codec.binary.Base64;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EditorActivity extends AppCompatActivity {

    private static final int CAMERA_PIC_REQUEST = 2;
    private static final int REQUEST_ACCESS_CAMERA = 3;

    private ImageView imageView;

    private TextView title;
    private TextView description;
    private TextView solution;

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
        imageView = findViewById(R.id.editor_image);
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
            Location loc = locationService.getLastLocation(5000);
            if (loc == null) {
                Toast.makeText(this, getString(R.string.locationError), Toast.LENGTH_LONG).show();
                return;
            }

            try {
                Barrier barrier = dataHandler.addBarrierAsync(title.getText().toString(), loc.getLongitude(), loc.getLatitude(), "", description.getText().toString(), "", null).get();
                List<Barrier> barriers = (List<Barrier>) dataCache.retrieve(SearchActivity.BARRIER_KEY);
                barriers.add(barrier);

                finish();
            }
            catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof BackendConnectionException) {
                    BackendConnectionException ex = (BackendConnectionException) e.getCause();
                    Toast.makeText(this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
                }
            }
        });
        t.start();
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
