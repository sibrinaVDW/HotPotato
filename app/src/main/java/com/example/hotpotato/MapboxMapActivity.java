package com.example.hotpotato;


import android.graphics.Color;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import androidx.core.app.ActivityCompat;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.style.light.Position;
import com.mapbox.android.core.location.LocationEngine;

//import com.mapbox.search.MapboxSearchSdk;
//import com.mapbox.search.location.DefaultLocationProvider;
//import com.mapbox.maps.MapView;
//import com.mapbox.search.ui.view.SearchBottomSheetView;

import java.util.List;

public class MapboxMapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngine, PermissionsListener {
    public LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private PermissionsManager permissionsManager;

    private static final String DISTANCE_SOURCE_ID = "DISTANCE_SOURCE_ID";
    private static final String DISTANCE_LINE_LAYER_ID = "DISTANCE_LINE_LAYER_ID";

    // Adjust private static final variables below to change the example's UI
    private static final int LINE_COLOR = Color.RED;
    private static final float LINE_WIDTH = 2f;

    private static final String ROUTE_LAYER_ID = "route-layer-id";
    private static final String ROUTE_SOURCE_ID = "route-source-id";
    private static final String ICON_LAYER_ID = "icon-layer-id";
    private static final String ICON_SOURCE_ID = "icon-source-id";
    private static final String RED_PIN_ICON_ID = "red-pin-icon-id";
    private MapView viewMap;
    private MapboxMap mapboxMap;
    private DirectionsRoute currentRoute;
    private MapboxDirections client;
    private Point origin;
    private Point destination;
    private String TAG = "LOG";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_mapbox_map);


        viewMap = findViewById(R.id.mapView);
        //viewMap.getMapAsync(this);
        //.setStyleUrl(Style.MAPBOX_STREETS);
        //mapMB.setStyleUrl(Style.MAPBOX_STREETS);
        viewMap.onCreate(savedInstanceState);
        viewMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull MapboxMap mapboxMap) {
                MapboxMapActivity.this.mapboxMap = mapboxMap;
                mapboxMap.setStyle(new Style.Builder()
                        .fromUri(Style.MAPBOX_STREETS)

// Add the source to the map
                        .withSource(new GeoJsonSource(DISTANCE_SOURCE_ID))

// Style and add the LineLayer to the map.
                        .withLayer(new LineLayer(DISTANCE_LINE_LAYER_ID, DISTANCE_SOURCE_ID).withProperties(
                                lineColor(LINE_COLOR),
                                lineWidth(LINE_WIDTH),
                                lineJoin(LINE_JOIN_ROUND))), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);


//                        Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
//                        if (lastKnownLocation!=null){
//                            origin = Point.fromLngLat(lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude());
//                            origin = Point.fromLngLat(-26.1449,28.1702);
//                            destination = Point.fromLngLat(-26.1170, 28.1547);
//                            initSource(style);
//
//                            initLayers(style);
//                            // Get the directions route from the Mapbox Directions API
//                            getRoute(mapboxMap, origin, destination);
//                        }
                        origin = Point.fromLngLat(28.1647, -26.1470);

                        destination = Point.fromLngLat(28.1547, -26.1170);
                        initSource(style);

                        initLayers(style);
                        // Get the directions route from the Mapbox Directions API
                        getRoute(mapboxMap, origin, destination);




                    }
                });
            }
        });

//        SearchBottomSheetView searchBottomSheetView = findViewById(R.id.search_view);
//        searchBottomSheetView.initializeSearch(savedInstanceState, new SearchBottomSheetView.Configuration());

    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

// Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

// Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

// Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

// Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    public void enableLocation() {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            initialLocationLayer();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void initialLocationLayer() {
    }

    private void initializeLocationEngine() {

    }

    /**
     * Add the route and marker sources to the map
     */
    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    /**
     * Add the route and marker icon layers to the map
     */
    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.mapbox_marker_icon_default)));

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                iconImage(RED_PIN_ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[]{0f, -9f})));
    }

        /**
         * Make a request to the Mapbox Directions API. Once successful, pass the route to the
         * route layer.
         * @param mapboxMap the Mapbox map object that the route will be drawn on
         * @param origin      the starting point of the route
         * @param destination the desired finish point of the route
         */
        private void getRoute (MapboxMap mapboxMap, Point origin, Point destination){
            client = MapboxDirections.builder()
                    .origin(origin)
                    .destination(destination)
                    .overview(DirectionsCriteria.OVERVIEW_FULL)
                    .profile(DirectionsCriteria.PROFILE_DRIVING)
                    .accessToken(getString(R.string.mapbox_access_token))
                    .build();

            client.enqueueCall(new Callback<DirectionsResponse>() {
                @Override
                public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                    Timber.d("Response code: " + response.code());
                    if (response.body() == null) {
                        Timber.e("No routes found, make sure you set the right user and access token.");
                        return;
                    } else if (response.body().routes().size() < 1) {
                        Timber.e("No routes found");
                        return;
                    }

// Get the directions route
                    currentRoute = response.body().routes().get(0);

// Make a toast which displays the route's distance
                    Toast.makeText(MapboxMapActivity.this,
                            (currentRoute.distance()/1000) + " Kilometers", Toast.LENGTH_LONG).show();

                    if (mapboxMap != null) {
                        mapboxMap.getStyle(new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {

// Retrieve and update the source designated for showing the directions route
                                GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

// Create a LineString with the directions route's geometry and
// reset the GeoJSON source for the route LineLayer source
                                if (source != null) {
                                    source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), PRECISION_6));
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                    Timber.e("Error: " + throwable.getMessage());
                    Toast.makeText(MapboxMapActivity.this, "Error: " + throwable.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        protected void onStart () {
            super.onStart();
            viewMap.onStart();
        }

        @Override
        protected void onStop () {
            super.onStop();
            viewMap.onStop();
        }

        @Override
        protected void onResume () {
            super.onResume();
            viewMap.onResume();
        }

        @Override
        protected void onSaveInstanceState (@NonNull Bundle outState){
            super.onSaveInstanceState(outState);
            viewMap.onSaveInstanceState(outState);
        }

        @Override
        public void onLowMemory () {
            super.onLowMemory();
            viewMap.onLowMemory();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
// Cancel the Directions API request
            if (client != null) {
                client.cancelCall();
            }
            viewMap.onDestroy();
        }

        @Override
        public void onExplanationNeeded (List < String > list) {
            Toast.makeText(this, "ON EXPLANATION NEEDED",
                    Toast.LENGTH_LONG).show();
        }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, "PERMISSION NOT GRANTED", Toast.LENGTH_LONG).show();
            finish();
        }
    }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        @Override
        public void getLastLocation
        (@NonNull LocationEngineCallback < LocationEngineResult > locationEngineCallback) throws
        SecurityException {

        }

        @Override
        public void requestLocationUpdates (@NonNull LocationEngineRequest
        locationEngineRequest, @NonNull LocationEngineCallback < LocationEngineResult > locationEngineCallback, @Nullable Looper
        looper) throws SecurityException {

        }

        @Override
        public void requestLocationUpdates (@NonNull LocationEngineRequest
        locationEngineRequest, PendingIntent pendingIntent) throws SecurityException {

        }

        @Override
        public void removeLocationUpdates
        (@NonNull LocationEngineCallback < LocationEngineResult > locationEngineCallback) {

        }

        @Override
        public void removeLocationUpdates (PendingIntent pendingIntent){

        }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        
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