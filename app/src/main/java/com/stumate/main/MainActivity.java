package com.stumate.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.stumate.main.profileDetails.ProfileNameActivity;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.userAuthentication.UserAuthActivity;
import com.stumate.main.userAuthentication.UserAuthStateActivity;
import com.stumate.main.utils.IntentVariables;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // UI variables
    TextView textView;
    TextView textView2;
    TextView textView3;
    Button button;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // close app
        if (getIntent().getBooleanExtra("Exit", false)) {
            finish();
        } else {
            // email deep link
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            if (pendingDynamicLinkData != null) {
                                Intent intent = getIntent();
                                Intent intent1 = new Intent(MainActivity.this, UserAuthStateActivity.class);
                                intent1.putExtra("Email", true);
                                intent1.setData(intent.getData());
                                startActivity(intent1);
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "getDynamicLink:onFailure", e);
                        }
                    });


            if (user != null) {
                MobileAds.initialize(this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                        Log.d(TAG, "onInitializationComplete: MobileAds initialized");
                    }
                });
                if (Objects.equals(user.getDisplayName(), "#stumate")) {
                    Intent intent = new Intent(this, ProfileNameActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, TabLayoutActivity.class);
                    startActivity(intent);
                }
            } else {
                setContentView(R.layout.activity_main);
                textView = (TextView) findViewById(R.id.textView);
                textView2 = (TextView) findViewById(R.id.textView2);
                // create new account
                button = (Button) findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, UserAuthActivity.class);
                        intent.putExtra(IntentVariables.newAccount, true);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
                // log in
                textView3 = (TextView) findViewById(R.id.textView3);
                textView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, UserAuthActivity.class);
                        intent.putExtra(IntentVariables.newAccount, false);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
            }
        }
    }
}
