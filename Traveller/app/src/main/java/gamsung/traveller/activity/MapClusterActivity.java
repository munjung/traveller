package gamsung.traveller.activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import gamsung.traveller.R;
import gamsung.traveller.dao.DataManager;
import gamsung.traveller.model.Photograph;
import gamsung.traveller.model.SearchPlace;

/**
 *  10번 화면.
 */




public class MapClusterActivity extends BaseMapActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<PhotoCluster>, ClusterManager.OnClusterInfoWindowClickListener<PhotoCluster>, ClusterManager.OnClusterItemClickListener<PhotoCluster>, ClusterManager.OnClusterItemInfoWindowClickListener<PhotoCluster>{


    private ClusterManager<PhotoCluster> mClusterManager;
    private Random mRandom = new Random(1984);

    protected int getLayoutId(){
        return R.layout.activity_map_cluster;
    }
    private class PhotoRenderer extends DefaultClusterRenderer<PhotoCluster>{
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PhotoRenderer(){
            super(getApplicationContext(), getMap(), mClusterManager);

            View Photoballoon = getLayoutInflater().inflate(R.layout.photo_balloon,null);
            mClusterIconGenerator.setContentView(Photoballoon);
            mClusterImageView = (ImageView) Photoballoon.findViewById(R.id.image);
            Drawable mydrawable = getDrawable(R.drawable.wround);
            mIconGenerator.setBackground(mydrawable);
            mImageView = new ImageView(getApplicationContext());
            mDimension =(int)getResources().getDimension(R.dimen.custom_profile_image);

            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension,mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding,padding,padding,padding);
            mImageView.setCropToPadding(true);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mIconGenerator.setContentView(mImageView);
        }
        @Override
        protected void onBeforeClusterItemRendered(PhotoCluster photoCluster, MarkerOptions markerOptions) {
            String imgpath = photoCluster.source;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=4;
            Bitmap bm = BitmapFactory.decodeFile(imgpath,options);
            Bitmap resizedbm = Bitmap.createScaledBitmap(bm,mDimension,mDimension,true);
            mImageView.setImageBitmap(resizedbm);
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

        }
        @Override
        protected void onBeforeClusterRendered(Cluster<PhotoCluster> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            int width = mDimension;
            int height = mDimension;
            PhotoCluster pc = (PhotoCluster) cluster.getItems().toArray()[0];
            String imgpath = pc.source;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=4;
            Bitmap prebm = BitmapFactory.decodeFile(imgpath,options);
            Bitmap bm = Bitmap.createScaledBitmap(prebm, width,height,true);
            mClusterIconGenerator.setBackground(null);
            mClusterImageView.setImageBitmap(bm);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<PhotoCluster> cluster) {
        ArrayList<String> photolist=new ArrayList<String>();
        String currAddress="";
        List<Address> addressList;
        Geocoder geocoder = new Geocoder(this);
        PhotoCluster pc = (PhotoCluster)cluster.getItems().toArray()[0];
        try{
            if (geocoder!=null) {
                addressList = geocoder.getFromLocation(pc.getPosition().latitude, pc.getPosition().longitude, 1);

                if (addressList != null && addressList.size() > 0) {
                    String locality = addressList.get(0).getLocality();
                    if (locality == null) {
                        locality = "";
                    } else {
                        locality = ", " + locality;
                    }
                    currAddress = addressList.get(0).getCountryName() + locality;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        for(PhotoCluster pcpc : cluster.getItems()){
            photolist.add(pcpc.source);
        }
        Intent intent = new Intent(MapClusterActivity.this,GridInClusterActivity.class);
        intent.putExtra("localname",currAddress);
        intent.putStringArrayListExtra("phototosend",photolist);
        startActivity(intent);
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PhotoCluster> cluster) {

    }

    @Override
    public boolean onClusterItemClick(PhotoCluster photoCluster) {
        Intent intent = new Intent(MapClusterActivity.this, MapRecyclerActivity.class);
        ArrayList<String> photo= new ArrayList<>();
        photo.add(photoCluster.source);
        intent.putStringArrayListExtra("photoset",photo);
        startActivity(intent);
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(PhotoCluster photoCluster) {

    }


    @Override
    protected void startmap() {


        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));

        findViewById(R.id.btnworldback).setVisibility(View.VISIBLE);
        UiSettings uiSettings = getMap().getUiSettings();
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setCompassEnabled(false);
        mClusterManager = new ClusterManager<PhotoCluster>(this, getMap());
        mClusterManager.setRenderer(new PhotoRenderer());
        getMap().setOnCameraIdleListener(mClusterManager);
        getMap().setOnMarkerClickListener(mClusterManager);
        getMap().setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        addItems();
        mClusterManager.cluster();
    }

    private void addItems(){
        DataManager dataManager = DataManager.getInstance(this);
        HashMap<Integer, Photograph> photoList = dataManager.getPhotoList();
        HashMap<Integer, SearchPlace> locallist = dataManager.getSearchPlaceList();
        if(photoList.size()>0) {
        for(Entry<Integer,Photograph> e:photoList.entrySet()){
            Photograph photo = e.getValue();
            int localkey = photo.getSearch_id();
            double lat = locallist.get(localkey).getLat();
            double lon = locallist.get(localkey).getLon();
            LatLng point = new LatLng(lat,lon);
            mClusterManager.addItem(new PhotoCluster(point,photo.getPath(),photo.getPath()));
        }

        }

        /*
        mClusterManager.addItem(new PhotoCluster(position(), "1","test_1.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "2","test_2.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "3","test_3.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "4","test_4.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "5","test_5.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "6","test_6.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "7","test_7.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "8","test_8.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "9","test_9.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "10","test_10.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "11","test_11.jpg"));
        mClusterManager.addItem(new PhotoCluster(position(), "12","test_12.jpg"));
*/
    }

    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
class PhotoCluster implements ClusterItem {
    public  final String name;
    public final String source;
    private final LatLng mPosition;
    public PhotoCluster(LatLng Position, String name, String pictureResource){
        this.name = name;
        source = pictureResource;
        mPosition = Position;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
