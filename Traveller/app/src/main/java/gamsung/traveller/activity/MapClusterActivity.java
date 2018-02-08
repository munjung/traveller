package gamsung.traveller.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;


import java.util.Random;

import gamsung.traveller.R;
import gamsung.traveller.model.Photograph;

public class MapClusterActivity extends FragmentActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<PhotoCluster>, ClusterManager.OnClusterInfoWindowClickListener<PhotoCluster>, ClusterManager.OnClusterItemClickListener<PhotoCluster>, ClusterManager.OnClusterItemInfoWindowClickListener<PhotoCluster>{

    GoogleMap cmMap;
    private ClusterManager<PhotoCluster> mClusterManager;
    private Random mRandom = new Random(1984);

    private class PhotoRenderer extends DefaultClusterRenderer<PhotoCluster>{
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PhotoRenderer(){
            super(getApplicationContext(), cmMap, mClusterManager);

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
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(PhotoCluster photoCluster, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            Drawable smalldraw = getResources().getDrawable(photoCluster.source);

            mImageView.setImageDrawable(smalldraw);
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
            Drawable drawable = getResources().getDrawable(pc.source);
            drawable.setBounds(0, 0, width, height);
            mClusterIconGenerator.setBackground(null);
            mClusterImageView.setImageDrawable(drawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    public boolean onClusterClick(Cluster<PhotoCluster> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<PhotoCluster> cluster) {

    }

    @Override
    public boolean onClusterItemClick(PhotoCluster photoCluster) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(PhotoCluster photoCluster) {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cluster);
        SupportMapFragment cmapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.cmap);
        cmapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        cmMap = googleMap;

        cmMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));

        mClusterManager = new ClusterManager<PhotoCluster>(this, cmMap);
        mClusterManager.setRenderer(new PhotoRenderer());
        cmMap.setOnCameraIdleListener(mClusterManager);
        cmMap.setOnMarkerClickListener(mClusterManager);
        cmMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }

    private void addItems(){
        mClusterManager.addItem(new PhotoCluster(position(), "1",R.drawable.test_1));
        mClusterManager.addItem(new PhotoCluster(position(), "2",R.drawable.test_2));
        mClusterManager.addItem(new PhotoCluster(position(), "3",R.drawable.test_3));
        mClusterManager.addItem(new PhotoCluster(position(), "4",R.drawable.test_4));
        mClusterManager.addItem(new PhotoCluster(position(), "5",R.drawable.test_5));
        mClusterManager.addItem(new PhotoCluster(position(), "6",R.drawable.test_6));
        mClusterManager.addItem(new PhotoCluster(position(), "7",R.drawable.test_7));
        mClusterManager.addItem(new PhotoCluster(position(), "8",R.drawable.test_8));
        mClusterManager.addItem(new PhotoCluster(position(), "9",R.drawable.test_9));
        mClusterManager.addItem(new PhotoCluster(position(), "10",R.drawable.test_10));
        mClusterManager.addItem(new PhotoCluster(position(), "11",R.drawable.test_11));
        mClusterManager.addItem(new PhotoCluster(position(), "12",R.drawable.test_12));


    }

    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
class PhotoCluster extends Photograph implements ClusterItem{
    public  final String name;
    public final int source;
    private final LatLng mPosition;
    public PhotoCluster(LatLng Position, String name, int pictureResource){
        this.name = name;
        source = pictureResource;
        mPosition = Position;
    }
    @Override
    public LatLng getPosition() {
        return null;
    }
}
