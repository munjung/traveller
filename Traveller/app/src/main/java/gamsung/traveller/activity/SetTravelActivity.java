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

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.IOException;

import gamsung.traveller.R;
import gamsung.traveller.frag.CalendarFragment;

/**
 * Created by jekan on 2018-01-30.
 */

public class SetTravelActivity extends AppCompatActivity {

    CalendarFragment calendarFragment;
  // ImageButton addImgBtn;
    ImageView setImage, addImgBtn;
    private static final int PICK_FROM_ALBUM = 0;
    Bitmap image_bitmap;
    TextView go, back;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_travel);
        calendarFragment = (CalendarFragment)getSupportFragmentManager().findFragmentById(R.id.dateFragment);
        addImgBtn = (ImageView) findViewById(R.id.addImgBtn);
        setImage = (ImageView)findViewById(R.id.setImage);

        addImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doTakeAlbumAction();
            }
        });

        go = (TextView)findViewById(R.id.go);
        back = (TextView)findViewById(R.id.back);
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

}
