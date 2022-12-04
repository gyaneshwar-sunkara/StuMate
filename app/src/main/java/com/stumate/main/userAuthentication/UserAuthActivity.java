package com.stumate.main.userAuthentication;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.stumate.main.R;
import com.stumate.main.utils.IntentVariables;
import com.stumate.main.utils.Tags;

public class  UserAuthActivity extends AppCompatActivity {
    // UI variables
    TextView userAuth;
    TextView textView;
    TextView textView2;
    EditText editText;
    ImageView imageView;
    Button button;
    Button button2;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private static boolean newAccount;

    private static final String TAG = "UserAuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_auth);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        newAccount = getIntent().getBooleanExtra(IntentVariables.newAccount, true);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_right, R.anim.right_left);
            }
        });
        button = (Button) findViewById(R.id.button);
        button.setTag(Tags.PHONE_TAG);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeUI();
            }
        });
        userAuth = (TextView) findViewById(R.id.authState);
        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText) findViewById(R.id.editText);
        textView2 = (TextView) findViewById(R.id.textView2);
        button2 = (Button) findViewById(R.id.button2);

        if (newAccount) {
            // TODO: Create new account
            userAuth.setText("New Account");

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button2.setEnabled(false);
                    if (button.getTag().equals(Tags.PHONE_TAG)) {
                        validate_email();
                    } else if (button.getTag().equals(Tags.EMAIL_TAG)) {
                        validate_phone();
                    }
                }
            });
        } else {
            // TODO: Login in user
            userAuth.setText("Log In");

            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button2.setEnabled(false);
                    if (button.getTag() != null) {
                        if (button.getTag().equals(Tags.PHONE_TAG)) {
                            validate_email();
                        } else if (button.getTag().equals(Tags.EMAIL_TAG)) {
                            validate_phone();
                        }
                    }
                }
            });
        }
    }

    private void changeUI() {
        if (button.getTag().equals(Tags.EMAIL_TAG)) {
            button2.setEnabled(true);
            button.setTag(Tags.PHONE_TAG);
            button.setText("Use Phone");
            textView.setText("Enter Your Email");
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            editText.getText().clear();
            editText.setHint("Email Address");
            editText.setError(null);
            return;
        }

        if (button.getTag().equals(Tags.PHONE_TAG)) {
            button2.setEnabled(true);
            button.setTag(Tags.EMAIL_TAG);
            button.setText("Use Email");
            textView.setText("Enter Your Phone");
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
            editText.getText().clear();
            editText.setHint("Phone Number");
            editText.setError(null);
        }
    }

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

    private void validate_email() {

        String email = editText.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // TODO: Check for existing users
            if (isConnected()) {
                auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(UserAuthActivity.this, new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "checking to see if user exists in firebase or not");
                            SignInMethodQueryResult result = task.getResult();

                            if (result != null && result.getSignInMethods() != null
                                    && result.getSignInMethods().size() > 0) {
                                if (newAccount) {
                                    button2.setEnabled(true);
                                    Log.d(TAG, "User exists");
                                    editText.setError("Email already registered");
                                    Snackbar.make(findViewById(R.id.userAuthContainer), "Log in instead!", Snackbar.LENGTH_SHORT).show();
                                } else {
                                    Intent intent = new Intent(UserAuthActivity.this, UserAuthStateActivity.class);
                                    intent.putExtra(IntentVariables.newAccount, newAccount);
                                    intent.putExtra(IntentVariables.auth, editText.getText().toString());
                                    intent.putExtra(IntentVariables.authState, Tags.EMAIL_TAG);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                }
                            } else {
                                Log.d(TAG, "User doesn't exist, create account");
                                if (newAccount) {
                                    Intent intent = new Intent(UserAuthActivity.this, UserAuthStateActivity.class);
                                    intent.putExtra(IntentVariables.newAccount, newAccount);
                                    intent.putExtra(IntentVariables.auth, editText.getText().toString());
                                    intent.putExtra(IntentVariables.authState, Tags.EMAIL_TAG);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                } else {
                                    button2.setEnabled(true);
                                    editText.setError("Email not registered");
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    }, 5000);
                                    Snackbar.make(findViewById(R.id.userAuthContainer), "Create New Account Instead!", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            button2.setEnabled(true);
                            Log.d(TAG, "User auth failed", task.getException());
                            Snackbar.make(findViewById(R.id.userAuthContainer), "Something's wrong, try again later!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                button2.setEnabled(true);
                editText.setError("No network");
            }
        } else {
            button2.setEnabled(true);
            editText.setError("Invalid email");
        }
    }

    private void validate_phone() {
        final String phone = "+91" + editText.getText().toString();
        if (Patterns.PHONE.matcher(phone).matches() && phone.length() == 13) {
            if (isConnected()) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users")
                        .whereEqualTo("phone", phone)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: documents found - " + task.getResult().size() + " " + task.getResult().isEmpty());
                                    if (!task.getResult().isEmpty()) {
                                        Log.d(TAG, "onComplete: phone number exists - " + task.getResult());
                                        if (newAccount) {
                                            button2.setEnabled(true);
                                            Log.d(TAG, "User exists");
                                            editText.setError("Phone already registered");
                                            Snackbar.make(findViewById(R.id.userAuthContainer), "Log in instead!", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            Intent intent = new Intent(UserAuthActivity.this, UserAuthStateActivity.class);
                                            intent.putExtra(IntentVariables.newAccount, newAccount);
                                            intent.putExtra(IntentVariables.auth, editText.getText().toString());
                                            intent.putExtra(IntentVariables.authState, Tags.PHONE_TAG);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.enter, R.anim.exit);
                                        }
                                    } else {
                                        Log.d(TAG, "User doesn't exist, create account");
                                        if (newAccount) {
                                            Intent intent = new Intent(UserAuthActivity.this, UserAuthStateActivity.class);
                                            intent.putExtra(IntentVariables.newAccount, newAccount);
                                            intent.putExtra(IntentVariables.auth, editText.getText().toString());
                                            intent.putExtra(IntentVariables.authState, Tags.PHONE_TAG);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.enter, R.anim.exit);
                                        } else {
                                            button2.setEnabled(true);
                                            editText.setError("Phone not registered");
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {

                                                }
                                            }, 5000);
                                            Snackbar.make(findViewById(R.id.userAuthContainer), "Create New Account Instead!", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    button2.setEnabled(true);
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    Snackbar.make(findViewById(R.id.userAuthContainer), "There is a problem, try again later!", Snackbar.LENGTH_SHORT).show();

                                }
                            }
                        });
            } else {
                button2.setEnabled(true);
                editText.setError("No network");
            }
        } else {
            button2.setEnabled(true);
            editText.setError("Invalid phone");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_right, R.anim.right_left);
    }

    @Override
    protected void onStart() {
        super.onStart();
        button2.setEnabled(true);
    }
}
