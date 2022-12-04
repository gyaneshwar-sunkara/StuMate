package com.stumate.main.userDetails;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stumate.main.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserClassActivity extends AppCompatActivity {
    Button button;
    ImageView imageView;
    Spinner college;
    Spinner dept;
    Spinner year;
    Spinner section;
    ProgressBar mProgressBar;

    private List<String> institutes;
    private List<String> classes;
    private static List<String> clubs;
    private AlertDialog.Builder builder;

    private static final String TAG = "UserClassActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_class);

        builder = new AlertDialog.Builder(this);

        college = (Spinner) findViewById(R.id.spinner);

        dept = (Spinner) findViewById(R.id.spinner2);

        year = (Spinner) findViewById(R.id.spinner3);

        section = (Spinner) findViewById(R.id.spinner4);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_right, R.anim.right_left);
            }
        });

        // TODO: take this retry structure as example its fucking cool ... for error handling i mean

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected()) {
                    Snackbar.make(findViewById(R.id.userClassContainer), "No Internet", Snackbar.LENGTH_SHORT).show();
                } else if (college.getSelectedItem() == null) {
                    button.setText("Next");
                    updateUI();
                }
            }
        });

        mProgressBar = findViewById(R.id.progressBar);

        if (isConnected()) {
            updateUI();
        } else {
            button.setText("Retry");
            Snackbar.make(findViewById(R.id.userClassContainer), "No Internet", Snackbar.LENGTH_SHORT).show();
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

    private void updateUI() {
        mProgressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("institutes").document("details");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mProgressBar.setVisibility(View.GONE);
                    DocumentSnapshot document = task.getResult();
                    // TODO: this is an asynchronous call handle it efficiently later ...
                    if (Objects.requireNonNull(document).exists()) {
                        HashMap<String, Object> doc = (HashMap<String, Object>) document.getData();
                        assert doc != null;
                        institutes = new ArrayList<>(doc.keySet());
                        Log.d(TAG, "DocumentSnapshot data: institutes " + institutes);

                        // TODO:: Debug accessing classes and clubs from "narsimha reddy " Hardcoded values
                        HashMap<String, Object> collegeData = (HashMap<String, Object>) (doc.get("Narsimha Reddy Engineering College"));
                        Log.d(TAG, "onComplete: classes " + collegeData);
                        clubs = (ArrayList<String>) collegeData.get("clubs");
                        Log.d(TAG, "onComplete: clubs " + clubs);
                        // TODO: Give classes its own activity : in far future...
                        classes = (ArrayList<String>) collegeData.get("classes");
                        Log.d(TAG, "onComplete: clubs " + classes);

                        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                                UserClassActivity.this, R.layout.layout_spinner_text, Objects.requireNonNull(institutes));
                        spinnerArrayAdapter.setDropDownViewResource(R.layout.layout_spinner_text);
                        college.setAdapter(spinnerArrayAdapter);

                        final List<String> deptList = Arrays.asList(getResources().getStringArray(R.array.group));
                        final ArrayAdapter<String> spinnerArrayAdapter0 = new ArrayAdapter<String>(
                                UserClassActivity.this, R.layout.layout_spinner_text, deptList);
                        spinnerArrayAdapter0.setDropDownViewResource(R.layout.layout_spinner_text);
                        dept.setAdapter(spinnerArrayAdapter0);

                        final List<String> yearList = Arrays.asList(getResources().getStringArray(R.array.year));
                        final ArrayAdapter<String> spinnerArrayAdapter2 = new ArrayAdapter<String>(
                                UserClassActivity.this, R.layout.layout_spinner_text, yearList);
                        spinnerArrayAdapter2.setDropDownViewResource(R.layout.layout_spinner_text);
                        year.setAdapter(spinnerArrayAdapter2);

                        final List<String> sectionList = Arrays.asList(getResources().getStringArray(R.array.section));
                        final ArrayAdapter<String> spinnerArrayAdapter3 = new ArrayAdapter<String>(
                                UserClassActivity.this, R.layout.layout_spinner_text, sectionList);
                        spinnerArrayAdapter3.setDropDownViewResource(R.layout.layout_spinner_text);
                        section.setAdapter(spinnerArrayAdapter3);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // TODO: Shared preferences
                                SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                                editor.putString("collegeName", college.getSelectedItem().toString());
                                String collegeShortName = "";
                                for (String x :
                                        college.getSelectedItem().toString().split(" ")) {
                                    collegeShortName += x.charAt(0);
                                }
                                Log.d(TAG, "onClick: collegeShortName - " + collegeShortName);
                                editor.putString("collegeShortName", collegeShortName);
                                editor.putString("class", dept.getSelectedItem().toString() + " " + year.getSelectedItem().toString().split(" ")[0] + " " + section.getSelectedItem().toString());
                                editor.apply();

                                builder.setMessage("You can not change your class later on, do you wish to proceed?")
                                        .setTitle(collegeShortName + " - " + dept.getSelectedItem().toString() + " " + year.getSelectedItem().toString().split(" ")[0] + " " + section.getSelectedItem().toString())
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                startActivity(new Intent(UserClassActivity.this, UserClubsActivity.class));
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                            }
                                        })
                                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                        .show();


                            }
                        });

                    } else {
                        button.setText("Retry");
                        Log.d(TAG, "No such document");
                        Snackbar.make(findViewById(R.id.userClassContainer), "Class details not found", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    button.setText("Retry");
                    mProgressBar.setVisibility(View.GONE);
                    Log.d(TAG, "get failed with ", task.getException());
                    Snackbar.make(findViewById(R.id.userClassContainer), "Poor network connection ...", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static List<String> getClubs() {
        return clubs;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_right, R.anim.right_left);
    }
}
