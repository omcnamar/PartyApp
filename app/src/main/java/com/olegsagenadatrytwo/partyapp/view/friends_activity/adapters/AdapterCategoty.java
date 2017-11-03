package com.olegsagenadatrytwo.partyapp.view.friends_activity.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.olegsagenadatrytwo.partyapp.R;
import com.olegsagenadatrytwo.partyapp.view.friends_activity.FriendsActivityContract;

import java.util.ArrayList;
import java.util.List;

public class AdapterCategoty extends RecyclerView.Adapter<AdapterCategoty.ViewHolder> {

    private List<String> categoryList = new ArrayList<>();
    private int positionPreviousColor = 1;
    private TextView tvPrevious;
    private FriendsActivityContract.view view;

    public AdapterCategoty(List<String> categoryList, FriendsActivityContract.view view) {
        this.categoryList = categoryList;
        this.view = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvCategory.setText(categoryList.get(position));
        if(positionPreviousColor == position){
            tvPrevious = holder.tvCategory;
            holder.tvCategory.setBackgroundResource(R.drawable.rounded_corners_black_filled);
        }else{
            holder.tvCategory.setBackgroundResource(R.drawable.rounded_corners_black);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPrevious.setBackgroundResource(R.drawable.rounded_corners_black);
                positionPreviousColor = position;
                //presenter.downloadMovieData(list.get(position).getId());
                holder.tvCategory.setBackgroundResource(R.drawable.rounded_corners_black_filled);
                tvPrevious = holder.tvCategory;
                view.caregoryChanged(categoryList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
        }
    }
}
