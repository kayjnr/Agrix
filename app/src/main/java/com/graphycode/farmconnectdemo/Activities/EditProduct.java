package com.graphycode.farmconnectdemo.Activities;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.graphycode.farmconnectdemo.Fragments.LocationFragment;
import com.graphycode.farmconnectdemo.Models.Products;
import com.graphycode.farmconnectdemo.R;

import java.io.File;
import java.io.IOException;

public class EditProduct extends AppCompatActivity {

    EditText prodNameField, priceField, quantityField, farmNameField, descField;
    ImageView imgSelectCam, imgSelectGal, imgSelectMic, imgSelectAud, imgSelectVidcam, imgSelectVid;
    Button upload, locationBtn;
    Spinner units, catSpinner;
    TextView imgName, more, audName, vidName;
    View view;
    LinearLayout vidLayout, audLayout;

    String mCategory, mUnit, s;
    File f;
    boolean expand = true;
    boolean fold, longRelease = false;

    public static String mLocation;


    Uri imageUri, audUri, vidUri, audDownloadUri, vidDownloadUri, downloadUri = null;
    ProgressDialog mProgress;


    StorageReference mStorage;
    StorageReference audStorage;
    StorageReference vidStorage;
    DatabaseReference mEdit;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        convertToJavaCode();

        mStorage = FirebaseStorage.getInstance().getReference();
        audStorage = FirebaseStorage.getInstance().getReference();
        vidStorage = FirebaseStorage.getInstance().getReference();
        mEdit = FirebaseDatabase.getInstance().getReference().child("Products");

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        mFileName = getExternalCacheDir().getAbsolutePath();
        mFileName += "/recorded_audio.mp3";

        convertToJavaCode();

        setData();

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
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(cameraIntent, CAMERA_REQUEST);
                f = new File(getExternalCacheDir(),
                        String.valueOf(System.currentTimeMillis()) + ".jpg");
                imageUri = Uri.fromFile(f);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        /////////////// Adding Image Files End Here /////////////////////////////////////////

        //////////////////// Adding Audio Files starts here ////////////////////////////

        ////////////////// microphone recording  click Listener ///////////////
        imgSelectMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EditProduct.this,
                        "Hold to record..", Toast.LENGTH_SHORT).show();
            }
        });

        imgSelectMic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longRelease = true;
                startRecording();
                Toast.makeText(EditProduct.this,
                        "Recording started..", Toast.LENGTH_SHORT).show();
                audName.setText("Recording....");
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
                        Toast.makeText(EditProduct.this,
                                "Recording stopped.."+audUri, Toast.LENGTH_SHORT).show();
                        Toast.makeText(EditProduct.this,
                                "Audio Added Successfully", Toast.LENGTH_LONG).show();
                        audName.setText("Stopped.....");
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
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
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
                upDate();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            File file = new File((imageUri.getPath()));
            String ap = file.getAbsolutePath();
            String mimeType = getContentResolver().getType(imageUri);
            //imgName.setText(ap +" "+mimeType);
            Toast.makeText(EditProduct.this,
                    "Image Added Successfully", Toast.LENGTH_LONG).show();
        }else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
            Toast.makeText(EditProduct.this,
                    "Image Added Successfully", Toast.LENGTH_LONG).show();
            //imgName.setText(f.getName());

        }else if (requestCode == AUDIO_REQUEST && resultCode == RESULT_OK){
            audUri = data.getData();
            //File file = new File((audUri.getPath()));
            //String ap = file.getAbsolutePath();
            audName.setText("Add Audio");
            Toast.makeText(EditProduct.this, "Audio Added Successfully"+audUri, Toast.LENGTH_LONG).show();
        }else  if (requestCode == GALLERY_VID_REQUEST && resultCode == RESULT_OK){
            vidUri = data.getData();
            //File file = new File((vidUri.getPath()));
            //String ap = file.getName();
            //vidName.setText(ap);
            Toast.makeText(EditProduct.this, "Video Added Successfully"+vidUri,Toast.LENGTH_LONG).show();
        }else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK){
            vidUri = data.getData();
            //File file = new File((vidUri.getPath()));
            //String ap = file.getName();
            //vidName.setText(ap);
            Toast.makeText(EditProduct.this, "Video Added Successfully"+vidUri,Toast.LENGTH_LONG).show();
        }
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

    private void startRecording() {
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

    private void setData(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle != null) {
            s = bundle.getString("key");

            DatabaseReference childRef = mEdit.child(s);
            childRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Products products = dataSnapshot.getValue(Products.class);
                    prodNameField.setText(products.getProduct_Name());
                    priceField.setText(selectedPrice(products.getPrice()));
                    quantityField.setText(selectedQuantity(products.getQuantity()));
                    farmNameField.setText(products.getFarm_Name());
                    descField.setText(products.getDescription());
                    mLocation = products.getLocation();
                    units.setSelection(selectedUnit(products.getQuantity()));
                    catSpinner.setSelection(selectedCat(products.getCategory()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void upDate(){
        final String prodName = prodNameField.getText().toString().trim();
        final String prodPrice = "GHC "+priceField.getText().toString().trim();
        final String prodQuantity = quantityField.getText().toString() +" "+ mUnit.trim();
        final String farm = farmNameField.getText().toString().trim();
        final String prodDesc = descField.getText().toString().trim();
        final String cat = mCategory.trim();
        final String loc = mLocation.trim();

        if (audUri != null){
            StorageReference filePath = audStorage.child("Product_Audio")
                    .child(/*System.currentTimeMillis()+*/"/new_audio.mp3");
            filePath.putFile(audUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    audDownloadUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(EditProduct.this, "audHurray", Toast.LENGTH_LONG).show();

                }
            });
        }

        if (vidUri != null){
            StorageReference filepath = vidStorage.child("Product_video")
                    .child("new video.mp4");
            filepath.putFile(vidUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    vidDownloadUri = taskSnapshot.getDownloadUrl();
                    Toast.makeText(EditProduct.this, "vid Hurray", Toast.LENGTH_LONG).show();
                }
            });
        }

        if (imageUri != null){
            StorageReference filePath = mStorage.child("Products_Images")
                    .child(imageUri.getLastPathSegment());
            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    downloadUri = taskSnapshot.getDownloadUrl();
                }
            });
        }

        if (mUnit != "-- Select Type --"
                && mCategory != "-- Select Category --" && mLocation != "-- Select Location --"){
            mProgress.setMessage("Updating...");
            mProgress.show();

                    //String uid = mAuth.getCurrentUser().getUid();

                    DatabaseReference newProduct = mEdit.child(s);

                    //newProduct.child("UserID").setValue(uid);
                    newProduct.child("Product_Name").setValue(prodName);
                    newProduct.child("Price").setValue(prodPrice);
                    newProduct.child("Category").setValue(cat);
                    newProduct.child("Quantity").setValue(prodQuantity);
                    newProduct.child("Farm_Name").setValue(farm);
                    newProduct.child("Description").setValue(prodDesc);
                    newProduct.child("Location").setValue(loc);
                    if (imageUri != null){
                        newProduct.child("Image").setValue(downloadUri.toString());
                    }
                    if (audDownloadUri != null){
                        newProduct.child("Audio").setValue(audDownloadUri.toString());
                    }
                    if (vidDownloadUri != null){
                        newProduct.child("Video").setValue(vidDownloadUri.toString());
                    }



                    mProgress.dismiss();
                    Toast.makeText(EditProduct.this, "Update Successful", Toast.LENGTH_LONG).show();


                    startActivity(new Intent(EditProduct.this, MyAdsActivity.class));

        }else{
            Toast.makeText(this, "All fields are required", Toast.LENGTH_LONG).show();
        }
    }

    private int selectedUnit(String s){
        int index = 0;
        String[] u = s.split("\\s+");
        if (u[1].equalsIgnoreCase("Bags")){
            index = 1;
        }else if (u[1].equalsIgnoreCase("Sacks")){
            index = 2;
        }else if (u[1].equalsIgnoreCase("Boxes")){
            index = 3;
        }
     return index;
    }

    private int selectedCat(String c) {
        int index = 0;
        if (c.equalsIgnoreCase("Tuber")) {
            index = 1;
        } else if (c.equalsIgnoreCase("Fruit")) {
            index = 2;
        } else if (c.equalsIgnoreCase("Vegetable")) {
            index = 3;
        } else if (c.equalsIgnoreCase("Grain")) {
            index = 4;
        } else if (c.equalsIgnoreCase("Cereal")) {
            index = 5;
        }
        return index;
    }

    private String selectedPrice(String p){
        String[] price = p.split("\\s+");
        return price[1];
    }

    private String selectedQuantity(String q){
        String[] quantity = q.split("\\s+");
        return quantity[0];
     }
}
