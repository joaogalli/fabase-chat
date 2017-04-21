package com.gihub.joaogalli.fabase_chat.ui.conversationlist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gihub.joaogalli.fabase_chat.R;
import com.gihub.joaogalli.fabase_chat.model.Conversation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joao.galli on 20/04/2017.
 */

public class ConversationListAdapter extends RecyclerView.Adapter<ConversationListAdapter.ViewHolder> {

    private List<Conversation> list = new ArrayList<>();

    private UserInteractionListener userInteractionListener;

    public interface UserInteractionListener {
        void onConversationSelect(Conversation conversation);
    }

    public ConversationListAdapter(UserInteractionListener userInteractionListener) {
        this.userInteractionListener = userInteractionListener;
    }

    public ConversationListAdapter(UserInteractionListener userInteractionListener, List<Conversation> list) {
        this.userInteractionListener = userInteractionListener;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.conversation_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mItem = list.get(position);
        holder.idView.setText(holder.mItem.getName());
        holder.contactsView.setText(buildContactsText(holder.mItem.getContacts()));
        holder.view.setOnClickListener(new ViewClickListener(position));
    }

    private String buildContactsText(List<String> contacts) {
//        StringBuilder sb = new StringBuilder();
//        for (String contact : contacts) {
//            sb.append(contact).append(", ");
//        }
//        return sb.toString();

        return contacts.size() + " usuário(s) registrado(s)";
    }

    private class ViewClickListener implements View.OnClickListener {

        private int position;

        public ViewClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (userInteractionListener != null) {
                userInteractionListener.onConversationSelect(list.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<Conversation> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Conversation mItem;
        public final View view;
        public final TextView idView;
        public final TextView contactsView;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            idView = (TextView) view.findViewById(R.id.conversation_id_view);
            contactsView = (TextView) view.findViewById(R.id.conversation_contacts_view);
        }
    }

}
