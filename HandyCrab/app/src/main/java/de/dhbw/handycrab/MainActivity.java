package de.dhbw.handycrab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void login(View view){
        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        TextView error = (TextView) findViewById(R.id.errorMessage);

        if(username.getText().equals("joel") && password.getText().equals("1234")){

        }
        else{
            error.setVisibility(View.VISIBLE);
        }
    }
}
