package com.blobcity.utils;

import android.content.Context;

import java.util.UUID;

import static com.blobcity.utils.ConstantPath.PREF_UNIQUE_ID;

public class UniqueUUid {
    private static String uniqueID = null;

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPrefs sharedPrefs = new SharedPrefs();
            uniqueID = sharedPrefs.getPrefVal(context, PREF_UNIQUE_ID);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                sharedPrefs.setPrefVal(context, PREF_UNIQUE_ID, uniqueID);
            }
        }
        return uniqueID;
    }
}
