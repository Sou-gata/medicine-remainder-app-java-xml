package com.sougata.meditrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HomeRecyclerViewAdapter extends RecyclerView.Adapter<HomeRecyclerViewHolder> {
    Context context;
    List<HomeMedicineListItem> items;
    private final OnItemClickListener listener;

    public HomeRecyclerViewAdapter(Context context, List<HomeMedicineListItem> items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HomeRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeRecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.rv_home_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeRecyclerViewHolder holder, int position) {
        int dose = items.get(position).getNumOfDose();
        String doseText = "(" + dose + (dose <= 1 ? " dose)" : " doses)");

        holder.image.setImageResource(items.get(position).getImage());
        holder.name.setText(items.get(position).getName());
        holder.dose.setText(doseText);
        holder.freq.setText(items.get(position).getRepeat() == 0 ? "Once" : "Repeated");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
