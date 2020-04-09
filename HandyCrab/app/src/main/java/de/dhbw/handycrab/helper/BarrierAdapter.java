package de.dhbw.handycrab.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.Barrier;

import java.util.List;

public class BarrierAdapter extends RecyclerView.Adapter<BarrierAdapter.BarrierViewHolder> {

    List<Barrier> barriers;

    public BarrierAdapter(List<Barrier> barriers) {
        this.barriers = barriers;
    }

    @NonNull
    @Override
    public BarrierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barrier_card, parent, false);
        BarrierViewHolder viewHolder = new BarrierViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BarrierViewHolder barrierViewHolder, int i) {
        barrierViewHolder.barrierTitle.setText(barriers.get(i).getTitle());
        barrierViewHolder.barrierDesc.setText(barriers.get(i).getDescription());
//        barrierViewHolder.barrierImage.setImageBitmap();
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
        public CardView cv;
        public TextView barrierTitle;
        public TextView barrierDesc;
        public ImageView barrierImage;

        public BarrierViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.list_barrier_card);
            barrierTitle = itemView.findViewById(R.id.barrier_title);
            barrierDesc = itemView.findViewById(R.id.barrier_desc);
            barrierImage = itemView.findViewById(R.id.barrier_image);
        }
    }

}
