package com.elpaablo.simtoggleplus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class SimService {

    public static final int SIM_READY = 5;
    public static final int SIM_ABSENT = 1;
    private static final String TAG = "SimToggle.SimService";
    private static SimService simService;
    private static final String METHOD_SET_SIM_STATE = "setSimPowerStateForSlot";
    private static final String METHOD_SET_NETWORK_TYPE = "setPreferredNetworkType";
    private static final String METHOD_GET_NETWORK_TYPE = "getPreferredNetworkType";

    private SimService() {
    }


    public static SimService getInstance(Context context) {

        if (simService == null) {
            simService = new SimService();
        }
        return simService;
    }

    public CharSequence getOperatorName(Context context, int slot) {
        return getOperatorNameForSlot(context, slot);
    }

    private CharSequence getOperatorNameForSlot(Context context, int slot) {
        SubscriptionInfo sInfo = getSubscriptionInfo(context, slot);
        CharSequence info = null;
        if (sInfo != null) {
            info = sInfo.getDisplayName();
        }
        return info;
    }

    @SuppressLint("MissingPermission")
    public int getSlotIndex(Context context, int subId) {
        int slotIndex = -1;
        SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        SubscriptionInfo info = Objects.requireNonNull(manager).getActiveSubscriptionInfo(subId);
        if (info != null) {
            slotIndex = info.getSimSlotIndex();
        }
        return slotIndex;
    }

    @SuppressLint("MissingPermission")
    @Nullable
    private SubscriptionInfo getSubscriptionInfo(Context context, int slot) {
        SubscriptionInfo info = null;
        SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        info = Objects.requireNonNull(manager).getActiveSubscriptionInfoForSimSlotIndex(slot);
        return info;
    }

    public int getSimState(Context context, int slot) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = 1;
        try {
            state = Objects.requireNonNull(tm).getSimState(slot);
        } catch (NullPointerException e) {
            //Log.d(TAG, " " + e.toString());
            state = -1;
        }
        return state;
    }

    public boolean setSimStateForSlot(Context context, int slotState, int slotID) {//0 - disabled, 1 - enabled
        boolean success = true;

        try {
            success = setSimStateForSlot(context, METHOD_SET_SIM_STATE, slotState, slotID);
        } catch (MethodNotFoundException e) {
            success = false;
            //Log.d(TAG, e.toString());
        }
        return success;
    }

    private boolean setSimStateForSlot(Context context, String predictedMethodName, int slotState, int slotID) throws MethodNotFoundException {

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        boolean success = true;
        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[2];
            parameter[0] = int.class;
            parameter[1] = int.class;
            Method setSim = telephonyClass.getMethod(predictedMethodName, parameter);

            Object[] obParameter = new Object[2];
            obParameter[0] = slotID;
            obParameter[1] = slotState;

            setSim.invoke(telephony, obParameter);
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
            throw new MethodNotFoundException(predictedMethodName);
        }
        return success;
    }

    public int getPreferredNetworkType(Context context) {
        int subId = getDefaultDataSubId(context);
        try {
            return getPreferredNetworkType(context, METHOD_GET_NETWORK_TYPE, subId);
        } catch (MethodNotFoundException e) {
            return -1;
        }
    }

    @SuppressLint("MissingPermission")
    private int getPreferredNetworkType(Context context, String predictedMethodName, int subId) throws MethodNotFoundException {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int netType = -1;
        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getNetType = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[1];
            obParameter[0] = subId;
            netType = (int) getNetType.invoke(telephony, obParameter);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MethodNotFoundException(predictedMethodName);
        }
        return netType;
    }

    public int getDefaultDataSubId(Context context) {
        int subId = -1;
        try {
            subId = getDefaultDataSubscriptionId(context);
        } catch (Exception e) {//remove if not needed
            e.printStackTrace();
        }
        return subId;
    }

    private int getDefaultDataSubscriptionId(Context context) {
        SubscriptionManager subManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        return Objects.requireNonNull(subManager).getDefaultDataSubscriptionId();
    }

    public boolean setNetworkType(Context context, int type) {
        boolean success = true;

        try {
            success = setDataNetowrkType(context, METHOD_SET_NETWORK_TYPE, type);
        } catch (MethodNotFoundException e) {
            success = false;
            //Log.d(TAG, e.toString());
        }
        return success;
    }

    private boolean setDataNetowrkType(Context context, String predictedMethodName, int type) throws MethodNotFoundException {

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int subId = getDefaultDataSubId(context);
        boolean success = true;
        try {

            Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

            Class<?>[] parameter = new Class[2];
            parameter[0] = int.class;
            parameter[1] = int.class;
            Method setNetType = telephonyClass.getMethod(predictedMethodName, parameter);
            Object[] obParameter = new Object[2];
            obParameter[0] = subId;
            obParameter[1] = type;
            setNetType.invoke(telephony, obParameter);
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
            throw new MethodNotFoundException(predictedMethodName);
        }

        return success;
    }

    private static class MethodNotFoundException extends Exception {

        private static final long serialVersionUID = -996812356902545308L;

        MethodNotFoundException(String info) {
            super(info);
        }
    }

    public static void pause(long milisecs) {
        try {
            Thread.sleep(milisecs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getTelephonyManagerMethodNamesForThisDevice(Context context) {

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Class<?> telephonyClass;
        String str = "";
        try {
            telephonyClass = Class.forName(telephony.getClass().getName());
            Method[] methods = telephonyClass.getMethods();
            for (Method method : methods) {

                str += (method + "\n\n"); //declared by: " + methods[idx].getDeclaringClass());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return str;
    }

    private static String getTransactionCode(Context context) {
        try {
            final TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final Class<?> mTelephonyClass = Class.forName(mTelephonyManager.getClass().getName());
            final Method mTelephonyMethod = mTelephonyClass.getDeclaredMethod("getITelephony");
            mTelephonyMethod.setAccessible(true);
            final Object mTelephonyStub = mTelephonyMethod.invoke(mTelephonyManager);
            final Class<?> mTelephonyStubClass = Class.forName(mTelephonyStub.getClass().getName());
            final Class<?> mClass = mTelephonyStubClass.getDeclaringClass();
            final Field field = mClass.getDeclaredField("TRANSACTION_setDataEnabled");
            field.setAccessible(true);
            return String.valueOf(field.getInt(null));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
