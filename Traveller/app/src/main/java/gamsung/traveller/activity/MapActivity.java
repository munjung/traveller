package gamsung.traveller.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.io.IOException;
import java.util.List;

import gamsung.traveller.R;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback, OnConnectionFailedListener, PlaceSelectionListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Marker mMarker;
    private GoogleApiClient mGoogleApiClient;
    private Place mPlace;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
  /*      mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
  */      SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        // Add a marker in Sydney, Australia, and move the camera.
       LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        uiSettings.setZoomControlsEnabled(true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPlaceSelected(Place place) {
        mPlace = place;
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place.getLatLng())
                .zoom(10)
                .build();
        mMap.clear();
        mMarker = mMap.addMarker(new MarkerOptions().position(mPlace.getLatLng()).title(mPlace.getName().toString()).snippet(mPlace.getAddress().toString()));
        mMarker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMarker.setDraggable(true);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        List<Address> address;
        String currentLocationAddress = "주소 정보 없음";
        marker.setTitle("사용자 지정 위치");
        Geocoder geocoder = new Geocoder(this);
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    currentLocationAddress = address.get(0).getAddressLine(0).toString();
                }
            }
        }catch (IOException e) {

            e.printStackTrace();
        }
        marker.setSnippet(currentLocationAddress);
        marker.showInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
return true;
    }
}



