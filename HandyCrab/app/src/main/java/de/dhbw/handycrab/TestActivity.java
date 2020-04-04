package de.dhbw.handycrab;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initializeData();

        RecyclerView rv = findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getBaseContext());
        rv.setLayoutManager(llm);

        BarrierAdapter adapter = new BarrierAdapter(persons);
        rv.setAdapter(adapter);
    }

    private List<Barrier> persons;

    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData(){
        persons = new ArrayList<>();
        persons.add(new Barrier("Emma Wilson", "23 years old"));
        persons.add(new Barrier("Lavery Maiss", "25 years old"));
        persons.add(new Barrier("Lillie Watts", "35 years old"));
        persons.add(new Barrier("Lillie Watts", "35 years old"));
        persons.add(new Barrier("Lillie Watts", "35 years old"));
        persons.add(new Barrier("Lillie Watts", "35 years old"));
        persons.add(new Barrier("Lillie Watts", "35 years old"));
    }
}

class Barrier {
    String title;
    String desc;

    Barrier(String title, String desc) {
        this.title = title;
        this.desc = desc;
    }
}


