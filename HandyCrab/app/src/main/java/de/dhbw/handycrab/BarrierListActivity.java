package de.dhbw.handycrab;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import de.dhbw.handycrab.helper.*;
import de.dhbw.handycrab.model.Barrier;

import javax.inject.Inject;
import java.util.List;

public class BarrierListActivity extends AppCompatActivity {


    public static String ACTIVE_BARRIER = "de.dhbw.handycrab.ACTIVE_BARRIER";

    private FloatingActionButton addButton;

    private List<Barrier> barriers;
    private ViewPagerAdapter sectionsPagerAdapter;
    private boolean userBarriers;

    @Inject
    IDataCache dataCache;


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

        Location searchLocation = null;
        if (userBarriers) {
            barriers = (List<Barrier>) dataCache.retrieve(SearchActivity.USER_BARRIERS);
            addButton.setVisibility(View.GONE);
        }
        else {
            barriers = (List<Barrier>) dataCache.retrieve(SearchActivity.BARRIER_LIST);
            Object value = dataCache.retrieve(SearchActivity.SEARCH_LOCATION);
            if(value instanceof Location){
                searchLocation = (Location) value;
            }
            addButton.setVisibility(View.VISIBLE);
        }

        sectionsPagerAdapter = new ViewPagerAdapter(this, getSupportFragmentManager(), barriers, searchLocation, this::selectBarrier);

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.barrierlist_tabLayout);
        tabs.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBarriers();
    }

    public void updateBarriers(){
        sectionsPagerAdapter.updateBarriers(barriers);
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
                barriers.sort(new BarrierDistanceComparator());
                updateBarriers();
                return true;
            case R.id.action_sort_by_distance_up:
                barriers.sort(new BarrierDistanceComparator().reversed());
                updateBarriers();
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

    private void selectBarrier(Barrier selectedBarrier) {
        dataCache.store(BarrierListActivity.ACTIVE_BARRIER, selectedBarrier);
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }

    public void addBarrier(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra(EditorActivity.NEW_BARRIER, true);
        startActivity(intent);
    }
}
