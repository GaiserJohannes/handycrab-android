package de.dhbw.handycrab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.dhbw.handycrab.helper.BarrierAdapter;
import de.dhbw.handycrab.helper.IDataHolder;
import de.dhbw.handycrab.model.Barrier;

import javax.inject.Inject;
import java.util.List;

public class BarrierListActivity extends AppCompatActivity {

    public static String ACTIVE_BARRIER = "de.dhbw.handycrab.ACTIVE_BARRIER";

    private List<Barrier> barriers;

    @Inject
    IDataHolder dataHolder;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            // viewHolder.getItemId();
            // viewHolder.getItemViewType();
            // viewHolder.itemView;
            Barrier thisItem = barriers.get(position);

            dataHolder.store(ACTIVE_BARRIER, thisItem);

            selectBarrier();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Program) getApplicationContext()).graph.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrier_list);

        barriers = (List<Barrier>) dataHolder.retrieve(SearchActivity.BARRIER_KEY);

        RecyclerView rv = findViewById(R.id.barrier_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(llm);

        BarrierAdapter adapter = new BarrierAdapter(barriers);
        adapter.setClickListener(onItemClickListener);
        rv.setAdapter(adapter);
    }

    private void selectBarrier() {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
