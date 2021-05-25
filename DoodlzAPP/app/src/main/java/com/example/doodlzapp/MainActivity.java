package com.example.doodlzapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.example.doodlzapp.PaintView.colorList;
import static com.example.doodlzapp.PaintView.current_brush;
import static com.example.doodlzapp.PaintView.current_size;
import static com.example.doodlzapp.PaintView.pathList;
import static com.example.doodlzapp.PaintView.sizeList;


public class MainActivity extends AppCompatActivity {

    private static final int SAVE_IMAGE_PERMISSION_REQUEST_CODE = 1;
    RelativeLayout content;
    public static Paint paint_brush = new Paint();
    public static Path path = new Path();

    public ProgressDialog pd;
    private BottomNavigationView mNavigationView;
    public MaterialToolbar mAppBarTop;
    private LinearLayout mColorLens;
    private SeekBar seekBarSize;
    public int colorSave = Color.BLACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }

    private void reset(){
        colorList.clear();
        pathList.clear();
        sizeList.clear();
        path.reset();

    }

    public void setCurrentBrush(int c){
        current_brush = c;
        path = new Path();
    }
    public void setCurrentSize(float size){
        current_size = size;
        path = new Path();
    }
    void init(){
        content = findViewById(R.id.display);

        mAppBarTop = findViewById(R.id.topAppBar);
        mNavigationView = findViewById(R.id.bottom_nav);
        mColorLens = findViewById(R.id.color_lens);
        seekBarSize = findViewById(R.id.seek_bar_size);

        pd = new ProgressDialog(MainActivity.this);

        mAppBarTop.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.save){
                        requestStorage();
                }
                else if(item.getItemId() == R.id.reset_item){
                    reset();
                }
                else if(item.getItemId() == R.id.color_lens_item){
                    ColorDialogFragment colorDialogFragment = new ColorDialogFragment(MainActivity.this, colorSave);
                    colorDialogFragment.show();
                }
                return true;
            }
        });

        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setCurrentSize(progress*2f + 40f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.pencil_item:
                        mColorLens.setVisibility(View.GONE);
                        seekBarSize.setVisibility(View.GONE);
                        setCurrentSize(8f);
                        setCurrentBrush(colorSave);
                        break;
                    case R.id.brush_item:
                        seekBarSize.setVisibility(View.VISIBLE);
                        mColorLens.setVisibility(View.GONE);
                        setCurrentSize(seekBarSize.getProgress()*2f + 40f);
                        setCurrentBrush(colorSave);
                        break;
                    case R.id.eraser_item:
                        seekBarSize.setVisibility(View.VISIBLE);
                        mColorLens.setVisibility(View.GONE);
                        setCurrentBrush(Color.WHITE);
                        setCurrentSize(seekBarSize.getProgress()*2f + 40f);
                        break;
                }
                return true;
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    void requestStorage(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SAVE_IMAGE_PERMISSION_REQUEST_CODE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            save();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case SAVE_IMAGE_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    save();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void save(){
        pd.setMessage("saving your image");
        pd.show();
        RelativeLayout savingLayout =(RelativeLayout)findViewById(R.id.display);
        File file = saveBitMap(MainActivity.this, savingLayout);
        if (file != null) {
            pd.cancel();
            Toast.makeText(getApplicationContext(),"Drawing saved to the gallery!",Toast.LENGTH_LONG).show();
            Log.i("TAG", "Drawing saved to the gallery!");
        } else {
            pd.cancel();
            Toast.makeText(getApplicationContext(),"Oops! Image could not be saved.",Toast.LENGTH_LONG).show();
            Log.i("TAG", "Oops! Image could not be saved.");
        }
    }


    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"PaintApp");

        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated){
                Log.i("TAG", "Can't create directory to save the image");
                Toast.makeText(getApplicationContext(),"Can't create directory to save the image.",Toast.LENGTH_SHORT).show();
            }

            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap =getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery( context,pictureFile.getAbsolutePath());
        return pictureFile;
    }


    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue scanning gallery.");
        }
    }


}


