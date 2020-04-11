package de.dhbw.handycrab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.concurrent.ExecutionException;

import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.BackendConnector;
import de.dhbw.handycrab.model.ErrorCode;
import de.dhbw.handycrab.model.User;

public class LoginActivity extends AppCompatActivity {

    private TextView username;
    private TextView email;
    private TextView password;
    private Button submit;
    private TabLayout tabLayout;

    private BackendConnector backendConnector = new BackendConnector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        email = (TextView) findViewById(R.id.mail);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        submit = (Button) findViewById(R.id.submit);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                submit.setText(tab.getText());
                //login
                if(tab.getPosition() == 0){
                    email.setVisibility(View.INVISIBLE);
                    username.setHint(getString(R.string.usernameOrEmail));
                }
                else{
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

    public void submit(View view){
        if(tabLayout.getSelectedTabPosition() == 0){
            login(view);
        }
        else{
            register(view);
        }
    }

    public void login(View view){
        User user = null;
        try {
            user = backendConnector.loginAsync(username.getText().toString(), password.getText().toString()).get();
            //todo go to barriersearchview
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                if(ex.getErrorCode() == ErrorCode.NO_CONNECTION_TO_SERVER){
                    Toast.makeText(LoginActivity.this, getString(R.string.noConnectionToServerError), Toast.LENGTH_SHORT).show();
                }
                else if(ex.getErrorCode() == ErrorCode.INCOMPLIETE){
                    Toast.makeText(LoginActivity.this, getString(R.string.incompleteError), Toast.LENGTH_SHORT).show();
                }
                else if(ex.getErrorCode() == ErrorCode.INVALID_LOGIN){
                    Toast.makeText(LoginActivity.this, getString(R.string.invalidLoginError), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void register(View view){
        User user = null;
        try {
            user = backendConnector.registerAsync(email.getText().toString(), username.getText().toString(), password.getText().toString()).get();
            //todo go to barriersearchview
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                BackendConnectionException ex = (BackendConnectionException) e.getCause();
                if(ex.getErrorCode() == ErrorCode.NO_CONNECTION_TO_SERVER){
                    Toast.makeText(LoginActivity.this, getString(R.string.noConnectionToServerError), Toast.LENGTH_SHORT).show();
                }
                else if(ex.getErrorCode() == ErrorCode.INCOMPLIETE){
                    Toast.makeText(LoginActivity.this, getString(R.string.incompleteError), Toast.LENGTH_SHORT).show();
                }
                else if(ex.getErrorCode() == ErrorCode.EMAIL_ALREADY_ASSIGNED){
                    Toast.makeText(LoginActivity.this, getString(R.string.emailAlreadyAssignedError), Toast.LENGTH_SHORT).show();
                }
                else if(ex.getErrorCode() == ErrorCode.USERNAME_ALREADY_ASSIGNED){
                    Toast.makeText(LoginActivity.this, getString(R.string.usernameAlreadyAssignedError), Toast.LENGTH_SHORT).show();
                }
                else if(ex.getErrorCode() == ErrorCode.INVALID_USERNAME){
                    Toast.makeText(LoginActivity.this, getString(R.string.invalidUsernameError), Toast.LENGTH_SHORT).show();
                }
                else if(ex.getErrorCode() == ErrorCode.INVALID_EMAIL){
                    Toast.makeText(LoginActivity.this, getString(R.string.invalidEmailError), Toast.LENGTH_SHORT).show();
                }
                else if(ex.getErrorCode() == ErrorCode.INVALID_PASSWORD){
                    Toast.makeText(LoginActivity.this, getString(R.string.invalidPasswordError), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
