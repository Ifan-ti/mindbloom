package com.project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.model.ExpertsModel;
import com.project.mindbloom.R;

import java.util.ArrayList;
import java.util.List;

public class ExpertListAdapter extends RecyclerView.Adapter<ExpertListAdapter.ExpertListViewHolder> {
    private static final String TAG = "ExpertListAdapter";
    private final Context context;
    private List<ExpertsModel> expertList;
    private OnItemClickListener listener;

    // ✅ 2. Buat Interface agar Fragment bisa mendengarkan klik
    public interface OnItemClickListener {
        void onItemClick(ExpertsModel expert);
    }

    // ✅ 3. Method untuk memasang listener dari Fragment
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public ExpertListAdapter(Context context) {
        this.context = context;
        this.expertList = new ArrayList<>();
    }

    public void setData(List<ExpertsModel> newExpertsList) {
        Log.d(TAG, "🔵 setData called with " + (newExpertsList != null ? newExpertsList.size() : "NULL") + " items");

        if (newExpertsList != null) {
            this.expertList = newExpertsList;
            notifyDataSetChanged();
            Log.d(TAG, "🟢 notifyDataSetChanged called, current size: " + expertList.size());
        } else {
            Log.e(TAG, "🔴 newExpertsList is NULL");
        }
    }

    @NonNull
    @Override
    public ExpertListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "🔵 onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.aset_item_card_expert, parent, false);
        return new ExpertListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpertListViewHolder holder, int position) {
        Log.d(TAG, "🔵 onBindViewHolder called for position: " + position);

        if (expertList == null || expertList.isEmpty()) {
            Log.e(TAG, "🔴 expertList is null or empty in onBindViewHolder");
            return;
        }

        ExpertsModel expert = expertList.get(position);
        Log.d(TAG, "🟢 Binding: " + expert.getName() + " - " + expert.getExpertise_area());

        holder.tvName.setText(expert.getName());
        holder.tvJob.setText(expert.getExpertise_area());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    // Kirim data expert yang diklik ke Fragment
                    listener.onItemClick(expert);
                    Log.d(TAG, "👆 Item clicked: " + expert.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = (expertList != null) ? expertList.size() : 0;
        Log.d(TAG, "🔵 getItemCount: " + count);
        return count;  // ✅ SUDAH BENAR
    }

    public static class ExpertListViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvJob;

        public ExpertListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.txtUsernmae);  // ⚠️ Pastikan ID ini sesuai dengan layout
            tvJob = itemView.findViewById(R.id.txtJob);
        }
    }
}