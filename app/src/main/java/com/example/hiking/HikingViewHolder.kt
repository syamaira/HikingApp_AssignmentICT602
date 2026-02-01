package com.example.hiking

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HikingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName: TextView = itemView.findViewById(R.id.tvTrailName)
    val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
    val tvDifficulty: TextView = itemView.findViewById(R.id.tvDifficulty)
}