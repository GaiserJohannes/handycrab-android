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
import de.dhbw.handycrab.model.*;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {

    private Barrier activeBarrier;

    private TextView title;
    private ImageView image;
    private TextView description;
    private TextView user;
    private TextView newSolution;
    private TextView distance;
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

            Solution solution = activeBarrier.getSolutions().get(position);

            applyVotes(solution, viewHolder.upvote, viewHolder.downvote, view);
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

        title = findViewById(R.id.detail_title);
        image = findViewById(R.id.detail_image);
        description = findViewById(R.id.detail_description);
        user = findViewById(R.id.detail_user);
        upvote = findViewById(R.id.detail_barrier_upvote);
        downvote = findViewById(R.id.detail_barrier_downvote);
        newSolution = findViewById(R.id.detail_new_solution);
        distance = findViewById(R.id.detail_distance);

        activeBarrier = (Barrier) dataCache.retrieve(BarrierListActivity.ACTIVE_BARRIER);

        RecyclerView recyclerView = findViewById(R.id.detail_solution_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(llm);

        adapter = new SolutionAdapter(activeBarrier.getSolutions());
        adapter.setVoteListener(onVoteListener);
        recyclerView.setAdapter(adapter);

        updateBarrier();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        User user = (User) dataCache.retrieve(LoginActivity.USER);
        if (activeBarrier.getUserId().equals(user.getId())) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
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
            case R.id.action_delete:
                // TODO delete Barrier
                dataHelper.deleteBarrierInList(activeBarrier);
                finish();
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        activeBarrier = (Barrier) dataCache.retrieve(BarrierListActivity.ACTIVE_BARRIER);
        updateBarrier();
    }

    private void updateBarrier() {
        title.setText(activeBarrier.getTitle());
        description.setText(activeBarrier.getDescription());

        String userName = dataHelper.getUsernameFromId(activeBarrier.getUserId());
        user.setText(userName);
        activeBarrier.setImageBitmapCallback((success, bitmap) -> {
            if (success) {
                image.setImageBitmap(bitmap);
            }
        });
        if (activeBarrier.getDistance() > 1000) {
            distance.setText(String.format(getString(R.string.distance_km), activeBarrier.getDistance() / 1000));
        }
        else {
            distance.setText(String.format(getString(R.string.distance_m), activeBarrier.getDistance()));
        }
        upvote.setText(String.format("%s", activeBarrier.getUpvotes()));
        downvote.setText(String.format("%s", activeBarrier.getDownvotes()));
        switch (activeBarrier.getVote()) {
            case UP:
                upvote.setAlpha(1.0f);
                downvote.setAlpha(0.5f);
                break;
            case DOWN:
                downvote.setAlpha(1.0f);
                upvote.setAlpha(0.5f);
                break;
        }
    }

    private void updateSolutions() {
        adapter.setDataset(activeBarrier.getSolutions());
        adapter.notifyDataSetChanged();
    }

    public void vote(View view) {
        applyVotes(activeBarrier, upvote, downvote, view);
    }

    private void applyVotes(Votable votable, Button upvote, Button downvote, View view) {
        switch (votable.getVote()) {
            case UP:
                if (view == upvote) {
                    voteVotable(votable, Vote.NONE);
                    votable.setUpvotes(votable.getUpvotes() - 1);
                    upvote.setAlpha(0.5f);
                }
                else {
                    voteVotable(votable, Vote.DOWN);
                    votable.setUpvotes(votable.getUpvotes() - 1);
                    votable.setDownvotes(votable.getDownvotes() + 1);
                    upvote.setAlpha(0.5f);
                    downvote.setAlpha(1.0f);
                }
                break;
            case DOWN:
                if (view == downvote) {
                    voteVotable(votable, Vote.NONE);
                    votable.setDownvotes(votable.getDownvotes() - 1);
                    downvote.setAlpha(0.5f);
                }
                else {
                    voteVotable(votable, Vote.UP);
                    votable.setUpvotes(votable.getUpvotes() + 1);
                    votable.setDownvotes(votable.getDownvotes() - 1);
                    downvote.setAlpha(0.5f);
                    upvote.setAlpha(1.0f);
                }
                break;
            default:
                if (view == upvote) {
                    voteVotable(votable, Vote.UP);
                    votable.setUpvotes(votable.getUpvotes() + 1);
                    upvote.setAlpha(1.0f);
                }
                else {
                    voteVotable(votable, Vote.DOWN);
                    votable.setDownvotes(votable.getDownvotes() + 1);
                    downvote.setAlpha(1.0f);
                }
                break;
        }
        upvote.setText(String.format("%s", votable.getUpvotes()));
        downvote.setText(String.format("%s", votable.getDownvotes()));
    }

    private void voteVotable(Votable votable, Vote vote) {
        try {
            votable.setVote(vote);
            if (votable instanceof Barrier) {
                dataHandler.voteBarrierAsync(((Barrier) votable).getId(), vote).get();
            }
            else if (votable instanceof Solution) {
                dataHandler.voteBarrierAsync(((Solution) votable).getId(), vote).get();
            }
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
                dataHelper.replaceBarrierInList(activeBarrier);
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
