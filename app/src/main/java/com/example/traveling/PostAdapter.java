package com.example.traveling;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        Post post = postList.get(position);

        holder.usernameText.setText(post.getUsername());
        holder.contentText.setText(post.getContent());
        holder.avatarImage.setImageResource(post.getAvatarResId());

        holder.locationText.setText(
                post.getLocation() + " • " + post.getDate()
        );
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        ImageView avatarImage;
        TextView usernameText;
        TextView contentText;
        TextView locationText;

        public PostViewHolder(View itemView) {
            super(itemView);

            avatarImage = itemView.findViewById(R.id.avatarImage);
            usernameText = itemView.findViewById(R.id.usernameText);
            contentText = itemView.findViewById(R.id.contentText);
            locationText = itemView.findViewById(R.id.locationText);
        }
    }
}