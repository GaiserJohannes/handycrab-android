package de.dhbw.handycrab;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
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
    private Button upvote;
    private Button downvote;

    @Inject
    IDataCache dataCache;

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        activeBarrier = (Barrier) dataCache.retrieve(BarrierListActivity.ACTIVE_BARRIER);

        title = findViewById(R.id.detail_title);
        description = findViewById(R.id.detail_description);
        user = findViewById(R.id.detail_user);
        upvote = findViewById(R.id.detail_barrier_upvote);
        downvote = findViewById(R.id.detail_barrier_downvote);

        title.setText(activeBarrier.getTitle());
        description.setText(activeBarrier.getDescription());
        user.setText(String.format("%s", activeBarrier.getUserId()));
        upvote.setText(String.format("%s", activeBarrier.getUpvotes()));
        downvote.setText(String.format("%s", activeBarrier.getDownvotes()));

        RecyclerView rv = findViewById(R.id.detail_solution_rv);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(llm);

        SolutionAdapter adapter = new SolutionAdapter(activeBarrier.getSolution());
        rv.setAdapter(adapter);
    }

    public void upvote(View view) {
        vote(Vote.UP);
    }

    public void downvote(View view) {
        vote(Vote.DOWN);
    }

    private void vote(Vote vote) {
        try {
            dataHandler.voteBarrierAsync(activeBarrier.getId(), vote).get();
        }
        catch (ExecutionException e) {
            if (e.getCause() instanceof BackendConnectionException) {
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                switch (ex.getErrorCode()) {
                    case NO_CONNECTION_TO_SERVER:
                        Toast.makeText(DetailActivity.this, getString(R.string.noConnectionToServerError), Toast.LENGTH_SHORT).show();
                        break;
                    case BARRIER_NOT_FOUND:
                        Toast.makeText(DetailActivity.this, getString(R.string.barrierNotFound), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(DetailActivity.this, getString(R.string.defaultError), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            else {
                Toast.makeText(DetailActivity.this, getString(R.string.defaultError), Toast.LENGTH_SHORT).show();
            }
        }
        catch (InterruptedException e) {
            Toast.makeText(DetailActivity.this, getString(R.string.defaultError), Toast.LENGTH_SHORT).show();
        }
    }
}
