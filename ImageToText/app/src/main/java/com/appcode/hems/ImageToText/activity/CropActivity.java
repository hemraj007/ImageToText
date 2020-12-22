package com.appcode.hems.ImageToText.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.appcode.hems.ImageToText.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CropActivity extends AppCompatActivity implements View.OnClickListener {

    public static Bitmap croppedImage;
    public static Uri uriImage;
    private CropImageView cropImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        initActivity();
    }

    private void initActivity() {
        // Access the image file and generate URI
        File filePath = new File(Environment.getExternalStorageDirectory() +
                MainActivity.DIRECTORY_PATH);
        File fileImage = new File(filePath, MainActivity.FILE_NAMES.get(MainActivity.CURRENT_PAGE));
        uriImage = Uri.fromFile(fileImage);

//        uriImage = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", fileImage);

        // Set URI image to display
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setImageUriAsync(Uri.parse(uriImage.toString()));

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.nextStep);
        mFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.nextStep) {
            cropImageView.setOnCropImageCompleteListener(new CropImageView.OnCropImageCompleteListener() {
                @Override
                public void onCropImageComplete(CropImageView view, CropImageView.CropResult result) {
                    croppedImage = result.getBitmap();
                    nextStep();
                }
            });
            cropImageView.getCroppedImageAsync();
        }
    }

    private void nextStep() {
        startActivity(new Intent(CropActivity.this, BinarizationActivity.class));
    }
}
