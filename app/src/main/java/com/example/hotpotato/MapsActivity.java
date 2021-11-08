package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.hotpotato.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback{

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
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;

        /**
         * Flag indicating whether a requested permission has been denied after returning in
         * {@link #onRequestPermissionsResult(int, String[], int[])}.
         */
        private boolean permissionDenied = false;
        private GoogleMap map;
        //private FusedLocationProviderClient fusedLocationClient;
        private Location usersLocation;

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
            //fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), "AIzaSyAIuRKBOcw8JNfNSDqmsO0d93k_pnf3MUk", Locale.UK);
            }

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
        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);
            enableMyLocation();

            /*LatLng menlyn = new LatLng(-25.7819,28.2768);*/
            //LatLng userPosition = new LatLng(usersLocation.getLatitude(),usersLocation.getLongitude());

           /* markerMenlyn = map.addMarker(new MarkerOptions()
                    .position(menlyn)
                    .title("Menlyn Mang"));
            markerMenlyn.setTag(0);*/

        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            /*CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(userPosition)      // Sets the center of the map to Mountain View
                    .zoom(13)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/

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
        @SuppressLint("MissingPermission")
        private void enableMyLocation() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (map != null) {
                    map.setMyLocationEnabled(true);
                    /*fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        usersLocation = location;
                                    }
                                }
                            });*/

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
                // Enable the my location layer if the permission has been granted.
                enableMyLocation();
            }
            else{
                // Permission was denied. Display an error message
                // Display the missing permission error dialog when the fragments resume.
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

                    // Set the fields to specify which types of place data to
                    // return after the user has made a selection.
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

                    if (i == EditorInfo.IME_ACTION_SEARCH ||
                            i == EditorInfo.IME_ACTION_DONE ||
                            keyEvent.getAction()==KeyEvent.ACTION_DOWN ||
                            keyEvent.getAction()==KeyEvent.KEYCODE_ENTER)
                    {
                        Toast.makeText(MapsActivity.this,"Searching",Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }
            });
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
    public void onMyLocationClick(@NonNull Location location) { Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }
}