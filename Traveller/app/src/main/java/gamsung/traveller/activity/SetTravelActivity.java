package gamsung.traveller.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.timessquare.CalendarPickerView;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.frag.CalendarFragment;
import gamsung.traveller.model.Route;
import gamsung.traveller.util.Converter;
import gamsung.traveller.util.DebugToast;

/**
 * Created by jekan on 2018-01-30.
 */

public class SetTravelActivity extends AppCompatActivity implements CalendarPickerView.OnDateSelectedListener {

    private static final int REQUEST_CODE_PICK_FROM_ALBUM = 0;
    private static final int REQUEST_CODE_EMPTY_MAIN = 1;

    public static final int RESULT_CODE_CREATE = 10;
    public static final int RESULT_CODE_EDIT = 11;

    private CalendarFragment calendarFragment;
    private ImageView imageRepresent, imageAddPhoto;
    private ImageView imageEmpty;
    private TextView txtGo, txtBack;
    private EditText editTxtTravelName;
    private ImageButton btnCancel, btnSave;

    private boolean isEditMode = false;
    private int editPosition = -1;
    private int editRouteId = -1;
    private String representPicturePath = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        //아무것도 없는 화면에서 처음 시작할때 안내 화면으로 먼저 이동한다
        this.firstActionNoneItem();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_travel);

        calendarFragment = (CalendarFragment)getSupportFragmentManager().findFragmentById(R.id.dateFragment);
        calendarFragment.setCalendarSelectedListener(this);

        imageRepresent = (ImageView)findViewById(R.id.image_represent_set_travel);
        imageAddPhoto = (ImageView) findViewById(R.id.image_add_photo_set_travel);
        imageAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakeAlbumAction();
            }
        });
        imageEmpty = (ImageView) findViewById(R.id.image_empty_set_travel);
        imageEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakeAlbumAction();
            }
        });

        txtGo = (TextView)findViewById(R.id.txt_set_travel_go);
        txtBack = (TextView)findViewById(R.id.txt_set_travel_back);
        editTxtTravelName = (EditText) findViewById(R.id.txt_name_set_travel);

        btnCancel = (ImageButton) findViewById(R.id.btn_cancel_set_travel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResult(RESULT_CANCELED);
                finish();
            }
        });
        btnSave = (ImageButton) findViewById(R.id.btn_save_set_travel);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String travelName = editTxtTravelName.getText().toString();
                if(TextUtils.isEmpty(travelName)){
                    //안내메세지
                    Toast.makeText(SetTravelActivity.this, "여행명을 입력해주세요", Toast.LENGTH_LONG).show();
                    return;
                }

                List<Date> selectedDates = calendarFragment.getSelectedDates();
                if(selectedDates.size() <= 1){
                    //안내 메세지
                    Toast.makeText(SetTravelActivity.this, "일정을 입력해주세요", Toast.LENGTH_LONG).show();
                    return;
                }

                Date goDate = selectedDates.get(0);
                Date backDate = selectedDates.get(selectedDates.size()-1);
//                String picturePath = (String)imageRepresent.getTag();


                Intent intent = new Intent();
                intent.putExtra("position", editPosition);
                intent.putExtra("routeId", editRouteId);
                intent.putExtra("title", travelName);
                intent.putExtra("goDate", goDate.getTime());
                intent.putExtra("backDate", backDate.getTime());
                intent.putExtra("picturePath", representPicturePath);

                if(isEditMode) {
                    setResult(RESULT_CODE_EDIT, intent);
                    Toast.makeText(SetTravelActivity.this, "변경되었습니다", Toast.LENGTH_LONG).show();
                }
                else {
                   setResult(RESULT_CODE_CREATE, intent);
                    Toast.makeText(SetTravelActivity.this, "저장되었습니다", Toast.LENGTH_LONG).show();
                }

                finish();
            }
        });

        //현재 있는 경로 아이템을 변경하려 들어온 경우
        this.updateActionForExistItem();
    }

    private void firstActionNoneItem(){

        Intent intent = getIntent();
        String tag = intent.getStringExtra("TAG_ACTIVITY");
        if(tag != null && tag.contains("first")){

            Intent firstIntent = new Intent(this, EmptyMainActivity.class);
            startActivityForResult(firstIntent, REQUEST_CODE_EMPTY_MAIN);
        }
    }

    private void updateActionForExistItem(){

        Intent intent = getIntent();
        this.editPosition = intent.getIntExtra(MainActivity.KEY_SEND_TO_ACTIVITY_POSITION, -1);
        this.editRouteId = intent.getIntExtra(MainActivity.KEY_SEND_TO_ACTIVITY_ROUTE_ID, -1);
        if(this.editRouteId > 0){
            DataManager dataManager = DataManager.getInstance(this);
            Route route = dataManager.getRouteList().get(this.editRouteId);
            editTxtTravelName.setText(route.getTitle());
            calendarFragment.setSelectedDates(route.getFromDate(), route.getToDate());

            this.invalidateCalenderGoBack();
            this.setPictureToRepresent(route.getPicturePath());

            this.isEditMode = true;
        }
    }

    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent,"select Picture"), REQUEST_CODE_PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CODE_PICK_FROM_ALBUM:
                loadPicture(data);
                break;

            case REQUEST_CODE_EMPTY_MAIN:
                if(resultCode == RESULT_CANCELED){
                    finish();
                }
                break;
        }
    }



    private void loadPicture(Intent data){
        try {

            Uri uri = data.getData();
            if(uri != null){
                String path = uri.toString();
                setPictureToRepresent(path);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setPictureToRepresent(String path){

        if(path != null && path.length() > 0) {
            Glide.with(this).load(path).into(imageRepresent);
//            imageRepresent.setTag(path);

            representPicturePath = path;
            imageEmpty.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDateSelected(Date date) {

        String strDate = Converter.convertDateToString("yyyy.MM.dd", date);
        DebugToast.show(this, strDate);

        invalidateCalenderGoBack();
    }

    @Override
    public void onDateUnselected(Date date) {

    }

    private void invalidateCalenderGoBack(){

        List<Date> dates = calendarFragment.getSelectedDates();
        if(dates == null || dates.size() == 0){
            txtGo.setText("출발일");
            txtBack.setText("도착일");
        }

        if(dates.size() == 1){
            txtGo.setText(Converter.convertDateToString("yyyy.MM.dd", dates.get(0)));
            txtBack.setText("도착일");
        }

        if(dates.size() > 1){
            txtGo.setText(Converter.convertDateToString("yyyy.MM.dd", dates.get(0)));
            txtBack.setText(Converter.convertDateToString("yyyy.MM.dd", dates.get(dates.size()-1)));
        }
    }
}
