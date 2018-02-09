package gamsung.traveller.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

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

    private ImageView _imageView;

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


        this.tryPermCheck();        //권한체크
        this.setRecyclerView();     //여행기록화면 세팅
        this.setImageView();        //빈화면 세팅
        this.endOperation();        //모든 세팅 완료후 동작 함수 실행
        this.setRegisterEvent();    //버튼 이벤트 등록
    }



    /*
     * initiazlie
     */
    private void setRecyclerView(){

        _recyclerAdapter = new RecyclerViewAdapter(this, _routeList);

        _recyclerView = (RecyclerView)findViewById(R.id.recycler_main_content);
        _recyclerView.setAdapter(_recyclerAdapter);
        _recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void setImageView(){

        _imageView = (ImageView) findViewById(R.id.image_empty_main);
        _imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setRegisterEvent(){

        FloatingActionButton fbtnAddItem = (FloatingActionButton)findViewById(R.id.fbtn_add_main);
        fbtnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRouteItem();
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




    /*
     * UI Operation
     */
    private void addRouteItem(){

        //temp code
        int idx = _recyclerAdapter.getItemCount() + 1;

        Route route = new Route();
        route.setTitle(String.valueOf(idx));
        route.setPicturPath(temp);

        _recyclerAdapter.addItem(route);

        this.endOperation();

        DebugToast.show(MainActivity.this, "add : " + idx);
    }

    private void removeRouteItem(int position){

        _recyclerAdapter.removeItem(position);

        this.endOperation();
    }

    private void updateRouteItem(int position, Route route){

        Route routeItem = _recyclerAdapter.getItem(position);
        //
        //

        _recyclerAdapter.updateItem();
    }

    private void endOperation(){

        if(_recyclerAdapter.getItemCount() > 0) {
            _imageView.setVisibility(View.INVISIBLE);
        }
        else{
            _imageView.setVisibility(View.VISIBLE);
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



//View Holder Adaper
class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RouteViewHolder> implements View.OnClickListener {

    private Context _context;
    private List<Route> _items;

    private View _deleteView;

    public RecyclerViewAdapter(Context context, List<Route> routeList) {

        if (routeList == null) {
            throw new IllegalArgumentException("route data must not be null");
        }

        this._context = context;
        this._items = routeList;
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_main_route_item, viewGroup, false);

        return new RouteViewHolder(_context, itemView);
    }

    @Override
    public void onBindViewHolder(RouteViewHolder viewHolder, int position) {

        Route item = _items.get(position);
        viewHolder.textView.setText(item.getTitle());

        if (!TextUtils.isEmpty(item.getPicturePath())) {
            Glide.with(_context).load(item.getPicturePath()).into(viewHolder.imageView);
        }

        viewHolder.deleteClickListener = this;
    }

    @Override
    public void onClick(View view) {

        DebugToast.show(_context, "delete button clicked");

        if(_deleteView == null) {
            _deleteView = LayoutInflater.from(_context).inflate(R.layout.layout_main_delete, (ViewGroup) ((MainActivity) _context).getWindow().getDecorView());

            //삭제
            TextView textView = (TextView)_deleteView.findViewById(R.id.txt_main_delete);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DebugToast.show(_context, "삭제");
                    _deleteView.setVisibility(View.VISIBLE);
                }
            });

            //삭제 취소
            View targetView = (View)_deleteView.findViewById(R.id.layout_main_delete_target);
            targetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DebugToast.show(_context, "취소");
                    _deleteView.setVisibility(View.INVISIBLE);
                }
            });
        }

        TextView itemTextView = view.findViewById(R.id.txt_route_item);
        TextView deleteTextView = (TextView)_deleteView.findViewById(R.id.txt_main_delete);
        deleteTextView.setText("'" + itemTextView.getText() + "'을 삭제합니다");

        ImageView imageView = (ImageView)_deleteView.findViewById(R.id.image_main_delete_target);
        imageView.setImageBitmap(getBitmapFromView(view));
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

    private Bitmap getBitmapFromView(View view){

        //item to bitmap
        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight() ,
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);

        return b;
    }


    //View Holder
    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        private Context _context;

        public ImageView imageView;
        public TextView textView;
        public View.OnClickListener deleteClickListener;

        private Button btnEdit;
        private Button btnDelete;

        public RouteViewHolder(Context context, final View itemView) {
            super(itemView);

            _context = context;

            imageView = (ImageView) itemView.findViewById(R.id.image_route_item);
            textView = (TextView) itemView.findViewById(R.id.txt_route_item);
            btnEdit = (Button) itemView.findViewById(R.id.btn_edit_route_item);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete_route_item);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DebugToast.show(_context, "image clicked");
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DebugToast.show(_context, "button clicked");
                }
            });

            if(deleteClickListener != null)
                btnDelete.setOnClickListener(deleteClickListener);
        }
    }
}