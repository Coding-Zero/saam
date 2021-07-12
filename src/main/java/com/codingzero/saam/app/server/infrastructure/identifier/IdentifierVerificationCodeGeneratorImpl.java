package com.codingzero.saam.app.server.infrastructure.identifier;

import com.codingzero.saam.common.IdentifierType;
import com.codingzero.saam.infrastructure.identifier.IdentifierVerificationCodeGenerator;
import com.codingzero.utilities.key.HMACKey;
import com.codingzero.utilities.key.RandomKey;


public class IdentifierVerificationCodeGeneratorImpl implements IdentifierVerificationCodeGenerator {

    @Override
    public String generate(IdentifierType type) {
        return RandomKey.nextUUIDKey()
                .toRandomHMACKey(HMACKey.Algorithm.SHA256)
                .toBase64String(true);
    }
}
