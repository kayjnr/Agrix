package com.graphycode.farmconnectdemo.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.graphycode.farmconnectdemo.Fragments.LocationFragment;
import com.graphycode.farmconnectdemo.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddProduct extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 404;
    private static final int MY_PERMISSIONS_REQUEST_RECORD = 505;
    EditText prodNameField, priceField, quantityField, farmNameField, descField;
    ImageView imgSelectCam, imgSelectGal, imgSelectMic, imgSelectAud, imgSelectVidcam, imgSelectVid;
    Button upload;
    public static Button locationBtn;
    Spinner units, catSpinner, locSpinner;
    TextView imgName, more, audName, vidName;
    View view;
    LinearLayout vidLayout, audLayout;
    String mCategory,  mUnit;
    File f;
    boolean expand = true;
    boolean fold, longRelease = false;

    public static String mLocation;


    Uri imageUri, audUri, vidUri, audDownloadUri, vidDownloadUri = null;
    ProgressDialog mProgress;


    StorageReference mStorage;
    StorageReference audStorage;
    StorageReference vidStorage;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    private static final int GALLERY_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int AUDIO_REQUEST = 3;
    private static final int GALLERY_VID_REQUEST = 4;
    static final int REQUEST_VIDEO_CAPTURE = 5;
    private static String mFileName = null;

    private MediaRecorder mRecorder = null;



    String[]quantityUnit = {"-- Select Type --", "Bags", "Sacks", "Boxes"};
    String[]spinArray = {"-- Select Category --","Tuber", "Fruit", "Vegetable", "Grain", "Cereal"};
    String[]locArray = {"-- Select Location --"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mStorage = FirebaseStorage.getInstance().getReference();
        audStorage = FirebaseStorage.getInstance().getReference();
        vidStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Products");
        //mDatabase.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/recorded_audio.mp3";


        convertToJavaCode();

        locationBtn.setText("-- Select Location --");

        populateSpinner(spinArray, catSpinner, "category");
        populateSpinner(quantityUnit, units, "unit");
        //populateSpinner(locArray,locSpinner, "location");

        final FragmentManager fragmentManager = getFragmentManager();
        final LocationFragment locationFragment = new LocationFragment();

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationFragment.show(fragmentManager, "Locations");
            }
        });


    /////////////// Adding Image Files start Here //////////////////////////////////
        imgSelectGal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");*/
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        imgSelectCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddProduct.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    RequestCameraPermission();

                }else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    /*
                    f = new File(getExternalCacheDir(),
                            String.valueOf(System.currentTimeMillis()) + ".jpg");
                    */


                    try {
                        f = File.createTempFile(
                                StampTime(),
                                ".jpg",
                                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imageUri = FileProvider.getUriForFile(AddProduct.this,
                            "com.graphycode.agrix.fileprovider", f); //Uri.fromFile(f);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    /////////////// Adding Image Files End Here /////////////////////////////////////////

    //////////////////// Adding Audio Files starts here ////////////////////////////

        ////////////////// microphone recording  click Listener ///////////////
        imgSelectMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AddProduct.this,
                        "Hold to record..", Toast.LENGTH_SHORT).show();
            }
        });

        imgSelectMic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (ContextCompat.checkSelfPermission(AddProduct.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    RequestRecordPermission();

                }else {
                    longRelease = true;
                    startRecording();
                    Toast.makeText(AddProduct.this,
                            "Recording started..", Toast.LENGTH_SHORT).show();
                    audName.setText("Recording....");
                }

                return true;
            }
        });

        imgSelectMic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (longRelease){
                    if (event.getAction() == event.ACTION_UP){
                        stopRecording();
                        audUri = Uri.fromFile(new File(mFileName));
                        /*Toast.makeText(AddProduct.this,
                                "Recording stopped.."+audUri, Toast.LENGTH_SHORT).show();*/
                        /*Toast.makeText(AddProduct.this,
                                "Audio Added Successfully", Toast.LENGTH_LONG).show();*/
                        audName.setText("Stopped.....");

                        showConfirmAudioPopup(audUri);
/*                        if (audUri != null){
                            mProgress.setMessage("Processing..");
                            mProgress.show();
                            StorageReference filePath = audStorage.child("Product_Audio")
                                    .child("new_audio.mp3");
                            filePath.putFile(audUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    audDownloadUri = taskSnapshot.getDownloadUrl();
                                    mProgress.dismiss();
                                    Toast.makeText(AddProduct.this, "Audio Added Successfully", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }*/
                        longRelease = false;
                        audName.setText("Add Audio");
                    }
                }

                return false;
            }
        });
        ///////////// microphone Listener ends ///////////////////////

        imgSelectAud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audioFile = new Intent(Intent.ACTION_GET_CONTENT);
                audioFile.setType("audio/*");
                startActivityForResult(audioFile, AUDIO_REQUEST);
            }
        });
    ////////////////// Adding Audio Files End Here ////////////////////////////////

    /////////////////// Adding Video Files Start Here ////////////////////////////
        imgSelectVidcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(AddProduct.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED){
                    RequestCameraPermission();

                }else {
                    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    }
                }
            }
        });


        imgSelectVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vid = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                /*Intent vid = new Intent(Intent.ACTION_GET_CONTENT);
                vid.setType("video/*");*/
                startActivityForResult(vid, GALLERY_VID_REQUEST);
            }
        });
    //////////////////// Adding Video Files Ends Here ///////////////////////////

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadProducts();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (expand){
                   audLayout.setVisibility(View.VISIBLE);
                   vidLayout.setVisibility(View.VISIBLE);
                   view.setVisibility(View.VISIBLE);
                   more.setText("less..");
                   expand = false;
                   fold = true;
               }else if (fold){
                   audLayout.setVisibility(View.GONE);
                   vidLayout.setVisibility(View.GONE);
                   view.setVisibility(View.GONE);
                   more.setText("more..");
                   expand = true;
                   fold = false;
               }
            }
        });
    }

    private void uploadProducts() {
        if (mLocation != null){
            final String prodName = prodNameField.getText().toString().trim();
            final String prodPrice = "GHC "+priceField.getText().toString().trim();
            final String prodQuantity = quantityField.getText().toString() +" "+ mUnit.trim();
            final String farm = farmNameField.getText().toString().trim();
            final String prodDesc = descField.getText().toString().trim();
            final String cat = mCategory.trim();
            final String loc = mLocation.trim();

/*            if (audUri != null){
                StorageReference filePath = audStorage.child("Product_Audio")
                        .child("new_audio.mp3");
                filePath.putFile(audUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        audDownloadUri = taskSnapshot.getDownloadUrl();
                        Toast.makeText(AddProduct.this, "audHurray", Toast.LENGTH_LONG).show();

                    }
                });
            }*/

/*            if (vidUri != null){
                StorageReference filepath = vidStorage.child("Product_video")
                        .child("new video.mp4");
                filepath.putFile(vidUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        vidDownloadUri = taskSnapshot.getDownloadUrl();
                        Toast.makeText(AddProduct.this, "vid Hurray", Toast.LENGTH_LONG).show();
                    }
                });
            }*/

            if (imageUri != null && mUnit != "-- Select Type --"
                    && mCategory != "-- Select Category --" && mLocation != "-- Select Location --"){
                mProgress.setMessage("Uploading...");
                mProgress.show();
                StorageReference filePath = mStorage.child("Products_Images")
                        .child(imageUri.getLastPathSegment());
                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();

                        String uid = mAuth.getCurrentUser().getUid();

                        DatabaseReference newProduct = mDatabase.push();

                        newProduct.child("UserID").setValue(uid);
                        newProduct.child("Product_Name").setValue(prodName);
                        newProduct.child("Price").setValue(prodPrice);
                        newProduct.child("Category").setValue(cat);
                        newProduct.child("Quantity").setValue(prodQuantity);
                        newProduct.child("Farm_Name").setValue(farm);
                        newProduct.child("Description").setValue(prodDesc);
                        newProduct.child("DateCreated").setValue(StampTime());
                        newProduct.child("Location").setValue(loc);
                        newProduct.child("Image").setValue(downloadUri.toString());
                        if (audDownloadUri != null){
                            newProduct.child("Audio").setValue(audDownloadUri.toString());
                        }
                        if (vidDownloadUri != null){
                            newProduct.child("Video").setValue(vidDownloadUri.toString());
                        }



                        mProgress.dismiss();
                        Toast.makeText(AddProduct.this, "Upload Successful", Toast.LENGTH_LONG).show();


                        startActivity(new Intent(AddProduct.this, MyAdsActivity.class));
                    }
                });
            }else{
                Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            File file = new File((imageUri.getPath()));
            String ap = file.getAbsolutePath();
            String mimeType = getContentResolver().getType(imageUri);
            //imgName.setText(ap +" "+mimeType);
            showConfirmImagePopup(imageUri);
/*            Toast.makeText(AddProduct.this,
                    "Image Added Successfully", Toast.LENGTH_LONG).show();*/
        }else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            showConfirmImagePopup(imageUri);
            /*Toast.makeText(AddProduct.this,
                    "Image Added Successfully", Toast.LENGTH_LONG).show();*/
            //imgName.setText(f.getName());

        }else if (requestCode == AUDIO_REQUEST && resultCode == RESULT_OK){
            audUri = data.getData();
            showConfirmAudioPopup(audUri);
/*            if (audUri != null){
                mProgress.setMessage("Processing..");
                mProgress.show();
                StorageReference filePath = audStorage.child("Product_Audio")
                        .child("new_audio.mp3");
                filePath.putFile(audUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        audDownloadUri = taskSnapshot.getDownloadUrl();
                        mProgress.dismiss();
                        Toast.makeText(AddProduct.this, "Audio Added Successfully", Toast.LENGTH_SHORT).show();

                    }
                });
            }*/
            //File file = new File((audUri.getPath()));
            //String ap = file.getAbsolutePath();
            audName.setText("Add Audio");
            /*Toast.makeText(AddProduct.this, "Audio Added Successfully"+audUri, Toast.LENGTH_LONG).show();*/
        }else  if (requestCode == GALLERY_VID_REQUEST && resultCode == RESULT_OK){
            vidUri = data.getData();
            showConfirmVideoPopup(vidUri);
/*            if (vidUri != null){
                mProgress.setMessage("Processing..");
                mProgress.show();
                StorageReference filepath = vidStorage.child("Product_video")
                        .child("new video.mp4");
                filepath.putFile(vidUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        vidDownloadUri = taskSnapshot.getDownloadUrl();
                        mProgress.dismiss();
                        Toast.makeText(AddProduct.this, "Video Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }*/
            //File file = new File((vidUri.getPath()));
            //String ap = file.getName();
            //vidName.setText(ap);
            /*Toast.makeText(AddProduct.this, "Video Added Successfully"+vidUri,Toast.LENGTH_LONG).show();*/
        }else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            vidUri = data.getData();
            showConfirmVideoPopup(vidUri);
/*            if (vidUri != null){
                mProgress.setMessage("Processing..");
                mProgress.show();
                StorageReference filepath = vidStorage.child("Product_video")
                        .child("new video.mp4");
                filepath.putFile(vidUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        vidDownloadUri = taskSnapshot.getDownloadUrl();
                        mProgress.dismiss();
                        Toast.makeText(AddProduct.this, "Video Added Successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }*/
            //File file = new File((vidUri.getPath()));
            //String ap = file.getName();
            //vidName.setText(ap);
            /*Toast.makeText(AddProduct.this, "Video Added Successfully"+vidUri,Toast.LENGTH_LONG).show();*/
        }
    }

    public void convertToJavaCode(){
        prodNameField = findViewById(R.id.prod_nameField);
        priceField = findViewById(R.id.priceField);
        quantityField = findViewById(R.id.quantityField);
        farmNameField = findViewById(R.id.farm_nameField);
        descField = findViewById(R.id.desField);
        imgSelectCam = findViewById(R.id.camera);
        imgSelectGal = findViewById(R.id.attachment);
        upload = findViewById(R.id.add_btn);
        units = findViewById(R.id.measure);
        catSpinner = findViewById(R.id.spinnerCat);
        //locSpinner = findViewById(R.id.locSpinner);
        imgName = findViewById(R.id.txt_img_name);
        audName = findViewById(R.id.txt_aud_name);
        vidName = findViewById(R.id.txt_vid_name);
        more = findViewById(R.id.txt_more);
        vidLayout = findViewById(R.id.video_layout);
        audLayout = findViewById(R.id.audio_layout);
        imgSelectMic = findViewById(R.id.mic);
        imgSelectAud = findViewById(R.id.aud_attachment);
        imgSelectVidcam = findViewById(R.id.video);
        imgSelectVid = findViewById(R.id.attachment_vid);
        view = findViewById(R.id.view);
        locationBtn = findViewById(R.id.location_btn);
    }

    public void populateSpinner(String[] s, final Spinner spinner, final String condition){
        ArrayAdapter<String> stringArrayAdapter = new
                ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item,s);
        spinner.setAdapter(stringArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (condition.equalsIgnoreCase("category")){
                    mCategory = spinner.getSelectedItem().toString();
                }else if (condition.equalsIgnoreCase("unit")){
                    mUnit = spinner.getSelectedItem().toString();
                }/*else if (condition.equalsIgnoreCase("location")){
                    mLocation = spinner.getSelectedItem().toString();

                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    /// recording things ////
    private void startRecording() {
        /*mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/recorded_audio.mp3";*/
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void RequestCameraPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddProduct.this,
                Manifest.permission.CAMERA)) {

            // Show an explanation to the user asynchronously -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Snackbar.make(
                    findViewById(R.id.mRootLayout),
                    "This is to let the app take photos and record videos.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(AddProduct.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    })
                    .show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        }
    }

    private void RequestRecordPermission() {
        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale(AddProduct.this,
                Manifest.permission.RECORD_AUDIO)) {

            // Show an explanation to the user asynchronously -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

            Snackbar.make(
                    findViewById(R.id.mRootLayout),
                    "This is to let the app take photos and record videos.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("Grant Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(AddProduct.this,
                                    new String[]{Manifest.permission.RECORD_AUDIO},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    })
                    .show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_RECORD);

        }
    }

    ///////  Date Format ////////
    private String StampTime() {
        return new SimpleDateFormat("EEE, MMM d, \"yy", Locale.UK).format(new Date());
    }

    private void showConfirmImagePopup(final Uri uri) {
        AlertDialog.Builder imagePopup = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dialog_image_preview, null);

        ImageView imageView = view.findViewById(R.id.imagePreview_image);
        imageView.setImageURI(uri);

        imagePopup.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Toast.makeText(AddProduct.this,
                        "Image saved Successfully", Toast.LENGTH_LONG).show();
            }
        });

        imagePopup.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                imageUri = null;

                Toast.makeText(AddProduct.this,"Image Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        imagePopup.setTitle("Image Preview");
        imagePopup.setView(view);
        imagePopup.show();
    }


    private void showConfirmAudioPopup(final Uri uri) {
        AlertDialog.Builder audioPopup = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dialog_audio_preview, null);

        final Button play = view.findViewById(R.id.audioPreview_btnPlay);
        Button stop = view.findViewById(R.id.audioPreview_btnStop);

        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()){
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), uri);
                        mediaPlayer.prepare();

                    }catch (IOException e){
                       e.printStackTrace();
                    }
                    mediaPlayer.start();
                    play.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                }else {
                    mediaPlayer.pause();
                    play.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.reset();

                    play.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);

                }
            }
        });

        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {


                // Stop and reset mediaPlayer
                mediaPlayer.stop();
                mediaPlayer.reset();


                // Set mediaPlayer's state to stopped and and set the
                // inflated view's play button's text to 'Play'.
                //isPaused = false;
                play.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });

        audioPopup.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (audUri != null){
                    mProgress.setMessage("Saving..");
                    mProgress.show();
                    StorageReference filePath = audStorage.child("Product_Audio")
                            .child("new_audio.mp3");
                    filePath.putFile(audUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            audDownloadUri = taskSnapshot.getDownloadUrl();
                            mProgress.dismiss();
                            Toast.makeText(AddProduct.this, "Audio Saved Successfully", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });

        audioPopup.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                audUri = null;

                Toast.makeText(AddProduct.this,"Audio Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        audioPopup.setTitle("Audio File");
        audioPopup.setView(view);
        audioPopup.show();
    }


    private void showConfirmVideoPopup(final Uri mCurrentVideoUri) {
        AlertDialog.Builder videoPopup = new AlertDialog.Builder(this);

        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.dialog_video_preview, null);

        final VideoView videoView = view.findViewById(R.id.videoPreview_video);
        final FrameLayout mediaControllerWrapper =
                view.findViewById(R.id.videoPreview_mediaControllerWrapper);

        videoView.setVideoURI(mCurrentVideoUri);

        videoView.requestFocus();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            MediaController mediacontroller;

            public void onPrepared(MediaPlayer mediaPlayer) {

                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mediaPlayerInner,
                                                   int width, int height) {

                        // Add mediaController
                        mediacontroller = new MediaController(videoView.getContext());
                        videoView.setMediaController(mediacontroller);

                        // Anchor it to videoView
                        mediacontroller.setAnchorView(videoView);

                        // Remove mediaController from its parent layout
                        ((ViewGroup) mediacontroller.getParent()).removeView(mediacontroller);

                        // Add media controller to the FrameLayout below videoView
                        mediaControllerWrapper.addView(mediacontroller);
                        mediacontroller.setVisibility(View.VISIBLE);
                        mediacontroller.show(0);
                    }
                });
                videoView.start();
            }
        });

        videoPopup.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Display audio saved message
                if (vidUri != null){
                    mProgress.setMessage("Saving..");
                    mProgress.show();
                    StorageReference filepath = vidStorage.child("Product_video")
                            .child("new video.mp4");
                    filepath.putFile(vidUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            vidDownloadUri = taskSnapshot.getDownloadUrl();
                            mProgress.dismiss();
                            Toast.makeText(AddProduct.this, "Video Added Successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        videoPopup.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(AddProduct.this, "Video Deleted", Toast.LENGTH_SHORT).show();

            }
        });

        videoPopup.setTitle("Video Preview");
        videoPopup.setCancelable(false);
        videoPopup.setView(view);
        videoPopup.show();
    }

}
