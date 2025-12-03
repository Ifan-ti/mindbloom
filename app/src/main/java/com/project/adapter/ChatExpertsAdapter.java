package com.project.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.project.mindbloom.R;
import com.project.model.MessageModel;
import java.util.ArrayList;
import java.util.List;

public class ChatExpertsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    private Context context;
    private List<MessageModel> messageList;
    private int currentUserId; // ID User yang sedang login

    public boolean hasMessageWithId(int messageId) {
        if (messageId == 0) return false;

        for (MessageModel msg : messageList) {
            if (msg.getId() == messageId) {
                return true;
            }
        }
        return false;
    }
    public ChatExpertsAdapter(Context context, int currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.messageList = new ArrayList<>();
    }

    public void setMessages(List<MessageModel> messages) {
        this.messageList. clear();
        this.messageList.addAll(messages);
        notifyDataSetChanged();
    }

    public void addMessage(MessageModel message) {
        // Cek duplikasi by ID
        if (hasMessageWithId(message.getId())) {
            Log.w("ChatAdapter", "Message ID " + message.getSenderId() + " already exists");
            return;
        }

        this.messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messageList.get(position);

        // Bandingkan int dengan int langsung
        if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_MY_MESSAGE; // Chat Kanan (Saya)
        } else {
            return VIEW_TYPE_OTHER_MESSAGE; // Chat Kiri (Lawan)
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            // Gunakan aset_item_chat_user.xml yang kamu upload sebelumnya
            View view = LayoutInflater.from(context).inflate(R.layout.aset_item_chat_user, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            // Gunakan aset_item_chat_expert.xml yang kamu upload sebelumnya
            View view = LayoutInflater.from(context).inflate(R.layout.aset_item_chat_expert, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(message);
        } else {
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder untuk Pesan Saya (Kanan)
    class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        MyMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
        void bind(MessageModel message) {
            messageText.setText(message.getMessage());
        }
    }

    // ViewHolder untuk Pesan Orang Lain/Psikolog (Kiri)
    class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        OtherMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
        void bind(MessageModel message) {
            messageText.setText(message.getMessage());
        }
    }
}