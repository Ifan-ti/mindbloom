package com.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout; // Import LinearLayout
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.project.model.dateModel;
import com.project.mindbloom.R;

public class dateAdapter extends RecyclerView.Adapter<dateAdapter.ViewHolder> {

    private Context context;
    private List<dateModel> tanggalList;

    public dateAdapter(Context context, List<dateModel> tanggalList) {
        this.context = context;
        this.tanggalList = tanggalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout 'aset_item_date_box.xml' Anda
        // (Pastikan namanya benar. Saya ambil dari XML kedua Anda)
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_date_box, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dateModel tanggal = tanggalList.get(position);

        // Mengatur data ke TextView
        holder.tvAngka.setText(tanggal.getTanggal());
        holder.tvBulan.setText(tanggal.getBulan());

        // Mengatur style JIKA itu adalah hari ini
        if (tanggal.isToday()) {
            // Ganti ini dengan drawable "terpilih" Anda (spt 'clickbtn')
            holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.main_blue));
            holder.tvAngka.setTextColor(ContextCompat.getColor(context, R.color.inactive_light_blue));
            holder.tvBulan.setTextColor(ContextCompat.getColor(context, R.color.inactive_light_blue));
        } else {
            // Style normal/default
            holder.background.setBackgroundColor(ContextCompat.getColor(context, R.color.inactive_light_blue));
            holder.tvAngka.setTextColor(ContextCompat.getColor(context, R.color.main_blue));
            holder.tvBulan.setTextColor(ContextCompat.getColor(context, R.color.main_blue));
        }
    }

    @Override
    public int getItemCount() {
        return tanggalList.size();
    }

    // ViewHolder ini menghubungkan ke ID di file 'aset_item_date_box.xml'
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAngka, tvBulan;
        LinearLayout background; // ID dari LinearLayout di item Anda

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Sesuaikan ID ini dengan 'aset_item_date_box.xml' Anda
            tvAngka = itemView.findViewById(R.id.tvDayNumber);
            tvBulan = itemView.findViewById(R.id.tvMonthName);
            background = itemView.findViewById(R.id.dateBackground);
        }
    }
}
