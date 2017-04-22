package com.gihub.joaogalli.fabase_chat.ui.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gihub.joaogalli.fabase_chat.R;
import com.gihub.joaogalli.fabase_chat.model.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by joaog on 21/04/2017.
 */

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ViewHolder> {

    private static final int MY_MESSAGE = 1, MY_IMAGE = 2, OTHERS_MESSAGE = 3, OTHERS_IMAGE = 4;

    private List<Message> list = new ArrayList<>();
    private FirebaseUser currentUser;

    public ChatRecyclerViewAdapter(FirebaseUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = list.get(position);

        String sender = message.getAuthorId();

        boolean hasImage = false;

        if (currentUser != null) {
            boolean isMine = currentUser.getUid().equals(message.getAuthorId());

            if (isMine) {
                return hasImage ? MY_IMAGE : MY_MESSAGE;
            } else {
                return hasImage ? OTHERS_IMAGE : OTHERS_MESSAGE;
            }
        } else {
            return MY_MESSAGE;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case MY_MESSAGE:
                View view1 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_chat_message, parent, false);
                return new MyMessageViewHolder(view1);

//            case MY_IMAGE:
//                View view2 = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.my_chat_image, parent, false);
//                return new MyImageViewHolder(view2);

            case OTHERS_MESSAGE:
                View view3 = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.others_chat_message, parent, false);
                return new OthersMessageViewHolder(view3);

//            case OTHERS_IMAGE:
//                View view4 = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.others_chat_image, parent, false);
//                return new OthersImageViewHolder(view4);

            default:
                throw new IllegalArgumentException("There is not a view holder for type: " + viewType);
        }
    }

    private SimpleDateFormat hourSdf = new SimpleDateFormat("HH:mm");

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.item = list.get(position);
        holder.mMessageView.setText(holder.item.getContent());

        if (holder.mAuthorView != null)
            holder.mAuthorView.setText(holder.item.getAuthorName());

        if (holder.item.getDateCreated() > 0) {
            Date date = new Date();
            date.setTime(holder.item.getDateCreated());
            holder.mHourView.setText(hourSdf.format(date));
            holder.mHourView.setVisibility(View.VISIBLE);
        } else {
            holder.mHourView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (list != null)
            return list.size();
        else
            return 0;
    }

    public void setList(List<Message> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Message item;
        public final View mView;
        public final TextView mMessageView;
        public final TextView mHourView;
        public final ImageView mAvatarView;
        public final TextView mAuthorView;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            mHourView = (TextView) itemView.findViewById(R.id.hour);
            mAvatarView = (ImageView) itemView.findViewById(R.id.avatar);
            mAuthorView = (TextView) itemView.findViewById(R.id.author);
        }
    }

    public class MyMessageViewHolder extends ViewHolder {

        public MyMessageViewHolder(View view) {
            super(view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessageView.getText() + "'";
        }
    }

    public class OthersMessageViewHolder extends ViewHolder {

        public OthersMessageViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessageView.getText() + "'";
        }
    }

}
