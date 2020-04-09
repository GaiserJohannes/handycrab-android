package de.dhbw.handycrab;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.dhbw.handycrab.helper.BarrierAdapter;
import de.dhbw.handycrab.helper.ServiceProvider;
import de.dhbw.handycrab.model.Barrier;

import java.util.List;

public class BarrierListActivity extends AppCompatActivity {

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
}
