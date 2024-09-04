package com.example.aicommunication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aicommunication.R;

import java.util.List;
public class ViewPage2Adapter extends RecyclerView.Adapter<ViewPage2Adapter.ViewHolder> {
    List<Integer> images;

    public ViewPage2Adapter(List<Integer> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_splash, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0)
            holder.imageView.setImageResource(images.get(images.size() - 1));
        else if (position == images.size() + 1)
            holder.imageView.setImageResource(images.get(0));
        else
            holder.imageView.setImageResource(images.get(position - 1));
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() + 2 : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSplash);
        }
    }
}

