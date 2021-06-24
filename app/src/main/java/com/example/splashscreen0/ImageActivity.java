package com.example.splashscreen0;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 23;
    ImageView imageView;
    SQLiteDatabase db;
    public static final int PICK_IMAGE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        //Permission to access external storage
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        imageView = (ImageView) findViewById(R.id.imageView);
        //creating database
        db = this.openOrCreateDatabase("test.db", Context.MODE_PRIVATE,null);
        //creating table for storing image
        db.execSQL("create table if not exists imageTb ( image blob )");

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, PICK_IMAGE);
    }

    public void viewImage(View view)
    {
        Cursor c = db.rawQuery("select * from imageTb", null);
        if(c.moveToNext())
        {
            byte[] image = c.getBlob(0);
            Bitmap bmp= BitmapFactory.decodeByteArray(image, 0 , image.length);
            imageView.setImageBitmap(bmp);
            Toast.makeText(this,"Done", Toast.LENGTH_SHORT).show();
        }
    }

    public  void fetchImage(View view) throws IOException {
        //File folder= new File(String.valueOf(getApplicationContext().getExternalFilesDir()));
        File folder= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/groot.jpg/");
//        File root = android.os.Environment.getExternalStorageDirectory();
//        File dir = new File(root.getAbsolutePath() + "/path");
//        dir.mkdirs();
        //File file = new File(dir, ".groot.jpg");
        Log.i("Path: ", folder.toString());
        FileInputStream fis = new FileInputStream(folder);
        byte[] image= new byte[fis.available()];
        fis.read(image);
        ContentValues values = new ContentValues();
        values.put("image",image);
        db.insert("imageTb", null,values);
        fis.close();
        Toast.makeText(this,"Image Fetched", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            //TODO: action
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
    }
}