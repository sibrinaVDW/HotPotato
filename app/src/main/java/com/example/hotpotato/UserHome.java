package com.example.hotpotato;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class UserHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(MAPBOX_ACCESS_TOKEN)
                .query(Point.fromLngLat(-77.03655, 38.89770))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build();
    }
}