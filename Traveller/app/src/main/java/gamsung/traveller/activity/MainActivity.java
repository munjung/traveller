package gamsung.traveller.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.net.URI;
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


    private RecyclerView _recyclerView;
    private RecyclerViewAdapter _recyclerAdapter;

    private DataManager _dataManager;
    private List<Route> _routeList;

    final int PERMISSION_CODE = 0;

    private String temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //data load
        _dataManager = DataManager.getInstance(this);
        _routeList = new ArrayList<Route>(_dataManager.getRouteList().values());


        //temp code
        List<String> images = getPathOfAllImages();
        temp = images.get(1);


        this.setRecyclerView();
        this.setRegisterEvent();
        this.tryPermCheck();
    }

    private void setRecyclerView(){

        _recyclerAdapter = new RecyclerViewAdapter(this, _routeList);

        _recyclerView = (RecyclerView)findViewById(R.id.recycler_history);
        _recyclerView.setAdapter(_recyclerAdapter);
        _recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void setRegisterEvent(){

        Button btnSample = (Button)findViewById(R.id.btn_sample);
        btnSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //temp code
                int idx = _recyclerAdapter.getItemCount() + 1;

                Route route = new Route();
                route.setTitle(String.valueOf(idx));
                route.setPicturPath(temp);

                _recyclerAdapter.addItem(route);

                DebugToast.show(MainActivity.this, "add");
            }
        });


        Button btnAddTravel = (Button) findViewById(R.id.btnAddTravel);
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


    //temp func
    private ArrayList<String> getPathOfAllImages()
    {
        ArrayList<String> result = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME };

        Cursor cursor = getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnDisplayName = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);

        if(cursor != null) {
            while (cursor.moveToNext()) {
                String pathOfImage = cursor.getString(columnIndex);
//                String nameOfFile = cursor.getString(columnDisplayName);

                if (!TextUtils.isEmpty(pathOfImage))
                    result.add(pathOfImage);
            }
            cursor.close();
        }

        return result;
    }
}




class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RouteViewHolder> {

    private Context _context;
    private List<Route> _items;

    public RecyclerViewAdapter(Context context, List<Route> routeList) {

        if (routeList == null) {
            throw new IllegalArgumentException("route data must not be null");
        }

        this._context = context;
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
        viewHolder.textView.setText(item.getTitle());

        if (!TextUtils.isEmpty(item.getPicturPath())) {

            Glide.with(_context).load(item.getPicturPath()).into(viewHolder.imageView);
        }
        viewHolder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }

    public void addItem(Route route) {

        if (_items.add(route))
            notifyDataSetChanged();
    }

    public void updateItem() {

        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        _items.remove(position);
        notifyDataSetChanged();
    }

    public Route getItem(int position) {
        return _items.get(position);
    }


    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView textView;
        public Button btn;

        public RouteViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.img_history);
            textView = (TextView) itemView.findViewById(R.id.tv_history);
            btn = (Button) itemView.findViewById(R.id.btn_history);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                }
            });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
    }
}