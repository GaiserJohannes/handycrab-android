package de.dhbw.handycrab;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.DataHelper;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.helper.SolutionAdapter;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.Solution;
import de.dhbw.handycrab.model.Vote;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {

    private Barrier activeBarrier;

    private TextView title;
    private ImageView image;
    private TextView description;
    private TextView user;
    private TextView newSolution;
    private Button upvote;
    private Button downvote;

    private SolutionAdapter adapter;

    @Inject
    IDataCache dataCache;

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    DataHelper dataHelper;

    private final View.OnClickListener onVoteListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SolutionAdapter.SolutionViewHolder viewHolder = (SolutionAdapter.SolutionViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Solution solution = activeBarrier.getSolution().get(position);

            switch (solution.getVote()) {
                case UP:
                    if (view == viewHolder.upvote) {
                        voteSolution(solution, Vote.NONE);
                        solution.setUpvotes(solution.getUpvotes() - 1);
                        viewHolder.upvote.setAlpha(0.5f);
                    }
                    else {
                        voteSolution(solution, Vote.DOWN);
                        solution.setUpvotes(solution.getUpvotes() - 1);
                        solution.setDownvotes(solution.getDownvotes() + 1);
                        viewHolder.upvote.setAlpha(0.5f);
                        viewHolder.downvote.setAlpha(1.0f);
                    }
                    break;
                case DOWN:
                    if (view == viewHolder.downvote) {
                        voteSolution(solution, Vote.NONE);
                        solution.setDownvotes(solution.getDownvotes() - 1);
                        viewHolder.downvote.setAlpha(0.5f);
                    }
                    else {
                        voteSolution(solution, Vote.UP);
                        solution.setUpvotes(solution.getUpvotes() + 1);
                        solution.setDownvotes(solution.getDownvotes() - 1);
                        viewHolder.downvote.setAlpha(0.5f);
                        viewHolder.upvote.setAlpha(1.0f);
                    }
                    break;
                default:
                    if (view == viewHolder.upvote) {
                        voteSolution(solution, Vote.UP);
                        solution.setUpvotes(solution.getUpvotes() + 1);
                        viewHolder.upvote.setAlpha(1.0f);
                    }
                    else {
                        voteSolution(solution, Vote.DOWN);
                        solution.setDownvotes(solution.getDownvotes() + 1);
                        viewHolder.downvote.setAlpha(1.0f);
                    }
                    break;
            }
            viewHolder.upvote.setText(String.format("%s", solution.getUpvotes()));
            viewHolder.downvote.setText(String.format("%s", solution.getDownvotes()));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        activeBarrier = (Barrier) dataCache.retrieve(BarrierListActivity.ACTIVE_BARRIER);

        title = findViewById(R.id.detail_title);
        image = findViewById(R.id.detail_image);
        description = findViewById(R.id.detail_description);
        user = findViewById(R.id.detail_user);
        upvote = findViewById(R.id.detail_barrier_upvote);
        downvote = findViewById(R.id.detail_barrier_downvote);
        newSolution = findViewById(R.id.detail_new_solution);

        RecyclerView recyclerView = findViewById(R.id.detail_solution_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(llm);

        adapter = new SolutionAdapter(activeBarrier.getSolution());
        adapter.setVoteListener(onVoteListener);
        recyclerView.setAdapter(adapter);

        updateBarrier();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent intent = new Intent(this, EditorActivity.class);
                intent.putExtra(EditorActivity.NEW_BARRIER, false);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateBarrier() {
        title.setText(activeBarrier.getTitle());
        description.setText(activeBarrier.getDescription());
        image.setImageResource(R.drawable.barrier);

        String userName = dataHelper.getUsernameFromId(activeBarrier.getUserId());
        user.setText(userName);

        upvote.setText(String.format("%s", activeBarrier.getUpvotes()));
        downvote.setText(String.format("%s", activeBarrier.getDownvotes()));
    }

    private void updateSolutions() {
        adapter.setDataset(activeBarrier.getSolution());
        adapter.notifyDataSetChanged();
    }

    public void vote(View view) {
        switch (activeBarrier.getVote()) {
            case UP:
                if (view == upvote) {
                    voteBarrier(Vote.NONE);
                    activeBarrier.setUpvotes(activeBarrier.getUpvotes() - 1);
                    upvote.setAlpha(0.5f);
                }
                else {
                    voteBarrier(Vote.DOWN);
                    activeBarrier.setUpvotes(activeBarrier.getUpvotes() - 1);
                    activeBarrier.setDownvotes(activeBarrier.getDownvotes() + 1);
                    upvote.setAlpha(0.5f);
                    downvote.setAlpha(1.0f);
                }
                break;
            case DOWN:
                if (view == downvote) {
                    voteBarrier(Vote.NONE);
                    activeBarrier.setDownvotes(activeBarrier.getDownvotes() - 1);
                    downvote.setAlpha(0.5f);
                }
                else {
                    voteBarrier(Vote.UP);
                    activeBarrier.setUpvotes(activeBarrier.getUpvotes() + 1);
                    activeBarrier.setDownvotes(activeBarrier.getDownvotes() - 1);
                    downvote.setAlpha(0.5f);
                    upvote.setAlpha(1.0f);
                }
                break;
            default:
                if (view == upvote) {
                    voteBarrier(Vote.UP);
                    activeBarrier.setUpvotes(activeBarrier.getUpvotes() + 1);
                    upvote.setAlpha(1.0f);
                }
                else {
                    voteBarrier(Vote.DOWN);
                    activeBarrier.setDownvotes(activeBarrier.getDownvotes() + 1);
                    downvote.setAlpha(1.0f);
                }
                break;
        }
        upvote.setText(String.format("%s", activeBarrier.getUpvotes()));
        downvote.setText(String.format("%s", activeBarrier.getDownvotes()));
    }

    private void voteBarrier(Vote vote) {
        try {
            activeBarrier.setVote(vote);
            dataHandler.voteBarrierAsync(activeBarrier.getId(), vote).get();
        }
        catch (ExecutionException | InterruptedException e) {
            if (e.getCause() instanceof BackendConnectionException) {
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                Toast.makeText(DetailActivity.this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(DetailActivity.this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void voteSolution(Solution solution, Vote vote) {
        try {
            solution.setVote(vote);
            dataHandler.voteBarrierAsync(solution.getId(), vote).get();
        }
        catch (ExecutionException | InterruptedException e) {
            if (e.getCause() instanceof BackendConnectionException) {
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                Toast.makeText(DetailActivity.this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(DetailActivity.this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addSolution(View view) {
        String solution = newSolution.getText().toString();
        if (solution.trim().length() > 0) {
            try {
                activeBarrier = dataHandler.addSolutionAsync(activeBarrier.getId(), null).get();
                dataCache.store(BarrierListActivity.ACTIVE_BARRIER, activeBarrier);
                updateBarrier();
                updateSolutions();
                newSolution.setText("");
            }
            catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof BackendConnectionException) {
                    BackendConnectionException ex = (BackendConnectionException) e.getCause();
                    Toast.makeText(DetailActivity.this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DetailActivity.this, getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
