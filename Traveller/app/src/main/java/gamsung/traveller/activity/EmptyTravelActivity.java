package gamsung.traveller.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.BatchUpdateException;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;

public class EmptyTravelActivity extends AppCompatActivity {

    private int routeId = -1;
    private String routeTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_travel);

        this.routeId = getIntent().getIntExtra("route id",-1);
        this.routeTitle = getIntent().getStringExtra("route title");
        if(routeId > 0){
            TextView txtTravelName = (TextView) findViewById(R.id.textTitle);
            txtTravelName.setText(this.routeTitle);
        }
        else{
            //exception
            Log.e("empty travel activity", "get invalid route id");
            Toast.makeText(this, "error : get invalid route id", Toast.LENGTH_LONG).show();
        }


        //다음 화면
        View view = findViewById(R.id.layout_empty_travel);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

        //취소
        ImageButton btnCancel = (ImageButton) findViewById(R.id.btn_home_empty_travle);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
