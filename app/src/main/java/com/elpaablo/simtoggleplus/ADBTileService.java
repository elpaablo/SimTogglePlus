package com.elpaablo.simtoggleplus;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import androidx.core.app.ActivityCompat;


public class ADBTileService extends TileService {

private static final String TAG = "SimTogglePlus.ADBTileService";
private static final String ADB = Settings.Global.ADB_ENABLED;
private static final int ENABLED = 1;
private static final int DISABLED = 0;
private Context context;


      /**
     * Called when the tile is clicked, regardless of its current state
     */
    @Override
    public void onClick() {
        if(permissionsGranted()) {
            super.onClick();
            Tile tile = getQsTile();
            ContentResolver cr = getContentResolver();
            int usb_debugging = Settings.Global.getInt(cr, ADB, -1);
            switch (usb_debugging) {
                case ENABLED:
                    Settings.Global.putInt(cr, ADB, DISABLED);
                    updateTileState(Tile.STATE_INACTIVE, tile);
                    break;
                case DISABLED:
                    Settings.Global.putInt(cr, ADB, ENABLED);
                    updateTileState(Tile.STATE_ACTIVE, tile);
                    break;
                default:
                    updateTileState(Tile.STATE_UNAVAILABLE, tile);
                    break;
            }

            //Log.d(TAG, " on click: " + usb_debugging);
        }
    }


    /**
     * Called when the tile is added to the QuickSettings pane
     */
    @Override
    public void onTileAdded() {
        super.onTileAdded();
        if (permissionsGranted()) {
            context = getApplicationContext();
            Tile tile = getQsTile();
            updateTileState(getNewTileState(Settings.Global.getInt(getContentResolver(), ADB, -1), tile), tile);
        }
    }

    /**
     * Called when the tile moves into listening state
     * For example, when the QuickSettings pane is expanded and the tile needs to keep the UI up
     * to date
     */
    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        int usb_debugging;
        if (permissionsGranted()) {
           updateTileState(getNewTileState(Settings.Global.getInt(getContentResolver(), ADB, -1), tile), tile);
        }
    }

    private int getNewTileState(int usbDebugging, Tile tile) {
        switch (usbDebugging) {
            case ENABLED:
                return Tile.STATE_ACTIVE;
            case DISABLED:
                return Tile.STATE_INACTIVE;
        }
        return Tile.STATE_UNAVAILABLE;
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
        CharSequence name = "USB debugging ";
        switch(tileState) {
            case Tile.STATE_ACTIVE:
                name = name + "enabled";
                break;
            case Tile.STATE_INACTIVE:
                name = name + "disabled";
                break;
            default:
                name = name + "unavailable";
                break;
        }
        Icon icon = Icon.createWithResource(getApplicationContext(), R.drawable.adb);
        tile.setIcon(icon);
        tile.setLabel(name);
        tile.setState(tileState);
        tile.updateTile();
    }

    private boolean permissionsGranted() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
    }
}
