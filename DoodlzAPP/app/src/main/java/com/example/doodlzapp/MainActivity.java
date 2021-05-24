package com.example.doodlzapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static com.example.doodlzapp.PaintView.colorList;
import static com.example.doodlzapp.PaintView.current_brush;
import static com.example.doodlzapp.PaintView.current_size;
import static com.example.doodlzapp.PaintView.pathList;
import static com.example.doodlzapp.PaintView.sizeList;

public class MainActivity extends AppCompatActivity {

    //public static android.graphics.Path path = new android.graphics.Path();
    public static Paint paint_brush = new Paint();
    public static Path path = new Path();

    public ProgressDialog pd;
    private BottomNavigationView mNavigationView;
    public MaterialToolbar mAppBarTop;
    private LinearLayout mColorLens;
    private SeekBar seekBarSize;
    private int colorSave = Color.BLACK;

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
        mAppBarTop = findViewById(R.id.topAppBar);
        mNavigationView = findViewById(R.id.bottom_nav);
        mColorLens = findViewById(R.id.color_lens);
        seekBarSize = findViewById(R.id.seek_bar_size);
        pd = new ProgressDialog(MainActivity.this);

        mAppBarTop.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.save){
                    save();
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
                    case R.id.color_lens_item:
                        seekBarSize.setVisibility(View.GONE);
                        mColorLens.setVisibility(View.VISIBLE);
                        setCurrentBrush(colorSave);
                        break;
                    /*case R.id.reset_item:
                        reset();
                        seekBarSize.setVisibility(View.GONE);
                        mColorLens.setVisibility(View.GONE);
                        break;*/
                }
                return true;
            }
        });
    }


//set color
    public void redColor(View v){
        paint_brush.setColor(Color.parseColor("#E61717"));

        setCurrentBrush(paint_brush.getColor());
    }
    public void yellowColor(View v){
        colorSave = Color.parseColor("#EAD522");
        paint_brush.setColor(Color.parseColor("#EAD522"));
        setCurrentBrush(paint_brush.getColor());
    }
    public void blueColor(View v){
        colorSave = Color.parseColor("#244DE3");
        paint_brush.setColor(Color.parseColor("#244DE3"));
        setCurrentBrush(paint_brush.getColor());
    }
    public void greenColor(View v){
        colorSave = Color.parseColor("#2BE633");
        paint_brush.setColor(Color.parseColor("#2BE633"));
        setCurrentBrush(paint_brush.getColor());
    }
    public void blackColor(View v){
        colorSave = Color.parseColor("#FF000000");
        paint_brush.setColor(Color.parseColor("#FF000000"));
        setCurrentBrush(paint_brush.getColor());
    }
    public void brownColor(View v){
        colorSave = Color.parseColor("#320606");
        paint_brush.setColor(Color.parseColor("#320606"));
        setCurrentBrush(paint_brush.getColor());
    }

    ////////////////

    public static Bitmap setViewToBitmapImage(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
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
