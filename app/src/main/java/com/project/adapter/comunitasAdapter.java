package com.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mindbloom.R;

import java.util.List;

public class comunitasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Post> postList;

    public comunitasAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @Override
    public int getItemViewType(int position) {
        return postList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Post.TYPE_IMAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_postingan_gambar, parent, false);
            return new ImagePostViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_postingan_teks, parent, false);
            return new TextPostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post post = postList.get(position);

        if (holder instanceof ImagePostViewHolder) {
            ImagePostViewHolder imageHolder = (ImagePostViewHolder) holder;
            imageHolder.tvUsername.setText(post.getUsername());
            imageHolder.tvTime.setText(post.getTime());
            imageHolder.tvDescription.setText(post.getDescription());
            imageHolder.imgPost.setImageResource(post.getImageResId());
        } else if (holder instanceof TextPostViewHolder) {
            TextPostViewHolder textHolder = (TextPostViewHolder) holder;
            textHolder.tvUsername.setText(post.getUsername());
            textHolder.tvTime.setText(post.getTime());
            textHolder.tvDescription.setText(post.getDescription());
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class ImagePostViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvTime, tvDescription;
        ImageView imgPost;

        public ImagePostViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgPost = itemView.findViewById(R.id.imgPost);
        }
    }

    public static class TextPostViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvTime, tvDescription;

        public TextPostViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}
