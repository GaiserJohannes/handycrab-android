package de.dhbw.handycrab.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import de.dhbw.handycrab.BarrierListActivity;
import de.dhbw.handycrab.DetailActivity;
import de.dhbw.handycrab.R;
import de.dhbw.handycrab.SearchActivity;
import de.dhbw.handycrab.helper.BarrierAdapter;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.Vote;

public class BarrierListFragment extends Fragment {


    @Inject
    IDataCache dataCache;
    private BarrierAdapter adapter;
    private List<Barrier> barriers;
    private RecyclerView recyclerView;

    private Consumer<Barrier> selectBarrier;

    public BarrierListFragment(List<Barrier> barriers, Consumer<Barrier> selectBarrier){
        this.barriers = barriers;
        this.selectBarrier = selectBarrier;
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();
            // viewHolder.getItemId();
            // viewHolder.getItemViewType();
            // viewHolder.itemView;
            Barrier thisItem = barriers.get(position);
            selectBarrier.accept(thisItem);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_barrier_list, container, false);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView = rootView.findViewById(R.id.barrier_list_rv);
        recyclerView.setLayoutManager(llm);
        recyclerView.setHasFixedSize(true);

        adapter = new BarrierAdapter(barriers);
        adapter.setClickListener(onItemClickListener);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    public void update(List<Barrier> barriers){
        if(adapter != null){
            adapter.updateBarriers(barriers);
        }
    }
}
