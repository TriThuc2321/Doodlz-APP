package com.example.doodlzapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static com.example.doodlzapp.MainActivity.paint_brush;

public class ColorDialogFragment extends Dialog  {
    private MainActivity parent;
    private SeekBar alphaSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar redSeekBar;
    private SeekBar blueSeekBar;
    private int color;

    private View colorSreen;

    public ColorDialogFragment(MainActivity mainActivity, int color) {
        super(mainActivity);
        this.parent = mainActivity;
        this.color = color;
    }
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.fragment_color_dialog);

        init();

        findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply();
                dismiss();
            }
        });
    }

    void init(){
        redSeekBar = findViewById(R.id.redSeekbar);
        greenSeekBar = findViewById(R.id.greenSeekbar);
        blueSeekBar = findViewById(R.id.blueSeekbar);
        alphaSeekBar = findViewById(R.id.alphaSeekbar);

        colorSreen = findViewById(R.id.colorDemo);

        blueSeekBar.setProgress(Color.blue(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        alphaSeekBar.setProgress(Color.alpha(color));

        colorSreen.setBackgroundColor(color);

        redSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        alphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);

    }
    private void apply(){
        paint_brush.setColor(color);
        parent.setCurrentBrush(paint_brush.getColor());
        parent.colorSave = color;
        int a = 1;
    }

    private SeekBar.OnSeekBarChangeListener colorChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            color = Color.argb(alphaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress());

                    colorSreen.setBackgroundColor(color);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


}

