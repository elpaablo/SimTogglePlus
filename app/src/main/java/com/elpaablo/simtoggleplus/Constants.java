package com.elpaablo.simtoggleplus;
import java.util.ArrayList;

final class Constants {
    public static final String PREF_FILE = "app_settings";
    public static final String PREF_SIM1_STATE = "sim1state";
    public static final String PREF_SIM2_STATE = "sim2state";
    public static final String PREF_NETWORK_TYPE = "pref_net_type";
    public static final String PREF_SIM1_CMD_SENT = "pref_sim1_cmd_sent";
    public static final String PREF_SIM2_SMD_SENT = "pref_sim2_cmd_sent";
    public static final String PREF_SIM1_CMD_RECEIVED = "pref_sim1_cmd_received";
    public static final String PREF_SIM2_CMD_RECEIVED = "pref_sim2_cmd_received";
    public static final String SIM_BACKGROUND_THREAD = "sim_background_thread";
    public static final int  MSG_TOGGLE_SIM1_CMD_RECEIVED = 1001;
    public static final int MSG_TOGGLE_SIM2_CMD_RECEIVED = 1002;

    public static final int PREF_DEFAULT_SIM_STATE_VALUE = -2;
    public static final int PREF_DEFAULT_NETWORK_TYPE = -3;
    public static final int READ_PHONE_STATE_REQUEST_CODE = 112;


    public static final int SLOT_ENABLED = 1;
    public static final int SLOT_DISABLED = 0;
    public static final int SIM_READY = 5;
    public static final int SIM_ABSENT = 1;

    public static final ArrayList<Integer> NON_LTE_NETWORK_TYPES = new ArrayList<Integer>(){
        {
            add(NETWORK_MODE_WCDMA_PREF);
            add(NETWORK_MODE_GSM_ONLY);
            add(NETWORK_MODE_WCDMA_ONLY);
            add(NETWORK_MODE_GSM_UMTS);
            add(NETWORK_MODE_CDMA);
            add(NETWORK_MODE_CDMA_NO_EVDO);
            add(NETWORK_MODE_EVDO_NO_CDMA);
            add(NETWORK_MODE_TDSCDMA_ONLY);
            add(NETWORK_MODE_TDSCDMA_WCDMA);
            add(NETWORK_MODE_NR_ONLY);
        }
    };

    public static final int DEFAULT_LTE_MODE = 12; //NETWORK_MODE_LTE_WCDMA

    /* NETWORK_MODE_* See ril.h RIL_REQUEST_SET_PREFERRED_NETWORK_TYPE */
    /**
     * GSM, WCDMA (WCDMA preferred)
     */
    public static final int NETWORK_MODE_WCDMA_PREF = 0;
    /**
     * GSM only
     */
    public static final int NETWORK_MODE_GSM_ONLY = 1;
    /**
     * WCDMA only
     */
    public static final int NETWORK_MODE_WCDMA_ONLY = 2;
    /**
     * GSM, WCDMA (auto mode, according to PRL)
     */
    public static final int NETWORK_MODE_GSM_UMTS = 3;
    /**
     * CDMA and EvDo (auto mode, according to PRL)
     */
    public static final int NETWORK_MODE_CDMA = 4;
    /**
     * CDMA only
     */
    public static final int NETWORK_MODE_CDMA_NO_EVDO = 5;
    /**
     * EvDo only
     */
    public static final int NETWORK_MODE_EVDO_NO_CDMA = 6;
    /**
     * GSM, WCDMA, CDMA, and EvDo (auto mode, according to PRL)
     */
    public static final int NETWORK_MODE_GLOBAL = 7;
    /**
     * LTE, CDMA and EvDo
     */
    public static final int NETWORK_MODE_LTE_CDMA_EVDO = 8;
    /**
     * LTE, GSM and WCDMA
     */
    public static final int NETWORK_MODE_LTE_GSM_WCDMA = 9;
    /**
     * LTE, CDMA, EvDo, GSM, and WCDMA
     */
    public static final int NETWORK_MODE_LTE_CDMA_EVDO_GSM_WCDMA = 10;
    /**
     * LTE only mode.
     */
    public static final int NETWORK_MODE_LTE_ONLY = 11;
    /**
     * LTE and WCDMA
     */
    public static final int NETWORK_MODE_LTE_WCDMA = 12;
    /**
     * TD-SCDMA only
     */
    public static final int NETWORK_MODE_TDSCDMA_ONLY = 13;
    /**
     * TD-SCDMA and WCDMA
     */
    public static final int NETWORK_MODE_TDSCDMA_WCDMA = 14;
    /**
     * LTE and TD-SCDMA
     */
    public static final int NETWORK_MODE_LTE_TDSCDMA = 15;
    /**
     * TD-SCDMA and GSM
     */
    public static final int NETWORK_MODE_TDSCDMA_GSM = 16;
    /**
     * TD-SCDMA, GSM and LTE
     */
    public static final int NETWORK_MODE_LTE_TDSCDMA_GSM = 17;
    /**
     * TD-SCDMA, GSM and WCDMA
     */
    public static final int NETWORK_MODE_TDSCDMA_GSM_WCDMA = 18;
    /**
     * LTE, TD-SCDMA and WCDMA
     */
    public static final int NETWORK_MODE_LTE_TDSCDMA_WCDMA = 19;
    /**
     * LTE, TD-SCDMA, GSM, and WCDMA
     */
    public static final int NETWORK_MODE_LTE_TDSCDMA_GSM_WCDMA = 20;
    /**
     * TD-SCDMA, CDMA, EVDO, GSM and WCDMA
     */
    public static final int NETWORK_MODE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 21;
    /**
     * LTE, TDCSDMA, CDMA, EVDO, GSM and WCDMA
     */
    public static final int NETWORK_MODE_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 22;
    /**
     * NR 5G only mode
     */
    public static final int NETWORK_MODE_NR_ONLY = 23;
    /**
     * NR 5G, LTE
     */
    public static final int NETWORK_MODE_NR_LTE = 24;
    /**
     * NR 5G, LTE, CDMA and EvDo
     */
    public static final int NETWORK_MODE_NR_LTE_CDMA_EVDO = 25;
    /**
     * NR 5G, LTE, GSM and WCDMA
     */
    public static final int NETWORK_MODE_NR_LTE_GSM_WCDMA = 26;
    /**
     * NR 5G, LTE, CDMA, EvDo, GSM and WCDMA
     */
    public static final int NETWORK_MODE_NR_LTE_CDMA_EVDO_GSM_WCDMA = 27;
    /**
     * NR 5G, LTE and WCDMA
     */
    public static final int NETWORK_MODE_NR_LTE_WCDMA = 28;
    /**
     * NR 5G, LTE and TDSCDMA
     */
    public static final int NETWORK_MODE_NR_LTE_TDSCDMA = 29;
    /**
     * NR 5G, LTE, TD-SCDMA and GSM
     */
    public static final int NETWORK_MODE_NR_LTE_TDSCDMA_GSM = 30;
    /**
     * NR 5G, LTE, TD-SCDMA, WCDMA
     */
    public static final int NETWORK_MODE_NR_LTE_TDSCDMA_WCDMA = 31;
    /**
     * NR 5G, LTE, TD-SCDMA, GSM and WCDMA
     */
    public static final int NETWORK_MODE_NR_LTE_TDSCDMA_GSM_WCDMA = 32;
    /**
     * NR 5G, LTE, TD-SCDMA, CDMA, EVDO, GSM and WCDMA
     */
    public static final int NETWORK_MODE_NR_LTE_TDSCDMA_CDMA_EVDO_GSM_WCDMA = 33;
}
