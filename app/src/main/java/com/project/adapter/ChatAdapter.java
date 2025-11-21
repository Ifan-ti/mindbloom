package com.project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mindbloom.R;
import com.project.modul.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    public static final int VIEW_TYPE_USER = 1;
    public static final int VIEW_TYPE_BOT = 2;
    public static final int VIEW_TYPE_LOADING = 3;

    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage msg = messageList.get(position);

        if (msg.isLoading()) return VIEW_TYPE_LOADING;
        if (msg.isUser()) return VIEW_TYPE_USER;
        return VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_USER)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
        else if (viewType == VIEW_TYPE_LOADING)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_loading, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bot, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.messageText.setText(messageList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView messageText;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}
