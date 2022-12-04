package com.stumate.main.profileDetails;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stumate.main.MainActivity;
import com.stumate.main.R;

public class ProfileNameActivity extends AppCompatActivity {
    // UI variables
    EditText editText;
    Button button;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_name);

        user = FirebaseAuth.getInstance().getCurrentUser();

        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Validate name and store it - (in SharedPreferences)
                String displayName = editText.getText().toString();

                if (validate(displayName)) {
                    SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                    editor.putString("displayName", displayName);
                    editor.apply();

                    startActivity(new Intent(ProfileNameActivity.this, ProfilePictureActivity.class));
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("Exit", true);
        startActivity(intent);
        finish();
    }

    public boolean validate(String s) {
        if (s.isEmpty()) {
            editText.setError("Name can't be empty");
            return false;
        }
        else {
            if (s.length() >= 22) {
                editText.setError("Enter name under 22 characters");
                return false;
            }
            if (s.startsWith("#")) {
                editText.setError("Name can't start with #");
                return false;
            }
        }
        return true;
    }
}
