package com.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.project.model.DiaryModel;
import com.project.mindbloom.Activity.FormDetailActivity;
import com.project.mindbloom.R;

public class ScrollDiaryAdapter extends RecyclerView.Adapter<ScrollDiaryAdapter.DiaryViewHolder> {
    private final Context context;

    private List<DiaryModel> DiaryList;
    public ScrollDiaryAdapter(Context context) {
        this.context = context;
        this.DiaryList = new ArrayList<>();
    }

    public void setData(List<DiaryModel> newDiaryList) {
        this.DiaryList = newDiaryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override // ANOTASI @Override wajib
    public ScrollDiaryAdapter.DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_card_scroll, parent, false);
        return new ScrollDiaryAdapter.DiaryViewHolder(view);
    }

    @Override // ANOTASI @Override wajib
    public void onBindViewHolder(@NonNull ScrollDiaryAdapter.DiaryViewHolder holder, int position) {
        DiaryModel Diary = DiaryList.get(position);

        // 1. Mengisi TextViews
        holder.tvTitle.setText(Diary.getTitle());
        holder.tvExcerpt.setText(Diary.getContent());

        Glide.with(context)
                .load(R.drawable.icon_app) // Muat logo statis Anda
                .placeholder(R.drawable.icon_app)
                .error(R.drawable.icon_app)
                .into(holder.ivArticleImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FormDetailActivity.class);

                // 🔥 KIRIM ID DIARY
                intent.putExtra(FormDetailActivity.EXTRA_DIARY_ID, Diary.getIdDiary());

                context.startActivity(intent);
            }
        });


    }
    @Override // ANOTASI @Override wajib
    public int getItemCount() {
        return DiaryList.size();
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvExcerpt, tvSelengkapnya;
        ImageView ivArticleImage;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txtJudulArtikel);
            tvExcerpt = itemView.findViewById(R.id.txtDescripsiArtikel);
            tvSelengkapnya = itemView.findViewById(R.id.txtSelengkapnya);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImage);
        }
    }


}
