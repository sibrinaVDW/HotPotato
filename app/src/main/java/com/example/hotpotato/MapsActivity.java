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
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        private ImageButton search;
        private EditText searchText;
        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private static int AUTOCOMPLETE_REQUEST_CODE = 1;
        private boolean permissionDenied = false;
        private GoogleMap map;
        private FusedLocationProviderClient fusedLocationClient;
        private Location usersLocation;
        private PlacesClient placesClient;
        private List<Place.Field> placeFields;
        Place mPlace;
        private Marker mMarker;
        private Button recenterUserLocation;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //Login
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                String email = user.getEmail();
                Uri photoUrl = user.getPhotoUrl();

                // Check if user's email is verified
                boolean emailVerified = user.isEmailVerified();

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
                String uid = user.getUid();
            }

            binding = ActivityMapsBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            //search = findViewById(R.id.btnSearch);
            //searchText = findViewById(R.id.SearchText);
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);

            if (!Places.isInitialized()) {
                Places.initialize(getApplicationContext(), "AIzaSyAIuRKBOcw8JNfNSDqmsO0d93k_pnf3MUk", Locale.UK);
            }
            placesClient = Places.createClient(this);

            //init();
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap)
        {
            map = googleMap;
            map.setOnMyLocationButtonClickListener(this);
            map.setOnMyLocationClickListener(this);

            if(!permissionDenied){
                GetDeviceLocation();
            }

            recenterUserLocation = findViewById(R.id.btnRecenter);
            recenterUserLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GetDeviceLocation();
                }
            });

            AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
            autocompleteFragment.setPlaceFields(fields);
            //set bounds for in radius of user location.
            //autocompleteFragment.setLocationBias(RectangularBounds.newInstance(new LatLng()));
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onError(@NonNull Status status) {

                }

                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    //LatLng selectedPlaceLatLng = new LatLng(place.getLatLng())

                    String landmarkName = place.getName();
                    String landmarkAddress = place.getAddress();
                    OpeningHours landmarkHours = place.getOpeningHours();
                    String landmarkPhoneNumber = place.getPhoneNumber();
                    Log.d(TAG, "Landmark Info: "+ landmarkName + " " + landmarkAddress + " " + landmarkPhoneNumber + " " + landmarkHours);
                    MoveCamera(place.getLatLng(),15f,landmarkName);
                }
            });
        }

        public void GetDeviceLocation()
        {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
            try
            {
                if(!permissionDenied)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Task location =  fusedLocationClient.getLastLocation();
                        location.addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Log.d(TAG,"found location");
                                    usersLocation = (Location) task.getResult();
                                    LatLng userLatLng = new LatLng(usersLocation.getLatitude(), usersLocation.getLongitude());
                                    MoveCamera(userLatLng,15f);
                                    enableMyLocation();
                                }
                                else{
                                    Log.d(TAG,"location is null");
                                    Toast.makeText(MapsActivity.this,"Couldn't find user location",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }

            }catch (SecurityException e)
            {
                Log.e(TAG, "GetDeviceLocation: Security Exception" + e.getLocalizedMessage());
            }
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
                    //map.getUiSettings().setMyLocationButtonEnabled(true);
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

        private void MoveCamera(LatLng latLng, float zoom)
        {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)      // Sets the center of the map to Mountain View
                    .zoom(zoom)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        private void hideSoftKeyboard(){
            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

    private void MoveCamera(LatLng latLng, float zoom, String title)
    {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        map.addMarker(options);
        mMarker = map.addMarker(options);

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) { Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }



    //-- BACKUP IDEA --
    /*private void init()
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
    }*/

}