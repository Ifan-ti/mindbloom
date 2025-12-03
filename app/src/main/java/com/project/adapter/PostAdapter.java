package com.project.adapter;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Pastikan Anda sudah menambahkan dependensi Glide
import com.project.client.TimeUtil;
import com.project.model.PostModel;
import com.project.mindbloom.R;
import java.util.List;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<PostModel> postList;

    // Tipe view untuk membedakan post
    private static final int VIEW_TYPE_IMAGE = 1;
    private static final int VIEW_TYPE_TEXT = 2;

    public PostAdapter(Context context, List<PostModel> postList) {
        this.context = context;
        this.postList = postList;
    }

    /**
     * Logika inti: Memeriksa apakah post memiliki gambar atau tidak.
     */
    @Override
    public int getItemViewType(int position) {
        PostModel post = postList.get(position);
        if (post.getCoverImageUrl() != null && !post.getCoverImageUrl().isEmpty()) {
            return VIEW_TYPE_IMAGE;
        } else {
            return VIEW_TYPE_TEXT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout yang berbeda berdasarkan viewType
        if (viewType == VIEW_TYPE_IMAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.aset_card_postingan_gambar, parent, false);
            return new ImagePostViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.aset_card_postingan_teks, parent, false);
            return new TextPostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PostModel post = postList.get(position);

        if (getItemViewType(position) == VIEW_TYPE_IMAGE) {
            ImagePostViewHolder imgHolder = (ImagePostViewHolder) holder;
            // ... (set text views Anda)
            imgHolder.tvUsername.setText(post.getUsername());
            imgHolder.tvUser.setText(post.getUsername());
            imgHolder.tvDescription.setText(post.getContentText());
            imgHolder.tvLikes.setText(String.valueOf(post.getLikeCount()));
            imgHolder.tvComments.setText(String.valueOf(post.getCommentCount()));
            imgHolder.tvTime.setText(TimeUtil.getRelativeTime(post.getCreatedAt()));

            // --- MULAI DEBUGGING ---
            String base64Image = post.getCoverImageUrl();

            // 🔥 LANGKAH 1: Log data string Base64 yang diterima
            Log.d("GAMBAR_DEBUG", "Mencoba memuat gambar untuk post ID: " + post.getPostId());

            if (base64Image != null && !base64Image.isEmpty()) {
                // Kita log 200 karakter pertama saja, jangan semuanya
                Log.d("GAMBAR_DEBUG", "Base64 (awal): " + base64Image.substring(0, Math.min(base64Image.length(), 200)));
            } else {
                Log.e("GAMBAR_DEBUG", "Error: String Base64 IS NULL atau KOSONG.");
                imgHolder.imgPost.setImageResource(R.drawable.logo_like); // Tampilkan error
                return; // Hentikan proses
            }

            try {
                // 2. Ubah string Base64 kembali menjadi byte[]
                byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);

                // 🔥 LANGKAH 2: Log ukuran data byte[]
                Log.d("GAMBAR_DEBUG", "Ukuran byte setelah decode: " + imageBytes.length);

                if (imageBytes.length == 0) {
                    Log.e("GAMBAR_DEBUG", "Error: Ukuran byte adalah 0. Gagal decode.");
                    imgHolder.imgPost.setImageResource(R.drawable.logo_like); // Tampilkan error
                    return; // Hentikan proses
                }

                // 3. Muat byte[] ke Glide
                Glide.with(context)
                        .load(imageBytes)
                        .placeholder(R.drawable.circle_bg_gray)
                        .error(R.drawable.logo_like)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imgHolder.imgPost);

            } catch (Exception e) {
                // 🔥 LANGKAH 3: Log jika proses decode gagal total
                Log.e("GAMBAR_DEBUG", "Error: Gagal decode Base64 (string tidak valid).", e);
                imgHolder.imgPost.setImageResource(R.drawable.logo_like);
            }
            // --- SELESAI DEBUGGING ---

        } else {
            // ... (kode TextPostViewHolder Anda tidak berubah)
            TextPostViewHolder txtHolder = (TextPostViewHolder) holder;
            txtHolder.tvUsername.setText(post.getUsername());
            txtHolder.tvDescription.setText(post.getContentText());
            txtHolder.tvLikes.setText(String.valueOf(post.getLikeCount()));
            txtHolder.tvComments.setText(String.valueOf(post.getCommentCount()));

            txtHolder.tvTime.setText(TimeUtil.getRelativeTime(post.getCreatedAt()));

        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<PostModel> newPosts) {
        postList.clear();
        postList.addAll(newPosts);
        notifyDataSetChanged();
    }

    // --- DUA VIEW HOLDER ---

    /**
     * ViewHolder untuk layout 'aset_card_postingan_gambar'
     */
    static class ImagePostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile, imgPost, btnMore;
        TextView tvUsername, tvTime, tvDescription, tvLikes, tvComments, tvUser;

        public ImagePostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            btnMore = itemView.findViewById(R.id.btnMore);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUser = itemView.findViewById(R.id.txtUser);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvComments = itemView.findViewById(R.id.tvComments);
        }
    }

    /**
     * ViewHolder untuk layout 'aset_card_postingan_teks'
     */
    static class TextPostViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile, btnMore;
        TextView tvUsername, tvTime, tvDescription, tvLikes, tvComments, tvUser;

        public TextPostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            btnMore = itemView.findViewById(R.id.btnMore);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvUser = itemView.findViewById(R.id.txtUser);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvComments = itemView.findViewById(R.id.tvComments);
        }
    }
}