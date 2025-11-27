package com.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.project.model.NotificationModel;
import com.project.mindbloom.R;
import java.util.List;

// 🔥 ADAPTER INI TELAH DIPERBARUI UNTUK MENGGUNAKAN LAYOUT ANDA
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private List<NotificationModel> notificationList;

    public NotificationAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Menggunakan layout 'item_notifikasi.xml' baru Anda
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notif = notificationList.get(position);

        // Menggunakan ID dari layout baru Anda
        holder.tvNama.setText(notif.getTitle());
        holder.tvdesk.setText(notif.getMessageText());
        // holder.tvWaktu.setText(notif.getCreatedAt()); // (Perlu helper format waktu)

        // Kita sembunyikan tvKomentar karena Model Notifikasi kita tidak punya data ini
        holder.tvKomentar.setVisibility(View.GONE);

        // Set ikon berdasarkan tipe notifikasi
        // Kita gunakan imgProfile sebagai placeholder ikon
        if ("like".equals(notif.getType())) {
            holder.imgProfile.setImageResource(R.drawable.logo_like);
            holder.imgProfile.setPadding(8,8,8,8); // (Opsional, agar ikon pas)
        } else if ("comment".equals(notif.getType())) {
            holder.imgProfile.setImageResource(R.drawable.logo_comment);
            holder.imgProfile.setPadding(8,8,8,8); // (Opsional)
        } else {
            holder.imgProfile.setImageResource(R.drawable.icon_app); // Default
            holder.imgProfile.setPadding(8,8,8,8);
        }

        // Gunakan viewDot untuk status 'dibaca'
        if (notif.isRead()) {
            holder.viewDot.setVisibility(View.INVISIBLE);
        } else {
            holder.viewDot.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateNotifications(List<NotificationModel> newNotifications) {
        notificationList.clear();
        notificationList.addAll(newNotifications);
        notifyDataSetChanged();
    }

    // 🔥 HOLDER INI TELAH DIPERBARUI
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProfile;
        TextView tvNama, tvWaktu, tvdesk, tvKomentar;
        View viewDot;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inisialisasi menggunakan ID dari layout baru Anda
            viewDot = itemView.findViewById(R.id.viewDot);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvdesk = itemView.findViewById(R.id.tvdesk);
            tvKomentar = itemView.findViewById(R.id.tvKomentar);
        }
    }
}