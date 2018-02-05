package gamsung.traveller.activity;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import gamsung.traveller.R;
import gamsung.traveller.dto.TableManager;
import gamsung.traveller.model.SearchPlace;

public class MapClusterActivity extends FragmentActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<SearchPlace>, ClusterManager.OnClusterInfoWindowClickListener<SearchPlace>, ClusterManager.OnClusterItemClickListener<SearchPlace>, ClusterManager.OnClusterItemInfoWindowClickListener<SearchPlace>{

    GoogleMap cmMap;
    private ClusterManager<SearchPlace> cmClusterManager;


    private class PlaceRenderer extends DefaultClusterRenderer<SearchPlace>{

        public PlaceRenderer() {
            super(getApplicationContext(), cmMap, cmClusterManager);


        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_cluster);
        SupportMapFragment cmapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        cmapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        cmMap = googleMap;
        UiSettings uiSettings = cmMap.getUiSettings();
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        cmMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public boolean onClusterClick(Cluster<SearchPlace> cluster) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<SearchPlace> cluster) {

    }


    @Override
    public boolean onClusterItemClick(SearchPlace searchPlace) {
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(SearchPlace searchPlace) {

    }
}
