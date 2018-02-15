package gamsung.traveller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import gamsung.traveller.frag.CalendarFragment;
import gamsung.traveller.util.DebugToast;

/**
 * Created by jekan on 2018-01-30.
 */

public class SetTravelActivity extends AppCompatActivity implements CalendarPickerView.OnDateSelectedListener {

    private static final int PICK_FROM_ALBUM = 0;

    private CalendarFragment calendarFragment;
    private ImageView imageRepresent, imageAddPhoto;
    private TextView txtGo, txtBack;
    private EditText editTxtTravelName;
    private ImageButton btnCancel, btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

                Date goDate = (Date)txtGo.getTag();
                Date backDate = (Date)txtBack.getTag();
                if(goDate == null || backDate == null) {
                    //안내 메세지
                    return;
                }

                Uri uri = (Uri)imageRepresent.getTag();
                if(uri == null){
                    //안내 메세지
                    return;
                }

                if(TextUtils.isEmpty(editTxtTravelName.getText())){
                    //안내메세지
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("title", editTxtTravelName.getText());
                intent.putExtra("goDate", goDate.getTime());
                intent.putExtra("backDate", backDate.getTime());
                intent.putExtra("pictruePath", uri.toString());

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"select Picture"), PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case PICK_FROM_ALBUM:
                loadPicture(data);
                break;
        }
    }
    private void loadPicture(Intent data){
        try {

            Uri uri = data.getData();
            Glide.with(this).load(uri).into(imageRepresent);
            imageRepresent.setTag(uri);

//            image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            // myBitmap = image_bitmap.copy(Bitmap.Config.ARGB_8888, true);
            //uploadImage.setImageBitmap(rotate(myBitmap, 0));
            //Glide.with(getApplicationContext()).load(image_bitmap).asBitmap().into(setImage);
//            setImage.setImageBitmap(image_bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDateSelected(Date date) {

        DebugToast.show(this, (String)android.text.format.DateFormat.format("yyyy.MM.dd", date));

        List<Date> dates = calendarFragment.getSelectedDates();
        if(dates == null || dates.size() == 0){
            txtGo.setTag(null);
            txtBack.setTag(null);
        }

        if(dates.size() == 1){
            txtGo.setTag(dates.get(0));
            txtBack.setTag(null);
        }

        if(dates.size() > 1){
            txtGo.setTag(dates.get(0));
            txtBack.setTag(dates.get(dates.size()-1));
        }

        invalidateCalenderGoBack();
    }

    @Override
    public void onDateUnselected(Date date) {

    }

    private void invalidateCalenderGoBack(){

        if(txtGo.getTag() == null){
            txtGo.setText("출발일");
        }
        else{
            Date date = (Date)txtGo.getTag();
            txtGo.setText((String)android.text.format.DateFormat.format("yyyy.MM.dd", date));
        }

        if(txtBack.getTag() == null){
            txtBack.setText("도착일");
        }
        else{
            Date date = (Date)txtBack.getTag();
            txtBack.setText((String)android.text.format.DateFormat.format("yyyy.MM.dd", date));
        }
    }
}
