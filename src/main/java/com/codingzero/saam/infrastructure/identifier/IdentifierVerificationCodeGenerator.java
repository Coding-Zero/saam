package com.codingzero.saam.infrastructure.identifier;

import com.codingzero.saam.common.IdentifierType;

public interface IdentifierVerificationCodeGenerator {

    String generate(IdentifierType type);

}
