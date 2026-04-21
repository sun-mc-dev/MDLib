package com.muhammaddaffa.mdlib.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachedConfig {

    private final Config config;
    private final Map<String, Object> cache = new HashMap<>();

    public CachedConfig(Config config) {
        this.config = config;
    }

    public void invalidate() {
        cache.clear();
        config.reloadConfig();
    }

    @SuppressWarnings("unchecked")
    private <T> T get(String path, T def) {
        if (cache.containsKey(path)) {
            Object cached = cache.get(path);
            return cached == null ? def : (T) cached;
        }
        Object val = config.getConfig().get(path, def);
        cache.put(path, val);
        return val == null ? def : (T) val;
    }

    public String getString(String path) {
        return getString(path, null);
    }

    public String getString(String path, String def) {
        return get(path, def);
    }

    public int getInt(String path) {
        return getInt(path, 0);
    }

    public int getInt(String path, int def) {
        return get(path, def);
    }

    public double getDouble(String path) {
        return getDouble(path, 0.0);
    }

    public double getDouble(String path, double def) {
        return get(path, def);
    }

    public boolean getBoolean(String path) {
        return getBoolean(path, false);
    }

    public boolean getBoolean(String path, boolean def) {
        return get(path, def);
    }

    public long getLong(String path) {
        return getLong(path, 0L);
    }

    public long getLong(String path, long def) {
        return get(path, def);
    }

    @SuppressWarnings("unchecked")
    public List<String> getStringList(String path) {
        if (cache.containsKey(path)) return (List<String>) cache.get(path);
        List<String> val = config.getStringList(path);
        cache.put(path, val);
        return val;
    }

    public Config getConfig() {
        return config;
    }
}