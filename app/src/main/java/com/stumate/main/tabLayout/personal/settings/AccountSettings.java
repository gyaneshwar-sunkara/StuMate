package com.stumate.main.tabLayout.personal.settings;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.stumate.main.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettings extends AppCompatActivity {
    private String uid;
    private String displayName;
    private String imageUrl;
    private String headerUrl;
    private String bio;
    private String email;
    private String phone;
    private String collegeShortName;
    private String collegeName;
    private String className;

    private Uri mImageUri;
    private Uri tmpURI;

    private String uploadImageUri = "";
    private String uploadHeaderUri = "";
    Map<String, Object> updates = new HashMap<>();
    private Map<String, Object> publicUser = new HashMap<>();
    private boolean nameFlag = true;
    private boolean bioFlag = true;
    private boolean profileFlag = true;
    private boolean headerFlag = true;
    private UserProfileChangeRequest.Builder profileChangeRequest = new UserProfileChangeRequest.Builder();
    private static final String TAG = "AccountSettings";

    private int flag = 0;

    private ImageView close;
    private ImageView done;

    private CircleImageView profilePhoto;
    private ImageView header;

    private TextView editPhoto;

    private EditText nameValue;
    private EditText bioValue;

    private TextView collegeValue;
    private TextView classValue;
    private TextView emailValue;
    private TextView phoneValue;
    private TextView genderValue;
    private TextView dobValue;

    private ImageView infoButton;
    private ImageView infoButton2;
    private ImageView emailEdit;
    private ImageView phoneEdit;
    private ImageView genderEdit;
    private ImageView dobEdit;

    private SharedPreferences prefs;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private StorageTask mUploadTask2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        prefs = getSharedPreferences("userDetails", MODE_PRIVATE);
        displayName = prefs.getString("displayName", "");
        imageUrl = prefs.getString("imageUrl", "");
        headerUrl = prefs.getString("headerUrl", "");
        bio = prefs.getString("bio", "");
        email = prefs.getString("email", "");
        phone = prefs.getString("phone", "");
        collegeName = prefs.getString("collegeName", "");
        collegeShortName = prefs.getString("collegeShortName", "");
        className = prefs.getString("class", "");
        uid = prefs.getString("uid", "");

        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Add a confirmation dialog to the user...
                if (!nameValue.getText().toString().equals(displayName) || !bioValue.getText().toString().equals(bio) || !imageUrl.equals(prefs.getString("imageUrl", "xxx")) || !headerUrl.equals(prefs.getString("headerUrl", "xxx"))) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettings.this);

                    builder.setMessage("Exiting without saving changes?")
                            .setTitle("Unsaved Changes")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();

                } else {
                    finish();
                }
            }
        });

        profilePhoto = findViewById(R.id.circleImageView);
        if (imageUrl != null && !imageUrl.equals("")) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.user)
                    .into(profilePhoto);
        }

        header = findViewById(R.id.header);
        if (headerUrl != null && !headerUrl.equals("")) {
            Glide.with(this)
                    .load(headerUrl)
                    .into(header);
        }
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettings.this);
                String[] values = {"remove header", "change header"};
                builder.setTitle(null)
                        .setItems(values, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                if (which == 0) {
                                    headerUrl = "";
                                    header.setImageResource(R.color.colorAccent);
                                } else if (which == 1) {
                                    flag = 1;
                                    openFileChooser();
                                }
                            }
                        });
                if (headerUrl != null && !headerUrl.equals("")) {
                    builder.show();
                } else {
                    flag = 1;
                    openFileChooser();
                }
            }
        });

        editPhoto = findViewById(R.id.textView4);
        editPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: prompt a dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettings.this);
                String[] values = {"remove photo", "change photo"};
                builder.setTitle(null)
                        .setItems(values, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                if (which == 0) {
                                    imageUrl = "";
                                    profilePhoto.setImageResource(R.drawable.user);
                                } else if (which == 1) {
                                    flag = 0;
                                    openFileChooser();
                                }
                            }
                        });

                if (imageUrl != null && !imageUrl.equals("")) {
                    builder.show();
                } else {
                    flag = 0;
                    openFileChooser();
                }
            }
        });

        nameValue = findViewById(R.id.enterName);
        nameValue.setText(displayName);

        bioValue = findViewById(R.id.enterBio);
        bioValue.setText(bio);

        collegeValue = findViewById(R.id.collegeValue);
        collegeValue.setText(collegeName);

        classValue = findViewById(R.id.classValue);
        classValue.setText(className);

        emailValue = findViewById(R.id.emailValue);
        if (email != null && !email.equals("")) {
            emailValue.setText(email);
        }

        phoneValue = findViewById(R.id.phoneValue);
        if (phone != null && !phone.equals("")) {
            phoneValue.setText(phone);
        }

        genderValue = findViewById(R.id.genderValue);
        dobValue = findViewById(R.id.dobValue);

        infoButton = findViewById(R.id.infoButton);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(infoButton, "Can't change institute", Snackbar.LENGTH_SHORT).show();
            }
        });

        infoButton2 = findViewById(R.id.infoButton2);
        infoButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(infoButton2, "Can't change class", Snackbar.LENGTH_SHORT).show();
            }
        });

        emailEdit = findViewById(R.id.emailEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        genderEdit = findViewById(R.id.genderEdit);
        dobEdit = findViewById(R.id.dobEdit);

        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                if (isConnected()) {
                    if (!nameValue.getText().toString().equals(displayName) || !bioValue.getText().toString().equals(bio) || !imageUrl.toString().equals(prefs.getString("imageUrl", "xxx")) || !headerUrl.toString().equals(prefs.getString("headerUrl", "xxx"))) {
                        SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                        if (!nameValue.getText().toString().equals(displayName)) {
                            if (validateName(nameValue.getText().toString())) {
                                editor.putString("displayName", nameValue.getText().toString());
                                publicUser.put("displayName", nameValue.getText().toString());
                                updates.put("displayName", nameValue.getText().toString());
                                profileChangeRequest.setDisplayName(nameValue.getText().toString());
                            } else {
                                nameFlag = false;
                            }
                        }
                        if (!bioValue.getText().toString().equals(bio)) {
                            if (validateBio(bioValue.getText().toString())) {
                                editor.putString("bio", bioValue.getText().toString());
                                updates.put("bio", bioValue.getText().toString());
                            } else {
                                bioFlag = false;
                            }
                        }
                        if (!imageUrl.equals(prefs.getString("imageUrl", "xxx"))) {
                            editor.putString("imageUrl", imageUrl);
                            profileFlag = false;
                            if (mUploadTask != null && mUploadTask.isInProgress()) {
                                Toast.makeText(AccountSettings.this, "Upload in progress, please wait...", Toast.LENGTH_SHORT).show();
                            } else {
                                if (isConnected()) {
                                    uploadProfilePicture();
                                } else {
                                    Snackbar.make(findViewById(R.id.profilePictureContainer), "No Network", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }
                        if (!headerUrl.equals(prefs.getString("headerUrl", "xxx"))) {
                            editor.putString("headerUrl", headerUrl);
                            headerFlag = false;
                            if (mUploadTask2 != null && mUploadTask2.isInProgress()) {
                                Toast.makeText(AccountSettings.this, "Upload in progress, please wait...", Toast.LENGTH_SHORT).show();
                            } else {
                                if (isConnected()) {
                                    uploadHeader();
                                } else {
                                    Snackbar.make(findViewById(R.id.profilePictureContainer), "No Network", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }
                        editor.apply();
                        saveChanges();
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(AccountSettings.this, "No network", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean validateName(String s) {
        if (s.isEmpty()) {
            nameValue.setError("Name can't be empty");
            return false;
        } else {
            if (s.length() >= 22) {
                nameValue.setError("Enter name under 22 characters");
                return false;
            }
            if (s.startsWith("#")) {
                nameValue.setError("Name can't start with #");
                return false;
            }
        }
        return true;
    }

    private boolean validateBio(String s) {
        if (s.isEmpty()) {
            bioValue.setError("You can skip this field");
            return false;
        } else if (s.length() > 200) {
            bioValue.setError("Less than 200 characters");
            return false;
        } else return true;
    }

    private void openFileChooser() {
        CropImage.startPickImageActivity(this);
    }

    private void openImageCropper(Uri imageUrl) {
        if (flag == 0) {
            CropImage.activity(imageUrl)
                    .setAspectRatio(1, 1)
                    .setAutoZoomEnabled(true)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setInitialCropWindowPaddingRatio(0)
                    .setOutputCompressQuality(20)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        } else if (flag == 1) {
            CropImage.activity(imageUrl)
                    .setAspectRatio(2, 1)
                    .setAutoZoomEnabled(true)
                    .setInitialCropWindowPaddingRatio(0)
                    .setOutputCompressQuality(40)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO: Add a confirmation dialog to the user...
        if (!nameValue.getText().toString().equals(displayName) || !bioValue.getText().toString().equals(bio) || !imageUrl.equals(prefs.getString("imageUrl", "xxx")) || !headerUrl.equals(prefs.getString("headerUrl", "xxx"))) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(AccountSettings.this);

            builder.setMessage("Exiting without saving changes?")
                    .setTitle("Unsaved Changes")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();

        } else {
            finish();
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
                    if (flag == 0) {
                        imageUrl = mImageUri.toString();
                        Glide.with(getApplicationContext())
                                .load(mImageUri)
                                .into(profilePhoto);
                    } else if (flag == 1) {
                        headerUrl = mImageUri.toString();
                        Glide.with(getApplicationContext())
                                .load(mImageUri)
                                .into(header);
                    }
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveChanges() {
        if (nameFlag && bioFlag && profileFlag && headerFlag) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (!nameValue.getText().toString().equals(displayName) || !imageUrl.equals(prefs.getString("imageUrl", "xxx"))) {
                FirebaseAuth.getInstance().getCurrentUser().updateProfile(profileChangeRequest.build());
                db.collection("institutes").document(collegeName).collection("public").document(uid)
                        .update(publicUser);
            }

            db.collection("users")
                    .document(uid)
                    .update(updates);


            findViewById(R.id.progressBar).setVisibility(View.GONE);
            finish();
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

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadProfilePicture() {
        if (imageUrl != null) {
            mStorageRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final StorageReference fileReference = mStorageRef.child("profilePicture"
                    + "." + getFileExtension(Uri.parse(imageUrl)));
            mUploadTask = fileReference.putFile(Uri.parse(imageUrl))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String DownloadUri = uri.toString();
                                            updates.put("imageUrl", DownloadUri);
                                            publicUser.put("imageUrl", DownloadUri);
                                            profileChangeRequest.setPhotoUri(Uri.parse(DownloadUri));
                                            SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                                            editor.putString("imageUrl", DownloadUri);
                                            Log.d(TAG, "onSuccess: profile picture updated " + DownloadUri);
                                            editor.apply();
                                            uploadImageUri = DownloadUri;
                                            profileFlag = true;
                                            saveChanges();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(done, "No uri found", Snackbar.LENGTH_LONG).show();
                                            uploadImageUri.notify();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(done, "Error occurred", Snackbar.LENGTH_LONG).show();
                            Log.e(TAG, "onFailure: profile picture update ", e);
                            uploadImageUri.notify();
                        }
                    });

        }
    }

    private void uploadHeader() {
        if (headerUrl != null) {
            mStorageRef = FirebaseStorage.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
            final StorageReference fileReference = mStorageRef.child("header"
                    + "." + getFileExtension(Uri.parse(headerUrl)));
            mUploadTask2 = fileReference.putFile(Uri.parse(headerUrl))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String DownloadUri = uri.toString();
                                            updates.put("headerUrl", DownloadUri);
                                            SharedPreferences.Editor editor = getSharedPreferences("userDetails", MODE_PRIVATE).edit();
                                            editor.putString("header", DownloadUri);
                                            Log.d(TAG, "onSuccess: header updated " + DownloadUri);
                                            editor.apply();
                                            uploadHeaderUri = DownloadUri;
                                            headerFlag = true;
                                            saveChanges();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Snackbar.make(done, "No uri found", Snackbar.LENGTH_LONG).show();
                                            uploadHeaderUri.notify();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(done, "Error occurred", Snackbar.LENGTH_LONG).show();
                            Log.e(TAG, "onFailure: profile picture update ", e);
                            uploadHeaderUri.notify();
                        }
                    });

        }
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
}
