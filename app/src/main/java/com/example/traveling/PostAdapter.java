package com.example.traveling;

import android.content.Intent;
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
        holder.avatarImage.setImageResource(post.getAvatarResId());
        holder.usernameText.setText(post.getUsername());
        holder.contentText.setText(post.getDescription());
        holder.locationText.setText("📍 " + post.getLocation());
        holder.dateText.setText(post.getPeriod());
        holder.avatarImage.setImageResource(post.getAvatarResId());
        holder.postImage.setImageResource(post.getImageResId());


        if(post.isLiked())
            holder.likeButton.setText("❤️ Liked");
        else
            holder.likeButton.setText("🤍 Like");

        holder.likeButton.setOnClickListener(v -> {
            post.toggleLike();
            notifyItemChanged(position);
        });

        holder.routeButton.setOnClickListener(v -> {

            String uri = "geo:0,0?q=" + post.getLocation();

            Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps");
            v.getContext().startActivity(intent);
        });
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

        TextView likeButton;
        TextView dateText;
        ImageView postImage;
        TextView routeButton;


        public PostViewHolder(View itemView) {
            super(itemView);

            avatarImage = itemView.findViewById(R.id.avatarImage);
            usernameText = itemView.findViewById(R.id.usernameText);
            contentText = itemView.findViewById(R.id.contentText);
            locationText = itemView.findViewById(R.id.locationText);
            likeButton = itemView.findViewById(R.id.likeButton);
            dateText = itemView.findViewById(R.id.dateText);
            postImage = itemView.findViewById(R.id.postImage);
            routeButton = itemView.findViewById(R.id.routeButton);

        }
    }
}