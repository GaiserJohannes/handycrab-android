package de.dhbw.handycrab.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.Barrier;

import java.util.List;

import static java.lang.String.format;

public class BarrierAdapter extends RecyclerView.Adapter<BarrierAdapter.BarrierViewHolder> {
    private View.OnClickListener clickListener;
    private List<Barrier> barriers;

    public BarrierAdapter(List<Barrier> barriers) {
        this.barriers = barriers;
    }

    @NonNull
    @Override
    public BarrierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barrier_card, parent, false);
        return new BarrierViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BarrierViewHolder barrierViewHolder, int i) {
        barrierViewHolder.barrierTitle.setText(barriers.get(i).getTitle());
        String desc = barriers.get(i).getDescription();
        if (desc.length() > 200) {
            desc = desc.substring(0, 200) + "...";
        }
        barrierViewHolder.barrierDesc.setText(desc);
        barriers.get(i).setImageBitmapCallback((success, bitmap) -> {
            if(success){
                barrierViewHolder.barrierImage.setImageBitmap(bitmap);
            }
            else{
                barrierViewHolder.barrierImage.setImageResource(R.drawable.handycrab);
            }
        });
        if(barriers.get(i).getDistance() > 1000){
            barrierViewHolder.barrierDistance.setText(format(barrierViewHolder.itemView.getContext().getString(R.string.distance_km), barriers.get(i).getDistance()/1000));
        }
        else{
            barrierViewHolder.barrierDistance.setText(format(barrierViewHolder.itemView.getContext().getString(R.string.distance_m), barriers.get(i).getDistance()));
        }
    }

    @Override
    public int getItemCount() {
        return barriers.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void updateBarriers(List<Barrier> barriers){
        this.barriers = barriers;
        notifyDataSetChanged();
    }

    public class BarrierViewHolder extends RecyclerView.ViewHolder {
        public TextView barrierTitle;
        public TextView barrierDesc;
        public TextView barrierDistance;
        public ImageView barrierImage;

        public BarrierViewHolder(View itemView) {
            super(itemView);
            barrierTitle = itemView.findViewById(R.id.barrier_title);
            barrierDesc = itemView.findViewById(R.id.barrier_desc);
            barrierImage = itemView.findViewById(R.id.barrier_image);
            barrierDistance = itemView.findViewById(R.id.barrier_dist);
            itemView.setTag(this);
            itemView.setOnClickListener(clickListener);
        }
    }
}
