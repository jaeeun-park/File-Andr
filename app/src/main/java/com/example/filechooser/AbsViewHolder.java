package com.example.filechooser;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;

public abstract class AbsViewHolder extends RecyclerView.ViewHolder {
    public AbsViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public View view;
    public TextView title;
    public TextView date;
    public TextView size;
    public ImageView icon;
    public abstract void setData(File data);
}
