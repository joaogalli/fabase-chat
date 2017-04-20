package com.gihub.joaogalli.fabase_chat.ui.conversationlist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.gihub.joaogalli.fabase_chat.R;
import com.gihub.joaogalli.fabase_chat.model.Conversation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConversationListActivity extends AppCompatActivity implements ValueEventListener {

    private ConversationListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConversationListAdapter();
        recyclerView.setAdapter(adapter);

        DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference("conversations-details");
        conversationsRef.addValueEventListener(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
            Iterable<DataSnapshot> children = dataSnapshot.getChildren();
            List<Conversation> list = new ArrayList<>();
            for (DataSnapshot child : children) {
                Conversation conversation = child.getValue(Conversation.class);
                conversation.setId(child.getKey());
                list.add(conversation);
            }
            adapter.setList(list);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Toast.makeText(this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
