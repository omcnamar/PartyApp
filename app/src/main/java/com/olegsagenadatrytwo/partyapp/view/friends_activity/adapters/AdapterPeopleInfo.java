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

public class AdapterPeopleInfo extends RecyclerView.Adapter<AdapterPeopleInfo.ViewHolder> {

    private List<String> idsList = new ArrayList<>();
    private FriendsActivityContract.view view;

    public AdapterPeopleInfo(List<String> idsList, FriendsActivityContract.view view) {
        this.idsList = idsList;
        this.view = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.people_user_info_init, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvID.setText(idsList.get(position));
    }

    @Override
    public int getItemCount() {
        return idsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvID;

        public ViewHolder(View itemView) {
            super(itemView);
            tvID = itemView.findViewById(R.id.tvUserID);
        }
    }
}
