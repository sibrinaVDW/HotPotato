package com.example.hotpotato;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Collections;
import java.util.List;

public class View_Holder extends RecyclerView.ViewHolder {

        TextView name;
        // address;
        ImageView imageView;

        View_Holder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.txtTitle);
        //address = itemView.findViewById(R.id.txtAddress);
        imageView = itemView.findViewById(R.id.imgThumbnail);

        itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        //popup to go to map.
                }
        });


        }
        }
