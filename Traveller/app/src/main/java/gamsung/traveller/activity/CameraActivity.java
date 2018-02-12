package gamsung.traveller.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import gamsung.traveller.R;


/**
 * Created by Jiwon on 2018. 02. 01
 * 18번 화면
 */

public class CameraActivity extends AppCompatActivity {

    Camera camera;
    CameraPreview preview;
    boolean isPreviewing = false;
    byte[] currentData;

    LayoutInflater controlInflater = null;
    ImageButton buttonTakePicture,camerachange,btnSave,btnComplete;
    RelativeLayout layoutBackground;
    ImageView cameraBar;
    final int RESULT_SAVEIMAGE = 0;
    private static int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        /*
        try {
            android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
        catch(Exception e){
            e.printStackTrace();
        }
*/
        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.addContentView(viewControl, layoutParamsControl);

        cameraBar = (ImageView) findViewById(R.id.cameraBar);

        btnSave = (ImageButton) findViewById(R.id.btnSavePic);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveImageTask().execute(currentData);
                changeToCameraMode();
                resetCam();
            }
        });

        buttonTakePicture = (ImageButton) findViewById(R.id.takepicture);
        buttonTakePicture.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) { // TODO Auto-generated method stub
                camera.takePicture(myShutterCallback, myPictureCallback_RAW, myPictureCallback_JPG);

            }
        });

        layoutBackground = (RelativeLayout) findViewById(R.id.background);
        layoutBackground.setOnClickListener(new LinearLayout.OnClickListener() {
            @Override
            public void onClick(View arg0) { // TODO Auto-generated method stub
                if(!isPreviewing) {
                    buttonTakePicture.setEnabled(false);
                    camera.autoFocus(myAutoFocusCallback);
                }
            }
        });

        camerachange = (ImageButton) findViewById(R.id.camerachange);
        camerachange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(CAMERA_FACING == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
                    resetCam();
                }

                else if(CAMERA_FACING == Camera.CameraInfo.CAMERA_FACING_BACK){
                    CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    resetCam();
                }
            }
        });

    }

    public void startCamera()  {

        //layoutBackground.performClick();

        if ( preview == null ) {
            preview = new CameraPreview(this, (SurfaceView) findViewById(R.id.surfaceView));
            preview.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            ((FrameLayout) findViewById(R.id.layout)).addView(preview);
            preview.setKeepScreenOn(true);

        }

        preview.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }

        int numCams = Camera.getNumberOfCameras();
        if (numCams > 0) {
            try {

                camera = Camera.open(CAMERA_FACING);
                // camera orientation
                camera.setDisplayOrientation(setCameraDisplayOrientation(this, CAMERA_FACING,
                        camera));
                // get Camera parameters
                Camera.Parameters params = camera.getParameters();
                // picture image orientation
                params.setRotation(setCameraDisplayOrientation(this, CAMERA_FACING, camera));
                camera.startPreview();

            } catch (RuntimeException ex) {

            }
        }

        preview.setCamera(camera);
    }


    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }


    AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) { // TODO Auto-generated method stub
            buttonTakePicture.setEnabled(true);
        }
    };

    ShutterCallback myShutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() { // TODO Auto-generated method stub
        }
    };

    PictureCallback myPictureCallback_RAW = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] arg0, Camera arg1) { // TODO Auto-generated method stub
        }
    };

    PictureCallback myPictureCallback_JPG = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera arg1) { // TODO Auto-generated method stub

            isPreviewing = true;

           //int w = camera.getParameters().getPictureSize().width;
            //int h = camera.getParameters().getPictureSize().height;

            changeToSaveMode();
            currentData = data;

        }
    };

    @Override
    public void onBackPressed() {

        if(isPreviewing) {
            changeToCameraMode();
            resetCam();
        }

        else
            super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(camera != null) {
            // Call stopPreview() to stop updating the preview surface
            camera.stopPreview();
            preview.setCamera(null);
            camera.release();
            camera = null;
        }
        ((FrameLayout) findViewById(R.id.layout)).removeView(preview);
        preview = null;
    }

    private void resetCam() {
        isPreviewing = false;
        startCamera();
    }

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {


            int orientation = setCameraDisplayOrientation(CameraActivity.this,
                    CAMERA_FACING, camera);

            //byte array를 bitmap으로 변환
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeByteArray( data[0], 0, data[0].length, options);
            //int w = bitmap.getWidth();
            //int h = bitmap.getHeight();

            //이미지를 디바이스 방향으로 회전

            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bitmap =  Bitmap.createBitmap(bitmap);

            //bitmap을 byte array로 변환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            currentData = stream.toByteArray();

            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/Traveller");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(currentData);
                outStream.flush();
                outStream.close();

                refreshGallery(outFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }

    /**
     *
     * @param activity
     * @param cameraId  Camera.CameraInfo.CAMERA_FACING_FRONT,
     *                    Camera.CameraInfo.CAMERA_FACING_BACK
     * @param camera
     *
     * Camera Orientation
     * reference by https://developer.android.com/reference/android/hardware/Camera.html
     */
    public static int setCameraDisplayOrientation(Activity activity,
                                                  int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);

        return result;
    }

    public void changeToCameraMode () {
        btnSave.setVisibility(View.GONE);
        cameraBar.setVisibility(View.VISIBLE);
        buttonTakePicture.setVisibility(View.VISIBLE);
        camerachange.setVisibility(View.VISIBLE);
    }

    public void changeToSaveMode () {

        cameraBar.setVisibility(View.GONE);
        buttonTakePicture.setVisibility(View.GONE);
        camerachange.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
    }
}
