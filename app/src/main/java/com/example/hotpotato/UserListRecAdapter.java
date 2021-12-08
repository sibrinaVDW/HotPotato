package com.example.hotpotato;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.style.light.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserListRecAdapter extends RecyclerView.Adapter<View_Holder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private String currentUserUid = currentUser.getUid();


    List<Data> landmarkData;
    List<Data> list = Collections.emptyList();
    Context context;
    Application application;

    public UserListRecAdapter(List<Data> data, Application application) {
        this.list = data;
        this.context = application;
        this.application = application;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list, parent, false);
        View_Holder holder = new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {


        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).name);
        holder.extraInfo.setText(list.get(position).extraInfo);
        holder.imageView.setImageResource(list.get(position).imageId);




        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                landmarkData = new ArrayList<>();
                /*Intent intent = new Intent(v.getContext(),FollowedUserFavs.class);
                intent.putExtra("followedUserID",list.get(holder.getAdapterPosition()).userID);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);*/
                LayoutInflater li = LayoutInflater.from(v.getContext().getApplicationContext());
                View userListpopup = LayoutInflater.from(v.getContext()).inflate(R.layout.activity_followed_user_favs,null);
                AlertDialog.Builder alertBuild = new AlertDialog.Builder(v.getContext()).setView(userListpopup).setTitle((list.get(holder.getAdapterPosition()).name+"'s Favourite Landmarks"));

                DocumentReference docRef = db.collection("Users").document(list.get(holder.getAdapterPosition()).userID).collection("FavouriteLandmarks").document("Landmarks");
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
                                            .accessToken(context.getString(R.string.mapbox_access_token))
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

                                                landmarkData.add(new Data(feature.placeName(), "",R.drawable.hotpotato_icon_foreground));
                                                RecyclerView recyclerView = userListpopup.findViewById(R.id.followedUserLandmarks);

                                                RecAdapter adapter = new RecAdapter(landmarkData, application);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));
                                                recyclerView.setAdapter(adapter);

                                            } else {
                                                // No result for the request were found.
                                                Toast.makeText(v.getContext(), "Not found", Toast.LENGTH_SHORT).show();
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

                AlertDialog alertDiag = alertBuild.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
