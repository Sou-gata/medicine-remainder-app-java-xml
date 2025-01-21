package com.sougata.meditrack;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeRecyclerViewHolder extends RecyclerView.ViewHolder{
    ImageView image;
    TextView name, dose, freq;
    public HomeRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.rv_tv_home_name_item);
        dose = itemView.findViewById(R.id.rv_tv_home_dose_item);
        image = itemView.findViewById(R.id.rv_iv_home_item);
        freq = itemView.findViewById(R.id.rv_tv_home_freq_item);
    }
}
