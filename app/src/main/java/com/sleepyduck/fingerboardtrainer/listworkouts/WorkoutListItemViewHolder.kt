package com.sleepyduck.fingerboardtrainer.listworkouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

class WorkoutListItemViewHolder(
        parent: ViewGroup, @LayoutRes
        layout: Int
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layout, parent, false))