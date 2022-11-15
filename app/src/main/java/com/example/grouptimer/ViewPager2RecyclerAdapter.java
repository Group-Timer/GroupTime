package com.example.grouptimer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public class ViewPager2RecyclerAdapter extends RecyclerView.Adapter<ViewPager2RecyclerHolder>
{
    @Override
    public ViewPager2RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);

        ViewPager2RecyclerHolder holder = new ViewPager2RecyclerHolder(view);

        return holder;
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
    holder.bind(bgColors[position], position)
}

    override fun getItemCount(): Int = bgColors.size
}
