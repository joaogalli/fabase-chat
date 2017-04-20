package com.gihub.joaogalli.fabase_chat.service;

import android.util.Log;

import com.gihub.joaogalli.fabase_chat.counter.Counter;
import com.gihub.joaogalli.fabase_chat.counter.SingleCounter;
import com.gihub.joaogalli.fabase_chat.model.Conversation;
import com.gihub.joaogalli.fabase_chat.model.UserConversations;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joao.galli on 20/04/2017.
 */

public class ConversationService {

    private static final String TAG = ConversationService.class.getSimpleName();

    private static final String CONVERSATION_DETAILS = "conversations-details";

    private static final String USER_CONVERSATIONS = "user-conversations";

    public void findOrCreateConversation(final Callback<Conversation> callback, final String... users) {
        final String mainUser = users[0];
        DatabaseReference userConversations = FirebaseDatabase.getInstance().getReference(USER_CONVERSATIONS);
        userConversations.orderByKey().equalTo(mainUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            DataSnapshot userChild = dataSnapshot.child(mainUser);
                            if (userChild.exists()) {
                                UserConversations userConversations = userChild.getValue(UserConversations.class);

                                final SingleCounter<Conversation> counter = new SingleCounter<>(
                                        userConversations.getConversations().size(), new Counter.Listener<SingleCounter<Conversation>>() {
                                    @Override
                                    public void onReached(SingleCounter<Conversation> counter) {
                                        if (counter.getValue() != null) {
                                            callback.onNext(counter.getValue());
                                        } else {
                                            createConversation(users);
                                        }
                                    }
                                });

                                for (String conversationId : userConversations.getConversations()) {
                                    doesConversationHaveUsers(conversationId, new Callback<Conversation>() {
                                        @Override
                                        public void onNext(Conversation c) {
                                            if (c != null) {
                                                counter.setValue(c);
                                            }
                                            counter.increase();
                                        }

                                        @Override
                                        public void onComplete() {
                                        }
                                    }, users);
                                }
                            }
                        } else {
                            createConversation(users);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, databaseError.getMessage());
                    }
                });
    }

    public void doesConversationHaveUsers(final String conversationId, final Callback<Conversation> callback, final String... users) {
        FirebaseDatabase.getInstance().getReference(CONVERSATION_DETAILS).orderByKey().equalTo(conversationId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot conversationSnapshot = dataSnapshot.child(conversationId);
                    if (conversationSnapshot.exists()) {
                        Conversation conversation = conversationSnapshot.getValue(Conversation.class);
                        if (conversation.getContacts().containsAll(Arrays.asList(users))) {
                            callback.onNext(conversation);
                        } else {
                            callback.onNext(null);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "error");
            }
        });
    }

    public void createConversation(String... users) {
        String conversationId;
        {
            DatabaseReference conversationsDetails = FirebaseDatabase.getInstance().getReference(CONVERSATION_DETAILS);
            Map<String, Object> map = new HashMap<>();
            conversationId = conversationsDetails.push().getKey();
            map.put(conversationId, new Conversation(users));
            conversationsDetails.updateChildren(map);
        }

        for (String user : users) {
            DatabaseReference userConversations = FirebaseDatabase.getInstance().getReference(USER_CONVERSATIONS);
            userConversations.orderByKey().equalTo(user)
                    .addListenerForSingleValueEvent(new CreateUserConversationsValueEventListener(user, conversationId));
        }
    }

    public interface Callback<T> {
        void onNext(T t);
        void onComplete();
    }

    private class CreateUserConversationsValueEventListener implements ValueEventListener {

        private String userId;
        private String conversationId;

        public CreateUserConversationsValueEventListener(String userId, String conversationId) {
            this.userId = userId;
            this.conversationId = conversationId;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot child = dataSnapshot.child(userId);
                UserConversations userConversations;
                if (child.exists()) {
                    userConversations = child.getValue(UserConversations.class);
                } else {
                    userConversations = new UserConversations();
                }
                userConversations.addConversation(conversationId);

                DatabaseReference userConversationsRef = FirebaseDatabase.getInstance().getReference("user-conversations").child(userId);
                userConversationsRef.setValue(userConversations);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, databaseError.getMessage());
        }

    }

}
