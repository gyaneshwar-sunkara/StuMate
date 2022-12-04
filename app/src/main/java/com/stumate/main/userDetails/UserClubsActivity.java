package com.stumate.main.userDetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.utils.Tags;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserClubsActivity extends AppCompatActivity {
    Button button2;
    ImageView imageView;
    ProgressBar mProgressBar;
    ImageView loading;
    TextView textView;
    TextView pleaseWait;
    ScrollView scrollView;

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db;
    String uid;
    FirebaseUser firebaseUser;
    UserProfileChangeRequest profileChangeRequest;

    List<String> _clubs = new ArrayList<>();
    List<String> _mates = new ArrayList<>();
    List<String> _pending = new ArrayList<>();
    List<String> _blocked = new ArrayList<>();
    List<String> _posts = new ArrayList<>();
    List<String> _inks = new ArrayList<>();


    private static final String TAG = "UserClubsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_clubs);

        db = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_right, R.anim.right_left);
            }
        });

        loading = (ImageView) findViewById(R.id.loading);
        textView = (TextView) findViewById(R.id.textView);
        pleaseWait = (TextView) findViewById(R.id.pleaseWait);
        scrollView = (ScrollView) findViewById(R.id.activity_user_clubs_scrollView);


        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {
                    visible(textView, loading, pleaseWait);
                    textView.setText("Finishing things up ...");
                    invisible(button2, imageView, scrollView);

                    // TODO: Done --- don't allow user sign in if database call dies ... (cause: no internet)
                    uploadUserData();
                } else {
                    Snackbar.make(findViewById(R.id.activity_user_clubs_constrainLayout), "No internet", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        updateUI();
    }

    private void visible(View... views) {
        for (View v :
                views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    private void invisible(View... views) {
        for (View v :
                views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    // TODO: Better way to check internet connection

    private boolean isConnected() {
        // check for internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        return isConnected;

        /*
        boolean isWiFi;
        if (isConnected)
            isWiFi = Objects.requireNonNull(networkInfo).getType() == ConnectivityManager.TYPE_WIFI;
            isMobile = Objects.requireNonNull(networkInfo).getType() == ConnectivityManager.TYPE_MOBILE;
         */
    }

    private void updateUI() {

        List<String> clubs = UserClassActivity.getClubs();
        if (clubs == null || clubs.isEmpty()) {
            Snackbar.make(findViewById(R.id.activity_user_clubs_constrainLayout), "No clubs found.. no worry you can create one after finishing up", Snackbar.LENGTH_LONG).show();
        } else {
            for (String s : clubs) {
                Log.d(TAG, "getClubs: " + s);
                final String key = s;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(32, 24, 32, 24);
                final Button btn = new Button(UserClubsActivity.this);
                btn.setText(key);
                btn.setTag(Tags.UNSELECTED_TAG);
                btn.setTextColor(getResources().getColor(R.color.black));
                btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (btn.getTag().equals(Tags.UNSELECTED_TAG)) {
                            _clubs.add(key);
                            btn.setTag(Tags.SELECTED_TAG);
                            btn.setTextColor(getResources().getColor(R.color.colorPrimary));
                            btn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        } else {
                            _clubs.remove(key);
                            btn.setTag(Tags.UNSELECTED_TAG);
                            btn.setTextColor(getResources().getColor(R.color.black));
                            btn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        }
                    }
                });
                LinearLayout linear = findViewById(R.id.activity_user_clubs_linearLayout);
                linear.addView(btn, params);
            }
        }

    }

    private void uploadUserData() {

        // TODO: Shared preferences
        SharedPreferences prefs = getSharedPreferences("userDetails", MODE_PRIVATE);
        String displayName = prefs.getString("displayName", "Stumate user");
        String email = prefs.getString("email", "No email");
        String imageUrl = prefs.getString("imageUrl", "");
        String phone = prefs.getString("phone", "");
        String bio = prefs.getString("bio", "");
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }
        Timestamp timestamp = new Timestamp(new Date());
        final String collegeName = prefs.getString("collegeName", "");
        String collegeShortName = prefs.getString("collegeShortName", "No CLG");
        final String className = prefs.getString("class", "");

        _clubs.add("# " + className);
        Map<String, Object> user = new HashMap<>();
        user.put("displayName", displayName);
        user.put("email", email);
        user.put("imageUrl", imageUrl);
        user.put("headerUrl", "");
        user.put("phone", phone);
        user.put("bio", bio);
        user.put("uid", uid);
        user.put("timestamp", timestamp);
        user.put("collegeName", collegeName);
        user.put("collegeShortName", collegeShortName);
        user.put("class", className);
        user.put("clubs", _clubs);
        user.put("mates", _mates);
        user.put("pending", _pending);
        user.put("blocked", _blocked);
        user.put("posts", _posts);
        user.put("inks", _inks);

        final Map<String, Object> publicUser = new HashMap<>();
        publicUser.put("displayName", displayName);
        publicUser.put("imageUrl", imageUrl);
        publicUser.put("class", className);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            //Add name and url to firebase auth db
            profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(imageUrl))
                    .build();
        } else {
            //Add name and url to firebase auth db
            profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
        }

        // Add a new document with the UID
        db.collection("users").document(uid)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseUser.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "onComplete: FirebaseAuth User profile updated");
                                            db.collection("institutes").document(collegeName).collection("public").document(uid)
                                                    .set(publicUser)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            firebaseUser.updateProfile(profileChangeRequest)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                Log.d(TAG, "onComplete: FirebaseAuth publicUser profile updated");

                                                                                for (String name :
                                                                                        _clubs) {
                                                                                    db.collection("institutes")
                                                                                            .document(collegeName)
                                                                                            .collection("clubs")
                                                                                            .document(name)
                                                                                            .update("mates", FieldValue.arrayUnion(uid));
                                                                                }
                                                                                startActivity(new Intent(UserClubsActivity.this, TabLayoutActivity.class));
                                                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Snackbar.make(findViewById(R.id.activity_user_clubs_constrainLayout), "Something's wrong... please retry :)", Snackbar.LENGTH_LONG).show();
                                                            invisible(textView, loading, pleaseWait);
                                                            visible(button2, imageView, scrollView);
                                                            Log.w(TAG, "Error adding document", e);
                                                        }
                                                    });
                                        }
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(findViewById(R.id.activity_user_clubs_constrainLayout), "Something's wrong... please retry :)", Snackbar.LENGTH_LONG).show();
                        invisible(textView, loading, pleaseWait);
                        visible(button2, imageView, scrollView);
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_right, R.anim.right_left);
    }
}
