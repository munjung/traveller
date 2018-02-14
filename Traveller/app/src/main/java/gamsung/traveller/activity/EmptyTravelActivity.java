package gamsung.traveller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;

public class EmptyTravelActivity extends AppCompatActivity {

    TextView travelName;
    DataManager _dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_travel);

        int routeId = getIntent().getIntExtra("RouteId",0);

        _dataManager = DataManager.getInstance(this);
        String travelTitle = _dataManager.getRouteList().get(routeId).getTitle().toString();

        View view = findViewById(R.id.layout_empty_travel);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(EmptyTravelActivity.this, EditLocationActivity.class);
                i.putExtra("TAG_ACTIVITY", "create");
                startActivity(i);
            }
        });

        travelName = (TextView) findViewById(R.id.textTitle);
        travelName.setText(travelTitle);

    }
}
