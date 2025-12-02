package com.project.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

        // Mengisi data
        holder.tvTitle.setText(article.getTitle());
        holder.tvExcerpt.setText(article.getExcerpt());

        // Memuat Gambar
        Glide.with(context)
                .load(article.getCoverImageUrl())
                .placeholder(R.drawable.icon_app)
                .error(R.drawable.icon_app)
                .into(holder.ivArticleImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Buat Intent untuk pindah ke Activity Detail
                Intent intent = new Intent(context, FormDetailActivity.class);

                // Kirim ID artikel menggunakan ID yang sudah kita buat getter-nya
                intent.putExtra(FormDetailActivity.EXTRA_ARTICLE_ID, article.getIdArticles());

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
        ImageView ivArticleImage;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            // 🔥 MENCIPTAKAN ID DARI LAYOUT CARD ARTIKEL BARU ANDA 🔥
            tvTitle = itemView.findViewById(R.id.txtJudulArtikel);
            tvExcerpt = itemView.findViewById(R.id.txtDescripsiArtikel);
            tvSelengkapnya = itemView.findViewById(R.id.txtSelengkapnya);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImage);
        }
    }

}
