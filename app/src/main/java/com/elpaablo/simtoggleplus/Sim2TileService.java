package com.elpaablo.simtoggleplus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.core.app.ActivityCompat;

import static java.util.Objects.isNull;


public class Sim2TileService extends TileService {

    private Handler mHandler = new Handler();

    private static final String TAG = "SimToggle.Sim2";
    //private static final String KEY_SIM_DISABLED_BY_USER = Constants.PREF_SIM1_DISABLED_BY_USER;
    private final int SLOT_INDEX = 1;
    private final int SIM_ENABLED = 1;
    private final int SIM_DISABLED = 0;
    private Context context;




    /**
     * Called when the tile is clicked, regardless of its current state
     */
    @Override
    public void onClick() {
        if (!permissionsGranted()) {
            if (isNull(MainActivity.active) || !MainActivity.active) {
                requestPermissions();
            }
        }
        toggleSimState();
    }

    /**
     * Called when the tile is added to the QuickSettings pane
     */
    @Override
    public void onTileAdded() {
        super.onTileAdded();
        mSharedPreferences.saveToggleCMDReceived(getApplicationContext(), SLOT_INDEX, false);
        mSharedPreferences.saveToggleCMDSent(getApplicationContext(), SLOT_INDEX, false);
        //Log.d(TAG, "on tile added running");
        initTileState();
    }

    @Override
    public void onDestroy(){
        onStartListening();
        super.onDestroy();
    }

    /**
     * Called when the tile moves into listening state
     * For example, when the QuickSettings pane is expanded and the tile needs to keep the UI up
     * to date
     */
    @Override
    public void onStartListening() {
        super.onStartListening();

        context = getApplicationContext();

        if(mSharedPreferences.getToggleCMDSent(context, SLOT_INDEX))//toggle waiting for brodcast receiver
            return;

        Tile tile = getQsTile();

        int oldSimState = mSharedPreferences.getSimState(context, SLOT_INDEX);
        int newSimState = oldSimState;
        if(oldSimState == Constants.PREF_DEFAULT_SIM_STATE_VALUE){
            initTileState();
        }
        else {

            if (permissionsGranted()) {
                newSimState = SimService.getInstance(context).getSimState(context, SLOT_INDEX);
                mSharedPreferences.saveSimState(context, newSimState, SLOT_INDEX);
                updateTileState(getNewTileState(newSimState, tile), tile);
            }
            //Log.d(TAG, "onStartListening old sim state: " + oldSimState + ", new sim state: " + newSimState);
        }
    }

    private void toggleSimState(){
        context = getApplicationContext();
        final Tile tile = getQsTile();
        boolean success = false;
        int simState = SimService.getInstance(context).getSimState(context, SLOT_INDEX);
        int slotState = Constants.SLOT_DISABLED;
        if (simState == SimService.SIM_ABSENT) {
            slotState = Constants.SLOT_ENABLED;
            updateTileState(Tile.STATE_ACTIVE, tile);
        } else {
            updateTileState(Tile.STATE_INACTIVE, tile);
        }

        mSharedPreferences.saveToggleCMDReceived(context, SLOT_INDEX, false); //reset cmd received before new cmd send
        SimService.getInstance(context).setSimStateForSlot(context, slotState, SLOT_INDEX);
        mSharedPreferences.saveToggleCMDSent(context, SLOT_INDEX, true); //flasg cmd sent

        new Handler().postDelayed(new Runnable() {
            public void run() {//wait for broadcast receiver
                boolean received = mSharedPreferences.getToggleCMDReceived(context, SLOT_INDEX); //check cmd received
                if(!received){
                    updateTileState(Tile.STATE_UNAVAILABLE, tile);//cmd not received, disable toggle
                }
                else{
                    mSharedPreferences.saveToggleCMDSent(context, SLOT_INDEX, false);  //reset cmd sent only if if cmd received
                }

                //Log.d(TAG, "waited for broadcast receiver. toggle cmd received:  " + received);
            }
        }, 5000);


        //Log.d(TAG, "onClick sim state: " + simState);
    }

    private int getNewTileState(int simState, Tile tile) {
        int newTileState;
        switch (simState) {
            /*case Constants.SIM_ABSENT:
                newTileState = Tile.STATE_INACTIVE;
                break;*/
            case SimService.SIM_READY:
                newTileState = Tile.STATE_ACTIVE;
                break;
            case -1:
                newTileState = Tile.STATE_UNAVAILABLE;
                break;
            default:
                newTileState = Tile.STATE_INACTIVE;
                break;
        }
        return newTileState;
    }

    /**
     * Method to set the state of the tile. This example only updates the icon since it's a toggle
     * action and the label remains same, but the label and content description can also be updated
     * by calling tile.setLabel() and tile.setContentDescription()
     *
     * @param tileState The state required to be set on the tile
     * @param tile      The Tile object required to be passed during invocation for operation upon
     */
    private void updateTileState(int tileState, Tile tile) {
        CharSequence name = "SIM" + (SLOT_INDEX + 1);
        context = getApplicationContext();
        if (permissionsGranted() && tileState == Tile.STATE_ACTIVE) {
            name = SimService.getInstance(context).getOperatorName(context, SLOT_INDEX);
        }
        int icon_image;
        if(tileState == Tile.STATE_UNAVAILABLE){
            icon_image = R.drawable.no_sim;
        }
        else{
            icon_image = R.drawable.sim_card;
        }
        Icon icon = Icon.createWithResource(getApplicationContext(), icon_image);
        tile.setIcon(icon);
        tile.setLabel(name);
        tile.setState(tileState);
        tile.updateTile();
    }

    private boolean permissionsGranted() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions(){
        //call main activity which checks permissions on start
        Intent intent = new Intent(this.getApplication(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initTileState(){
        context = getApplicationContext();
        Tile tile = getQsTile();
        int simState = Constants.SIM_ABSENT;
        if(permissionsGranted()) {
            simState = SimService.getInstance(context).getSimState(context, SLOT_INDEX);
            //Log.d(TAG, "setDefaultSimState with permissions. requested sim state: " + simState);
        }
        else{
            //Log.d(TAG, "default state aplied without permissions: " + simState);
        }
        mSharedPreferences.saveSimState(context, simState, SLOT_INDEX);
        updateTileState(getNewTileState(simState, tile), tile);
    }



}