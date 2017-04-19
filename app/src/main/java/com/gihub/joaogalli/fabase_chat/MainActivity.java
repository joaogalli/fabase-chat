package com.gihub.joaogalli.fabase_chat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gihub.joaogalli.fabase_chat.model.Conversation;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mFirebaseAuth;

    private FirebaseUser mFirebaseUser;

    private GoogleApiClient mGoogleApiClient;

    private DatabaseReference mFirebaseDatabaseReference;

    private TextView userTextView;

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
//                mGoogleApiClient = new GoogleApiClient.Builder(this)
//                        .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                        .addApi(Auth.GOOGLE_SIGN_IN_API)
//                        .build();
                break;

            case R.id.create_button:
                Log.i(TAG, "Create Button");
                mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("conversations-details");

                Map<String, Object> map = new HashMap<>();
                map.put(mFirebaseDatabaseReference.push().getKey(), new Conversation("teste@teste.com", "ex1@teste.com"));
                mFirebaseDatabaseReference.updateChildren(map);
                break;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "opa");
    }
}
