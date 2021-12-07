package com.example.hotpotato;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
                        Intent i = new Intent(v,Ratings.class);
                        Toast.makeText(context, "clicked  " + list.get(position).name, Toast.LENGTH_SHORT).show();
                        i.putExtra("landmark",list.get(position).name);
                        v.startActivity(i);
                        //Toast.makeText(v,"Clicked",Toast.LENGTH_SHORT).show();
                }
        });

        }

@Override
public int getItemCount() {
        return list.size();
        }
        }
