package de.dhbw.handycrab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.dhbw.handycrab.helper.BarrierAdapter;
import de.dhbw.handycrab.helper.BarrierDateComparator;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.helper.VotableComparator;
import de.dhbw.handycrab.model.Barrier;

import javax.inject.Inject;
import java.util.List;

public class BarrierListActivity extends AppCompatActivity {

    public static String ACTIVE_BARRIER = "de.dhbw.handycrab.ACTIVE_BARRIER";

    private FloatingActionButton addButton;

    private BarrierAdapter adapter;
    private List<Barrier> barriers;
    private boolean userBarriers;

    @Inject
    IDataCache dataCache;

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            // viewHolder.getItemId();
            // viewHolder.getItemViewType();
            // viewHolder.itemView;
            Barrier thisItem = barriers.get(position);

            dataCache.store(ACTIVE_BARRIER, thisItem);

            selectBarrier();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barrier_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            userBarriers = savedInstanceState.getBoolean(SearchActivity.USER_BARRIERS);
        }
        else {
            Bundle extras = getIntent().getExtras();
            userBarriers = extras != null && extras.getBoolean(SearchActivity.USER_BARRIERS);
        }

        addButton = findViewById(R.id.add_barrier);

        RecyclerView recyclerView = findViewById(R.id.barrier_list_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(llm);

        if (userBarriers) {
            barriers = (List<Barrier>) dataCache.retrieve(SearchActivity.USER_BARRIERS);
            addButton.setVisibility(View.GONE);
        }
        else {
            barriers = (List<Barrier>) dataCache.retrieve(SearchActivity.BARRIER_LIST);
            addButton.setVisibility(View.VISIBLE);
        }

        adapter = new BarrierAdapter(barriers);
        adapter.setClickListener(onItemClickListener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateBarriers();
    }

    @SuppressWarnings("unchecked")
    private void updateBarriers() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_barrier_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_date_down:
                barriers.sort(new BarrierDateComparator());
                updateBarriers();
                return true;
            case R.id.action_sort_by_date_up:
                barriers.sort(new BarrierDateComparator().reversed());
                updateBarriers();
                return true;
            case R.id.action_sort_by_votes_down:
                barriers.sort(new VotableComparator());
                updateBarriers();
                return true;
            case R.id.action_sort_by_votes_up:
                barriers.sort(new VotableComparator().reversed());
                updateBarriers();
                return true;
            case R.id.action_sort_by_distance_down:
                return true;
            case R.id.action_sort_by_distance_up:
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(SearchActivity.USER_BARRIERS, userBarriers);

        super.onSaveInstanceState(savedInstanceState);
    }


    private void selectBarrier() {
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }

    public void addBarrier(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(EditorActivity.NEW_BARRIER, true);
        startActivity(intent);
    }
}
