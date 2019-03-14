package com.codingzero.saam.protocol.rest.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthContext {

    private Map<String, Object> context;

    public AuthContext() {
        this.context = new HashMap<>();
    }

    public Object get(String key) {
        return context.get(key);
    }

    public void put(String key, Object value) {
        context.put(key, value);
    }

    public Map<String, Object> listAll() {
        return Collections.unmodifiableMap(context);
    }
}
