package com.example.hotpotato;


import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.api.tilequery.MapboxTilequery;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;

import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
//import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.Image;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.VectorSource;
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

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineDasharray;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineTranslate;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

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

import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textHaloBlur;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.textSize;

import org.w3c.dom.Text;

import java.util.List;

public class MapboxMapActivity extends AppCompatActivity implements LocationEngine, PermissionsListener, MapboxMap.OnCameraIdleListener, OnMapReadyCallback, Callback<DirectionsResponse> {
    public LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private PermissionsManager permissionsManager;
    String userID;

    private static final String DISTANCE_SOURCE_ID = "DISTANCE_SOURCE_ID";
    private static final String DISTANCE_LINE_LAYER_ID = "DISTANCE_LINE_LAYER_ID";

    private static final String DIRECTIONS_LAYER_ID = "DIRECTIONS_LAYER_ID";
    private static final String LAYER_BELOW_ID = "road-label-small";
    private static final String SOURCE_ID = "SOURCE_ID";

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
    private Point origin = Point.fromLngLat(90.399452, 23.777176);
    private Point destination = Point.fromLngLat(90.399452, 23.777176);
    private String TAG = "LOG";
    private FeatureCollection dashedLineDirectionsFeatureCollection;
    int c = 0;
    private String symbolIconId = "symbolIconId";
    String startLocation="";
    String endLocation="";
    double distance;
    String st;
    private String geojsonSourceLayerId = "geojsonSourceLayerId";
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private CarmenFeature home;
    private CarmenFeature work;

    private String[] profiles = new String[]{
            DirectionsCriteria.PROFILE_DRIVING,
            DirectionsCriteria.PROFILE_CYCLING,
            DirectionsCriteria.PROFILE_WALKING
    };

    private TextView poiInfoText;
    private String selectedPointInfo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Users");
    String units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_mapbox_map);
        Intent intent = getIntent();
        userID = intent.getStringExtra("user");

        poiInfoText = findViewById(R.id.elevation_query_api_response_elevation_numbers_only);



        viewMap = findViewById(R.id.mapView);
        //viewMap.getMapAsync(this);
        //.setStyleUrl(Style.MAPBOX_STREETS);
        //mapMB.setStyleUrl(Style.MAPBOX_STREETS);
        viewMap.onCreate(savedInstanceState);
        viewMap.getMapAsync(this);
        /*viewMap.getMapAsync(new OnMapReadyCallback() {
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
                        ImageView hoveringMarker = new ImageView(MapboxMapActivity.this);
                        hoveringMarker.setImageResource(R.drawable.mapbox_marker_icon_default);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
                        hoveringMarker.setLayoutParams(params);
                        viewMap.addView(hoveringMarker);

                        mapboxMap.addOnCameraIdleListener(MapboxMapActivity.this);

                        Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
                        if (lastKnownLocation!=null){
                            origin = Point.fromLngLat(lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude());
                            //origin = Point.fromLngLat(-26.1449,28.1702);
                            //destination = Point.fromLngLat(lastKnownLocation.getLongitude()+0.05,lastKnownLocation.getLatitude()+0.05);
                            initSource(style);

                            initLayers(style);
                            //initDottedLineSourceAndLayer(style);
                            // Get the directions route from the Mapbox Directions API
                            //getRoute(mapboxMap, origin, destination);
                        }
                        //origin = Point.fromLngLat(28.1647, -26.1470);
                        //initDottedLineSourceAndLayer(style);
                        //destination = Point.fromLngLat(28.1547, -26.1170);
                        //initSource(style);

                        //initLayers(style);
                        // Get the directions route from the Mapbox Directions API
                        //getRoute(mapboxMap, origin, destination);




                    }
                });
            }
        });*/

//        SearchBottomSheetView searchBottomSheetView = findViewById(R.id.search_view);
//        searchBottomSheetView.initializeSearch(savedInstanceState, new SearchBottomSheetView.Configuration());

    }

    @Override
    public void onMapReady(final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override

            public void onStyleLoaded(@NonNull Style style) {

                enableLocationComponent(style);
                initSearchFab();

                addUserLocations();
                //Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_location_on_24, null);
                //Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                // Add the symbol layer icon to map for future use
                //style.addImage(symbolIconId, mBitmap);

                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);

                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);


                initSource(style);

                initLayers(style);
                //  CameraPosition position = new CameraPosition.Builder().target(new LatLng(destination.latitude(), destination.longitude()))
                //      .zoom(13).tilt(13).build();
                //  mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100);
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    LatLng source;

                    @Override
                    public boolean onMapClick(@NonNull LatLng point) {

                        //Popup for clicking on a landmark to bring up options for that landmark.
                        LayoutInflater li = LayoutInflater.from(getApplicationContext());
                        View popupView = LayoutInflater.from(MapboxMapActivity.this).inflate(R.layout.activity_pop_information,null);
                        AlertDialog.Builder alertBuild = new AlertDialog.Builder(MapboxMapActivity.this).setView(popupView).setTitle("Select Option");
                        AlertDialog alertDiag = alertBuild.show();

                        TextView address = popupView.findViewById(R.id.txtPopupAddress);
                        reverseGeocodeFunc(point,c);
                        address.setText("" + selectedPointInfo);
                        Toast.makeText(MapboxMapActivity.this, ""  + selectedPointInfo, Toast.LENGTH_LONG).show();

                        ImageButton route = popupView.findViewById(R.id.btnRoute);
                        route.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                moveDestinationMarkerToNewLocation(point);
                                reverseGeocodeFunc(point, c);
                                alertDiag.dismiss();
                            }
                        });

                        ImageButton favourite  = popupView.findViewById(R.id.btnFavorite);
                        favourite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CollectionReference ref = db.collection("Users");
                                GeoPoint newPoint = new GeoPoint(point.getLatitude(),point.getLongitude());
                                ref.document(userID).collection("FavouriteLandmarks").document("Landmarks")
                                 .update("ListLandmarks", FieldValue.arrayUnion(newPoint)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(MapboxMapActivity.this, "Added to your favorites!", Toast.LENGTH_LONG).show();
                                    }
                                });
                                alertDiag.dismiss();
                            }
                        });

                        ImageButton ratings = popupView.findViewById(R.id.btnMapPopupBack);
                        ratings.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDiag.dismiss();
                                reverseGeocodeFunc(point, c);
                                Intent i = new Intent(MapboxMapActivity.this,Ratings.class);
                                i.putExtra("place", selectedPointInfo);
                                startActivity(i);
                            }
                        });

                        ImageButton back = popupView.findViewById(R.id.btnMapPopupBack);
                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDiag.dismiss();
                            }
                        });

                        //reverseGeocodeFunc(point, 0);

                        /*if (c == 0) {
                            origin = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());
                            source = point;
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(new LatLng(mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude()));
                            markerOptions.title("Source");
                            mapboxMap.addMarker(markerOptions);
                            //reverseGeocodeFunc(point, c);

                            destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                            //getRoute(mapboxMap, origin, destination);
                            //MarkerOptions markerOptions2 = new MarkerOptions();
                            //markerOptions2.position(point);
                            //markerOptions2.title("destination");
                            //mapboxMap.addMarker(markerOptions2);
                            reverseGeocodeFunc(point, c);
                            //getRoute(mapboxMap, origin, destination, profiles[0]);
                            moveDestinationMarkerToNewLocation(point);
                        }
                        *//*if (c == 1) {
                            destination = Point.fromLngLat(point.getLongitude(), point.getLatitude());
                            getRoute(mapboxMap, origin, destination);
                            MarkerOptions markerOptions2 = new MarkerOptions();
                            markerOptions2.position(point);
                            markerOptions2.title("destination");
                            mapboxMap.addMarker(markerOptions2);
                            reverseGeocodeFunc(point, c);
                            //getRoute(mapboxMap, origin, destination);
                            // double d = point.distanceTo(source);


                        }*//*



                      *//*  startActivityForResult(
                                new PlacePicker.IntentBuilder()
                                        .accessToken("pk.eyJ1IjoiemFoaWQxNiIsImEiOiJja2UxZ3lpaGE0NHFuMnJtcXc5djcxeGVtIn0.V5lnAKqektnfC1pARBQYUQ")
                                        .placeOptions(PlacePickerOptions.builder()
                                                .statingCameraPosition(new CameraPosition.Builder()
                                                        .target(point).zoom(16).build())
                                                .build())
                                        .build(this), REQUEST_CODE);
                        *//*
                        if (c > 0) {
                            c = 0;
                            //recreate();
                            moveDestinationMarkerToNewLocation(point);
                            reverseGeocodeFunc(point, c);
                            // mapboxMap.clear();
                            //   Toast.makeText(MainActivity.this,d+" metres", Toast.LENGTH_LONG).show();

                        }

                        c++;*/
                        return true;
                    }

                });

            }
        });
    }
    //tile query stuff that gets elevation and puts numbers where you click,


    /*private void makeElevationRequestToTilequeryApi(@NonNull final Style style, @NonNull LatLng point) {
        MapboxTilequery elevationQuery = MapboxTilequery.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .tilesetIds("mapbox.mapbox-terrain-v2")
                .query(Point.fromLngLat(point.getLongitude(), point.getLatitude()))
                .geometry("polygon")
                .layers("contour")
                .build();

        elevationQuery.enqueueCall(new Callback<FeatureCollection>() {
            @Override
            public void onResponse(Call<FeatureCollection> call, Response<FeatureCollection> response) {

                if (response.body().features() != null) {
                    List<Feature> featureList = response.body().features();

                    String listOfElevationNumbers = "";

// Build a list of the elevation numbers in the response.
                    for (Feature singleFeature : featureList) {
                        listOfElevationNumbers = listOfElevationNumbers + singleFeature.getStringProperty("ele") + ", ";
                    }

// Set this TextViews with the response info/JSON.
                    elevationQueryNumbersOnlyResponseTextView.setText(String.format(getString(
                            R.string.elevation_numbers_only_textview), featureList.size(), listOfElevationNumbers));
                    elevationQueryJsonResponseTextView.setText(response.body().toJson());

// Update the SymbolLayer that's responsible for showing the number text with the highest/lowest
// elevation number
                    if (featureList.size() > 0) {
                        GeoJsonSource resultSource = style.getSourceAs(RESULT_GEOJSON_SOURCE_ID);
                        if (resultSource != null) {
                            resultSource.setGeoJson(featureList.get(featureList.size() - 1));
                        }
                    }
                } else {
                    String noFeaturesString = getString(R.string.elevation_tilequery_no_features);
                    Timber.d(noFeaturesString);
                    Toast.makeText(MapboxMapActivity.this, noFeaturesString, Toast.LENGTH_SHORT).show();
                    elevationQueryNumbersOnlyResponseTextView.setText(noFeaturesString);
                    elevationQueryJsonResponseTextView.setText(noFeaturesString);
                }
            }

            @Override
            public void onFailure(Call<FeatureCollection> call, Throwable throwable) {
                Timber.d("Request failed: %s", throwable.getMessage());
                Toast.makeText(MapboxMapActivity.this,
                        R.string.elevation_tilequery_api_response_error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    *//**
     * Add a map layer which will show a text number of the highest or lowest elevation number returned
     * by the Tilequery API.
     *//*
    private void addResultLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(RESULT_GEOJSON_SOURCE_ID));
        loadedMapStyle.addLayer(new SymbolLayer(LAYER_ID, RESULT_GEOJSON_SOURCE_ID).withProperties(
                textField(get("ele")),
                textColor(Color.BLUE),
                textSize(23f),
                textHaloBlur(10f),
                textIgnorePlacement(true),
                textAllowOverlap(true)
        ));
    }
*/

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(MapboxMapActivity.this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(MapboxMapActivity.this, loadedMapStyle).build());

            // Enable to make component visible
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
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


    private void reverseGeocodeFunc(LatLng point,int c)
    {
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken("sk.eyJ1IjoibWFqb3Job2JvIiwiYSI6ImNrdnR6cG05d2JodWozMHM3MW5udzhvMWkifQ.9hXJQ3dq_vYeRZqLA5YIog")
                .query(Point.fromLngLat(point.getLongitude(), point.getLatitude()))
                .geocodingTypes(GeocodingCriteria.TYPE_POI)
                .build();
        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                List<CarmenFeature> results = response.body().features();

                if (results.size() > 0) {
                    // CarmenFeature feature =results.get(0);
                    CarmenFeature feature;
                    // Log the first results Point.
                    Point firstResultPoint = results.get(0).center();
                    //   for (int i = 0; i < results.size(); i++) {
                    //  feature = results.get(i);
                    feature=results.get(0);
                    if(c==0)
                    {
                        //startLocation+=feature.placeName();
                        //startLocation=startLocation.replace(", Dhaka, Bangladesh",".");
                        //TextView tv =findViewById(R.id.s);
                        //tv.setText(startLocation);
                        endLocation += feature.placeName();
                        endLocation = endLocation.replace(", Dhaka, Bangladesh", ".");

                    }
                    if(c==1) {

                        //TextView tv2 = findViewById(R.id.d);
                        //tv2.setText(endLocation);
                    }

                    // endLocation = endLocation.replace(",Dhaka,Bangladesh", " ");
                    // Toast.makeText(MapsActivity.this, endLocation, Toast.LENGTH_LONG).show();





                    // startLocation=feature.placeName()+"";

                    //   Toast.makeText(MainActivity.this, "" + results.get(i), Toast.LENGTH_LONG).show();
                    selectedPointInfo = feature.placeName();

                    //Toast.makeText(MapboxMapActivity.this, "" + feature.placeName(), Toast.LENGTH_LONG).show();
                    poiInfoText.setText(""+feature.placeName());


                    /*Intent i = new Intent(getApplicationContext() , PopInformation.class);
                    i.putExtra("address", feature.address());
                    i.putExtra("name",feature.placeName());
                    i.putExtra("latlng",point);
                    startActivity(i);*/
                    //  }
                    Log.d("MyActivity", "onResponse: " + firstResultPoint.toString());

                } else {

                    // No result for your request were found.
                    Toast.makeText(MapboxMapActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    private void initLayers(@NonNull Style loadedMapStyle) {
        LineLayer routeLayer = new LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID);

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(5f),
                lineColor(Color.parseColor("#009688"))
        );
        loadedMapStyle.addLayer(routeLayer);

// Add the red marker icon image to the map
        loadedMapStyle.addImage(RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.mapbox_marker_icon_default)));


// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(new SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                PropertyFactory.iconImage(RED_PIN_ICON_ID),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconOffset(new Float[]{0f, -9f})));
    }

    private void initSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(ROUTE_SOURCE_ID));

        GeoJsonSource iconGeoJsonSource = new GeoJsonSource(ICON_SOURCE_ID, FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(Point.fromLngLat(origin.longitude(), origin.latitude())),
                Feature.fromGeometry(Point.fromLngLat(destination.longitude(), destination.latitude()))}));
        loadedMapStyle.addSource(iconGeoJsonSource);
    }

    @Override
    public void onCameraIdle() {
        /*if (mapboxMap != null) {
            Location lastKnownLocation = mapboxMap.getLocationComponent().getLastKnownLocation();
            if (lastKnownLocation!=null){
                origin = Point.fromLngLat(lastKnownLocation.getLongitude(),lastKnownLocation.getLatitude());
                //origin = Point.fromLngLat(-26.1449,28.1702);
                //destination = Point.fromLngLat(lastKnownLocation.getLongitude()+0.05,lastKnownLocation.getLatitude()+0.05);
                // Get the directions route from the Mapbox Directions API
                //getRoute(mapboxMap, origin, destination);
            }
            if (origin != null){
                Log.e("origin isnt nulL!!!!!!!!","NOT NULL ORIGIN");
                Point destinationPoint = Point.fromLngLat(
                        mapboxMap.getCameraPosition().target.getLongitude(),
                        mapboxMap.getCameraPosition().target.getLatitude());
                getRoute(mapboxMap, origin, destinationPoint);
            }
            Log.e("origin is nulL!!!!!!!!","WHY IS ORIGIN NULl?????????");
        }*/
    }
    private void initSearchFab() {
        findViewById(R.id.fab_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new PlaceAutocomplete.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken() != null ? Mapbox.getAccessToken() : getString(R.string.mapbox_access_token))
                        .placeOptions(PlaceOptions.builder()
                                .backgroundColor(Color.parseColor("#EEEEEE"))
                                .limit(10)
                                .addInjectedFeature(home)
                                .addInjectedFeature(work)
                                .build(PlaceOptions.MODE_CARDS))
                        .build(MapboxMapActivity.this);
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
            }
        });
    }

    private void addUserLocations() {
        home = CarmenFeature.builder().text("Mapbox SF Office")
                .geometry(Point.fromLngLat(-122.3964485, 37.7912561))
                .placeName("50 Beale St, San Francisco, CA")
                .id("mapbox-sf")
                .properties(new JsonObject())
                .build();

        work = CarmenFeature.builder().text("Mapbox DC Office")
                .placeName("740 15th Street NW, Washington DC")
                .geometry(Point.fromLngLat(-77.0338348, 38.899750))
                .id("mapbox-dc")
                .properties(new JsonObject())
                .build();
    }

    /**
     * Make a request to the Mapbox Directions API. Once successful, pass the route to the
     * route layer.
     * @param mapboxMap the Mapbox map object that the route will be drawn on
     * @param destination the desired finish point of the route
     */
    public void getRoute (MapboxMap mapboxMap, Point destination, String profile){

        origin = Point.fromLngLat(mapboxMap.getLocationComponent().getLastKnownLocation().getLongitude(), mapboxMap.getLocationComponent().getLastKnownLocation().getLatitude());

        client = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(profile)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        client.enqueueCall( this);

        //popup for time and distance.
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View popupView = LayoutInflater.from(MapboxMapActivity.this).inflate(R.layout.activity_pop_information,null);
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(MapboxMapActivity.this).setView(popupView).setTitle("Select Option");
        AlertDialog alertDiag = alertBuild.show();

        DocumentReference docRef = ref.document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        DocumentSnapshot doc = task.getResult();
                        if (document != null) {
                            units = document.getString("unitsPref");
                            //also get landmark pref.
                        }

                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
            /*client.enqueueCall(new Callback<DirectionsResponse>() {
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
                    Toast.makeText(MapboxMapActivity.this,String.format("%.2f Kilometers",currentRoute.distance()/1000)
                            , Toast.LENGTH_LONG).show();

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
                    drawNavigationPolylineRoute(response.body().routes().get(0));
                }

                @Override
                public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                    Timber.e("Error: " + throwable.getMessage());
                    Toast.makeText(MapboxMapActivity.this, "Error: " + throwable.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });*/
    }

    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
        boolean routeFound = false;
// You can get the generic HTTP info about the response
        if (response.body() == null) {
            Toast.makeText(MapboxMapActivity.this, "NO routes found make sure to set right user and access token", Toast.LENGTH_LONG).show();
            return;
        } else if (response.body().routes().size() < 1) {
            Toast.makeText(MapboxMapActivity.this, "NO routes found", Toast.LENGTH_LONG).show();
        }else{
            routeFound = true;
        }

        if (routeFound){
            final DirectionsRoute currentRoute = response.body().routes().get(0);
            // Toast.makeText(MainActivity.this,currentRoute.distance()+" metres ",Toast.LENGTH_SHORT).show();
            if(units == "km"){
                distance = currentRoute.distance() / 1000;
            }
            else if(units == "mi"){
                distance = (currentRoute.distance() / 1000) / 1.609344;
            }

            st = String.format("%.2f K.M", distance);
            TextView dv=findViewById(R.id.distanceText);
            dv.setText(st);

            if (mapboxMap != null) {
                mapboxMap.getStyle(new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

// Retrieve and update the source designated for showing the directions route
                        GeoJsonSource source = style.getSourceAs(ROUTE_SOURCE_ID);

// Create a LineString with the directions route's geometry and
// reset the GeoJSON source for the route LineLayer source
                        if (source != null) {
                            source.setGeoJson(LineString.fromPolyline(currentRoute.geometry(), Constants.PRECISION_6));
                        }
                    }

                });

            }
        }

// Get the directions route

        //String f=startLocation+endLocation+st;
        //TextView tv=findViewById(R.id.s);
        // TextView tv2 = findViewById(R.id.d);
        // startLocation=startLocation.replace("Bangladesh",".");
        //  endLocation=endLocation.replace("Bangladesh",".");

        // tv.setText(startLocation);
        // tv2.setText(endLocation);


        // tv.setText(distance+" K.M");
        //Toast.makeText(MainActivity.this,st,Toast.LENGTH_LONG).show();




    }

    @Override
    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
        Timber.e("Error: " + throwable.getMessage());
        Toast.makeText(MapboxMapActivity.this, "Error: " + throwable.getMessage(),
                Toast.LENGTH_SHORT).show();
    }


    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {

            // Retrieve selected location's CarmenFeature
            CarmenFeature selectedCarmenFeature = PlaceAutocomplete.getPlace(data);

            // Create a new FeatureCollection and add a new Feature to it using selectedCarmenFeature above.
            // Then retrieve and update the source designated for showing a selected location's symbol layer icon




            if (mapboxMap != null) {
                Style style = mapboxMap.getStyle();
                if (style != null) {
                    GeoJsonSource source = style.getSourceAs(geojsonSourceLayerId);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(
                                new Feature[]{Feature.fromJson(selectedCarmenFeature.toJson())}));
                    }



                    // Move map camera to the selected location
                    mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                                            ((Point) selectedCarmenFeature.geometry()).longitude()))
                                    .zoom(14)
                                    .build()), 4000);


                    moveDestinationMarkerToNewLocation(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                            ((Point) selectedCarmenFeature.geometry()).longitude()));
                    reverseGeocodeFunc(new LatLng(((Point) selectedCarmenFeature.geometry()).latitude(),
                            ((Point) selectedCarmenFeature.geometry()).longitude()), c);

                }
            }
        }

        if(requestCode == 2){
            //response from popup.
        }

        /*if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
// Retrieve the information from the selected location's CarmenFeature
            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
// Set the TextView text to the entire CarmenFeature. The CarmenFeature
// also be parsed through to grab and display certain information such as
// its placeName, text, or coordinates.
            if (carmenFeature != null) {
                Toast.makeText(MapsActivity.this, String.format(address
                        , carmenFeature.toJson()), Toast.LENGTH_SHORT).show();
            }
        }*/
    }
    /**
     * Update the GeoJson data that's part of the LineLayer.
     *
     * @param route The route to be drawn in the map's LineLayer that was set up above.
     */
    private void drawNavigationPolylineRoute(final DirectionsRoute route) {
        if (mapboxMap != null) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    List<Feature> directionsRouteFeatureList = new ArrayList<>();
                    LineString lineString = LineString.fromPolyline(route.geometry(), PRECISION_6);
                    List<Point> coordinates = lineString.coordinates();
                    for (int i = 0; i < coordinates.size(); i++) {
                        directionsRouteFeatureList.add(Feature.fromGeometry(LineString.fromLngLats(coordinates)));
                    }
                    dashedLineDirectionsFeatureCollection = FeatureCollection.fromFeatures(directionsRouteFeatureList);
                    GeoJsonSource source = style.getSourceAs(SOURCE_ID);
                    if (source != null) {
                        source.setGeoJson(dashedLineDirectionsFeatureCollection);
                    }
                }
            });
        }
    }
    /**
     * Move the destination marker to wherever the map was tapped on.
     *
     * @param pointToMoveMarkerTo where the map was tapped on
     */
    public void moveDestinationMarkerToNewLocation(LatLng pointToMoveMarkerTo) {
        mapboxMap.getStyle(new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                GeoJsonSource destinationIconGeoJsonSource = style.getSourceAs(ICON_SOURCE_ID);
                if (destinationIconGeoJsonSource != null) {
                    destinationIconGeoJsonSource.setGeoJson(Feature.fromGeometry(Point.fromLngLat(
                            pointToMoveMarkerTo.getLongitude(), pointToMoveMarkerTo.getLatitude())));
                            getRoute(mapboxMap,Point.fromLngLat(pointToMoveMarkerTo.getLongitude(),pointToMoveMarkerTo.getLatitude()),profiles[0]);
                }
            }
        });
    }
    /**
     * Set up a GeoJsonSource and LineLayer in order to show the directions route from the device location
     * to the place picker location
     */
    private void initDottedLineSourceAndLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(SOURCE_ID));
        loadedMapStyle.addLayerBelow(
                new LineLayer(
                        DIRECTIONS_LAYER_ID, SOURCE_ID).withProperties(
                        lineWidth(4.5f),
                        lineColor(Color.BLACK),
                        lineTranslate(new Float[] {0f, 4f}),
                        lineDasharray(new Float[] {1.2f, 1.2f})
                ), LAYER_BELOW_ID);
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
    public void onPointerCaptureChanged(boolean hasCapture) {

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