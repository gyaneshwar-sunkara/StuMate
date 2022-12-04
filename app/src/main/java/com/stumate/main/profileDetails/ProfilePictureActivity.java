package com.stumate.main.profileDetails;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.stumate.main.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePictureActivity extends AppCompatActivity {
    ImageView imageView;
    CircleImageView imageView2;
    ImageView select;
    Button button;
    Button button2;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    private StorageReference mStorageRef;

    FirebaseUser firebaseUser;

    private static final String TAG = "ProfilePictureActivity";

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mStorageRef = FirebaseStorage.getInstance().getReference(firebaseUser.getUid());

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.left_right, R.anim.right_left);
            }
        });
        imageView2 = (CircleImageView) findViewById(R.id.imageView2);

        select = (ImageView) findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Gallery Intent
                openFileChooser();
            }
        });

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Validate name and store it - (in SharedPreferences)
                SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                editor.putString("imageUrl", null);
                editor.apply();
                startActivity(new Intent(ProfilePictureActivity.this, ProfileBioActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Validate name and store it - (in SharedPreferences)

                // TODO: check and remove this clause
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ProfilePictureActivity.this, "Upload in progress, slow network connection...please wait", Toast.LENGTH_SHORT).show();
                } else {
                    if(isConnected()) {
                        uploadFile();
                    }
                    else {
                        Snackbar.make(findViewById(R.id.profilePictureContainer), "No Network", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
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

    // TODO: Add other functionalism's : camera photos manager ...

    private void openFileChooser() {
        CropImage.startPickImageActivity(this);
    }

    private void openImageCropper(Uri imageUrl) {
        CropImage.activity(imageUrl)
                .setAspectRatio(1,1)
                .setAutoZoomEnabled(true)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setInitialCropWindowPaddingRatio(0)
                .setOutputCompressQuality(20)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(this);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (mImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                openImageCropper(mImageUri);
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mImageUri = imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                }
            } else {
                // no permissions required or already granted, can start crop image activity
                openImageCropper(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                if (mImageUri != null) {
                    Glide.with(getApplicationContext())
                            .load(mImageUri)
                            .into(imageView2);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    private void uploadFile() {
        button.setVisibility(View.GONE);
        button2.setEnabled(false);
        if (mImageUri != null) {

            final StorageReference fileReference = mStorageRef.child("profilePicture"
                    + "." + getFileExtension(mImageUri));
            Toast.makeText(ProfilePictureActivity.this, "Uploading image just a sec...", Toast.LENGTH_SHORT).show();
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String DownloadUri = uri.toString();
                                            SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                                            editor.putString("imageUrl", DownloadUri);
                                            Log.d(TAG, "onSuccess: profile picture uri " + DownloadUri);
                                            editor.apply();
                                            button.setVisibility(View.VISIBLE);
                                            button2.setEnabled(true);
                                            startActivity(new Intent(ProfilePictureActivity.this, ProfileBioActivity.class));
                                            overridePendingTransition(R.anim.enter, R.anim.exit);

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(findViewById(R.id.profilePictureContainer), "No uri found", Snackbar.LENGTH_LONG).show();
                                            button.setVisibility(View.VISIBLE);
                                            button2.setEnabled(true);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(findViewById(R.id.profilePictureContainer), "Error occurred", Snackbar.LENGTH_LONG).show();
                            Toast.makeText(ProfilePictureActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            button.setVisibility(View.VISIBLE);
                            button2.setEnabled(true);
                        }
                    });

        } else {
            Snackbar.make(findViewById(R.id.profilePictureContainer), "No file selected, you can skip this field", Snackbar.LENGTH_LONG).show();
            button.setVisibility(View.VISIBLE);
            button2.setEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.left_right, R.anim.right_left);
    }
}
