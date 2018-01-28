package gamsung.traveller.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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



public class MapActivity extends FragmentActivity implements OnMapReadyCallback, OnConnectionFailedListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Marker mMarker;
    private GoogleApiClient mGoogleApiClient;
    private Place mPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

 //       PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
//                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
//        autocompleteFragment.setOnPlaceSelectedListener(this);

        Button bt = findViewById(R.id.btmsearch);
        bt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.mInfoll).setVisibility(View.VISIBLE);
  //              findViewById(R.id.mSelectll).setVisibility(View.GONE);
                openAutocompleteActivity();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        // Add a marker in Sydney, Australia, and move the camera.
       LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
/*
    @Override
    public void onPlaceSelected(final Place place) {

        mPlace = place;
        TextView PlaceName = (TextView) findViewById(R.id.tvPlaceName);
        TextView PlaceAddress = (TextView)findViewById(R.id.tvPlaceAddress);
        final ImageView PlacePhoto = (ImageView)findViewById(R.id.ivPlace);
        final int ivwidth = PlacePhoto.getWidth();
        final int ivheight = PlacePhoto.getHeight();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place.getLatLng())
                .zoom(10)
                .build();
        mMap.clear();
        mMarker = mMap.addMarker(new MarkerOptions().position(mPlace.getLatLng()).title(mPlace.getName().toString()).snippet(mPlace.getAddress().toString()));
        mMarker.hideInfoWindow();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        final ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback = new ResultCallback<PlacePhotoResult>() {
            @Override
            public void onResult(PlacePhotoResult placePhotoResult) {
                if(!placePhotoResult.getStatus().isSuccess()){
                    return;
                }

                PlacePhoto.setImageBitmap(placePhotoResult.getBitmap());

            }
        };
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, mPlace.getId()).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
            @Override
            public void onResult(PlacePhotoMetadataResult photos) {
                if(!photos.getStatus().isSuccess()){
                    return;
                }
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                if(photoMetadataBuffer.getCount()>0){
                    photoMetadataBuffer.get(0).getScaledPhoto(mGoogleApiClient, ivwidth, ivheight).setResultCallback(mDisplayPhotoResultCallback);
                }

                photoMetadataBuffer.release();
            }
        });

        mMarker.setDraggable(true);

       PlaceName.setText(mPlace.getName());
        PlaceAddress.setText(mPlace.getAddress());
//        findViewById(R.id.mInfoll).setVisibility(View.VISIBLE);
//        findViewById(R.id.mSelectll).setVisibility(View.GONE);


        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
    }

    @Override
    public void onError(Status status) {

    }
*/
    @Override
    public void onMarkerDragStart(Marker marker) {
        LinearLayout infoll=(LinearLayout)findViewById(R.id.mInfoll);
        LinearLayout selectll = (LinearLayout)findViewById(R.id.mSelectll);
 //       infoll.setVisibility(GONE);
 //       selectll.setVisibility(GONE);

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
        marker.hideInfoWindow();
        LinearLayout selectll = (LinearLayout)findViewById(R.id.mSelectll);
 //       selectll.setVisibility(VISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
return true;
    }


    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                mPlace = place;
                TextView PlaceName = (TextView) findViewById(R.id.tvPlaceName);
                TextView PlaceAddress = (TextView)findViewById(R.id.tvPlaceAddress);
                final ImageView PlacePhoto = (ImageView)findViewById(R.id.ivPlace);
                final int ivwidth = PlacePhoto.getWidth();
                final int ivheight = PlacePhoto.getHeight();

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(10)
                        .build();
                mMap.clear();
                mMarker = mMap.addMarker(new MarkerOptions().position(mPlace.getLatLng()).title(mPlace.getName().toString()).snippet(mPlace.getAddress().toString()));
                mMarker.hideInfoWindow();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                final ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback = new ResultCallback<PlacePhotoResult>() {
                    @Override
                    public void onResult(PlacePhotoResult placePhotoResult) {
                        if(!placePhotoResult.getStatus().isSuccess()){
                            return;
                        }

                        PlacePhoto.setImageBitmap(placePhotoResult.getBitmap());

                    }
                };
                Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, mPlace.getId()).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if(!photos.getStatus().isSuccess()){
                            return;
                        }
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if(photoMetadataBuffer.getCount()>0){
                            photoMetadataBuffer.get(0).getScaledPhoto(mGoogleApiClient, ivwidth, ivheight).setResultCallback(mDisplayPhotoResultCallback);
                        }

                        photoMetadataBuffer.release();
                    }
                });

                mMarker.setDraggable(true);

                PlaceName.setText(mPlace.getName());
                PlaceAddress.setText(mPlace.getAddress());
//        findViewById(R.id.mInfoll).setVisibility(View.VISIBLE);
//        findViewById(R.id.mSelectll).setVisibility(View.GONE);


                mMap.setOnMarkerClickListener(this);
                mMap.setOnMarkerDragListener(this);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
        }
    }
}



