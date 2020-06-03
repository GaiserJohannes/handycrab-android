package de.dhbw.handycrab.helper;

import android.content.Context;
import android.location.Location;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.List;
import java.util.function.Consumer;

import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.view.BarrierListFragment;
import de.dhbw.handycrab.view.HandyCrabMapFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter implements OnMapReadyCallback {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.barrier_list, R.string.map};
    private final Context mContext;
    private List<Barrier> barriers;

    private HandyCrabMapFragment mapFragment;
    private BarrierListFragment listFragment;

    public ViewPagerAdapter(Context context, FragmentManager fm, List<Barrier> barriers, Location searchLocation, Consumer<Barrier> selectBarrier) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mContext = context;
        this.barriers = barriers;
        mapFragment = new HandyCrabMapFragment(selectBarrier);
        mapFragment.getMapAsync(this);
        mapFragment.setMarkerBarriers(barriers);
        if(searchLocation != null){
            mapFragment.setLocation(searchLocation, 15, context.getString(R.string.search_center));
        }
        listFragment = new BarrierListFragment(barriers, selectBarrier);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 1){
            return mapFragment;
        }
        return listFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    public void updateBarriers(List<Barrier> barriers){
        listFragment.update(barriers);
        mapFragment.setMarkerBarriers(barriers);
        mapFragment.updateMarker();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(barriers.size() > 0){
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        }
    }
}