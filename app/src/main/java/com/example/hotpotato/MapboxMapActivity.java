package com.example.hotpotato;

import android.app.PendingIntent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.maps.MapView;

import java.util.List;


public class MapboxMapActivity extends AppCompatActivity implements OnMapReadyCallback,LocationEngine,PermissionsListener {
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private PermissionsManager permissionsManager;

    MapView mapView;
    public com.mapbox.maps.MapView viewMap;
    MapboxMap mapMB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapbox_map);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        viewMap = findViewById(R.id.mapView);
        //viewMap.onCreate(savedInstanceState);
        //viewMap.getMapAsync(this::onMapReady);

        viewMap.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
        /*mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((OnMapReadyCallback) this);*/
    }

    public void onMapReady(MapboxMap mapboxMap) {
        mapMB = mapboxMap;
        //Position origin = Position.fromCoordinates(mapboxMap.getMyLocation().getLongitude(), mapboxMap.getMyLocation().getLatitude());
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewMap.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewMap.onStop();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        viewMap.onResume();
//    }
//
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        viewMap.onSaveInstanceState(outState);
//    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        viewMap.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewMap.onDestroy();
    }

    @Override
    public void onExplanationNeeded(List<String> list) {

    }

    @Override
    public void onPermissionResult(boolean b) {

    }

    @Override
    public void getLastLocation(@NonNull LocationEngineCallback<LocationEngineResult> locationEngineCallback) throws SecurityException {

    }

    @Override
    public void requestLocationUpdates(@NonNull LocationEngineRequest locationEngineRequest, @NonNull LocationEngineCallback<LocationEngineResult> locationEngineCallback, @Nullable Looper looper) throws SecurityException {

    }

    @Override
    public void requestLocationUpdates(@NonNull LocationEngineRequest locationEngineRequest, PendingIntent pendingIntent) throws SecurityException {

    }

    @Override
    public void removeLocationUpdates(@NonNull LocationEngineCallback<LocationEngineResult> locationEngineCallback) {

    }

    @Override
    public void removeLocationUpdates(PendingIntent pendingIntent) {

    }


    //@SuppressWarnings( {"MissingPermission"})
   /* private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            LocationLayerPlugin locationLayerPlugin = new LocationLayerPlugin(viewMap, mapMB, locEngine);
            locationLayerPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        locEngine = new LostLocationEngine(MapsActivity.this);
        locEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            setCameraPosition(lastLocation);
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 16));
    }*/

}