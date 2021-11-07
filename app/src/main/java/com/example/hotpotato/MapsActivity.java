package com.example.hotpotato;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.hotpotato.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback{

        private GoogleMap mMap;
        private ActivityMapsBinding binding;
        /*private Marker markerMenlyn;
        private Marker markerBrooklyn;
        private Marker markerArcadia;*/

        private EditText searchText;

        /**
         * Request code for location permission request.
         *
         * @see #onRequestPermissionsResult(int, String[], int[])
         */
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

        /**
         * Flag indicating whether a requested permission has been denied after returning in
         * {@link #onRequestPermissionsResult(int, String[], int[])}.
         */
        private boolean permissionDenied = false;
        private GoogleMap map;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            binding = ActivityMapsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            searchText = findViewById(R.id.SearchText);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            init();
        }

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);
            enableMyLocation();

            LatLng pretoria = new LatLng(-25.7,28.2);
            LatLng menlyn = new LatLng(-25.7819,28.2768);
            LatLng brooklyn = new LatLng(-25.7646,28.2393);
            LatLng arcadia = new LatLng(-25.7453,28.2030);

           /* markerMenlyn = map.addMarker(new MarkerOptions()
                    .position(menlyn)
                    .title("Menlyn Mang"));
            markerMenlyn.setTag(0);

            markerBrooklyn = map.addMarker(new MarkerOptions()
                    .position(brooklyn)
                    .title("Brooklyn Mang"));
            markerBrooklyn.setTag(0);

            markerArcadia = map.addMarker(new MarkerOptions()
                    .position(arcadia)
                    .title("Arcadia Mang"));
            markerArcadia.setTag(0);*/

            //LatLng mountainView = new LatLng(37.4, -122.1);

            // Move the camera instantly to Sydney with a zoom of 15.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(pretoria, 15));

        // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(menlyn)      // Sets the center of the map to Mountain View
                    .zoom(13)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        }

        @Override
        public void onPointerCaptureChanged(boolean hasCapture) {

        }

        @Override
        public boolean onMyLocationButtonClick() {
            Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
            // Return false so that we don't consume the event and the default behavior still occurs
            // (the camera animates to the user's current position).
            return false;
        }

        /**
         * Enables the My Location layer if the fine location permission has been granted.
         */
        private void enableMyLocation() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (map != null) {
                    map.setMyLocationEnabled(true);
                }
            } else {
                // Permission to access the location is missing. Show rationale and request permission
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
            if(requestCode != LOCATION_PERMISSION_REQUEST_CODE){
                return;
            }

            if(PermissionUtils.isPermissionGranted(permissions,grantResults,Manifest.permission.ACCESS_FINE_LOCATION)){
                enableMyLocation();
            }
            else{
                permissionDenied = true;
            }
        }

        @Override
        protected void onResumeFragments(){
            super.onResumeFragments();
            if(permissionDenied){
                showMissingPermissionError();
                permissionDenied = false;
            }
        }

        private void showMissingPermissionError() {
            PermissionUtils.PermissionDeniedDialog.newInstance(true).show(getSupportFragmentManager(), "dialog");
        }

        private void init()
        {
            searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int i, KeyEvent keyEvent) {
                    if (i == EditorInfo.IME_ACTION_SEARCH ||
                            i == EditorInfo.IME_ACTION_DONE ||
                            keyEvent.getAction()==KeyEvent.ACTION_DOWN ||
                            keyEvent.getAction()==KeyEvent.KEYCODE_ENTER)
                    {
                        geolocation();
                    }
                    return false;
                }
            });
        }

        private void geolocation()
        {
            String searching = searchText.getText().toString();
            Geocoder geocoder = new Geocoder(MapsActivity.this);
            List<Address> list = new ArrayList<>();
            Toast.makeText(MapsActivity.this, "yes", Toast.LENGTH_LONG).show();
            try
            {
                list = geocoder.getFromLocationName(searching,1);
            }
            catch (IOException e)
            {

            }
            if (list.size() > 0)
            {
                Address address = list.get(0);
                Toast.makeText(this, address.toString(), Toast.LENGTH_LONG).show();
            }
        }

        private void MoveCamera(LatLng latLng, float zoom, String name)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)      // Sets the center of the map to Mountain View
                    .zoom(zoom)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            Marker marker = map.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(name));
            marker.setTag(0);
        }

        private void hideSoftKeyboard(){
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }
}