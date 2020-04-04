package de.dhbw.handycrab;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    public void switchRadius(View view) {
        findViewById(R.id.radius1).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        findViewById(R.id.radius2).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        findViewById(R.id.radius3).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        view.setBackgroundTintList(ColorStateList.valueOf(0xFFFF503C));
    }

    public void switchLocation(View view) {
        findViewById(R.id.radius1).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        findViewById(R.id.radius2).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        findViewById(R.id.radius3).setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        view.setBackgroundTintList(ColorStateList.valueOf(0xFFFF503C));
    }

    public void searchBarriers(View view) {

    }

}
