package gamsung.traveller.activity;

import android.graphics.drawable.GradientDrawable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Route;
import gamsung.traveller.util.DebugToast;

/*
 * 네신중평님이 마법을 부릴 2,3번 화면
 */


public class MainActivity extends AppCompatActivity {


    private ViewGroup _mainContentsLayout;

    private RecyclerView _recyclerView;
    private RecyclerViewAdapter _recyclerAdapter;

    private DataManager _dataManager;
    private List<Route> _routeList;

    final int PERMISSION_CODE = 0;
    public Button btnAddTravel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //data load
        _dataManager = DataManager.getInstance(this);

        this.set_recyclerView();
        this.setRegisterEvent();
    }

    private void set_recyclerView(){

        _routeList = new ArrayList<Route>(_dataManager.getRouteList().values());
        _recyclerAdapter = new RecyclerViewAdapter(_routeList);

        _recyclerView = (RecyclerView)findViewById(R.id.recycler_history);
        _recyclerView.setAdapter(_recyclerAdapter);
        _recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void setRegisterEvent(){

        Button btn = (Button)findViewById(R.id.btn_sample);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Route route = new Route();
                route.setTitle("temp");

                _recyclerAdapter.addItem(route);

                DebugToast.show(MainActivity.this, "add");
            }
        });
    }
}




class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RouteViewHolder> {

    private List<Route> _items;

    public RecyclerViewAdapter(List<Route> routeList) {

        if (routeList == null) {
            throw new IllegalArgumentException("route data must not be null");
        }

        this._items = routeList;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.view_main_route, viewGroup, false);

        return new RouteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder viewHolder, int position) {

        Route item = _items.get(position);
        viewHolder.label.setText(item.getTitle());
//        viewHolder.image.setBackgroundResource(item.getPictureId());
        viewHolder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    public void addItem(Route route){

        if(_items.add(route))
            notifyDataSetChanged();
    }

    public void removeItem(int position){
        _items.remove(position);
        notifyDataSetChanged();
    }

    public Route getItem(int position){
        return _items.get(position);
    }


    public static class RouteViewHolder extends RecyclerView.ViewHolder{

        public ImageView image;
        public TextView label;

        public RouteViewHolder(View itemView){
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.img_history);
            label = (TextView) itemView.findViewById(R.id.tv_history);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });
        tryPermCheck();

        btnAddTravel = (Button) findViewById(R.id.btnAddTravel);
        btnAddTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, TravelViewActivity.class);
                startActivity(i);
            }
        });

    }

    //PERMISSON
    public void tryPermCheck(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED
                        && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //1
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
                    alert.setMessage("권한 설정이 필요합니다.");
                    alert.show();

                }
                break;
        }
    }
}
