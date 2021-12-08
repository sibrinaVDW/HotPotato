package com.example.hotpotato;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;
import java.util.List;

public class RecAdapter extends RecyclerView.Adapter<View_Holder> {

        List<Data> list = Collections.emptyList();
        Context context;

public RecAdapter(List<Data> data, Application application) {
        this.list = data;
        this.context = application;
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
public void onBindViewHolder(@NonNull View_Holder holder, @SuppressLint("RecyclerView") int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.name.setText(list.get(position).name);
        holder.extraInfo.setText(list.get(position).extraInfo);
        holder.imageView.setImageResource(list.get(position).imageId);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        //Toast.makeText(context, "Recycle Click" + list.get(position), Toast.LENGTH_SHORT).show();

                        Context v = view.getContext();
                        showOpts(v,list.get(position).name);
                        /*Intent i = new Intent(v,Ratings.class);
                        Toast.makeText(context, "clicked  " + , Toast.LENGTH_SHORT).show();
                        i.putExtra("landmark",list.get(position).name);list.get(position).name
                        v.startActivity(i);*/
                        //Toast.makeText(v,"Clicked",Toast.LENGTH_SHORT).show();
                }
        });

        }

        private void showOpts(Context v,String landmarkName)
        {
                final Dialog dialog = new Dialog(v);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottomratinglayout);

                LinearLayout vewMapLayout = dialog.findViewById(R.id.layoutViewmap);
                LinearLayout viewRatingsLayout = dialog.findViewById(R.id.layoutViewratings);
                LinearLayout createRatingsLayout = dialog.findViewById(R.id.layoutAddrating);

                vewMapLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                FirebaseAuth mAuth;
                                mAuth = FirebaseAuth.getInstance();
                                Intent i = new Intent(v.getContext(),MapboxMapActivity.class);
                                //.makeText(context, "clicked  " + , Toast.LENGTH_SHORT).show();
                                i.putExtra("favorite",landmarkName);
                                i.putExtra("user",mAuth.getCurrentUser().getUid());
                                v.getContext().startActivity(i);
                                //Toast.makeText(MapboxMapActivity.this, "Distance is chosen",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                        }
                });

                viewRatingsLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent i = new Intent(v.getContext(),Ratings.class);
                                //Toast.makeText(context, "clicked  " + landmarkName, Toast.LENGTH_SHORT).show();
                                i.putExtra("landmark",landmarkName);
                                v.getContext().startActivity(i);
                                //Toast.makeText(MapboxMapActivity.this, "Time is chosen",Toast.LENGTH_LONG).show();
                                dialog.dismiss();

                        }
                });

                createRatingsLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent i = new Intent(v.getContext(),AddRating.class);
                                //Toast.makeText(context, "clicked  " + landmarkName, Toast.LENGTH_SHORT).show();
                                i.putExtra("landmark",landmarkName);
                                v.getContext().startActivity(i);
                                //Toast.makeText(MapboxMapActivity.this, "Time is chosen",Toast.LENGTH_LONG).show();
                                //start intent for creating rating.
                                dialog.dismiss();

                        }
                });

                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
        }


@Override
public int getItemCount() {
        return list.size();
        }
        }
