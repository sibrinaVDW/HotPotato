package com.example.hotpotato;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class View_Holder extends RecyclerView.ViewHolder {

        TextView name;
        TextView extraInfo;
        ImageView imageView;

        View_Holder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.txtTitle);
        extraInfo = itemView.findViewById(R.id.txtAddInfo);
        imageView = itemView.findViewById(R.id.imgThumbnail);

        itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        //popup to go to map.
                        //opts for lists (friends/fav landmarks)
                        //Intent i = new Intent(View_Holder.this, Ratings.class);

                }
        });


        }
        }
