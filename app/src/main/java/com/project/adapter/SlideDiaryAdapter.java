package com.project.adapter;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.model.DiaryModel;
import com.project.mindbloom.Activity.FormDetailActivity;
import com.project.mindbloom.R;

import java.util.ArrayList;
import java.util.List;

public class SlideDiaryAdapter extends RecyclerView.Adapter<SlideDiaryAdapter.SliderViewHolder> {
    private final Context context;
    private List<DiaryModel> DiaryList;

    private static final int VIEW_TYPE_ITEM = 1;
    private static final int VIEW_TYPE_EMPTY = 0;

    public SlideDiaryAdapter(Context context) {
        this.context = context;
        this.DiaryList = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        // Logika ini sudah benar
        return DiaryList.isEmpty() ? VIEW_TYPE_EMPTY : VIEW_TYPE_ITEM;
    }

    public void setData(List<DiaryModel> newDiaryList) {
        this.DiaryList = newDiaryList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SlideDiaryAdapter.SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Logika ini sudah benar
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_card_slide, parent, false);
        return new SlideDiaryAdapter.SliderViewHolder(view);
    }

    // 🔥 PERBAIKAN UTAMA ADA DI SINI 🔥
    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {

        holder.ivDiaryImage.setVisibility(INVISIBLE);
        holder.ivDiaryImage.setVisibility(VISIBLE);
        holder.btnNext.setVisibility(INVISIBLE);

        // Cek Tipe View SEBELUM mengambil data
        if (getItemViewType(position) == VIEW_TYPE_EMPTY) {

            // --- INI ADALAH CARD KOSONG ---
            holder.tvTitle.setText("Belum Ada Diary");
            holder.tvExcerpt.setText("Kamu belum menulis diary hari ini. Yuk, mulai tulis ceritamu!");
            holder.tvSelengkapnya.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null); // Matikan klik

        } else {

            // --- INI ADALAH CARD DIARY NORMAL ---
            // Ambil data HANYA di dalam blok 'else'
            DiaryModel diary = DiaryList.get(position);

            holder.tvTitle.setText(diary.getTitle());
            holder.tvExcerpt.setText(diary.getContent());

            String mood = diary.getMoodTag();

            if ("happy".equals(mood)) {
                Glide.with(context)
                        .load(R.drawable.mood1)
                        .into(holder.ivDiaryImage);
            } else if ("sad".equals(mood)) {
                Glide.with(context)
                        .load(R.drawable.mood3)
                        .into(holder.ivDiaryImage);
            } else if ("angry".equals(mood)) {
                Glide.with(context)
                        .load(R.drawable.mood4)
                        .into(holder.ivDiaryImage);
            } else if ("neutral".equals(mood)) {
                Glide.with(context)
                        .load(R.drawable.mood2)
                        .into(holder.ivDiaryImage);
            }

            // Tampilkan kembali elemen yang disembunyikan
            holder.tvSelengkapnya.setVisibility(View.VISIBLE);
            holder.ivDiaryImage.setVisibility(View.VISIBLE);
            holder.btnNext.setVisibility(VISIBLE);


            // TODO: Tambahkan kode Glide di sini jika diary punya gambar
            // Glide.with(context).load(diary.getGambarUrl()).into(holder.ivArticleImage);

            // Aktifkan klik
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FormDetailActivity.class);
                intent.putExtra(FormDetailActivity.EXTRA_DIARY_ID, diary.getIdDiary());
                context.startActivity(intent);
            });
        }

        // HAPUS SEMUA KODE DUPLIKAT YANG ADA DI SINI
        // (Kode 'holder.tvTitle.setText(Diary.getTitle());' dll. di luar if/else)
    }

    // 🔥 PERBAIKAN KEDUA ADA DI SINI 🔥
    @Override
    public int getItemCount() {
        // Jika list kosong, kembalikan 1 (untuk card "kosong")
        // Jika tidak, kembalikan ukuran list
        return DiaryList.isEmpty() ? 1 : DiaryList.size();
    }


    // ViewHolder (Tidak berubah, sudah benar)
    public static class SliderViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvExcerpt, tvSelengkapnya;
        ImageView ivDiaryImage, ivArticleImage ;
        ImageButton btnNext;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.txtJudulArtikel);
            tvExcerpt = itemView.findViewById(R.id.txtDescripsiArtikel);
            tvSelengkapnya = itemView.findViewById(R.id.txtSelengkapnya);
            ivDiaryImage = itemView.findViewById(R.id.ivArticleImageArticles);
            ivArticleImage = itemView.findViewById(R.id.ivArticleImageArticles);
            btnNext = itemView.findViewById(R.id.btnNext2);



        }
    }
}