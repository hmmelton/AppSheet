package com.hmmelton.appsheet.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hmmelton.appsheet.models.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by harrison on 4/19/17.
 * This is an adapter for a RecyclerView displaying a list of User information.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {

    // List of users whose information to display
    private List<User> mUsers;

    /**
     * Constructor
     * @param users List of Users to display
     */
    public UserInfoAdapter(List<User> users) {
        mUsers = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate Android-provided layout with primary and secondary TextViews
        View rootView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        // Pass root view to view holder and return
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Fetch user from given position
        User user = mUsers.get(position);
        // Set user's name and age to primary view
        holder.primaryTextView.setText(user.getName() + ", " + user.getAge());
        // Set user's phone number to secondary view
        holder.secondaryTextView.setText(user.getNumber());
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    /**
     * This method adds a collection of Users to the adapter's list.
     * @param newUsers List of Users to add to adapter
     */
    public void addAll(List<User> newUsers) {
        mUsers.addAll(newUsers);
        // Notify change starting at previous list size, ending at previous list size plus elements
        // added
        notifyItemRangeInserted(mUsers.size() - newUsers.size(), newUsers.size());
    }

    /**
     * This is a view holder class for each cell in the RecyclerView associated with the enclosing
     * RecyclerView adapter.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(android.R.id.text1)
        TextView primaryTextView;
        @BindView(android.R.id.text2)
        TextView secondaryTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
