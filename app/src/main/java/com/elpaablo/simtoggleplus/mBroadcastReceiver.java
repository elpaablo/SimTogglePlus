package com.elpaablo.simtoggleplus;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Objects;

import static android.service.quicksettings.TileService.requestListeningState;


public class mBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SimTogglePlus.mBroadcastReceiver";
    private static final int TOGGLE_SIM1 = 1;
    private static final int TOGGLE_SIM2 = 2;
    private static final int TOGGLE_LTE = 3;
    private static final int TOGGLE_ADB = 4;



    /**
     * com.android.internal.telephony.TelephonyIntents.ACTION_SIM_STATE_CHANGED
     * <p>
     * Broadcast Action: The sim card state has changed. The intent will have
     * the following extra values:</p>
     * <ul>
     * <li><em>phoneName</em> - A string version of the phone name.</li>
     * <li><em>ss</em> - The sim state. One of <code>"ABSENT"</code>
     * <code>"LOCKED"</code> <code>"READY"</code> <code>"ISMI"</code>
     * <code>"LOADED"</code></li>
     * <li><em>reason</em> - The reason while ss is LOCKED, otherwise is null
     * <code>"PIN"</code> locked on PIN1 <code>"PUK"</code> locked on PUK1
     * <code>"NETWORK"</code> locked on Network Personalization</li>
     * </ul>
     *
     * <p class="note">
     * Requires the READ_PHONE_STATE permission.
     *
     * <p class="note">
     * This is a protected intent that can only be sent by the system.
     */
    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";
    private static final String ACTION_PERMISSIONS_GRANTED ="com.elpaablo.com.elpaablo.simtoggleplus.PERMISSIONS_GRANTED";
    private static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String EXTRA_SIM_STATE = "ss";
    private static final String EXTRA_SLOT_INDEX = "phone";
    private static final String EXTRA_SIM_STATE_READY = "LOADED";
    private static final String EXTRA_SIM_STATE_ABSENT = "ABSENT";

    private static String keySimState;



    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (Objects.requireNonNull(action)) {
            case ACTION_SIM_STATE_CHANGED:
                //Log.d(TAG, "ACTION_SIM_STATE_CHANGED");
                Bundle extras = intent.getExtras();
                assert extras != null;
                String extra_state = Objects.requireNonNull(extras).getString(EXTRA_SIM_STATE);
                printExtras(extras);
                //Log.d(TAG, "SIM Action : " + action + " / State : " + extra_state);
                Object slot;
                try {
                    slot = extras.get(EXTRA_SLOT_INDEX);
                    //Log.d(TAG, "onReceive slot: " + String.valueOf(slot));
                } catch (NullPointerException e) {
                    //Log.d(TAG, e.toString());
                    return;
                }
                int slotIndex = (int) slot;
               String key;
               //int msgCode;
                if(Objects.requireNonNull(extra_state).equalsIgnoreCase(EXTRA_SIM_STATE_ABSENT) || extra_state.equalsIgnoreCase(EXTRA_SIM_STATE_READY)) {
                    if(slotIndex == 0) {
                        key = Constants.PREF_SIM1_CMD_RECEIVED;
                        //msgCode = Constants.MSG_TOGGLE_SIM1_CMD_RECEIVED;
                    }
                    else {
                        key = Constants.PREF_SIM2_CMD_RECEIVED;
                        //msgCode = Constants.MSG_TOGGLE_SIM2_CMD_RECEIVED;
                    }
                    if(mSharedPreferences.getToggleCMDSent(context, slotIndex)){
                        mSharedPreferences.saveToggleCMDReceived(context, slotIndex, true);
                    }
                    mSharedPreferences.saveToggleCMDSent(context, slotIndex, false); //reset cmd sent

                    //callTileOnStartListening(context, slotIndex + 1);
                    callTileOnStartListening(context, TOGGLE_LTE);
                }
                break;

            case ACTION_PERMISSIONS_GRANTED:
                callAllOnStartListening(context);
                break;
            case ACTION_BOOT_COMPLETED:
                //Log.d(TAG, "ACTION_BOOT_COMPLETED");
                //reset preferences
                mSharedPreferences.resetPreferences(context);
                //callTileOnStartListening(context, 1);
                //callTileOnStartListening(context, 2);
                break;
        }
    }

    private void printExtras(Bundle extras) {
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                //Log.d(TAG, "Received SIM extras : " + key + " = " + value);
            }
        }
    }

    private void callAllOnStartListening(Context context)
    {
        callTileOnStartListening(context, TOGGLE_SIM1);
        callTileOnStartListening(context, TOGGLE_SIM2);
        callTileOnStartListening(context, TOGGLE_LTE);
        callTileOnStartListening(context, TOGGLE_ADB);

    }

    private void callTileOnStartListening(Context context, int tileIndex) {
        ComponentName cname = null;
        switch(tileIndex) {
            case TOGGLE_SIM1:
                cname = new ComponentName(context, Sim1TileService.class);
                break;
            case TOGGLE_SIM2:
                cname = new ComponentName(context, Sim2TileService.class);
                break;
            case TOGGLE_LTE:
                cname = new ComponentName(context, LTETileService.class);
                break;
            case TOGGLE_ADB:
                cname = new ComponentName(context, ADBTileService.class);
                break;
        }
        if(cname != null) {
            requestListeningState(context, cname);
        }
    }
}