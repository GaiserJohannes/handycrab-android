package de.dhbw.handycrab;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import de.dhbw.handycrab.helper.IDataHolder;
import de.dhbw.handycrab.model.Barrier;

import javax.inject.Inject;

public class DetailActivity extends AppCompatActivity {

    private Barrier activeBarrier;

    @Inject
    IDataHolder dataHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Program) getApplicationContext()).graph.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        activeBarrier = (Barrier) dataHolder.retrieve(BarrierListActivity.ACTIVE_BARRIER);

        ((TextView) findViewById(R.id.detail_title)).setText(activeBarrier.getTitle());
        ((TextView) findViewById(R.id.detail_description)).setText(activeBarrier.getDescription());
        ((TextView) findViewById(R.id.detail_user)).setText(String.format("%s", activeBarrier.getUserId()));
    }

}
