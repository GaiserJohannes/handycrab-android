package de.dhbw.handycrab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.dhbw.handycrab.helper.BarrierAdapter;
import de.dhbw.handycrab.helper.ServiceProvider;
import de.dhbw.handycrab.model.Barrier;

import java.util.List;

public class BarrierListActivity extends AppCompatActivity {

    public static String ACTIVE_BARRIER = "de.dhbw.handycrab.ACTIVE_BARRIER";

    private List<Barrier> barriers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrier_list);

        barriers = (List<Barrier>) ServiceProvider.DataHolder.retrieve(SearchActivity.BARRIER_KEY);

        RecyclerView rv = findViewById(R.id.barrier_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(llm);

        BarrierAdapter adapter = new BarrierAdapter(barriers);
        rv.setAdapter(adapter);

    }

    public void selectBarrier(View view) {
        ServiceProvider.DataHolder.store(ACTIVE_BARRIER, barriers.get(0));

        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
