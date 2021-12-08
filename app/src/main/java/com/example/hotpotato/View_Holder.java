package com.example.hotpotato;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class View_Holder extends RecyclerView.ViewHolder  {

        TextView name;
        TextView extraInfo;
        ImageView imageView;

        View_Holder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.txtTitle);
        extraInfo = itemView.findViewById(R.id.txtAddInfo);
        imageView = itemView.findViewById(R.id.imgThumbnail);
        }

        }
