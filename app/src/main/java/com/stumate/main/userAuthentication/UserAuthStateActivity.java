package com.stumate.main.userAuthentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;
import com.stumate.main.profileDetails.ProfileNameActivity;
import com.stumate.main.tabLayout.TabLayoutActivity;
import com.stumate.main.utils.IntentVariables;
import com.stumate.main.utils.Tags;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class UserAuthStateActivity extends AppCompatActivity {
    // layout views
    TextView textView;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textView6;
    EditText editText;
    ImageView imageView;
    ImageView imageView2;
    Button button;

    private static final String TAG = "UserAuthStateActivity";

    // state codes
    private static final int STATE_WAIT = 0;
    private static final int STATE_NO_NETWORK = 1;
    private static final int STATE_PHONE_VERIFICATION = 2;
    private static final int STATE_PHONE_VERIFY_SUCCESS = 3;
    private static final int STATE_PHONE_VERIFY_FAILED = 4;
    private static final int STATE_EMAIL_VERIFICATION = 5;
    private static final int STATE_EMAIL_VERIFY_SUCCESS = 6;
    private static final int STATE_EMAIL_VERIFY_FAILED = 7;

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private boolean verificationInProgress = false;
    private boolean verifiedUser = false;

    private static int CURRENT_TAG;
    private static String email;
    private static String phone;
    private boolean newAccount;

    private String verificationId;
    private FirebaseAuth mAuth;
    private ActionCodeSettings actionCodeSettings;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    FirebaseUser firebaseUser;
    UserProfileChangeRequest profileChangeRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth_state);

        // check for new account (or login if false)
        newAccount = getIntent().getBooleanExtra(IntentVariables.newAccount, true);

        if (getIntent().getIntExtra(IntentVariables.authState, 100) == Tags.EMAIL_TAG) {
            CURRENT_TAG = Tags.EMAIL_TAG;
            email = getIntent().getStringExtra(IntentVariables.auth);
        } else if (getIntent().getIntExtra(IntentVariables.authState, 100) == Tags.PHONE_TAG) {
            CURRENT_TAG = Tags.PHONE_TAG;
            phone = "+91" + getIntent().getStringExtra(IntentVariables.auth);
        }

        // layout views
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_right, R.anim.right_left);
            }
        });
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        editText = (EditText) findViewById(R.id.editText);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatePhoneNumber(phone))
                    resendVerificationCode(phone, mResendToken);
            }
        });
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        textView6 = (TextView) findViewById(R.id.textView6);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifiedUser) {
                    SharedPreferences prefs = getSharedPreferences("temp", MODE_PRIVATE);
                    boolean newAccount = prefs.getBoolean("newAccount", true);
                    if (newAccount) {
                        startActivity(new Intent(UserAuthStateActivity.this, ProfileNameActivity.class));
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    } else {
                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (Objects.equals(firebaseUser.getDisplayName(), "#stumate")) {
                            Intent intent = new Intent(UserAuthStateActivity.this, ProfileNameActivity.class);
                            startActivity(intent);
                        } else {
                            if (isConnected()) {
                                Intent intent = new Intent(UserAuthStateActivity.this, TabLayoutActivity.class);
                                startActivity(intent);

                            } else {
                                Snackbar.make(findViewById(R.id.userAuthStateConstrainLayout), "No Network", Snackbar.LENGTH_SHORT).show();
                            }

                        }
                    }
                } else if (CURRENT_TAG == Tags.PHONE_TAG) {
                    String code = editText.getText().toString();

                        if (!TextUtils.isEmpty(code)) {
                            if (code.length() != 6) {
                                editText.setError("Invalid Code");
                            }
                            else {
                                if (!verifiedUser) {
                                    imageView.setVisibility(View.INVISIBLE);
                                    verifyPhoneNumberWithCode(verificationId, code);
                                }
                            }
                        } else {
                            editText.setError("Enter Code");
                        }
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "VerificationCompleted:" + phoneAuthCredential);
                verificationInProgress = false;
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "VerificationFailed", e);
                verificationInProgress = false;
                if (!verifiedUser) {
                    updateUI(STATE_PHONE_VERIFY_FAILED);
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Snackbar.make(findViewById(R.id.userAuthStateConstrainLayout), "Invalid phone number", Snackbar.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseTooManyRequestsException) {
                        Snackbar.make(findViewById(R.id.userAuthStateConstrainLayout), "Quota Exceeded - Too many requests.", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.d(TAG, "CodeSent:" + s);
                verificationId = s;
                mResendToken = forceResendingToken;
                updateUI(STATE_PHONE_VERIFICATION, phone);
            }
        };

        actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://stumate-bakend.firebaseapp.com/")
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.stumate.main",
                                true, /* installIfNotAvailable */
                                "1"    /* minimumVersion */)
                        .build();


        // deep link verification
        if (getIntent().getBooleanExtra("Email", false)) {
            verifySignInLink(getIntent().getData().toString());
        } else if (newAccount) {
            startAuthentication();
        } else {
            startAuthentication();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (verificationInProgress && validatePhoneNumber(phone)) {
            startPhoneNumberVerification(phone);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, verificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        verificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_right, R.anim.right_left);
    }

    private boolean isConnected() {
        // check for internet connection
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

        /*
        boolean isWiFi;
        if (isConnected)
            isWiFi = Objects.requireNonNull(networkInfo).getType() == ConnectivityManager.TYPE_WIFI;
            isMobile = Objects.requireNonNull(networkInfo).getType() == ConnectivityManager.TYPE_MOBILE;
         */
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

    private void gone(View... views) {
        for (View v :
                views) {
            v.setVisibility(View.GONE);
        }
    }

    private void startAuthentication() {
        if (!isConnected()) {
            updateUI(STATE_NO_NETWORK);
            Snackbar.make(findViewById(R.id.userAuthStateConstrainLayout), "No network", Snackbar.LENGTH_SHORT).show();
        } else if (CURRENT_TAG == Tags.EMAIL_TAG) {
            SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
            editor.putString("email", email);
            editor.putString("phone", null);
            editor.apply();

            SharedPreferences.Editor editor1 = getSharedPreferences("temp", MODE_PRIVATE).edit();
            editor1.putBoolean("newAccount", newAccount);
            editor1.putInt("first", 0);
            editor1.apply();

            sendSignInLink(email, actionCodeSettings);
        } else if (CURRENT_TAG == Tags.PHONE_TAG) {
            SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
            editor.putString("email", null);
            editor.putString("phone", phone);
            editor.apply();

            SharedPreferences.Editor editor1 = getSharedPreferences("temp", MODE_PRIVATE).edit();
            editor1.putBoolean("newAccount", newAccount);
            editor1.apply();
            startPhoneNumberVerification(phone);
        }
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches() && phoneNumber.length() == 13) {
            return true;
        }
        return false;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                90,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        verificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            button.setEnabled(false);
                            AuthResult result = task.getResult();
                            // You can access the new user via result.getUser()
                            // Additional user info profile *not* available via:
                            // result.getAdditionalUserInfo().getProfile() == null
                            // You can check if the user is new or existing:

                            firebaseUser = result.getUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();


                            SharedPreferences prefs = getSharedPreferences("temp", MODE_PRIVATE);
                            final boolean newAccount = prefs.getBoolean("newAccount", true);

                            if (result.getAdditionalUserInfo().isNewUser()) {
                                profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName("#stumate")
                                        .build();

                                Map<String, Object> user = new HashMap<>();
                                user.put("phone", phone);
                                user.put("uid", firebaseUser.getUid());

                                db.collection("users").document(firebaseUser.getUid())
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
                                                                    Log.d(TAG, "verifyPhoneWithCode:success");
                                                                    verifiedUser = true;

                                                                    if (newAccount) {
                                                                        startActivity(new Intent(UserAuthStateActivity.this, ProfileNameActivity.class));
                                                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                                                    } else {
                                                                        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                                                        if (Objects.equals(firebaseUser.getDisplayName(), "#stumate")) {
                                                                            Intent intent = new Intent(UserAuthStateActivity.this, ProfileNameActivity.class);
                                                                            startActivity(intent);
                                                                        } else {
                                                                            Intent intent = new Intent(UserAuthStateActivity.this, TabLayoutActivity.class);
                                                                            startActivity(intent);

                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });
                                                Log.d(TAG, "DocumentSnapshot added");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                button.setEnabled(true);
                                                Snackbar.make(findViewById(R.id.activity_user_clubs_constrainLayout), "Something's wrong... please retry :)", Snackbar.LENGTH_LONG).show();
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            } else {
                                Log.d(TAG, "verifyPhoneWithCode:success  --  old user");

                                if (newAccount) {
                                    startActivity(new Intent(UserAuthStateActivity.this, ProfileNameActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                } else {
                                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (Objects.equals(firebaseUser.getDisplayName(), "#stumate")) {
                                        Intent intent = new Intent(UserAuthStateActivity.this, ProfileNameActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(UserAuthStateActivity.this, TabLayoutActivity.class);
                                        startActivity(intent);

                                    }
                                }
                            }
                        } else {
                            if (!verifiedUser) {
                                button.setEnabled(true);
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    editText.setError("Invalid code.");
                                } else {
                                    updateUI(STATE_PHONE_VERIFY_FAILED);
                                    Snackbar.make(findViewById(R.id.userAuthStateConstrainLayout), "SignIn Failed", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });

    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        Toast.makeText(this, "Code Re-Sent", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                90,
                TimeUnit.SECONDS,
                this,
                mCallbacks,
                token);
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            AuthResult result = task.getResult();
                            // You can access the new user via result.getUser()
                            // Additional user info profile *not* available via:
                            // result.getAdditionalUserInfo().getProfile() == null
                            // You can check if the user is new or existing:

                            firebaseUser = result.getUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            if (result.getAdditionalUserInfo().isNewUser()) {
                                profileChangeRequest = new UserProfileChangeRequest.Builder()
                                        .setDisplayName("#stumate")
                                        .build();

                                Map<String, Object> user = new HashMap<>();
                                user.put("phone", phone);
                                user.put("uid", firebaseUser.getUid());

                                db.collection("users").document(firebaseUser.getUid())
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
                                                                    Log.d(TAG, "signInWithPhoneAuthCredential:success");
                                                                    verifiedUser = true;
                                                                    updateUI(STATE_PHONE_VERIFY_SUCCESS, credential);
                                                                }
                                                            }
                                                        });
                                                Log.d(TAG, "DocumentSnapshot added - User phone number");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(findViewById(R.id.activity_user_clubs_constrainLayout), "Something's wrong... please retry :)", Snackbar.LENGTH_LONG).show();
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                            else {
                                Log.d(TAG, "signInWithPhoneAuthCredential:success  --  old user");
                                verifiedUser = true;
                                updateUI(STATE_PHONE_VERIFY_SUCCESS, credential);
                            }
                        }
                    }
                });
    }

    public void sendSignInLink(final String email, ActionCodeSettings actionCodeSettings) {
        // [START auth_send_sign_in_link]
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            updateUI(STATE_EMAIL_VERIFICATION, email);
                        } else {
                            updateUI(STATE_EMAIL_VERIFY_FAILED);
                        }
                    }
                });
    }

    public void verifySignInLink(String emailLink) {
        // [START auth_verify_sign_in_link]
        SharedPreferences prefs = getSharedPreferences("userDetails", MODE_PRIVATE);
        final String sEmail = prefs.getString("email", "error");

        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {

            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(sEmail, emailLink)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Successfully signed in with email link!");
                                AuthResult result = task.getResult();
                                // You can access the new user via result.getUser()
                                // Additional user info profile *not* available via:
                                // result.getAdditionalUserInfo().getProfile() == null
                                // You can check if the user is new or existing:
                                if (result.getAdditionalUserInfo().isNewUser()) {
                                    profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName("#stumate")
                                            .build();

                                    firebaseUser = result.getUser();

                                    firebaseUser.updateProfile(profileChangeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: FirebaseAuth User profile updated");
                                                        verifiedUser = true;
                                                        updateUI(STATE_EMAIL_VERIFY_SUCCESS, sEmail);
                                                    }
                                                }
                                            });
                                }
                                else {
                                    Log.d(TAG, "onComplete: FirebaseAuth User profile updated  --  old user");
                                    verifiedUser = true;
                                    updateUI(STATE_EMAIL_VERIFY_SUCCESS, sEmail);
                                }

                            } else {
                                updateUI(STATE_EMAIL_VERIFY_FAILED);
                                Log.e(TAG, "Error signing in with email link", task.getException());
                            }
                        }
                    });
        }
    }

//    private void getUserDetails() {
//        visible(imageView, imageView2, textView, textView4);
//        gone(textView2, textView3, textView5, textView6, editText, button);
//        imageView2.setImageResource(R.drawable.ic_more_horiz_40dp);
//        textView.setText("Getting things together");
//        textView4.setText("Another second please...");
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("users")
//                .document(user.getUid())
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                            if (document.exists()) {
//                                String displayName = (String) document.get("displayName");
//                                String imageUrl = (String) document.get("imageUrl");
//                                String headerUrl = (String) document.get("headerUrl");
//                                String bio = (String) document.get("bio");
//                                String collegeName = (String) document.get("collegeName");
//                                String collegeShortName = (String) document.get("collegeShortName");
//                                String userClass = (String) document.get("class");
//                                String email = (String) document.get("email");
//                                String phone = (String) document.get("phone");
//                                String uid = (String) document.get("uid");
//
////                                List<String> clubs = (List<String>) document.get("clubs");
////                                List<String> mates = (List<String>) document.get("mates");
////                                List<String> posts = (List<String>) document.get("Post");
////                                List<String> inks = (List<String>) document.get("inks");
////                                List<String> pending = (List<String>) document.get("pending");
////                                List<String> blocked = (List<String>) document.get("blocked");
//
//                                SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
//                                editor.putString("displayName", displayName);
//                                editor.putString("imageUrl", imageUrl);
//                                editor.putString("headerUrl", headerUrl);
//                                editor.putString("email", email);
//                                editor.putString("phone", phone);
//                                editor.putString("uid", uid);
//                                editor.putString("bio", bio);
//                                editor.putString("class", userClass);
//                                editor.putString("collegeName", collegeName);
//                                editor.putString("collegeShortName", collegeShortName);
//                                editor.apply();
//
//                                Intent intent = new Intent(UserAuthStateActivity.this, TabLayoutActivity.class);
//                                startActivity(intent);
//
//                            } else {
//                                visible(imageView, imageView2, textView, textView4);
//                                gone(textView2, textView3, textView5, textView6, editText, button);
//                                imageView2.setImageResource(R.drawable.ic_error_outline_black_80dp);
//                                textView.setText("Well there's a problem");
//                                textView4.setText("Can't find user details");
//                                Log.d(TAG, "No such document");
//                                Snackbar.make(findViewById(R.id.activity_user_clubs_constrainLayout), "User detials not found", Snackbar.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            visible(imageView, imageView2, textView, textView4);
//                            gone(textView2, textView3, textView5, textView6, editText, button);
//                            imageView2.setImageResource(R.drawable.ic_error_outline_black_80dp);
//                            textView.setText("Well there's a problem");
//                            textView4.setText("Data retrieval Failed.");
//                            // TODO: exception handling
//                            Log.d(TAG, "get failed with ", task.getException());
//                            Snackbar.make(findViewById(R.id.activity_user_clubs_constrainLayout), "Something's wrong... retry", Snackbar.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private void updateUI(int uiState) {
        updateUI(uiState, null, null);
    }

    private void updateUI(int uiState, String str) {
        updateUI(uiState, null, str);
    }

    private void updateUI(int uiState, PhoneAuthCredential credential) {
        updateUI(uiState, credential, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred, final String str) {
        switch (uiState) {
            case STATE_WAIT:
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                visible(imageView, imageView2, textView4);
                gone(textView2, textView3, textView6, editText, button, textView, textView5);
                imageView2.setImageResource(R.drawable.ic_more_horiz_40dp);
                textView4.setText("Please wait...");
                break;
            case STATE_NO_NETWORK:
                visible(imageView, imageView2, textView, textView4, textView5);
                gone(textView2, textView3, textView6, editText, button);
                imageView2.setImageResource(R.drawable.ic_cloud_off_black_80dp);
                textView.setText("Well, there's a problem");
                textView4.setText("No Internet Connection.");
                textView5.setText("Retry?");
                textView5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updateUI(STATE_WAIT);
                        startAuthentication();
                    }
                });
                break;
            case STATE_PHONE_VERIFICATION:
                View view2 = this.getCurrentFocus();
                if (view2 != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(editText, 0);
                }
                visible(imageView, textView, textView2, textView3, textView6, button, editText);
                invisible(textView4, textView5, imageView2);

                imageView2.setImageResource(R.drawable.ic_verified_user_82dp);
                textView.setText("We sent you a code");
                textView2.setText("Enter it below to verify +91 " + str.substring(3));
                textView3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        resendVerificationCode(str, mResendToken);
                    }
                });
                break;
            case STATE_PHONE_VERIFY_SUCCESS:
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        editText.setText("");
                        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        editText.setText(cred.getSmsCode());  // Instant verification
                        editText.setEnabled(false);

                        textView3.setText("Instant Verification Done");
                        textView3.setOnClickListener(null);
                    }
                }
                break;
            case STATE_PHONE_VERIFY_FAILED:
                visible(imageView, imageView2, textView, textView4);
                invisible(textView2, textView3, textView5, textView6, editText, button);
                imageView2.setImageResource(R.drawable.ic_error_outline_black_80dp);
                textView.setText("Well there's a problem");
                textView4.setText("Phone Verification Failed.");
                break;
            case STATE_EMAIL_VERIFICATION:
                visible(imageView, textView, textView2, textView4, imageView2);
                gone(textView3, editText, textView5);
                imageView2.setImageResource(R.drawable.ic_more_horiz_40dp);
                textView.setText("We emailed a link");
                textView2.setText("Click on it to verify " + str);
                textView4.setText("We are waiting ...");
                break;
            case STATE_EMAIL_VERIFY_SUCCESS:
                visible(imageView, textView, textView2, imageView2, textView4, textView6, button);
                invisible(textView3, editText, textView5);
                imageView2.setImageResource(R.drawable.ic_verified_user_82dp);
                textView.setText("Email Verified");
                textView2.setText("Click next to proceed");
                textView4.setText("Verified");
                break;
            case STATE_EMAIL_VERIFY_FAILED:
                visible(imageView, imageView2, textView, textView4);
                gone(textView2, textView3, textView5, textView6, editText, button);
                imageView2.setImageResource(R.drawable.ic_error_outline_black_80dp);
                textView.setText("Well there's a problem");
                textView4.setText("Email Verification Failed.");
                break;
        }
    }
}