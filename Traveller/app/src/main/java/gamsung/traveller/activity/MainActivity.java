package gamsung.traveller.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Route;
import gamsung.traveller.util.DebugToast;
import gamsung.traveller.util.ImageSupporter;

/*
 * 네신중평님이 마법을 부릴 2,3번 화면
 */


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //permission code
    private final int PERMISSION_CODE = 0;

    //intent tag name for route id
    public final static String KEY_SEND_TO_ACTIVITY_ROUTE_ID = "routeId";
    public final static String KEY_SEND_TO_ACTIVITY_POSITION = "position";

    //Activity Result Request Code
    private final static int REQUEST_CODE_GO_SET_TRAVEL = 1;
    private final static int REQUEST_CODE_GO_MAP_PICTURE = 2;
    private final static int REQUEST_CODE_GO_EDIT_LOCATION = 3;

    private ImageView _imageView;

    private RecyclerView _recyclerView;
    private RecyclerViewAdapter _recyclerAdapter;

    private DataManager _dataManager;
    private List<Route> _routeList;

    private List<String> photoList;
    private int temp_id;


    @Override
    public void onBackPressed() {

        View deleteView = _recyclerAdapter.get_deleteView();
        if(deleteView != null && deleteView.getVisibility() == View.VISIBLE){
            deleteView.setVisibility(View.INVISIBLE);
            return;
        }

        ActivityCompat.finishAffinity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_GO_SET_TRAVEL:
                this.receiveFromSetTravel(resultCode, data);
                break;
            case REQUEST_CODE_GO_MAP_PICTURE:
                this.receiveFromMapPictrue(resultCode, data);
                break;
            case REQUEST_CODE_GO_EDIT_LOCATION:
                this.receiveFromEditLocation(requestCode, data);
                break;
        }
    }

    private void receiveFromSetTravel(int resultCode, Intent data){

        if(resultCode < 10)
            return;

        //parse intent
        int position = data.getIntExtra("position", -1);
        int routeId = data.getIntExtra("routeId", -1);
        String title = data.getStringExtra("title");
        Date goDate = new Date(data.getLongExtra("goDate", 0));
        Date backDate = new Date(data.getLongExtra("backDate", 0));
        String picturePath = data.getStringExtra("picturePath");

        //generate route
        Route route = new Route();
        route.set_id(routeId);
        route.setTitle(title);
        route.setFromDate(goDate);
        route.setToDate(backDate);
        route.setPicturPath(picturePath);

        switch (resultCode){
            case SetTravelActivity.RESULT_CODE_CREATE:  //add route recycler item
                if(_dataManager.insertRoute(route) > 0)
                    _recyclerAdapter.addItem(route);
                break;

            case SetTravelActivity.RESULT_CODE_EDIT:    //update route recycler item
                if(_dataManager.updateRoute(route) > 0)
                    _recyclerAdapter.updateItem(position, route);
                break;

        }
    }

    private void receiveFromEditLocation(int resultCode, Intent data){

        //no need : 이미 DB에 spot들을 저장하고 돌아옴 or 취소
    }

    private void receiveFromMapPictrue(int resultCode, Intent data){

        //no need : 단순 보여주기이므로 신경 안써도 됨
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        this.tryPermCheck();        //권한체크

        //data load
        _dataManager = DataManager.getInstance(this);
        _routeList = new ArrayList<Route>(_dataManager.getRouteList().values());
        if(_routeList.size() == 0){

            //data가 없는 경우 바로 SetTravelActivity로 이동
            Intent intent = new Intent(MainActivity.this, SetTravelActivity.class);
            intent.putExtra("TAG_ACTIVITY", "first");
            startActivityForResult(intent, REQUEST_CODE_GO_SET_TRAVEL);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRecyclerView();     //여행기록화면 세팅
        this.setImageView();        //빈화면 세팅
        this.setRegisterEvent();    //버튼 이벤트 등록

        //temp code
        photoList = ImageSupporter.getPathOfAllImages(this);
    }



    /*
     * initiazlie
     */
    private void setRecyclerView(){

        _recyclerAdapter = new RecyclerViewAdapter(this, _routeList, this);

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

        Button btnAddItem = (Button)findViewById(R.id.btn_add_main);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRouteItem();
            }
        });

        Button btnPictureMap = (Button) findViewById(R.id.btn_map_main);
        btnPictureMap.setOnClickListener(new View.OnClickListener() {
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

        if(_recyclerAdapter.getItemCount() == 0){

            Route route = new Route();
            route.setTitle("temp");
            _recyclerAdapter.addItem(route);
        }

        //temp code
        int idx = _recyclerAdapter.getItemCount() + 1;

        Route route = new Route();
        route.setTitle(String.valueOf(idx));
        if(photoList.size() > 0){

            route.setPicturPath(photoList.get(temp_id++));
        }

        _recyclerAdapter.addItem(route);
    }

    public void hideKeypad(){

        EditText editText = findViewById(R.id.txt_search_main);

        InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }




    @Override
    public void onClick(View view) {

        int sendId =_recyclerAdapter.get_clickListenerArgs().getSend_id();
        int position = _recyclerAdapter.get_clickListenerArgs().getItem_position();
        Route route = _recyclerAdapter.getItem(position);

        switch (sendId){
            case RouteItemClickListenerArguments.EDIT_CLICK:

                Intent editClickIntent = new Intent(this, SetTravelActivity.class);
                editClickIntent.putExtra(KEY_SEND_TO_ACTIVITY_ROUTE_ID, route.get_id());
                editClickIntent.putExtra(KEY_SEND_TO_ACTIVITY_POSITION, position);
                startActivityForResult(editClickIntent, REQUEST_CODE_GO_SET_TRAVEL);
                break;

            case RouteItemClickListenerArguments.IMAGE_CLICK:
                int spotSize = _dataManager.getSpotListWithRouteId(route.get_id()).size();
                if(spotSize == 0){

                    Intent imgClickIntent = new Intent(this, EmptyTravelActivity.class);
                    imgClickIntent.putExtra(KEY_SEND_TO_ACTIVITY_ROUTE_ID, route.get_id());
                    startActivityForResult(imgClickIntent, REQUEST_CODE_GO_EDIT_LOCATION);
                }
                else{

                    Intent imgClickIntent = new Intent(this, TravelViewActivity.class);
                    imgClickIntent.putExtra(KEY_SEND_TO_ACTIVITY_ROUTE_ID, route.get_id());
                    startActivityForResult(imgClickIntent, REQUEST_CODE_GO_EDIT_LOCATION);
                }
                break;

            case RouteItemClickListenerArguments.GOTO_PICTURE_CLICK:

                Intent gotoPictureClickIntent = new Intent(this, GridInCluster.class);
                gotoPictureClickIntent.putExtra(KEY_SEND_TO_ACTIVITY_ROUTE_ID, route.get_id());
                startActivityForResult(gotoPictureClickIntent, REQUEST_CODE_GO_MAP_PICTURE);
                break;

            case RouteItemClickListenerArguments.DELETE_CLICK:
                if(_dataManager.deleteRoute(route.get_id())){
                    Log.d("delete", "route" + route.get_id());
                }
                break;
        }
    }
}



//View Holder Adaper
class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RouteViewHolder> {

    private Context _context;
    private List<Route> _items;

    private View _deleteView;
    private Button _addBtnForVisible;

    private View.OnClickListener _clickListener;
    private RouteItemClickListenerArguments _clickListenerArgs;

    public RecyclerViewAdapter(Context context, List<Route> routeList, View.OnClickListener clickListener) {

        if (routeList == null) {
            throw new IllegalArgumentException("route data must not be null");
        }

        this._context = context;
        this._items = routeList;

        this._clickListener = clickListener;
        this._clickListenerArgs = new RouteItemClickListenerArguments();
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        final View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_main_route_item, viewGroup, false);
        final RouteViewHolder viewHolder = new RouteViewHolder(_context, itemView);


        viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _clickListenerArgs.setSend_id(RouteItemClickListenerArguments.IMAGE_CLICK);
                _clickListenerArgs.setItem_position(viewHolder.getAdapterPosition());
                _clickListener.onClick(view);
            }
        });

        //click edit button on view holder
        viewHolder.getBtnEdit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _clickListenerArgs.setSend_id(RouteItemClickListenerArguments.EDIT_CLICK);
                _clickListenerArgs.setItem_position(viewHolder.getAdapterPosition());
                _clickListener.onClick(view);

            }
        });

        viewHolder.getBtnGoToPicture().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _clickListenerArgs.setSend_id(RouteItemClickListenerArguments.GOTO_PICTURE_CLICK);
                _clickListenerArgs.setItem_position(viewHolder.getAdapterPosition());
                _clickListener.onClick(view);
            }
        });

        //click delete button on view holder
        viewHolder.getBtnDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //click delete button
                if(_deleteView == null) {

                    //attach delete view
                    ViewGroup inflateViewGroup = ((MainActivity)_context).findViewById(R.id.layout_main_inflate);
                    _deleteView = LayoutInflater.from(_context).inflate(R.layout.layout_main_delete, inflateViewGroup);

                    //delete item
                    TextView textView = (TextView)_deleteView.findViewById(R.id.txt_main_delete);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DebugToast.show(_context, "삭제");
                            hideDeleteView();

                            _clickListenerArgs.setSend_id(RouteItemClickListenerArguments.DELETE_CLICK);
                            _clickListenerArgs.setItem_position(viewHolder.getAdapterPosition());
                            _clickListener.onClick(view);

                            //remove view holder position(saved viewholder position in deleteview's tag)
                            removeItem((int)_deleteView.getTag());
                        }
                    });

                    //cancel delete view
                    View targetView = (View)_deleteView.findViewById(R.id.layout_main_delete_target);
                    targetView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            DebugToast.show(_context, "취소");
                            hideDeleteView();
                        }
                    });
                }
                else{
                    showDeleteView();
                }

                //set view holder position
                _deleteView.setTag(viewHolder.getAdapterPosition());

                //set text => delete view(text)
                TextView itemTextView = viewHolder.itemView.findViewById(R.id.txt_route_item);
                TextView deleteTextView = (TextView)_deleteView.findViewById(R.id.txt_main_delete);
                deleteTextView.setText("'" + itemTextView.getText() + "'을 삭제합니다");

                //set image => delete view(image)
                ImageView imageView = (ImageView)_deleteView.findViewById(R.id.image_main_delete_target);
                imageView.setImageBitmap(getBitmapFromView(viewHolder.itemView));
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RouteViewHolder viewHolder, final int position) {

        Route item = _items.get(position);
        viewHolder.getTextView().setText(item.getTitle());

        if (!TextUtils.isEmpty(item.getPicturePath())) {
            //load image
            Glide.with(_context).load(item.getPicturePath()).into(viewHolder.imageView);

            //show picture button
            viewHolder.getBtnGoToPicture().setVisibility(View.VISIBLE);
            //show shadow image
            viewHolder.getLayoutShadow().setVisibility(View.VISIBLE);
            //change image delete button
            viewHolder.getBtnDeleteImage().setBackground(_context.getResources().getDrawable(R.drawable.btn_delete));
        }
    }

    @Override
    public int getItemCount() {
        return _items.size();
    }


    public void addItem(Route route) {

        _items.add(route);
        notifyItemInserted(_items.size() - 1);
    }

    public void updateItem(int position, Route route) {

        _items.set(position, route);
        notifyItemChanged(position);
    }

    public void removeItem(int position) {

        _items.remove(position);
        notifyItemRemoved(position);
    }

    public Route getItem(int position) {
        return _items.get(position);
    }

    public View get_deleteView(){
        return _deleteView;
    }

    public void hideDeleteView(){

        //hide delete view
        _deleteView.setVisibility(View.INVISIBLE);

        //show floating button
        MainActivity mainActivity = (MainActivity) _context;
        _addBtnForVisible = mainActivity.findViewById(R.id.btn_add_main);
        _addBtnForVisible.setVisibility(View.VISIBLE);
    }

    public void showDeleteView(){

        //hide keypad
        MainActivity mainActivity = (MainActivity) _context;
        mainActivity.hideKeypad();

        //hide floating button
        _addBtnForVisible = mainActivity.findViewById(R.id.btn_add_main);
        _addBtnForVisible.setVisibility(View.INVISIBLE);

        //visible delete view
        _deleteView.setVisibility(View.VISIBLE);
    }

    private Bitmap getBitmapFromView(View view){

        //item to bitmap
        Bitmap b = Bitmap.createBitmap(view.getWidth(), view.getHeight() ,
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        view.draw(c);

        return b;
    }

    public RouteItemClickListenerArguments get_clickListenerArgs() {
        return _clickListenerArgs;
    }


    //View Holder
    public static class RouteViewHolder extends RecyclerView.ViewHolder {

        private Context _context;

        private ImageView imageView;
        private TextView textView;

        private View btnDelete;
        private View btnDeleteImage;
        private View btnEdit;
        private View btnGoToPicture;
        private RelativeLayout layoutShadow;

        public RouteViewHolder(Context context, final View itemView) {
            super(itemView);

            _context = context;

            imageView = (ImageView) itemView.findViewById(R.id.image_route_item);
            textView = (TextView) itemView.findViewById(R.id.txt_route_item);
            btnEdit = (View) itemView.findViewById(R.id.btn_edit_route_item);
            btnGoToPicture = (View) itemView.findViewById(R.id.btn_goto_picture);
            btnDelete = (View) itemView.findViewById(R.id.btn_delete_route_item);
            btnDeleteImage = (View)itemView.findViewById(R.id.btn_delete_image_route_item);
            layoutShadow = (RelativeLayout) itemView.findViewById(R.id.shadow_backlayer);
        }

        public ImageView getImageView() {
            return imageView;
        }
        public TextView getTextView() {
            return textView;
        }
        public View getBtnDelete() {
            return btnDelete;
        }
        public View getBtnDeleteImage() {
            return btnDeleteImage;
        }
        public View getBtnEdit() {
            return btnEdit;
        }
        public View getBtnGoToPicture() {
            return btnGoToPicture;
        }
        public RelativeLayout getLayoutShadow() {
            return layoutShadow;
        }
    }
}

class RouteItemClickListenerArguments{

    public final static int EDIT_CLICK = 1;
    public final static int IMAGE_CLICK = 2;
    public final static int GOTO_PICTURE_CLICK = 3;
    public final static int DELETE_CLICK = 4;

    private int send_id;
    private int item_position;


    public int getSend_id() {
        return send_id;
    }

    public void setSend_id(int send_id) {
        this.send_id = send_id;
    }

    public int getItem_position() {
        return item_position;
    }

    public void setItem_position(int item_position) {
        this.item_position = item_position;
    }
}

