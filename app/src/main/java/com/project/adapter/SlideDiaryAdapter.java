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
import com.project.data.ArticleModel;
import com.project.data.DiaryModel;
import com.project.mindbloom.FormDetail;
import com.project.mindbloom.R;

import java.util.ArrayList;
import java.util.List;

public class SlideDiaryAdapter extends RecyclerView.Adapter<SlideDiaryAdapter.SliderViewHolder>{
    private final Context context;
    private List<DiaryModel> DiaryList;

    public SlideDiaryAdapter(Context context) {
        this.context = context;
        this.DiaryList = new ArrayList<>();
    }

    public void setData(List<DiaryModel> newDiaryList) {
        this.DiaryList = newDiaryList;
        // Sekarang notifyDataSetChanged() akan berfungsi karena sudah inherit
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SlideDiaryAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 🔥 Menggunakan layout item yang sama (aset_item_articel_card) 🔥
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_card_slide, parent, false);
        return new SlideDiaryAdapter.SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideDiaryAdapter.SliderViewHolder holder, int position) {
        DiaryModel Diary = DiaryList.get(position);

        // Mengisi data
        holder.tvTitle.setText(Diary.getTitle());
        holder.tvExcerpt.setText(Diary.getContent());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FormDetail.class);

                // 🔥 KIRIM ID DIARY
                intent.putExtra(FormDetail.EXTRA_DIARY_ID, Diary.getId());

                context.startActivity(intent);
            }
        });

    }
        @Override
        public int getItemCount () {
            return DiaryList.size();

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
