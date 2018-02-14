package gamsung.traveller.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
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
import java.io.Serializable;
import java.nio.Buffer;
import java.util.List;
import java.util.Locale;

import gamsung.traveller.R;
import gamsung.traveller.model.SearchPlace;
import gamsung.traveller.model.Spot;

/**
 * 7, 8번 화면. 구글맵 정책 문제로 10번화면과 레이아웃은 같이 씀
 */

public class MapActivity extends BaseMapActivity implements OnMapReadyCallback, OnConnectionFailedListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private Marker mMarker;

    private GoogleApiClient mGoogleApiClient;

    BufferPlace bufferplace=new BufferPlace(0,0,null,null);

    @Override
    protected void startmap() {
        mMap = getMap();

        TextView tvname = findViewById(R.id.tvPlaceName);

        LinearLayout infoll = findViewById(R.id.mInfoll);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setIndoorLevelPickerEnabled(false);
        uiSettings.setCompassEnabled(false);
        LatLng seoul = new LatLng(-37, 126);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        Button bt = findViewById(R.id.btmsearch);
        Button buttongo = findViewById(R.id.btchooseplace);
        buttongo.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapActivity.this,EditLocationActivity.class);
                intent.putExtra("bufferplace",bufferplace);
                Toast.makeText(getApplicationContext(), "lat:"+bufferplace.getLat()+"lon: "+bufferplace.getLon()+"name: "+bufferplace.getPlace_name()+"address: "+bufferplace.getPlace_address(), Toast.LENGTH_SHORT).show();
                /**
                 * 지원님 여기에요
                 *
                 *
                 */
            }
        });
        bt.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView mapiv=findViewById(R.id.ivPlace);
                TextView tvname=findViewById(R.id.tvPlaceName);
                TextView tvaddress=findViewById(R.id.tvPlaceAddress);
                mapiv.setImageResource(0);
                tvname.setText("장소를 선택해주세요!");
                tvaddress.setText("");
                openAutocompleteActivity();
                mMap.clear();
                findViewById(R.id.mInfoll).setClickable(false);
                findViewById(R.id.mInfoll).setVisibility(View.VISIBLE);
                findViewById(R.id.mSelectll).setVisibility(View.GONE);
            }
        });
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
/*        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess()) {
                            final Spot iniplace = places.get(0);
                            mMap.addMarker(new MarkerOptions().position(iniplace.getLatLng()));
                            tvname.setText(iniplace.getName());
                            final CharSequence thirdPartyAttributions = places.getAttributions();
                        }
                        places.release();
                    }
                });
*/
        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));

        mMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LinearLayout infoll=(LinearLayout)findViewById(R.id.mInfoll);
        RelativeLayout selectll = findViewById(R.id.mSelectll);
        infoll.setVisibility(View.GONE);
        selectll.setVisibility(View.GONE);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        List<Address> address;
        List<Address> kaddress;
        String currentLocationAddress = "주소 정보 없음";
        String currentLocationName = "장소 정보 없음";

        TextView tvname = findViewById(R.id.tvselname);
        TextView tvsel = findViewById(R.id.tvSelectAddress);
        marker.setTitle("사용자 지정 위치");
        Locale locale = new Locale("en");
        Geocoder geocoder = new Geocoder(this,locale);
        Geocoder geokoder = new Geocoder(this);
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    kaddress = geokoder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                    currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    String locality = kaddress.get(0).getLocality();
                    if(locality==null){
                        locality="";
                    }
                    else{
                        locality=", "+locality;
                    }
                    currentLocationName = kaddress.get(0).getCountryName()+locality;
                    bufferplace=new BufferPlace(marker.getPosition().latitude,marker.getPosition().longitude,currentLocationName,currentLocationAddress);
                }
            }
        }catch (IOException e) {

            e.printStackTrace();
        }
        marker.hideInfoWindow();

        tvsel.setText(currentLocationAddress);
        tvname.setText(currentLocationName);
        RelativeLayout selectll = findViewById(R.id.mSelectll);
        selectll.setVisibility(View.VISIBLE);


    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        List<Address> address;
        List<Address> kaddress;
        TextView tvname = findViewById(R.id.tvselname);
        String currentLocationAddress = "주소 정보 없음";
        String currentLocationName = "장소 정보 없음";
        Locale locale = new Locale("en");
        Geocoder geocoder = new Geocoder(MapActivity.this,locale);
        Geocoder geokoder = new Geocoder(MapActivity.this);
        try {
            if (geocoder != null) {
                address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                if (address != null && address.size() > 0) {
                    // 주소 받아오기
                    kaddress = geokoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    String locality = kaddress.get(0).getLocality();
                    if(locality==null){
                        locality="";
                    }
                    else{
                        locality=", "+locality;
                    }
                    currentLocationName = kaddress.get(0).getCountryName()+locality;
                    bufferplace=new BufferPlace(latLng.latitude,latLng.longitude,currentLocationName,currentLocationAddress);
                }
            }
        }catch (IOException e) {

            e.printStackTrace();
        }

        Marker lcmarker = mMap.addMarker(new MarkerOptions().position(latLng));
        TextView tvlongclick = findViewById(R.id.tvSelectAddress);
        tvlongclick.setText(currentLocationAddress);
        tvname.setText(currentLocationName);
        lcmarker.hideInfoWindow();
        lcmarker.setDraggable(true);
        mMap.setOnMarkerDragListener(this);
        findViewById(R.id.mInfoll).setVisibility(View.GONE);
        findViewById(R.id.mSelectll).setVisibility(View.VISIBLE);
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

    abstract class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

        private int mHeight;

        private int mWidth;

        public PhotoTask(int width, int height) {
            mHeight = height;
            mWidth = width;
        }

        /**
         * Loads the first photo for a place id from the Geo Data API.
         * The place id must be the first (and only) parameter.
         */
        @Override
        protected AttributedPhoto doInBackground(String... params) {
            if (params.length != 1) {
                return null;
            }
            final String placeId = params[0];
            AttributedPhoto attributedPhoto = null;

            PlacePhotoMetadataResult result = Places.GeoDataApi
                    .getPlacePhotos(mGoogleApiClient, placeId).await();

            if (result.getStatus().isSuccess()) {
                PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
                if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                    // Get the first bitmap and its attributions.
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                    CharSequence attribution = photo.getAttributions();
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto = new AttributedPhoto(attribution, image);
                }
                // Release the PlacePhotoMetadataBuffer.
                photoMetadataBuffer.release();
            }
            return attributedPhoto;
        }

        /**
         * Holder for an image and its attribution.
         */
        class AttributedPhoto {

            public final CharSequence attribution;

            public final Bitmap bitmap;

            public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
                this.attribution = attribution;
                this.bitmap = bitmap;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                final Place mPlace = place;
                TextView PlaceName = (TextView) findViewById(R.id.tvPlaceName);
                TextView PlaceAddress = (TextView)findViewById(R.id.tvPlaceAddress);
                final ImageView PlacePhoto = (ImageView)findViewById(R.id.ivPlace);
                final int ivwidth = PlacePhoto.getWidth();
                final int ivheight = PlacePhoto.getHeight();

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(10)
                        .build();

                mMarker = mMap.addMarker(new MarkerOptions().position(mPlace.getLatLng()).title(mPlace.getName().toString()).snippet(mPlace.getAddress().toString()));
                mMarker.hideInfoWindow();

                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
/*                final ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback = new ResultCallback<PlacePhotoResult>() {
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
*/
                mMarker.setDraggable(true);
                new PhotoTask(PlacePhoto.getWidth(),PlacePhoto.getHeight()){
                    @Override
                    protected void onPreExecute() {
                        // Display a temporary image to show while bitmap is loading.
//                        PlacePhoto.setImageResource(R.drawable.empty_photo);
                    }

                    @Override
                    protected void onPostExecute(AttributedPhoto attributedPhoto) {
                        if (attributedPhoto != null) {
                            // Photo has been loaded, display it.
                            PlacePhoto.setImageBitmap(attributedPhoto.bitmap);
                            Toast.makeText(getApplicationContext(), attributedPhoto.attribution, Toast.LENGTH_SHORT).show();
                            // Display the attribution as HTML content if set.

                        }
                    }
                }.execute(mPlace.getId());
                PlaceName.setText(mPlace.getName());
                PlaceAddress.setText(mPlace.getAddress());
                final LinearLayout infoll = findViewById(R.id.mInfoll);
                infoll.setClickable(true);
                infoll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        bufferplace= new BufferPlace(mPlace.getLatLng().latitude,mPlace.getLatLng().longitude,mPlace.getName().toString(),mPlace.getAddress().toString());
                        Intent intent = new Intent(MapActivity.this,EditLocationActivity.class);
                        intent.putExtra("bufferplace",bufferplace);
                        Toast.makeText(getApplicationContext(), "lat:"+bufferplace.getLat()+"lon: "+bufferplace.getLon()+"name: "+bufferplace.getPlace_name()+"address: "+bufferplace.getPlace_address(), Toast.LENGTH_SHORT).show();
                        /**
                         * 지원님 여기에요
                         *
                         *
                          */


                    }
                });


//                mMap.setOnMarkerClickListener(this);
                mMap.setOnMarkerDragListener(this);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.


            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled operation.
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    class BufferPlace extends SearchPlace implements Serializable{
        public BufferPlace(double lat, double lon, String name, String address){
            this.setLat(lat);
            this.setLon(lon);
            this.setPlace_name(name);
            this.setPlace_address(address);
        }
    }
}


