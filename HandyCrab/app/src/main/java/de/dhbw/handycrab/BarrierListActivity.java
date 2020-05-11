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

import com.google.android.material.tabs.TabLayout;

import de.dhbw.handycrab.helper.BarrierDateComparator;
import de.dhbw.handycrab.helper.BarrierDistanceComparator;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.helper.VotableComparator;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.helper.ViewPagerAdapter;

import javax.inject.Inject;

import java.util.List;

public class BarrierListActivity extends AppCompatActivity {


    public static String ACTIVE_BARRIER = "de.dhbw.handycrab.ACTIVE_BARRIER";

    private List<Barrier> barriers;
    private ViewPagerAdapter sectionsPagerAdapter;

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

        barriers = (List<Barrier>) dataCache.retrieve(SearchActivity.BARRIER_LIST);
        Location searchLocation = (Location) dataCache.retrieve(SearchActivity.SEARCH_LOCATION);
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
            case R.id.action_sort_by_date:
                barriers.sort(new BarrierDateComparator());
                updateBarriers();
                return true;
            case R.id.action_sort_by_votes:
                barriers.sort(new VotableComparator());
                updateBarriers();
                return true;
            case R.id.action_sort_by_distance:
                barriers.sort(new BarrierDistanceComparator());
                updateBarriers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
