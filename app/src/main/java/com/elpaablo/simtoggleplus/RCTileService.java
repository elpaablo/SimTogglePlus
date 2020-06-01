package com.elpaablo.simtoggleplus;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

import java.util.ArrayList;

public class RCTileService extends TileService  {

    private static final String TAG = "ChargingLimiter.ToggleChargeLimiter";
    private static final String PATH_TO_RESTRICTED_CHARGING = "/sys/class/qcom-battery/restricted_charging";
    private static final String CMD_ENABLE_RESTRICTED_CHARGING = "echo 1 > " + PATH_TO_RESTRICTED_CHARGING;
    private static final String CMD_DISABLE_RESTRICTED_CHARGING = "echo 0 > " + PATH_TO_RESTRICTED_CHARGING;
    private static final char ENABLED = '1';
    private static final char DISABLED = '0';
    private Context context;


    /**
     * Called when the tile is clicked, regardless of its current state
     */
    @Override
    public void onClick() {
        super.onClick();
        context = getApplicationContext();
        Tile tile = getQsTile();
        ArrayList<String> commands = new ArrayList<>();
        char state = IOUtils.readFromFile(context, PATH_TO_RESTRICTED_CHARGING).charAt(0);
        switch (state) {
            case ENABLED:
                //if (RootUtils.canRunRootCommands()) {
                    commands.add(CMD_DISABLE_RESTRICTED_CHARGING);
                    RootUtils.execute(commands);
                    updateTileState(Tile.STATE_INACTIVE, tile);
                //}
                break;
            case DISABLED:
                //if (RootUtils.canRunRootCommands()) {
                    commands.add(CMD_ENABLE_RESTRICTED_CHARGING);
                    RootUtils.execute(commands);
                    updateTileState(Tile.STATE_INACTIVE, tile);
                //}
                updateTileState(Tile.STATE_ACTIVE, tile);
                break;
            default:
                updateTileState(Tile.STATE_UNAVAILABLE, tile);
                break;
        }

        //Log.d(TAG, " on click: " + state);
    }


    /**
     * Called when the tile is added to the QuickSettings pane
     */
    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Tile tile = getQsTile();
        updateTileState(getNewTileState(IOUtils.readFromFile(getApplicationContext(), PATH_TO_RESTRICTED_CHARGING), tile), tile);
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
        updateTileState(getNewTileState(IOUtils.readFromFile(getApplicationContext(), PATH_TO_RESTRICTED_CHARGING), tile), tile);
    }

    private int getNewTileState(String restrictedCharging, Tile tile) {
        char state = restrictedCharging.charAt(0);
        switch (state) {
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
        CharSequence name = "Restricted charging ";
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
        Icon icon = Icon.createWithResource(getApplicationContext(), R.drawable.tile_icon);
        tile.setIcon(icon);
        tile.setLabel(name);
        tile.setState(tileState);
        tile.updateTile();
    }
}
