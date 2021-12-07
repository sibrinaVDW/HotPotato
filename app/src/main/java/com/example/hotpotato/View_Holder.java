package com.example.hotpotato;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        TextView name;
        TextView extraInfo;
        ImageView imageView;

        View_Holder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.txtTitle);
        extraInfo = itemView.findViewById(R.id.txtAddInfo);
        imageView = itemView.findViewById(R.id.imgThumbnail);
        }



        @Override
        public void onClick(View view) {
                Context v = view.getContext();
                Intent i = new Intent(v,Ratings.class);
                v.startActivity(i);
                Toast.makeText(v,"Clicked",Toast.LENGTH_SHORT).show();
        }

        }
