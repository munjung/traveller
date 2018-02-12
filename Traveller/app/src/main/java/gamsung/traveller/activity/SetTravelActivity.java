package gamsung.traveller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    private ImageView setImage, addImgBtn;
    private Bitmap image_bitmap;
    private TextView txtGo, txtBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_travel);

        calendarFragment = (CalendarFragment)getSupportFragmentManager().findFragmentById(R.id.dateFragment);
        calendarFragment.setCalendarSelectedListener(this);

        setImage = (ImageView)findViewById(R.id.setImage);
        addImgBtn = (ImageView) findViewById(R.id.addImgBtn);
        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakeAlbumAction();
            }
        });

        txtGo = (TextView)findViewById(R.id.txt_set_travel_go);
        txtBack = (TextView)findViewById(R.id.txt_set_travel_back);
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
        try{
            image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
           // myBitmap = image_bitmap.copy(Bitmap.Config.ARGB_8888, true);
            setImage.setAdjustViewBounds(true);
            //uploadImage.setImageBitmap(rotate(myBitmap, 0));
            //Glide.with(getApplicationContext()).load(image_bitmap).asBitmap().into(setImage);
            setImage.setImageBitmap(image_bitmap);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
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

    private void checkInvalid(){

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
