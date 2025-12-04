package com.project.adapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.model.ArticleModel;
import com.project.mindbloom.Activity.FormDetailActivity;
import com.project.mindbloom.R;

import java.util.ArrayList;
import java.util.List;

public class SlideArtikelAdapter extends RecyclerView.Adapter<SlideArtikelAdapter.SliderViewHolder> {


    private final Context context;
    private List<ArticleModel> articleList;

    // CONSTRUCTOR: Nama harus sama dengan class
    public SlideArtikelAdapter(Context context) {
        this.context = context;
        this.articleList = new ArrayList<>();
    }

    public void setData(List<ArticleModel> newArticleList) {
        this.articleList = newArticleList;
        // Sekarang notifyDataSetChanged() akan berfungsi karena sudah inherit
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🔥 Menggunakan layout item yang sama (aset_item_articel_card) 🔥
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_card_slide, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        ArticleModel article = articleList.get(position);

        holder.ivArticleImage.setVisibility(VISIBLE);
        holder.ivDiaryImage.setVisibility(INVISIBLE);


        // Mengisi data
        holder.tvTitle.setText(article.getTitle());
        holder.tvExcerpt.setText(article.getExcerpt());

        // Memuat Gambar
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk pindah ke Activity Detail
                Intent intent = new Intent(context, FormDetailActivity.class);

                // Kirim ID artikel menggunakan ID yang sudah kita buat getter-nya

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

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    // ViewHolder untuk Item Slider (Menggunakan ID layout item yang sama)
    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvExcerpt, tvSelengkapnya;
        ImageView ivArticleImage, ivDiaryImage;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            // 🔥 MENCIPTAKAN ID DARI LAYOUT CARD ARTIKEL BARU ANDA 🔥
            tvTitle = itemView.findViewById(R.id.txtJudulArtikel);
            tvExcerpt = itemView.findViewById(R.id.txtDescripsiArtikel);
            tvSelengkapnya = itemView.findViewById(R.id.txtSelengkapnya);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImageArticles);
            ivDiaryImage = itemView.findViewById(R.id.ivArticleImageDiary);
        }
    }

}
