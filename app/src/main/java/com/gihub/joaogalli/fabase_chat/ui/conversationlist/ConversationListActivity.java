package com.gihub.joaogalli.fabase_chat.ui.conversationlist;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gihub.joaogalli.fabase_chat.FirebaseConstants;
import com.gihub.joaogalli.fabase_chat.R;
import com.gihub.joaogalli.fabase_chat.model.Conversation;
import com.gihub.joaogalli.fabase_chat.service.ConversationService;
import com.gihub.joaogalli.fabase_chat.ui.chat.ChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConversationListActivity extends AppCompatActivity implements ValueEventListener, ConversationListAdapter.UserInteractionListener {

    private ConversationListAdapter adapter;

    private ConversationService conversationService;

    private EditText conversationCreationText;

    private DatabaseReference conversationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        conversationService = new ConversationService();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ConversationListAdapter(this);
        recyclerView.setAdapter(adapter);

        conversationsRef = FirebaseDatabase.getInstance().getReference(FirebaseConstants.CONVERSATIONS_DETAILS);
        conversationsRef.addValueEventListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();
            }
        });
    }

    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.layout_conversation_creation, null);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.create_and_enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (!TextUtils.isEmpty(conversationCreationText.getText())) {
                            createConversation(conversationCreationText.getText().toString());
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        conversationCreationText = (EditText) view.findViewById(R.id.conversation_creation_name);

        dialog.show();
    }

    private void createConversation(String conversationName) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        conversationService.createConversation(conversationName, uid);
    }

    @Override
    protected void onStop() {
        super.onStop();
        conversationsRef.removeEventListener(this);
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

    public Activity getActivity() {
        return this;
    }

    @Override
    public void onConversationSelect(Conversation conversation) {
        // TODO adicionar usuário na conversation

        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.CONVERSATION_PARAMETER, conversation);
        startActivity(intent);
    }
}
