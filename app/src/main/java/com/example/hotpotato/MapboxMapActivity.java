package com.example.hotpotato;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.maps.MapView;


public class MapboxMapActivity extends AppCompatActivity {

    MapView mapView;
    com.mapbox.mapboxsdk.maps.MapView viewMap;
    MapboxMap mapMB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapbox_map);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        viewMap = findViewById(R.id.mapView);
        //viewMap.onCreate(savedInstanceState);
        viewMap.getMapAsync(this::onMapReady);
        //viewMap.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS);
        /*mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((OnMapReadyCallback) this);*/
    }

    public void onMapReady(MapboxMap mapboxMap) {
        mapMB = mapboxMap;
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewMap.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewMap.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewMap.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewMap.onSaveInstanceState(outState);
    }

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

}