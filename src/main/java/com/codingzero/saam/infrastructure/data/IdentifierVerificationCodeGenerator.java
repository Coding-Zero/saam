package com.codingzero.saam.infrastructure.data;

import com.codingzero.saam.common.IdentifierType;

public interface IdentifierVerificationCodeGenerator {

    String generate(IdentifierType type);

}
