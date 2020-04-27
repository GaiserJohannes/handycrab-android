package de.dhbw.handycrab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.model.User;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    public static String USER = "de.dhbw.handycrab.USER";
    private TextView username;
    private TextView email;
    private TextView password;
    private Button submit;
    private TabLayout tabLayout;

    @Inject
    IDataCache dataHolder;

    @Inject
    IHandyCrabDataHandler backendConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Program.getApplicationGraph().inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        email = findViewById(R.id.mail);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                submit.setText(tab.getText());
                //login
                if (tab.getPosition() == 0) {
                    email.setVisibility(View.INVISIBLE);
                    username.setHint(getString(R.string.usernameOrEmail));
                }
                //register
                else {
                    email.setVisibility(View.VISIBLE);
                    username.setHint(getString(R.string.username));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void submit(View view) {
        try {
            User user = null;
            if (tabLayout.getSelectedTabPosition() == 0) {
                user = backendConnector.loginAsync(username.getText().toString(), password.getText().toString()).get();
            }
            else {
                user = backendConnector.registerAsync(email.getText().toString(), username.getText().toString(), password.getText().toString()).get();
            }
            dataHolder.store(USER, user);
            successLogin();
        }
        catch (ExecutionException e) {
            if (e.getCause() instanceof BackendConnectionException) {
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                Toast.makeText(this, ex.getDetailedMessage(this), Toast.LENGTH_SHORT).show();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void successLogin() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }
}
