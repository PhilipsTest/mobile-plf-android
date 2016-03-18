package com.philips.pins.shinelib;

import android.content.SharedPreferences;
import android.os.Handler;

import com.philips.pins.shinelib.utility.SHNLogger;

import java.util.Map;
import java.util.Set;

public class SharedPreferencesWrapper implements SharedPreferences {

    private interface Getter<T> {
        T get(SharedPreferences sharedPreferences, String key, T defaultValue);
    }

    private static String TAG = SharedPreferencesWrapper.class.getSimpleName();

    private static final int DELAY_MILLIS = 50;
    private SharedPreferences sharedPreferences;
    private Handler handler;
    private long internalThreadId;

    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            SHNLogger.wtf(TAG, "The internal thread was not responding! Custom SharedPreference's execution time has exceeded expected!");

            assert (false);
        }
    };

    public SharedPreferencesWrapper(SharedPreferences sharedPreferences, Handler handler, long internalThreadId) {
        this.sharedPreferences = sharedPreferences;
        this.handler = handler;
        this.internalThreadId = internalThreadId;
    }

    private <T> T getValue(String key, T defaultValue, Getter<T> getter) {
        assertCorrectThread();
        handler.postDelayed(timeOut, DELAY_MILLIS);
        T val = getter.get(sharedPreferences, key, defaultValue);
        handler.removeCallbacks(timeOut);
        return val;
    }

    private <T> T getValue(Getter<T> getter) {
        return getValue("", null, getter);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return getValue(key, defaultValue, new Getter<Integer>() {
            @Override
            public Integer get(SharedPreferences sharedPreferences, String key, Integer defaultValue) {
                return sharedPreferences.getInt(key, defaultValue);
            }
        });
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getValue(key, defaultValue, new Getter<String>() {
            @Override
            public String get(SharedPreferences sharedPreferences, String key, String defaultValue) {
                return sharedPreferences.getString(key, defaultValue);
            }
        });
    }

    @Override
    public Map<String, ?> getAll() {
        return getValue(new Getter<Map<String, ?>>() {
            @Override
            public Map<String, ?> get(SharedPreferences sharedPreferences, String key, Map<String, ?> defaultValue) {
                return sharedPreferences.getAll();
            }
        });
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> set) {
        return getValue(key, set, new Getter<Set<String>>() {
            @Override
            public Set<String> get(SharedPreferences sharedPreferences, String key, Set<String> defaultValue) {
                return sharedPreferences.getStringSet(key, defaultValue);
            }
        });
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return getValue(key, defaultValue, new Getter<Long>() {
            @Override
            public Long get(SharedPreferences sharedPreferences, String key, Long defaultValue) {
                return sharedPreferences.getLong(key, defaultValue);
            }
        });
    }

    @Override
    public float getFloat(String key, float defaultValue) {
        return getValue(key, defaultValue, new Getter<Float>() {
            @Override
            public Float get(SharedPreferences sharedPreferences, String key, Float defaultValue) {
                return sharedPreferences.getFloat(key, defaultValue);
            }
        });
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getValue(key, defaultValue, new Getter<Boolean>() {
            @Override
            public Boolean get(SharedPreferences sharedPreferences, String key, Boolean defaultValue) {
                return sharedPreferences.getBoolean(key, defaultValue);
            }
        });
    }

    @Override
    public boolean contains(String key) {
        return getValue(key, false, new Getter<Boolean>() {
            @Override
            public Boolean get(SharedPreferences sharedPreferences, String key, Boolean defaultValue) {
                return sharedPreferences.contains(key);
            }
        });
    }

    @Override
    public Editor edit() {
        return getValue(new Getter<Editor>() {
            @Override
            public Editor get(SharedPreferences sharedPreferences, String key, Editor defaultValue) {
                return sharedPreferences.edit();
            }
        });
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        assertCorrectThread();

        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        assertCorrectThread();

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    private void assertCorrectThread() {
        if (Thread.currentThread().getId() != internalThreadId) {
            throw new RuntimeException("Thread violation, PersistentStorage should be accessed on the same thread as it was created on.");
        }
    }
}
