package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.IdentifierType;

public interface PasswordHelper {

    String encodePassword(String password);

    boolean verify(String password, String encodedPassword);

    String generateResetCode(IdentifierType type);

}
