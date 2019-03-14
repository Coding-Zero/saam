package com.codingzero.saam.protocol.rest.auth;

/**
 * Created by ruisun on 2016-11-24.
 */
public class AuthenticationFailedException extends RuntimeException {

    public AuthenticationFailedException(String message) {
        super(message);
    }
}
