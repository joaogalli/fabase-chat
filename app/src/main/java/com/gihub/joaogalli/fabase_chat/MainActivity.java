package com.gihub.joaogalli.fabase_chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gihub.joaogalli.fabase_chat.model.Conversation;
import com.gihub.joaogalli.fabase_chat.service.ConversationService;
import com.gihub.joaogalli.fabase_chat.ui.conversationlist.ConversationListActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;

    private FirebaseUser mFirebaseUser;

    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference mFirebaseDatabaseReference;

    private TextView userTextView;

    private ConversationService conversationService;
    private String testeEmail = "teste", ex1Email = "ex1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userTextView = (TextView) findViewById(R.id.user_view);

        FirebaseApp.initializeApp(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mFirebaseAuth = FirebaseAuth.getInstance();

        conversationService = new ConversationService();

        verifyUser();
    }

    private void verifyUser() {
        Log.i(TAG, "verifyUser()");
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            userTextView.setText(mFirebaseUser.getUid());
        } else {
            userTextView.setText("user null");
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_view:
                Log.i(TAG, "User View");
                verifyUser();
                break;

            case R.id.login_button:
                Log.i(TAG, "Login Button");
                mFirebaseAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i(TAG, "onComplete");
                        verifyUser();
                    }
                });
                break;

            case R.id.create_button:
                Log.i(TAG, "Create Button");
                conversationService.createConversation(testeEmail, ex1Email);
                break;

            case R.id.find_button:
                Log.i(TAG, "Find or create");
                conversationService.findOrCreateConversation("sei la", new ConversationService.Callback<Conversation>() {
                    @Override
                    public void onNext(Conversation conversation) {
                        Toast.makeText(MainActivity.this, "Conversation: " + conversation, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                    }
                }, testeEmail, ex1Email);
                break;

            case R.id.read_button:
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseConstants.CONVERSATIONS_DETAILS);

                // .child("contacts")
                // .equalTo("teste@teste.com")
                mFirebaseDatabaseReference.orderByChild("contacts").equalTo(testeEmail)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                                    Log.i(TAG, value.getClass().getSimpleName());

                                    Set<Map.Entry<String, Object>> entries = value.entrySet();
                                    for (Map.Entry<String, Object> entry : entries) {
                                        Log.i(TAG, "Key: " + entry.getKey());
                                    }
                                } else {
                                    Log.i(TAG, "empty");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "error");
                            }
                        });
                break;
            case R.id.conversation_list_button:
                startActivity(new Intent(this, ConversationListActivity.class));
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "opa");
    }
}
