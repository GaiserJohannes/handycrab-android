package de.dhbw.handycrab.helper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.dhbw.handycrab.R;
import de.dhbw.handycrab.model.Solution;

import java.util.List;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.SolutionViewHolder> {
    private List<Solution> solutions;

    public SolutionAdapter(List<Solution> solutions) {
        this.solutions = solutions;
    }

    @NonNull
    @Override
    public SolutionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.solution_card, parent, false);
        return new SolutionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SolutionViewHolder solutionViewHolder, int i) {
        solutionViewHolder.solutionUser.setText(solutions.get(i).getUserID().toString());
        solutionViewHolder.solutionText.setText(solutions.get(i).getText());
        solutionViewHolder.upvote.setText(String.format("%s", solutions.get(i).getUpvotes()));
        solutionViewHolder.downvote.setText(String.format("%s", solutions.get(i).getDownvotes()));
    }

    @Override
    public int getItemCount() {
        return solutions.size();
    }

    public class SolutionViewHolder extends RecyclerView.ViewHolder {
        public TextView solutionUser;
        public TextView solutionText;
        public Button upvote;
        public Button downvote;

        public SolutionViewHolder(View itemView) {
            super(itemView);
            solutionUser = itemView.findViewById(R.id.solution_user);
            solutionText = itemView.findViewById(R.id.solution_text);
            upvote = itemView.findViewById(R.id.solution_upvote);
            downvote = itemView.findViewById(R.id.solution_downvote);
        }
    }

}
