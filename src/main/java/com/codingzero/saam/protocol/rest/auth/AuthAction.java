package com.codingzero.saam.protocol.rest.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ruisun on 2016-06-11.
 */
public class AuthAction {

    private static final Logger LOG = LoggerFactory.getLogger(AuthAction.class);

    private final List<AuthHandler> authHandlerHandlers;
    private final Map<String, AuthHandler> actionAuthHandlerDict;

    public AuthAction(List<AuthHandler> authHandlerHandlers) {
        this.authHandlerHandlers = Collections.unmodifiableList(authHandlerHandlers);
        this.actionAuthHandlerDict = new HashMap<>();
        for (AuthHandler handler: authHandlerHandlers) {
            if (actionAuthHandlerDict.containsKey(handler.getName().toLowerCase())) {
                LOG.warn("Auth handler has already exist, " + handler.getName());
            }
            this.actionAuthHandlerDict.put(handler.getName().toLowerCase(), handler);
        }
    }

//    private void verify(AuthContext ctx) {
//        if (isDefaultNameValue()) {
//            for (AuthHandler authHandler : authHandlerHandlers) {
//                if (authHandler.verify(ctx)) {
//                    return;
//                }
//            }
//        }
//        for (String name: configuration.name()) {
//            AuthHandler handler = actionAuthHandlerDict.get(name.toLowerCase());
//            if (null == handler) {
//                throw new IllegalArgumentException("No such auth handler, \'" + name + "\'");
//            }
//            if (handler.verify(ctx)) {
//                return;
//            }
//        }
//        throw new AuthenticationFailedException(
//                "This action is not authorized to perform!");
//    }
//
//    private boolean isDefaultNameValue() {
//        for (String name: configuration.name()) {
//            if (name.length() > 0) {
//                return false;
//            }
//        }
//        return true;
//    }

}