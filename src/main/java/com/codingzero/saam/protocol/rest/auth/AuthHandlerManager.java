package com.codingzero.saam.protocol.rest.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthHandlerManager implements AuthHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthHandlerManager.class);

    private final List<AuthHandler> handlers;
    private final Map<String, AuthHandler> handlerMap;

    public AuthHandlerManager(List<AuthHandler> handlers) {
        this.handlers = Collections.unmodifiableList(handlers);
        this.handlerMap = new HashMap<>();
        for (AuthHandler handler: handlers) {
            if (handlerMap.containsKey(handler.getName().toLowerCase())) {
                LOG.warn("Auth handler has already exist, " + handler.getName());
            }
            this.handlerMap.put(handler.getName().toLowerCase(), handler);
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean verify(AuthContext ctx) {
        if (isRunAllAuthHandler(ctx)) {
            for (AuthHandler authHandler : handlers) {
                if (authHandler.verify(ctx)) {
                    return true;
                }
            }
            return false;
        } else {
            AuthHandler handler = handlerMap.get(ctx.getAuthHandlerName().toLowerCase());
            if (null == handler) {
                throw new IllegalArgumentException("No such auth handler, \'" + ctx.getAuthHandlerName()+ "\'");
            }
            return handler.verify(ctx);
        }
    }

    private boolean isRunAllAuthHandler(AuthContext ctx) {
        return null == ctx.getAuthHandlerName() || ctx.getAuthHandlerName().trim().length() == 0;
    }

}