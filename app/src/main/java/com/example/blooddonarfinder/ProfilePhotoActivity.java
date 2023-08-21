package com.example.blooddonarfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.blooddonarfinder.other.CircleTransform;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ProfilePhotoActivity extends AppCompatActivity {

    private static final int galleryPicker = 1;
    private BottomSheetDialog bottomSheetDialog;
    private ProgressDialog progressDialog;
    private int IMAGE_GALLERY_REQUEST = 1;
    private int CAMERA_REQUEST=11;
    private Uri imageUri;
    TouchImageView pPic;
    ImageView pPics;
    private Uri mCropImageUri;
    ParseFile file;
    ImageView mProfilePic;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);
        setTitle("Profile Photo");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);

        byte[] bytes = getIntent().getByteArrayExtra("bitmapbytes");
        if(bytes!=null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            TouchImageView imageView = (TouchImageView) findViewById(R.id.pPic);
            imageView.setImageBitmap(bmp);
        }else{
            TouchImageView imageView = (TouchImageView) findViewById(R.id.pPic);
            imageView.setImageResource(R.drawable.profile_pic_change);
        }

        //getImage();
        // for ScreenShot protection
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.option_editPic) {

           // showBottomSheetPickPhoto();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .setMultiTouchEnabled(true)
                    .start(this);

            /*Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent,galleryPicker);

            Toast.makeText(this, "Pic Selected", Toast.LENGTH_SHORT).show();
*/
        }

        if (item.getItemId() == R.id.option_deletePic) {

            new AlertDialog.Builder(this).setTitle("Are you sure?")
                    .setMessage("Remove profile photo?")
                    .setPositiveButton("CONFIRM",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    // Perform Action & Dismiss dialog

                                    deleteObject();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();

        }


        return super.onOptionsItemSelected(item);
    }

    private void refreshIntent() {
        Intent intent = new Intent(ProfilePhotoActivity.this,ProfilePhotoActivity.class);
        startActivity(intent);
        finish();
    }

    public void deleteObject() {
        // TODO: modify me!
        final boolean deleteAttributesOnly = true;

        final ParseQuery<ParseObject> query2 = ParseQuery.getQuery("UserDetails");
        query2.whereEqualTo("MobileNumber",ParseUser.getCurrentUser().getUsername());
        query2.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    if(objects.size()>0){
                        for(ParseObject object:objects){
                            // Retrieve the object by id
                            query2.getInBackground(object.getObjectId(), new GetCallback<ParseObject>() {
                                public void done(ParseObject entity, ParseException e) {
                                    if (e == null) {
                                        if (deleteAttributesOnly) {
                                            // If you want to undefine a specific field, do this:
                                            entity.remove("image");
                                            // Then save the changes
                                            entity.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    Toast.makeText(ProfilePhotoActivity.this, "Profile photo remove Successfully!", Toast.LENGTH_SHORT).show();
                                                    refreshIntent();
                                                }
                                           });
                                        } else {
                                            // Otherwise, you can delete the entire ParseObject from the database
                                            entity.deleteInBackground();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

    }

/*
    private void showBottomSheetPickPhoto() {

        final View view = getLayoutInflater().inflate(R.layout.bottom_sheet_pick, null);

        ((View) view.findViewById(R.id.ln_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);

                }else {
                    Toast.makeText(ProfilePhotoActivity.this, "Camera Open", Toast.LENGTH_SHORT).show();
                    takePicture();
                }
                bottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.ln_gallery)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_GALLERY_REQUEST);

                } else {
                    // getPhoto();
                    openGallery();
                    Toast.makeText(ProfilePhotoActivity.this, "Gallery Open", Toast.LENGTH_SHORT).show();
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog = new BottomSheetDialog(this);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                bottomSheetDialog = null;
            }
        });
        bottomSheetDialog.show();
    }

    private void takePicture() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(galleryIntent,IMAGE_GALLERY_REQUEST);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select image"), IMAGE_GALLERY_REQUEST);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if(requestCode==IMAGE_GALLERY_REQUEST) {

            if (resultCode == RESULT_OK && data != null) {
                Uri imageUri = CropImage.getPickImageResultUri(this, data);

                // For API >= 23 we need to check specifically that we have permissions to read external storage.
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, IMAGE_GALLERY_REQUEST);
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    CropImage.activity(imageUri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .setMultiTouchEnabled(true)
                            .start(this);
                }
            }
        }else {
                if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {

                    Uri uri = CropImage.getCaptureImageOutputUri(this);

            }
        }*/
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uploadToParse(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

    }




    private void uploadToParse(Uri uri) {
        if (uri != null) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            progressDialog.setCancelable(false);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                final ParseFile file = new ParseFile("image.png", byteArray);
                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserDetails");
                query1.whereEqualTo("MobileNumber", ParseUser.getCurrentUser().getUsername());

                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if(objects.size()>0) {
                                objects.get(0).put("image",file);
                                objects.get(0).saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        progressDialog.dismiss();
                                        getImage();

                                    }
                                });
                            }
                        } else {
                            Toast.makeText(ProfilePhotoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
               /* final ParseObject object = new ParseObject("ProfileImage");
                object.put("image", file);
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            progressDialog.dismiss();
                            Toast.makeText(ProfilePhotoActivity.this, "Profile Uploading Successfully.", Toast.LENGTH_SHORT).show();
                            //queryParseProfileImages(object);
                            getImage();
                        } else {
                            Toast.makeText(ProfilePhotoActivity.this, "Profile could not be uploading-Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/


                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                CropImage.activity(mCropImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .setMultiTouchEnabled(true)
                        .start(this);

            }

    }

    //Method containing ParseQuery to download/pull back the image that was uploaded to Parse
    //Inside the Image View
   /* private void queryParseProfileImages(final ParseObject imageUploadPassed) {

        ParseFile userImageRetrievedObj = (ParseFile) imageUploadPassed.get("image");
        userImageRetrievedObj.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {


                    final String imgUrl = imageUploadPassed.getParseFile("image").getUrl();


                    mProfilePic = (ImageView) findViewById(R.id.pPic);
                    Picasso.with(ProfilePhotoActivity.this).load(imgUrl).into(mProfilePic);

                    imageUploadPassed.pinInBackground();


                } else {
                    // something went wrong
                }
            }
        });
    }*/

    public void getImage(){
        // fetched image

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery("UserDetails");
        query1.whereEqualTo("MobileNumber", ParseUser.getCurrentUser().getUsername());


        query1.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {

                   // final String imgUrl = objects.get(0).getParseFile("image").getUrl();


                   // mProfilePic = (ImageView) findViewById(R.id.pPic);
                   // Picasso.with(ProfilePhotoActivity.this).load(imgUrl).into(mProfilePic);
                            if(objects.size()>0){
                                for(ParseObject object:objects){
                                    ParseFile file = (ParseFile) object.get("image");
                                    if(file!=null) {
                                        file.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] data, ParseException e) {
                                                if (e == null && data != null) {
                                                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                                    TouchImageView imageView = (TouchImageView) findViewById(R.id.pPic);
                                                    imageView.setImageBitmap(bitmap);
                                                }
                                            }
                                        });
                                    }else {
                                        TouchImageView imageView = (TouchImageView) findViewById(R.id.pPic);
                                        imageView.setImageResource(R.drawable.profile_pic_change);
                                    }
                                }
                            }
                } else {
                    Toast.makeText(ProfilePhotoActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}