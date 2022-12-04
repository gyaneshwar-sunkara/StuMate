package com.stumate.main.profileDetails;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.stumate.main.R;
import com.stumate.main.userDetails.UserClassActivity;

public class ProfileBioActivity extends AppCompatActivity {
    // UI variables
    EditText editText;
    Button button;
    Button button2;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_bio);
        editText = (EditText) findViewById(R.id.editText);

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_right, R.anim.right_left);
            }
        });

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileBioActivity.this, UserClassActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });

        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bio = editText.getText().toString();
                if(validateBio(bio)) {
                    SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                    editor.putString("bio", bio);
                    editor.apply();

                    startActivity(new Intent(ProfileBioActivity.this, UserClassActivity.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
    }

    private boolean validateBio(String s) {
        if (s.isEmpty()) {
            editText.setError("You can skip this field");
            return false;
        }
        else if (s.length() > 200) {
            editText.setError("Less than 200 characters");
            return false;
        }
        else return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_right, R.anim.right_left);
    }
}