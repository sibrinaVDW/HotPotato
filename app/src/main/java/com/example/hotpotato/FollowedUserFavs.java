package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
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

public class FollowedUserFavs extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Users");

    String followedUserID;
    List<Data> landmarkData;
    List<Data> userData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_followed_user_favs);
        Intent intent = getIntent();
        followedUserID = intent.getStringExtra("followedUserID");
        landmarkData = new ArrayList<>();
        userData = new ArrayList<>();

        Log.e("Loaded"," followed user page loaded");

        DocumentReference docRef = ref.document(followedUserID).collection("FavouriteLandmarks").document("Landmarks");
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
                                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                                    .build();

                            reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
                                @Override
                                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                                    List<CarmenFeature> results = response.body().features();

                                    if (results.size() > 0) {
                                        CarmenFeature feature;
                                        Point firstResultPoint = results.get(0).center();
                                        feature=results.get(0);

                                        // Toast.makeText(UserHome.this, "" + feature.placeName(), Toast.LENGTH_LONG).show();
                                        List<Data> users = new ArrayList<>();
                                        landmarkData.add(new Data(feature.placeName(), "Click for more options",R.drawable.hotpotato_icon_foreground));
                                        RecyclerView recyclerView = findViewById(R.id.followedUserLandmarks);
                                        RecAdapter adapter = new RecAdapter(landmarkData, getApplication());
                                        recyclerView.setLayoutManager(new LinearLayoutManager(FollowedUserFavs.this));
                                        recyclerView.setAdapter(adapter);

                                    } else {
                                        // No result for the request were found.
                                        Toast.makeText(FollowedUserFavs.this, "Not found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            });
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
    }
}
