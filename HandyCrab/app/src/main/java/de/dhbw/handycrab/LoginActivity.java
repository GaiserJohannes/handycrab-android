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
import de.dhbw.handycrab.model.User;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private TextView username;
    private TextView email;
    private TextView password;
    private Button submit;
    private TabLayout tabLayout;

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
        if (tabLayout.getSelectedTabPosition() == 0) {
            login(view);
        }
        else {
            register(view);
        }
    }

    public void login(View view) {
        User user = null;
        try {
            user = backendConnector.loginAsync(username.getText().toString(), password.getText().toString()).get();
            // TODO store user? -> DataHolder inject
            successLogin();
        }
        catch (ExecutionException e) {
            if (e.getCause() instanceof BackendConnectionException) {
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                switch (ex.getErrorCode()) {
                    case NO_CONNECTION_TO_SERVER:
                        Toast.makeText(LoginActivity.this, getString(R.string.noConnectionToServerError), Toast.LENGTH_SHORT).show();
                        break;
                    case INCOMPLETE:
                        Toast.makeText(LoginActivity.this, getString(R.string.incompleteError), Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_LOGIN:
                        Toast.makeText(LoginActivity.this, getString(R.string.invalidLoginError), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void register(View view) {
        User user = null;
        try {
            user = backendConnector.registerAsync(email.getText().toString(), username.getText().toString(), password.getText().toString()).get();
            // TODO store User? -> inject DataHolder
            successLogin();
        }
        catch (ExecutionException e) {
            if (e.getCause() instanceof BackendConnectionException) {
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                switch (ex.getErrorCode()) {
                    case NO_CONNECTION_TO_SERVER:
                        Toast.makeText(LoginActivity.this, getString(R.string.noConnectionToServerError), Toast.LENGTH_SHORT).show();
                        break;
                    case INCOMPLETE:
                        Toast.makeText(LoginActivity.this, getString(R.string.incompleteError), Toast.LENGTH_SHORT).show();
                        break;
                    case EMAIL_ALREADY_ASSIGNED:
                        Toast.makeText(LoginActivity.this, getString(R.string.emailAlreadyAssignedError), Toast.LENGTH_SHORT).show();
                        break;
                    case USERNAME_ALREADY_ASSIGNED:
                        Toast.makeText(LoginActivity.this, getString(R.string.usernameAlreadyAssignedError), Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_USERNAME:
                        Toast.makeText(LoginActivity.this, getString(R.string.invalidUsernameError), Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_EMAIL:
                        Toast.makeText(LoginActivity.this, getString(R.string.invalidEmailError), Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_PASSWORD:
                        Toast.makeText(LoginActivity.this, getString(R.string.invalidPasswordError), Toast.LENGTH_SHORT).show();
                        break;
                }
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
