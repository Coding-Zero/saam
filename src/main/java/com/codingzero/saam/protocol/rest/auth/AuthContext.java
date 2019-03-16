package com.codingzero.saam.protocol.rest.auth;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AuthContext {

    private String token;
    private String authHandlerName;
    private Map<String, Object> context;

    public AuthContext(String token, String authHandlerName) {
        this.token = token;
        this.authHandlerName = authHandlerName;
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

    public String getToken() {
        return token;
    }

    public String getAuthHandlerName() {
        return authHandlerName;
    }
}
