package com.project.adapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.model.ArticleModel;
import com.project.mindbloom.Activity.FormDetailActivity;
import com.project.mindbloom.R; // R.drawable.icon_app ada di sini

import java.util.List;
import java.util.ArrayList;

// 🔥 PERBAIKAN UTAMA: Tambahkan 'extends RecyclerView.Adapter' 🔥
// Dan pastikan nama class utama adalah ArticleAdapter (kapital A)
public class ScrollArtikelAdapter extends RecyclerView.Adapter<ScrollArtikelAdapter.ArticleViewHolder> {

    private final Context context;
    private List<ArticleModel> articleList;




    // CONSTRUCTOR: Nama harus sama dengan class
    public ScrollArtikelAdapter(Context context) {
        this.context = context;
        this.articleList = new ArrayList<>();
    }

    public void setData(List<ArticleModel> newArticleList) {
        this.articleList = newArticleList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override // ANOTASI @Override wajib
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_card_scroll, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override // ANOTASI @Override wajib
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ArticleModel article = articleList.get(position);
        holder.ivArticleImage.setVisibility(VISIBLE);
        holder.ivDiaryImage.setVisibility(INVISIBLE);

        // 1. Mengisi TextViews
        holder.tvTitle.setText(article.getTitle());
        holder.tvExcerpt.setText(article.getExcerpt());

        // 2. Memuat Gambar menggunakan Glide
        // Pastikan getCoverImageUrl() sudah diimplementasikan di ArticleModel
        String base64ImageString = article.getCoverImageUrl();

        if (base64ImageString == null || base64ImageString.isEmpty()) {
            // Tangani kasus jika string kosong atau null
            return;
        }

        try {
            // 1. Mendekode string Base64 ke byte array
            // Menggunakan flag DEFAULT atau NO_WRAP (tergantung cara encoding API)
            byte[] decodedString = Base64.decode(base64ImageString, Base64.DEFAULT);

            // 2. Mengubah byte array menjadi Bitmap
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // 3. Menampilkan Bitmap di ImageView
            holder.ivArticleImage.setImageBitmap(decodedByte);

        } catch (IllegalArgumentException e) {
            // Tangani jika string Base64 tidak valid
            e.printStackTrace();
        }


        // 🔥 TAMBAHAN: Tambahkan listener klik di sini
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk pindah ke Activity Detail
                Intent intent = new Intent(context, FormDetailActivity.class);

                // Kirim ID artikel menggunakan ID yang sudah kita buat getter-nya

                intent.putExtra(FormDetailActivity.EXTRA_ARTICLE_ID, article.getId_articles());


                intent.putExtra("title", article.getTitle());
                intent.putExtra("content", article.getContent());
                intent.putExtra("author", article.getAuthor());
                intent.putExtra("peninjau", article.getPeninjau());
                intent.putExtra("readcount", article.getReadCount());
                intent.putExtra("created_at", article.getDate());
                intent.putExtra("cover", article.getCoverImageUrl());


                // Mulai Activity baru
                context.startActivity(intent);
            }
        });
    }

    @Override // ANOTASI @Override wajib
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvExcerpt, tvSelengkapnya;
        ImageView ivArticleImage, ivDiaryImage;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txtJudulArtikel);
            tvExcerpt = itemView.findViewById(R.id.txtDescripsiArtikel);
            tvSelengkapnya = itemView.findViewById(R.id.txtSelengkapnya);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImageArticles);
            ivDiaryImage = itemView.findViewById(R.id.ivArticleImageDiary);

        }
    }



}