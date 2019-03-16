package com.codingzero.saam.protocol.rest.auth;

/**
 * Created by ruisun on 2016-11-24.
 */
public class AuthFailedException extends RuntimeException {

    public AuthFailedException(String message) {
        super(message);
    }
}
