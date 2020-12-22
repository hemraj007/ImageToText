package com.appcode.hems.ImageToText.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.appcode.hems.ImageToText.BuildConfig;
import com.appcode.hems.ImageToText.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity.class";
    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    public static String TITLE;
    public static int TOTAL_PAGES;
    public static String CONTENT;
    public static int CURRENT_PAGE;
    public static String DIRECTORY_PATH = "/Android/data/com.appcode.hems.ImageToText";
    public static ArrayList<String> FILE_NAMES = new ArrayList<>();
    private EditText editTitle, editPages;
    private Button buttonGenerate;
//    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        initActivity();

        buttonGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TITLE = editTitle.getText().toString();
                String pages = editPages.getText().toString();
                if (TITLE.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter the title of PDF",
                            Toast.LENGTH_SHORT).show();
                } else if (pages.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter the total pages in PDF",
                            Toast.LENGTH_SHORT).show();
                } else {
                    TOTAL_PAGES = Integer.parseInt(pages);
                    CURRENT_PAGE = 0;
                    CONTENT = "";
                    if (CURRENT_PAGE < TOTAL_PAGES) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Request Camera Permissions
                            requestCameraPermission();
                        } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Request External Storage Permissions
                            requestExternalStoragePermission();
                        } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                                Manifest.permission.WRITE_CONTACTS)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Request Contact Permissions
                            requestContactPermission();
                        } else {
                            startCamera();
                        }
                    }
                }
            }
        });
    }

    private void requestContactPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_CONTACTS}, 3);
    }

    private void requestExternalStoragePermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA}, 1);
    }

    private void initActivity() {
        // Initialize the view elements
        editTitle = (EditText) findViewById(R.id.editTitle);
        editPages = (EditText) findViewById(R.id.editPages);
        buttonGenerate = (Button) findViewById(R.id.buttonGenerate);
    }

    private void startCamera() {
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = getOutputMediaFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i(TAG, "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
//        long TIME_OUT = 100;
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startActivity(new Intent(MainActivity.this, CaptureActivity.class));
//            }
//        }, TIME_OUT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
//            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(photo);
//            File pictureFile = getOutputMediaFile();
//            if (pictureFile == null) {
//                Log.v(TAG, "Error creating output file");
//                return;
//            }
//            try {
//                FileOutputStream fos = new FileOutputStream(pictureFile);
//
////                BitmapFactory.Options options = new BitmapFactory.Options();
////                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
////                Bitmap bm= BitmapFactory.decodeByteArray(data, 0, data.length);
////                Bitmap capturedImage = rotateImage(bm , 90);
//                photo.compress(Bitmap.CompressFormat.JPEG, 100,fos);
////                fos.write(data);
//                fos.flush();
//                fos.close();
//            } catch (IOException e) {
//                Log.v(TAG, e.getMessage());
//            }

            // Start crop activity
            nextStep();
        }
    }

    private void nextStep() {
        startActivity(new Intent(this, CropActivity.class));
    }

    private static File getOutputMediaFile() throws IOException {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        } else {
            File folder_gui = new File(Environment.getExternalStorageDirectory() +
                    MainActivity.DIRECTORY_PATH);
            if (!folder_gui.exists()) {
                folder_gui.mkdirs();
            }
            MainActivity.FILE_NAMES.add("img" + System.currentTimeMillis() + ".jpg");
            return new File(folder_gui, MainActivity.FILE_NAMES.get(MainActivity.CURRENT_PAGE));
        }
    }

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = String.valueOf(System.currentTimeMillis());
//        String imageFileName = "img" + timeStamp ;
//        File folder_gui = new File(Environment.getExternalStorageDirectory() +
//                    MainActivity.DIRECTORY_PATH);
//            if (!folder_gui.exists()) {
//                folder_gui.mkdirs();
//            }
//            MainActivity.FILE_NAMES.add("img" + System.currentTimeMillis() + ".jpg");
//
//            return new File(folder_gui, MainActivity.FILE_NAMES.get(MainActivity.CURRENT_PAGE));
//
//        // Save a file: path for use with ACTION_VIEW intents
////        MainActivity.FILE_NAMES.add(image.getName());
////        Log.i(TAG, "Image Im getting"+image.getName()+"absolute"+image.getAbsolutePath());
////        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
////        return image;
//    }
//

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestExternalStoragePermission();
                } else {
                    Toast.makeText(getApplicationContext(), "Camera permission not granted.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            case 2:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestContactPermission();
                } else {
                    Toast.makeText(getApplicationContext(), "External Storage permission not granted.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            case 3:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                } else {
                    Toast.makeText(getApplicationContext(), "Contact permission not granted.",
                            Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }
}
