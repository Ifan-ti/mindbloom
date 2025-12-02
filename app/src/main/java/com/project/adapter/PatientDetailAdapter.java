package com.project.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mindbloom.R;
import com.project.model.PatientDetailModel;

import java.util.ArrayList;
import java.util.List;

// FIX 1: Class harus extend RecyclerView.Adapter dan menggunakan ViewHolder kustom
public class PatientDetailAdapter extends RecyclerView.Adapter<PatientDetailAdapter.PatientDetailViewHolder> {

    private final Context context;
    private List<PatientDetailModel> patientDetailList;

    // FIX 2: Listener harus menggunakan interface kustom
    private onItemClickListener listener;


    // FIX 3: Interface harus public/static agar mudah diakses dari luar
    public interface onItemClickListener {
        void onItemClick(PatientDetailModel patient);
    }

    // FIX 4: Konvensi nama method untuk listener
    public void setOnItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public PatientDetailAdapter(Context context) {
        this.context = context;
        this.patientDetailList = new ArrayList<>();
    }

    public void setData(List<PatientDetailModel> newPatientDetailList) {
        this.patientDetailList = newPatientDetailList;
        // Wajib dipanggil agar RecyclerView me-refresh data
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    // FIX 5: Return type harus PatientDetailViewHolder
    public PatientDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_card_pasient_list, parent, false);
        // FIX 6: Return ViewHolder yang baru dibuat
        return new PatientDetailViewHolder(view);
    }

    // FIX 7: Argumen pertama harus PatientDetailViewHolder
    @Override
    public void onBindViewHolder(@NonNull PatientDetailViewHolder holder, int position) {
        if (patientDetailList == null || patientDetailList.isEmpty()) {
            return;
        }

        PatientDetailModel patient = patientDetailList.get(position);

        // ✅ NULL SAFETY untuk setiap TextView
        if (holder.tvName != null) {
            String name = patient.getName();
            if (name != null && !name.trim().isEmpty()) {
                holder. tvName.setText(name);
            } else if (patient.getUsername() != null) {
                holder.tvName. setText(patient.getUsername());
            } else {
                holder. tvName.setText("Unknown");
            }
        }

        // ✅ TAMBAHKAN: Tampilkan status "Pending" atau last message
        if (holder.txtLastChat != null) {
            String requestStatus = patient.getRequestStatus();

            if ("pending".equals(requestStatus)) {
                holder.txtLastChat. setText("⏳ Menunggu persetujuan Anda...");
                holder.txtLastChat.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                String lastMessage = patient.getLastMessage();
                holder.txtLastChat.setText(lastMessage != null ? lastMessage : "");
                holder.txtLastChat. setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            }
        }

        if (holder.txtTime != null) {
            String lastTime = patient.getLastSentTime();
            holder.txtTime. setText(lastTime != null ? lastTime : "");
        }

        // Base64 Image Handling (tetap sama)
        String base64String = patient.getAvatar_base64();
        if (holder.ftProfil != null) {
            if (base64String != null && !base64String.isEmpty()) {
                try {
                    byte[] imageBytes = android.util.Base64.decode(base64String, android.util. Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    holder.ftProfil. setImageBitmap(decodedImage);
                } catch (IllegalArgumentException e) {
                    holder.ftProfil.setImageResource(R.drawable.add_icon);
                }
            } else {
                holder.ftProfil.setImageResource(R.drawable.add_icon);
            }
        }

        // Click Listener
        holder.itemView. setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(patient);
            }
        });
    }

    @Override
    // FIX 10: Implementasi getItemCount yang hilang
    public int getItemCount() {
        return patientDetailList != null ? patientDetailList.size() : 0;
    }

    // Class PatientDetailViewHolder sudah benar
    public static class PatientDetailViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, txtLastChat, txtTime;
        ImageView ftProfil;

        public PatientDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNamaPasient);
            txtLastChat = itemView.findViewById(R.id.tvLastMessage);
            txtTime = itemView.findViewById(R.id.tvWaktu);
            ftProfil = itemView.findViewById(R.id.imgAvatarList);
        }
    }
}