package app.com.choptransit.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import app.com.choptransit.models.response.DriverData;
import app.com.choptransit.models.response.PassengerResponse;

public class SharePrefData {
    private static final SharePrefData instance = new SharePrefData();
    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public static synchronized SharePrefData getInstance() {
        return instance;
    }

    public void setContext(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharePrefData() {
    }

    public void setPrefString(Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(key, value).apply();
    }


    public void setPassengerData(Context context, String key, PassengerResponse.PassengerData passenger) {
        Gson gson = new Gson();
        String json = gson.toJson(passenger);
        setPrefString(context, key, json);
    }

    public PassengerResponse.PassengerData getPassengerPref(Context context, String key) {
        Gson gson = new Gson();
        String json = getPrefString(context, key);

        return gson.fromJson(json, PassengerResponse.PassengerData.class);
    }

    public void setDriverData(Context context, String key, DriverData data) {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        setPrefString(context, key, json);
    }

    public DriverData getDriverData(Context context, String key) {
        Gson gson = new Gson();
        String json = getPrefString(context, key);

        return gson.fromJson(json, DriverData.class);
    }

    public void setPrefInt(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(key, value).apply();
    }

    public void setPrefBoolean(Context context, String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(key, value).apply();
    }

    public void setPrefLong(Context context, String key, Long value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(key, value).apply();
    }

    public Long getPrefLong(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0L);
    }

    public String getPrefString(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public int getPrefInt(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }

    public boolean getPrefBoolean(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false);
    }

    public void deletePrefData(Context context, String key) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(key).apply();
    }

    public boolean containsPrefData(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }

}
