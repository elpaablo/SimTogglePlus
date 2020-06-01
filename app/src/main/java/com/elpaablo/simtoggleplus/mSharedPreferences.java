package com.elpaablo.simtoggleplus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


class mSharedPreferences {
    private static final String KEY_SIM1_STATE = Constants.PREF_SIM1_STATE;
    private static final String KEY_SIM2_STATE = Constants.PREF_SIM2_STATE;
    private static final String PREF_FILE = Constants.PREF_FILE;
    private static final String KEY_NETWORK_TYPE = Constants.PREF_NETWORK_TYPE;
    private static final String KEY_TOGGLE_SIM1_CMD_SENT = Constants.PREF_SIM1_CMD_SENT;
    private static final String KEY_TOGGLE_SIM2_CMD_SENT = Constants.PREF_SIM2_SMD_SENT;
    private static final String KEY_TOGGLE_SIM1_CMD_RECEIVED = Constants.PREF_SIM1_CMD_RECEIVED;
    private static final String KEY_TOGGLE_SIM2_CMD_RECEIVED = Constants.PREF_SIM2_CMD_RECEIVED;

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;

    public static void saveSimState(Context context, int simState, int slotIndex){
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        editor = pref.edit();
        if(slotIndex == 0) {
            editor.putInt(KEY_SIM1_STATE, simState);
        } else if(slotIndex == 1) {
            editor.putInt(KEY_SIM2_STATE, simState);
        }
        editor.commit();
    }
    public static int getSimState(Context context, int slotIndex){
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        int defaultValue;
        int simState = defaultValue = Constants.PREF_DEFAULT_SIM_STATE_VALUE;
        if(slotIndex == 0) {
            simState = pref.getInt(KEY_SIM1_STATE, defaultValue);
        } else if(slotIndex == 1) {
            simState = pref.getInt(KEY_SIM2_STATE, defaultValue);
        }
        return simState;
    }
    public static int getNetworkType(Context context){
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        return pref.getInt(KEY_NETWORK_TYPE, Constants.PREF_DEFAULT_NETWORK_TYPE);
    }
    public static void saveNetworkType(Context context, int type) {
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.putInt(KEY_NETWORK_TYPE, type);
        editor.commit();
    }

    public static boolean getToggleCMDSent(Context context, int slotIndex){
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        if(slotIndex == 0)
            return pref.getBoolean(KEY_TOGGLE_SIM1_CMD_SENT, false);
        else if(slotIndex == 1)
            return pref.getBoolean(KEY_TOGGLE_SIM2_CMD_SENT, false);
        return false;
    }

    public static boolean getToggleCMDReceived(Context context, int slotIndex){
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        if(slotIndex == 0)
            return pref.getBoolean(KEY_TOGGLE_SIM1_CMD_RECEIVED, false);
        else if(slotIndex == 1)
            return pref.getBoolean(KEY_TOGGLE_SIM2_CMD_RECEIVED, false);
        return false;
    }


    public static void saveToggleCMDSent(Context context, int slotIndex, boolean sent) {
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        editor = pref.edit();
        if(slotIndex == 0)
            editor.putBoolean(KEY_TOGGLE_SIM1_CMD_SENT, sent);
        else if(slotIndex == 1)
            editor.putBoolean(KEY_TOGGLE_SIM2_CMD_SENT, sent);
        editor.commit();
    }

    public static void saveToggleCMDReceived(Context context, int slotIndex, boolean received) {
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        editor = pref.edit();
        if(slotIndex == 0)
            editor.putBoolean(KEY_TOGGLE_SIM1_CMD_RECEIVED, received);
        else if(slotIndex == 1)
            editor.putBoolean(KEY_TOGGLE_SIM2_CMD_RECEIVED, received);
        editor.commit();
    }

    public static void resetPreferences(Context context){
        pref = context.getSharedPreferences(PREF_FILE, Activity.MODE_PRIVATE);
        editor = pref.edit();
        editor.clear();
        editor.commit();
    }

}
