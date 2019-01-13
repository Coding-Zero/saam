package com.codingzero.saam.infrastructure.database;

import com.codingzero.saam.common.IdentifierType;

public interface IdentifierVerificationCodeGenerator {

    String generate(IdentifierType type);

}
