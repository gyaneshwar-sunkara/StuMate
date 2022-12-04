package com.stumate.main.tabLayout.posts.uploads;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.snackbar.Snackbar;
import com.stumate.main.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class FileUpload extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int UPLOAD_POST = 11;

    private ImageView close;
    private ImageView next;
    private ImageView imageView;
    private ImageView select;
    private Uri mImageUri;
    private Uri tmpURI;
    private ImageView retake;
    private ImageView crop;
    private InterstitialAd interstitialAd;
    private static final String TAG = "FileUpload";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-6129418659811438/3006479086");
        AdRequest adRequest2 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest2);

        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        next = findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(next, "Choose an image to proceed", Snackbar.LENGTH_SHORT).show();
            }
        });
        imageView = findViewById(R.id.image_view);
        retake = findViewById(R.id.retake);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
        crop = findViewById(R.id.crop);
        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tmpURI != null) {
                    openImageCropper(tmpURI);
                }
            }
        });
        select = findViewById(R.id.select);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        CropImage.startPickImageActivity(this);
    }

    private void openImageCropper(Uri imageUrl) {
        CropImage.activity(imageUrl)
                .setAspectRatio(1, 1)
                .setAutoZoomEnabled(true)
                .setInitialCropWindowPaddingRatio(0)
                .setOutputCompressQuality(40)
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
            if (tmpURI != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
                tmpURI = imageUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                }
            } else {
                // no permissions required or already granted, can start crop image activity
                openImageCropper(imageUri);
            }
            tmpURI = imageUri;
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                if (mImageUri != null) {
                    retake.setVisibility(View.VISIBLE);
                    crop.setVisibility(View.VISIBLE);
                    select.setVisibility(View.GONE);
                    TextView textView = findViewById(R.id.textView);
                    textView.setText("Retake Image");
                    findViewById(R.id.textView2).setVisibility(View.VISIBLE);
                    Glide.with(getApplicationContext())
                            .load(mImageUri)
                            .into(imageView);
                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(FileUpload.this, TagNCaption.class);
                            intent.putExtra("uri", mImageUri.toString());
                            startActivityForResult(intent, UPLOAD_POST); // TODO Start activity for result and when result is obtained execute finish() if uploading post...
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                        }
                    });
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == UPLOAD_POST && resultCode == RESULT_OK) {
            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
            else {
                Log.d(TAG, "onClick: Interstitial was not loaded");
            }
            finish();
        }
    }
}