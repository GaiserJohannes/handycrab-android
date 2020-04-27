package de.dhbw.handycrab;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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
import de.dhbw.handycrab.model.Vote;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class DetailActivity extends AppCompatActivity {

    private Barrier activeBarrier;

    private TextView title;
    private TextView description;
    private TextView user;
    private TextView newSolution;
    private Button upvote;
    private Button downvote;

    private RecyclerView rv;

    @Inject
    IDataCache dataCache;

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    DataHelper dataHelper;

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
        description = findViewById(R.id.detail_description);
        user = findViewById(R.id.detail_user);
        upvote = findViewById(R.id.detail_barrier_upvote);
        downvote = findViewById(R.id.detail_barrier_downvote);
        newSolution = findViewById(R.id.detail_new_solution);

        rv = findViewById(R.id.detail_solution_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(llm);

        updateBarrier();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    private void updateBarrier() {
        title.setText(activeBarrier.getTitle());
        description.setText(activeBarrier.getDescription());

        String userName = dataHelper.getUsernameFromId(activeBarrier.getUserId());
        user.setText(userName);

        upvote.setText(String.format("%s", activeBarrier.getUpvotes()));
        downvote.setText(String.format("%s", activeBarrier.getDownvotes()));

        SolutionAdapter adapter = new SolutionAdapter(activeBarrier.getSolution());
        rv.setAdapter(adapter);
    }

    public void upvote(View view) {
        if (activeBarrier.getVote() == Vote.UP) {
            vote(Vote.NONE);
            upvote.setAlpha(0.5f);
        }
        else {
            vote(Vote.UP);
            downvote.setAlpha(0.5f);
            upvote.setAlpha(1.0f);
        }
    }

    public void downvote(View view) {
        if (activeBarrier.getVote() == Vote.DOWN) {
            vote(Vote.NONE);
            downvote.setAlpha(0.5f);
        }
        else {
            vote(Vote.DOWN);
            upvote.setAlpha(0.5f);
            downvote.setAlpha(1.0f);
        }
    }

    private void vote(Vote vote) {
        try {
            activeBarrier.setVote(vote);
            dataHandler.voteBarrierAsync(activeBarrier.getId(), vote).get();
        }
        catch (ExecutionException | InterruptedException e) {
            if (e.getCause() instanceof BackendConnectionException) {
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                dataHelper.showError(this, ex);
            }
            else {
                Toast.makeText(DetailActivity.this, getString(R.string.defaultError), Toast.LENGTH_SHORT).show();
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
            }
            catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof BackendConnectionException) {
                    BackendConnectionException ex = (BackendConnectionException) e.getCause();
                    dataHelper.showError(this, ex);
                }
                else {
                    Toast.makeText(DetailActivity.this, getString(R.string.defaultError), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
