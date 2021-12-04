package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHome extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Users");
    ImageButton goToMap;
    ImageButton imageButton13;
    TextView landmarksDisplay;
    String dispLandmarks = "";
    List<Data> landmarkData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        goToMap = findViewById(R.id.imageButton9);
        //landmarksDisplay = findViewById(R.id.txtLandmarks);
        Intent intent = getIntent();
        String userID = intent.getStringExtra("user").toString();
        landmarkData = new ArrayList<>();
        //landmarkData = fill_with_data();
        DocumentReference docRef = ref.document(userID).collection("FavouriteLandmarks").document("Landmarks");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<Object> geoPoint = (List<Object>) document.get("ListLandmarks");
                        for (Object geoObject : geoPoint){
                            GeoPoint gp = (GeoPoint) geoObject;
                            MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                                    .accessToken(getString(R.string.mapbox_access_token))
                                    .query(Point.fromLngLat(gp.getLongitude(),gp.getLatitude()))
                                    .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
                                    .build();

                            reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
                                @Override
                                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                                    List<CarmenFeature> results = response.body().features();

                                    if (results.size() > 0) {
                                        CarmenFeature feature;
                                        Point firstResultPoint = results.get(0).center();
                                        feature=results.get(0);
                                        Toast.makeText(UserHome.this, "" + feature.placeName(), Toast.LENGTH_LONG).show();
                                        //landmarksDisplay.setText(feature.placeName());
                                        dispLandmarks += "" + feature.placeName() + "\n";
                                        //landmarkData.add(new Data(feature.placeName(),R.drawable.hotpotato_icon_foreground));
                                    } else {
                                        // No result for your request were found.
                                        Toast.makeText(UserHome.this, "Not found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
                        }

                        //landmarksDisplay.setText(dispLandmarks);

                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recLandmarkView);
                        FavLandmarksAdapter adapter = new FavLandmarksAdapter(landmarkData, getApplication());
                        recyclerView.setLayoutManager(new LinearLayoutManager(UserHome.this));
                        recyclerView.setAdapter(adapter);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });




        goToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserHome.this,MapboxMapActivity.class);
                startActivity(i);
            }
        });

        imageButton13 = (ImageButton) findViewById(R.id.imageButton13);

        imageButton13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext() , MenuPop.class);
                startActivity(i);
            }
        });
    }

    //tester, this somehow works
    public List<Data> fill_with_data() {

        List<Data> data = new ArrayList<>();
        data.add(new Data("C", R.drawable.hotpotato_icon_foreground));
        data.add(new Data("C++", R.drawable.hotpotato_icon_foreground));
        data.add(new Data("Java", R.drawable.hotpotato_icon_foreground));
        return data;
    }
}