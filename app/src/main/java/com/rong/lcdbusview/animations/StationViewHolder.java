package com.rong.lcdbusview.animations;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class StationViewHolder extends RecyclerView.ViewHolder  {
    public ImageView bus;
    public TextView stationName;
    public View left;
    public View right;

    public StationViewHolder(View itemView) {
        super(itemView);
    }
}
