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
    private View.OnClickListener voteListener;

    public SolutionAdapter(List<Solution> solutions) {
        solutions.sort(new VotableComparator());
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

        switch (solutions.get(i).getVote()) {
            case UP:
                solutionViewHolder.upvote.setAlpha(1.0f);
                solutionViewHolder.downvote.setAlpha(0.5f);
                break;
            case DOWN:
                solutionViewHolder.downvote.setAlpha(1.0f);
                solutionViewHolder.upvote.setAlpha(0.5f);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return solutions.size();
    }

    public void setVoteListener(View.OnClickListener voteListener) {
        this.voteListener = voteListener;
    }

    public void setDataset(List<Solution> solutions) {
        solutions.sort(new VotableComparator());
        this.solutions = solutions;
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

            upvote.setTag(this);
            downvote.setTag(this);
            upvote.setOnClickListener(voteListener);
            downvote.setOnClickListener(voteListener);
        }
    }
}
