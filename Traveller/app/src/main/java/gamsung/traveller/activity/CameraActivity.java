package gamsung.traveller.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.annotation.SuppressLint;
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
import android.media.Image;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.Route;
import gamsung.traveller.model.Spot;


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
    ImageButton buttonTakePicture,camerachange,btnSave,btnComplete,btnBack,btnCancel;
    RelativeLayout layoutBackground,popupRelative;
    ImageView shadowBackground;
    final int RESULT_SAVEIMAGE = 0;
    private static int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK;
    ArrayList<String> routeTitleList, spotTitleList;
    HashMap <Integer, Route> routeList;
    HashMap<Integer, Spot> tempSpotHashMap;
    DataManager _datamanager;
    String spotName,routeName;
    Spinner routeSpinner, spotSpinner;
    private boolean isEdit = false;
    private int editRouteId = -1;
    private String editSpotTitle ="";
    private int editSpotId = -1;
    public int searchID=-1;
    private int CATEGORY_ID;
    private String picturePath;

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        routeTitleList = new ArrayList<>();
        routeTitleList.add(0,getString(R.string.selectroute));

        spotTitleList = new ArrayList<>();
        spotTitleList.add(0, getString(R.string.selectspot));

        _datamanager = DataManager.getInstance(this);
        routeList = new HashMap<>();
        routeList = _datamanager.getRouteList();

        ArrayList<Route> tempList = new ArrayList<>();
        tempList.addAll(routeList.values());

        for ( Route e : tempList) {
            routeTitleList.add(e.getTitle());
        }

        try {
            android.provider.Settings.System.putInt(getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 1);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        catch(Exception e){
            e.printStackTrace();
        }

        controlInflater = LayoutInflater.from(getBaseContext());
        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.addContentView(viewControl, layoutParamsControl);

        btnSave = (ImageButton) findViewById(R.id.btnSavePic);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToSelectMode();

                ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(CameraActivity.this, android.R.layout.simple_spinner_dropdown_item, routeTitleList);
                //스피너 속성
                routeSpinner = (Spinner) findViewById(R.id.dropDownRoute);
                routeSpinner.setAdapter(mAdapter);
                routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        spotTitleList.clear();
                        spotTitleList.add(getString(R.string.selectspot));
                        spotSpinner.setSelection(0);
                        routeName = routeSpinner.getSelectedItem().toString();

                        if(!routeSpinner.getSelectedItem().toString().equals(getString(R.string.selectroute))) {

                            ArrayList<Route> tempRouteList = new ArrayList<>();
                            tempRouteList.addAll(routeList.values());

                            int routeId = 0;

                            for (Route e : tempRouteList) {
                                if (e.getTitle().equals(routeSpinner.getSelectedItem().toString())) {
                                    routeId = e.get_id();
                                    editRouteId = routeId;
                                    tempSpotHashMap = _datamanager.getSpotListWithRouteId(routeId);
                                    ArrayList<Spot> tempSpotList = new ArrayList<>();
                                    tempSpotList.addAll(tempSpotHashMap.values());
                                    for (int i = 0; i < tempSpotList.size(); i++) {
                                        spotTitleList.add(tempSpotList.get(i).getMission());
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ArrayAdapter<String> nAdapter = new ArrayAdapter<String>(CameraActivity.this, android.R.layout.simple_spinner_dropdown_item, spotTitleList);
                //스피너 속성
                spotSpinner = (Spinner) findViewById(R.id.dropDownSpot);
                spotSpinner.setAdapter(nAdapter);
                spotSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        spotName = spotSpinner.getSelectedItem().toString();

                        if(!spotSpinner.getSelectedItem().equals(getString(R.string.selectspot))) {
                            editSpotTitle = spotSpinner.getSelectedItem().toString();
                            ArrayList<Spot> tempSpotList = new ArrayList<>();
                            tempSpotList.addAll(tempSpotHashMap.values());
                            for(Spot s : tempSpotList) {
                                if(s.getMission().equals(spotSpinner.getSelectedItem().toString())) {
                                    editSpotId = s.get_id();
                                    searchID = s.getSearch_id();
                                    CATEGORY_ID = s.getCategory_id();
                                    picturePath = s.getPicture_path();
                                }

                            }
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }
        });

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToCameraMode();
                resetCam();
            }
        });

        btnCancel = (ImageButton) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToCameraMode();
                resetCam();
            }
        });

        btnComplete =(ImageButton) findViewById(R.id.btnComplete);
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spotName.equals(getString(R.string.selectspot))) {
                    Toast.makeText(CameraActivity.this,R.string.warning_spinner,Toast.LENGTH_LONG).show();
                }

                else if(routeName.equals(getString(R.string.selectroute))) {
                    Toast.makeText(CameraActivity.this,R.string.warning_spinner,Toast.LENGTH_LONG).show();
                }

                else {
                    new SaveImageTask().execute(currentData);
                    changeToCameraMode();
                    resetCam();
                }
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

        popupRelative = (RelativeLayout) findViewById(R.id.popupRelative);
        shadowBackground = (ImageView) findViewById(R.id.shadow_background);

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

            //int w = camera.getParameters().getPictureSize().width;
            //int h = camera.getParameters().getPictureSize().height;

            int orientation = setCameraDisplayOrientation(CameraActivity.this,
                    CAMERA_FACING, camera);

            //byte array를 bitmap으로 변환
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeByteArray( data[0], 0, data[0].length, options);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            //이미지를 디바이스 방향으로 회전

            Matrix matrix = new Matrix();
            matrix.postRotate(orientation);
            bitmap =  Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

            //bitmap을 byte array로 변환
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            currentData = stream.toByteArray();

            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/yeogi");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                updateSpot();
                updatePhoto(sdCard.getAbsolutePath() + "/yeogi/"+fileName);

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
        buttonTakePicture.setVisibility(View.VISIBLE);
        camerachange.setVisibility(View.VISIBLE);
        btnComplete.setVisibility(View.GONE);
        shadowBackground.setVisibility(View.GONE);
        popupRelative.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
    }

    public void changeToSaveMode () {

        btnSave.setVisibility(View.VISIBLE);
        buttonTakePicture.setVisibility(View.INVISIBLE);
        camerachange.setVisibility(View.GONE);
        btnComplete.setVisibility(View.GONE);
        shadowBackground.setVisibility(View.GONE);
        popupRelative.setVisibility(View.GONE);
        btnBack.setVisibility(View.VISIBLE);
    }

    public void changeToSelectMode () {

        buttonTakePicture.setVisibility(View.INVISIBLE);
        camerachange.setVisibility(View.GONE);
        btnSave.setVisibility(View.GONE);
        btnComplete.setVisibility(View.VISIBLE);
        shadowBackground.setVisibility(View.VISIBLE);
        popupRelative.setVisibility(View.VISIBLE);
        btnBack.setVisibility(View.VISIBLE);
    }

    private void updatePhoto(String filepath) {
        Photograph photoforSet = new Photograph();
        photoforSet.setPath(filepath);
        photoforSet.setRoute_id(editRouteId);
        photoforSet.setSpot_id(editSpotId);
        photoforSet.setSearch_id(searchID);
        photoforSet.setDate(new Date(System.currentTimeMillis()));
        _datamanager.insertPhoto(photoforSet);
    }

    private void updateSpot(){

//                        Bundle bundle = savedInstanceState;
//                        int route_id = bundle.getInt("route id");
//                        int spot_id = bundle.getInt("spot list");

        Spot editSpot = new Spot();
        editSpot.set_id(editSpotId);
        editSpot.setRoute_id(editRouteId);
        editSpot.setMission(editSpotTitle);
        editSpot.setSearch_id(searchID);
        editSpot.setCategory_id(CATEGORY_ID);
        editSpot.setPicture_path(picturePath);

        //혹시나 싶어서 변수에 저장해보니 a엔 0이 뜬다
        int a = _datamanager.updateSpot(editSpot);

        if(_datamanager.updateSpot(editSpot) > 0){
            //finish();
            //Toast.makeText(CameraActivity.this, "저장되었습니다.", Toast.LENGTH_LONG).show();
        }
        else{

            Log.e("update spot", "error : not updated");
            Toast.makeText(CameraActivity.this, "error: not updated", Toast.LENGTH_LONG).show();
        }
    }
}