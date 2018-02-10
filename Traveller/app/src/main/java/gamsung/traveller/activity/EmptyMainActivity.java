package gamsung.traveller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import gamsung.traveller.R;

/**
 * Created by shin on 2018. 2. 10..
 */

public class EmptyMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_main);

        View view = findViewById(R.id.layout_empty_main);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EmptyMainActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }
}
