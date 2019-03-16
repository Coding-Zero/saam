package com.codingzero.saam.protocol.rest.auth;

public interface AuthHandler {

    String getType();

    boolean verify(AuthContext ctx);
}
