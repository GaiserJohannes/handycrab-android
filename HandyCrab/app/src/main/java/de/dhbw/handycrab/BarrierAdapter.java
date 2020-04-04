package de.dhbw.handycrab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BarrierAdapter extends RecyclerView.Adapter<BarrierAdapter.BarrierViewHolder> {

    List<Barrier> barriers;

    BarrierAdapter(List<Barrier> barriers){
        this.barriers = barriers;
    }

    @NonNull
    @Override
    public BarrierViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.barrier_card, viewGroup, false);
        BarrierViewHolder pvh = new BarrierViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull BarrierViewHolder barrierViewHolder, int i) {
        barrierViewHolder.barrierTitle.setText(barriers.get(i).title);
        barrierViewHolder.barrierDesc.setText(barriers.get(i).desc);
    }

    @Override
    public int getItemCount() {
        return barriers.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class BarrierViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView barrierTitle;
        TextView barrierDesc;

        BarrierViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.list_barrier_card);
            barrierTitle = (TextView)itemView.findViewById(R.id.list_barrier_title);
            barrierDesc = (TextView)itemView.findViewById(R.id.list_barrier_desc);
        }
    }

}
