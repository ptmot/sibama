package com.sibama2024ai

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class PrecipitationData(val date: String, val precipitationSum: Double)

class PrecipitationAdapter(private val precipitationList: List<PrecipitationData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_precipitation_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_precipitation, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val precipitationData = precipitationList[position - 1]  // Adjust for header
            holder.textViewDate.text = precipitationData.date
            holder.textViewPrecipitation.text = precipitationData.precipitationSum.toString() + " mm"
            holder.textViewPrecipitation.textSize = 11f
            holder.textViewPrecipitation.setTextColor(Color.rgb(0, 0, 0))
            holder.textViewDate.textSize = 11f
            holder.textViewDate.setTextColor(Color.rgb(0, 0, 0))
        }
    }

    override fun getItemCount(): Int {
        return precipitationList.size + 1  // +1 for header
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewPrecipitation: TextView = itemView.findViewById(R.id.textViewPrecipitation)
    }
}
