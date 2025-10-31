package com.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.data.ArticleModel;
import com.bumptech.glide.Glide;
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

        // 1. Mengisi TextViews
        holder.tvTitle.setText(article.getTitle());
        holder.tvExcerpt.setText(article.getExcerpt());

        // 2. Memuat Gambar menggunakan Glide
        // Pastikan getCoverImageUrl() sudah diimplementasikan di ArticleModel
        Glide.with(context)
                .load(article.getCoverImageUrl())
                .placeholder(R.drawable.icon_app)
                .error(R.drawable.icon_app)
                .into(holder.ivArticleImage);
    }

    @Override // ANOTASI @Override wajib
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvExcerpt, tvSelengkapnya;
        ImageView ivArticleImage;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txtJudulArtikel);
            tvExcerpt = itemView.findViewById(R.id.txtDescripsiArtikel);
            tvSelengkapnya = itemView.findViewById(R.id.txtSelengkapnya);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImage);
        }
    }
}