package com.gihub.joaogalli.fabase_chat.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gihub.joaogalli.fabase_chat.FirebaseConstants;
import com.gihub.joaogalli.fabase_chat.R;
import com.gihub.joaogalli.fabase_chat.model.Conversation;
import com.gihub.joaogalli.fabase_chat.model.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements ChildEventListener {

    public static final String CONVERSATION_PARAMETER = "conversation_parameter";
    private static final String TAG = ChatActivity.class.getSimpleName();

    private Conversation currentConversation;

    private LinearLayoutManager layoutManager;

    private DatabaseReference conversationRef;

    private EditText messageText;

    private FirebaseUser currentUser;

    private ChatRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null && intent.getParcelableExtra(CONVERSATION_PARAMETER) != null) {
            currentConversation = intent.getParcelableExtra(CONVERSATION_PARAMETER);
            getSupportActionBar().setTitle(currentConversation.getName());
        } else {
            Toast.makeText(this, "Não pode iniciar uma conversa, sem dizer qual é.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        messageText = (EditText) findViewById(R.id.new_chat_message);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        adapter = new ChatRecyclerViewAdapter(currentUser);
        recyclerView.setAdapter(adapter);

        conversationRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseConstants.CONVERSATIONS_MESSAGES).child(currentConversation.getId());
//        conversationRef.addValueEventListener(this);

//        DatabaseReference messagesRef = conversationRef.child(FirebaseConstants.CONVERSATIONS_MESSAGES_MESSAGES);
        conversationRef.addChildEventListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        conversationRef.removeEventListener(this);
    }

    public void send(View view) {
        Editable text = messageText.getText();
        if (!TextUtils.isEmpty(text)) {
            // TODO ver se precisa repetir
            DatabaseReference messagesRef = conversationRef.child(FirebaseConstants.CONVERSATIONS_MESSAGES_MESSAGES);
            Message message = new Message();
            message.setAuthorId(currentUser.getUid());
            message.setContent(text.toString());

            DatabaseReference push = messagesRef.push();
            message.setId(push.getKey());

            Map<String, Object> map = new HashMap<>();
            map.put(push.getKey(), message);
            messagesRef.updateChildren(map);

            map.clear();
            map.put("dateCreated", ServerValue.TIMESTAMP);
            messagesRef.child(push.getKey()).updateChildren(map);

            messageText.setText("");
            adapter.notifyDataSetChanged();
        }
    }

    public void update(DataSnapshot dataSnapshot) {
        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
        List<Message> list = new ArrayList<>();
        for (DataSnapshot child : children) {
            list.add(child.getValue(Message.class));
        }
        adapter.setList(list);
        scrollToBottom();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.i(TAG, "onChildAdded");
        update(dataSnapshot);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.i(TAG, "onChildChanged");
        update(dataSnapshot);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void scrollToBottom() {
        if (adapter != null)
            layoutManager.scrollToPosition(adapter.getItemCount() - 1);
//        chatGoToBottomView.setVisibility(View.GONE);
    }

}
