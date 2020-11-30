package com.elpaablo.simtoggleplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class RCDialog extends Activity implements View.OnTouchListener{
    private static String TAG = "ChargingLimiter.RCDialog";

    private static final String PATH_TO_RESTRICTED_CURRENT = "/sys/class/qcom-battery/restricted_current";
    //private static final String PATH_TO_RESTRICTED_CURRENT = "/sys/class/qcom-battery/restrict_cur";

    private float dX;
    private float dY;
    private int lastAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v =  inflater.inflate(R.layout.qs_dialog,null, false);
        setContentView(v);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (Objects.equals(action, "android.service.quicksettings.action.QS_TILE_PREFERENCES")) {
            Bundle bundle = intent.getExtras();
            String msg = "no extras";
            if (bundle != null) {
                try {
                    msg = bundle.get("android.intent.extra.COMPONENT_NAME").toString();
                } catch (NullPointerException e) {

                    return;
                }
            }

            //Log.e("component: ", " ::: " + msg);

            Dialog dialog = getDialog(this);
            if (!msg.contains("RCTileService")) {
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                i.setData(uri);
                startActivity(i);
                dialog.dismiss();
                finish();
                return;
            }


            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            if(v.getParent() != null) {
                ((ViewGroup)v.getParent()).removeView(v); // <- fix
            }
            dialog.setContentView(v);
            dialog.show();

            if (dialog.getWindow() != null) {
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
                wmlp.dimAmount = 0.0f;
                wmlp.gravity = Gravity.CENTER_VERTICAL;
                window.setAttributes(wmlp);
                window.setBackgroundDrawableResource(R.drawable.shadow);
                dialog.getWindow().setLayout(800, 500);
                if(v.getParent() != null) {
                    ((ViewGroup)v.getParent()).removeView(v); // <- fix
                }
                dialog.setContentView(v);
                dialog.show();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy(){

        super.onDestroy();
    }


    @SuppressLint("ClickableViewAccessibility")
    private Dialog getDialog(final Context context) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final LinearLayout mainLayout = findViewById(R.id.main_layout);
        final TextView progressText = mainLayout.findViewById(R.id.textProgress);
        final SeekBar seekBar = mainLayout.findViewById(R.id.seekBar);

        String current = IOUtils.readFromFile(context, PATH_TO_RESTRICTED_CURRENT);
        int value = seekBar.getProgress();
        try {
            float f = Float.parseFloat(current);
            value = (int) f / 100000;

        } catch (Exception e) {
            //do nothing
        }
        if (value >= 0 && value <= 30) {
            progressText.setText(value * 100 + " mA");
            seekBar.setProgress(value);
        }
       /* //TODO verificar min e max para a posição do texto
        if(value < 8)
           value = 7;
        else if (value > 24)
            value = 25;
        progressText.setX(value * 20 - 50);*/


        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //Log.d(TAG, "onStopTracking");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                //Log.d(TAG, "onStartTracking");
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress, boolean fromUser) {
                //Log.d("value: ", "" + seekBar.getProgress());
                progressText.setText(progress * 100 + " mA");

             /*  if (progress > 4 && progress < 24) {
                   int width = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
                   int thumbPos = seekBar.getPaddingLeft() + width * progress / seekBar.getMax();
                   progressText.measure(0, 0);
                   int txtW = progressText.getMeasuredWidth();
                   int delta = txtW / 2;
                   progressText.setX(seekBar.getX() + thumbPos - delta);
                   //progressText.setBackgroundResource(R.drawable.shadow);
                   //progressText.setPadding(4, 2, 4, 2 );

               }*/
            }
        };
        seekBar.setOnSeekBarChangeListener(seekBarListener);

        //remove parent from mainLayout to avoid errors

        builder.setView(mainLayout);
        final AlertDialog dialog = builder.create();
        final Button bCancel = findViewById(R.id.bCancel);
        Button bConfirm = findViewById(R.id.bConfirm);

        bCancel.setOnTouchListener(new View.OnTouchListener() {
            Rect rect = null;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v == null) return true;
                Button button = v.findViewById(R.id.bCancel);
                int textColor = getResources().getColor(R.color.colorButtonText, null);
                Drawable background = getDrawable(R.mipmap.red_dot);
                int action = event.getAction();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        textColor = getResources().getColor(R.color.colorAccent, null);
                        button.setBackground(getDrawable(R.mipmap.transparent_dot));
                        button.setTextColor(textColor);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (rect != null && !rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                            // The motion event was outside of the view, handle this as a non-click event
                            button.setTextColor(textColor);
                            button.setBackground(background);
                            return true;
                        }
                        //inside the view

                        Toast toast = Toast.makeText(RCDialog.this,"Changes cancelled...",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER,0,0);
                        toast.show();
                        dialog.dismiss();
                        finish();
                        return true;
                    default:
                        return true;
                }
            }

        });

        bConfirm.setOnTouchListener(new View.OnTouchListener() {

            private Rect rect = null;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v == null) return true;
                Button button = v.findViewById(R.id.bConfirm);
                int textColor = getResources().getColor(R.color.colorButtonText, null);
                Drawable background = getDrawable(R.mipmap.green_dot);
                int action = event.getAction();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rect = new Rect(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
                        textColor = getResources().getColor(R.color.colorAccent, null);
                        button.setBackground(getDrawable(R.mipmap.transparent_dot));
                        button.setTextColor(textColor);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (rect != null && !rect.contains(v.getLeft() + (int) event.getX(), v.getTop() + (int) event.getY())) {
                            // The motion event was outside of the view, handle this as a non-click event
                            button.setTextColor(textColor);
                            button.setBackground(background);
                            return true;
                        }
                        //inside the view
                        int value = seekBar.getProgress();
                        if(save(String.valueOf(value))) {
                            Toast toast = Toast.makeText(RCDialog.this, "Saving changes...", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        else {
                            Toast toast = Toast.makeText(RCDialog.this, "Changes cancelled", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        dialog.dismiss();
                        finish();
                        return true;
                    default:
                        return true;
                }
            }
        });

        return dialog;
    }


    private boolean save(String value) {


        /****** root method ******/
         ArrayList<String> commands = new ArrayList<>();
        if (RootUtils.canRunRootCommands()) {
            String current = value + "00000";
            String command = "echo " + current + " > " + PATH_TO_RESTRICTED_CURRENT;
            commands.add(command);
            RootUtils.execute(commands);
            //Log.d(TAG, "command: " + command);
            return true;
        }
        return false;

        /****** no root method. depends on changing restricted current node
         ****** ownership to system and/or give it write permission ******/
       /* String current = value + "00000";
        return IOUtils.writeToFile(this, PATH_TO_RESTRICTED_CURRENT, current);*/
    }


    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    //Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
                break;

            default:
                return false;
        }
        return true;
    }
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

}

