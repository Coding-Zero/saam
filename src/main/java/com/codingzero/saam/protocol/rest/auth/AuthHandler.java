package com.codingzero.saam.protocol.rest.auth;

public interface AuthHandler {

    String getName();

    boolean verify(AuthContext ctx);
}
