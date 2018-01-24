package gamsung.traveller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import gamsung.traveller.R;

public class TravelViewActivity extends AppCompatActivity {

    /**
     * 준규가 다 만들어줄 9,13화면
     */

    public Button btnAddLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_view);

        btnAddLocation = findViewById(R.id.btnAddLocation);
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TravelViewActivity.this, EditLocationActivity.class);
                startActivity(i);
            }
        });

    }
}
