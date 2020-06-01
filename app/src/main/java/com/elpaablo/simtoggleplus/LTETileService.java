package com.elpaablo.simtoggleplus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.core.app.ActivityCompat;
import static java.util.Objects.isNull;


public class LTETileService extends TileService {

    private static final String TAG = "SimToggle.toggleLTE";

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
        else{
            toggleLTE();
        }
    }

    /**
     * Called when the tile is added to the QuickSettings pane
     */
    @Override
    public void onTileAdded() {
        super.onTileAdded();
        //Log.d(TAG, "on tile added running");
        initTileState();
    }

    /**
     * Called when the tile moves into listening state
     * For example, when the QuickSettings pane is expanded and the tile needs to keep the UI up
     * to date
     */
    @Override
    public void onStartListening() {
        Tile tile = getQsTile();
        context = getApplicationContext();
        int savedNetworkType = mSharedPreferences.getNetworkType(context);
        int subId = -1;
        int slotIndex = -1;
        int simState = -1;
        int newNetworkType = -1;
        SimService service = SimService.getInstance(context);

        if (permissionsGranted()) {
            subId = service.getDefaultDataSubId(context);
            if(subId > 0) {
                slotIndex = service.getSlotIndex(context, subId);
                if(slotIndex > -1) {
                    simState = service.getSimState(context, slotIndex);
                    if (simState == Constants.SIM_READY) {
                        newNetworkType = service.getPreferredNetworkType(context);
                        mSharedPreferences.saveNetworkType(context, newNetworkType);
                    }
                }
            }
            if(newNetworkType > -1) {
                int tileState;
                if (Constants.NON_LTE_NETWORK_TYPES.contains(newNetworkType)) {
                    tileState = Tile.STATE_INACTIVE;
                } else {
                    tileState = Tile.STATE_ACTIVE;
                }
                updateTileState(tileState, tile);
            }
            else{
                updateTileState(getNewTileState(newNetworkType, tile), tile);
            }

        } else {
            initTileState();
        }
        //Log.d(TAG, "onStartListening old network type: " + savedNetworkType + ", new network type: " + newNetworkType + ", subId: " + subId
                //+ ", simState: " + simState + ", slotIndex: " + slotIndex);
    }


    private void toggleLTE(){

        context = getApplicationContext();
        Tile tile = getQsTile();
        SimService service = SimService.getInstance(context);
        int oldNetworkType = SimService.getInstance(context).getPreferredNetworkType(context);
        int newNetworkType = oldNetworkType;

        //Log.d(TAG, "onClick network type: " + newNetworkType);

        if(Constants.NON_LTE_NETWORK_TYPES.contains(oldNetworkType)){
            newNetworkType = Constants.DEFAULT_LTE_MODE;
        }
        else{
            newNetworkType = Constants.NETWORK_MODE_GSM_ONLY;
        }
        if(newNetworkType > -1) {
            boolean success = service.setNetworkType(context, newNetworkType);
            if(success) {
                mSharedPreferences.saveNetworkType(context, newNetworkType);
                updateTileState(getNewTileState(newNetworkType, tile), tile);
            }
            else{
                updateTileState(getNewTileState(oldNetworkType, tile), tile);
            }
        }
        else {
            updateTileState(getNewTileState(oldNetworkType, tile), tile);
        }
        //Log.d(TAG, "onClick network type: " + newNetworkType);
    }

    private int getNewTileState(int netType, Tile tile) {
        int newTileState;
        switch (netType) {
            case Constants.DEFAULT_LTE_MODE:
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
        CharSequence name = "LTE";
        switch (tileState){
            case Tile.STATE_ACTIVE:
                name = name + " on";
                break;
            case Tile.STATE_INACTIVE:
                name = name + " off";
                break;
        }

        Icon icon = Icon.createWithResource(getApplicationContext(), R.drawable.lte_text);
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
        int netType = Constants.DEFAULT_LTE_MODE;
        if(permissionsGranted()) {
            netType = SimService.getInstance(context).getPreferredNetworkType(context);
            //Log.d(TAG, "default LTE state with permissions. requested network type: " + netType);
        }
        else{
            //Log.d(TAG, "default LTE state aplied without permissions: " + netType);
        }
        mSharedPreferences.saveNetworkType(context, netType);
        updateTileState(getNewTileState(netType, tile), tile);
    }
}
